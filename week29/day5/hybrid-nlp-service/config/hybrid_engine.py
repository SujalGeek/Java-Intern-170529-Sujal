from services.rag_engine import RAGEngine
from services.question_generator import QuestionGenerator
from sentence_transformers import util
from config.semantic_engine import SemanticEngine
from config.rule_engine import keyword_coverage, grammar_score, bloom_cap

rag_engine = RAGEngine()
question_generator = QuestionGenerator()
print("Loading SBERT model")
# sbert_model = SentenceTransformer("all-MiniLM-L6-v2")
semantic_engine = SemanticEngine()

# ---------------------------------------------------
# INDEX BOOK FUNCTION (REQUIRED BY app.py)
# ---------------------------------------------------
def index_book(pdf_path):

    success = rag_engine.index_pdf(pdf_path)

    if success:
        return {
            "status": "success",
            "message": "Book indexed successfully"
        }
    else:
        return {
            "status": "failed",
            "message": "Book indexing failed"
        }


# ---------------------------------------------------
# GENERATE QUIZ FUNCTION
# ---------------------------------------------------
def generate_quiz(concept, description, bloom_level="Understand", count=3):

    query = f"{concept}. {description}"

    #Retrieve from PDF
    retrieved_chunks, confidence, mode = rag_engine.retrieve(query)

    if len(retrieved_chunks) == 0:
        return {
            "status": "failed",
            "message": "No indexed book found."
        }

    #  Merge top chunks
    context = "\n\n".join(retrieved_chunks)

    print("\n Final Context Sent To LLM:\n")
    print(context[:500])
    print("\n-----------------------------\n")

    # Generate via Groq
    questions = question_generator.generate_questions(
        context=context,
        bloom_level=bloom_level,
        count=count
    )

    return {
        "status": "success",
        "bloom_level": bloom_level,
        "questions": questions,
        "question_count": len(questions),
        "retrieval_confidence": confidence,
        "retrieval_mode": mode
    }

def generate_reference_answer(question, bloom_level="Understand"):

    print("\n Generating Reference Answer...")
    print("Question:", question)

    # Retrieve context from indexed PDF
    retrieved_chunks, confidence, mode = rag_engine.retrieve(question)

    if len(retrieved_chunks) == 0:
        return {
            "status": "failed",
            "message": "No indexed book found."
        }

    context = "\n\n".join(retrieved_chunks)

    print("\n Context Used for Reference Answer:\n")
    print(context[:800])

    # Prompt LLM for model answer
    answer = question_generator.generate_reference_answer(
        context=context,
        question=question,
        bloom_level=bloom_level
    )

    return {
        "status": "success",
        "reference_answer": answer,
        "retrieval_confidence": confidence,
        "retrieval_mode": mode
    }


def evaluate_student_answer(question, student_answer, bloom_level="Understand"):

    print("\nEvaluating Student Answer...")
    print(" Question:", question)

    # -------------------------------------------------
    # STEP 1: Retrieve Context
    # -------------------------------------------------
    retrieved_chunks, confidence, mode = rag_engine.retrieve(question)

    if not retrieved_chunks:
        return {
            "status": "failed",
            "message": "No indexed book found."
        }

    context = "\n\n".join(retrieved_chunks)

    # -------------------------------------------------
    # STEP 2: Generate Reference Answer
    # -------------------------------------------------
    reference_answer = question_generator.generate_reference_answer(
        context=context,
        question=question,
        bloom_level=bloom_level
    )

    # -------------------------------------------------
    # STEP 3: Semantic Similarity
    # -------------------------------------------------
    emb_student = rag_engine.model.encode(student_answer, convert_to_tensor=True)
    emb_reference = rag_engine.model.encode(reference_answer, convert_to_tensor=True)

    semantic_similarity = util.cos_sim(emb_student, emb_reference).item()
    semantic_similarity = max(0.0, float(semantic_similarity))

    print(" Semantic Similarity:", round(semantic_similarity, 3))

    grammar_quality = grammar_score(student_answer)
    word_count = len(student_answer.split())

    # -------------------------------------------------
    #  HARD OFF-TOPIC FILTER (Very Strict)
    # -------------------------------------------------
    if semantic_similarity < 0.50:
        final_score = round(semantic_similarity * 35, 2)

        return {
            "status": "success",
            "score": final_score,
            "semantic_similarity": round(semantic_similarity, 3),
            "keyword_coverage": 0.0,
            "grammar_score": grammar_quality,
            "retrieval_confidence": confidence,
            "retrieval_mode": mode,
            "feedback": "Answer is off-topic or conceptually incorrect.",
            "reference_answer": reference_answer
        }

    # -------------------------------------------------
    # STEP 4: Keyword Coverage
    # -------------------------------------------------
    reference_words = [
        w for w in reference_answer.lower().split()
        if len(w) > 4 and w.isalpha()
    ]

    keywords = list(set(reference_words))[:30]
    keyword_score = keyword_coverage(student_answer, keywords)

    print("Keyword Coverage:", keyword_score)
    print("Grammar Score:", grammar_quality)

    # -------------------------------------------------
    # STEP 5: Balanced Hybrid Score
    # -------------------------------------------------
    base_score = (
        0.55 * semantic_similarity +
        0.30 * keyword_score +
        0.15 * grammar_quality
    )

    # -------------------------------------------------
    # STEP 6: Depth / Length Control
    # -------------------------------------------------
    if word_count < 5:
        base_score *= 0.4
    elif word_count < 10:
        base_score *= 0.65
    elif word_count < 20:
        base_score *= 0.85

    # -------------------------------------------------
    # STEP 7: Bloom Cap
    # -------------------------------------------------
    base_score = bloom_cap(base_score, bloom_level)

    # -------------------------------------------------
    # FINAL SCALING
    # -------------------------------------------------
    final_score = round(base_score * 100, 2)

    print("Final Score:", final_score)

    # -------------------------------------------------
    # Feedback Logic
    # -------------------------------------------------
    if final_score >= 85:
        feedback = "Excellent answer. Strong conceptual clarity and coverage."
    elif final_score >= 65:
        feedback = "Good answer but needs deeper explanation and refinement."
    elif final_score >= 45:
        feedback = "Partial understanding shown. Several key concepts missing."
    else:
        feedback = "Answer shows weak understanding or incorrect interpretation."

    return {
        "status": "success",
        "score": final_score,
        "semantic_similarity": round(semantic_similarity, 3),
        "keyword_coverage": keyword_score,
        "grammar_score": grammar_quality,
        "retrieval_confidence": confidence,
        "retrieval_mode": mode,
        "feedback": feedback,
        "reference_answer": reference_answer
    }
