# # from services.semantic_engine import SemanticEngine
# # from services.rule_engine import RuleEngine


# # class HybridEngine:

# #     def __init__(self):
# #         self.semantic = SemanticEngine()


# #     def grade(self,payload):
# #         student = payload["student_answer"]
# #         references = payload["teacher_answer"]
# #         keywords=payload.get("keywords",[])
# #         bloom= payload.get("bloom_level","Understand")
# #         max_score=payload.get("max_score",10)


# #         semantic_score = self.semantic.similarity(student,references)
# #         keyword_score = RuleEngine.keyword_coverage(student,keywords)
# #         grammar_score = RuleEngine.grammar_score(student)

# #         rule_score = round(0.6*keyword_score + 0.4*grammar_score,3)
# #         hybrid = round(0.5*semantic_score + 0.5*rule_score,3)

# #         if keyword_score == 0:
# #             hybrid = min(hybrid,0.4)

# #         bloom_caps = {
# #             "Remember": 0.6,
# #             "Understand": 0.75,
# #             "Apply": 0.85,
# #             "Analyze": 1.0
# #         }
        

# #         final_norm = min(hybrid,bloom_caps.get(bloom,1.0))
# #         final_score = round(final_norm*max_score,2)

# #         status = "Pass" if final_norm >= 0.5 else "Fail"


# #         return {
# #             "semantic_similarity": semantic_score,
# #             "keyword_coverage": keyword_score,
# #             "grammar_score": grammar_score,
# #             "rule_based_score": rule_score,
# #             "final_normalized_score": round(final_norm, 3),
# #             "final_score": final_score,
# #             "status": status,
# #             "bloom_level": bloom
# #         }


# # config/hybrid_engine.py

# from config.semantic_engine import SemanticEngine
# from config.rule_engine import keyword_coverage, grammar_score, bloom_cap

# semantic_engine = SemanticEngine()


# def evaluate_answer(
#     student_answer,
#     reference_answers,
#     keywords,
#     bloom_level="Understand",
#     max_score=10
# ):
#     # ---------- Semantic Layer ----------
#     semantic_score = semantic_engine.semantic_similarity(
#         student_answer, reference_answers
#     )

#     # ---------- Rule Layer ----------
#     key_score = keyword_coverage(student_answer, keywords)
#     gram_score = grammar_score(student_answer)

#     rule_score = round(
#         0.6 * key_score + 0.4 * gram_score, 3
#     )

#     # ---------- Hybrid Fusion ----------
#     hybrid_score = round(
#         0.5 * semantic_score + 0.5 * rule_score, 3
#     )

#     # ---------- Hard Constraints ----------
#     if key_score == 0:
#         hybrid_score = min(hybrid_score, 0.4)

#     if semantic_score < 0.45:
#         hybrid_score *= 0.8

#     # ---------- Bloom Normalization ----------
#     final_normalized = round(
#         bloom_cap(hybrid_score, bloom_level), 3
#     )

#     final_score = round(final_normalized * max_score, 2)

#     status = "Pass" if final_normalized >= 0.5 else "Fail"

#     return {
#         "semantic_similarity": semantic_score,
#         "keyword_coverage": key_score,
#         "grammar_score": gram_score,
#         "rule_based_score": rule_score,
#         "final_normalized_score": final_normalized,
#         "final_score": final_score,
#         "status": status,
#         "bloom_level": bloom_level
#     }
# the above we have been used many times

# config/hybrid_engine.py

# from services.rag_engine import RAGEngine
# from services.question_generator import QuestionGenerator

# rag_engine = RAGEngine()
# question_generator = QuestionGenerator()

# # ------------------------------------------
# # INDEX BOOK
# # ------------------------------------------
# def index_book(pdf_path):
#     return rag_engine.index_pdf(pdf_path)

# # ------------------------------------------
# # GENERATE QUIZ (HYBRID RAG)
# # ------------------------------------------
# def generate_quiz(concept, description, bloom_level="Understand", count=3):

#     query = f"{concept}. {description}"

#     retrieved_chunks, confidence, mode = rag_engine.retrieve(query)

#     if mode != "not_indexed" and len(retrieved_chunks) > 0:
#         merged_context = "\n\n".join(retrieved_chunks[:3])
#     else:
#         merged_context = query

#     questions = question_generator.generate_questions(
#         context=merged_context,
#         bloom_level=bloom_level,
#         count=count
#     )

#     # Fallback safety
#     if len(questions) == 0:
#         questions = question_generator.generate_questions(
#             context=query,
#             bloom_level=bloom_level,
#             count=count
#         )

#     return {
#         "status": "success",
#         "bloom_level": bloom_level,
#         "retrieval_confidence": confidence,
#         "retrieval_mode": mode,
#         "question_count": len(questions),
#         "questions": questions
#     }

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

    print("\n📄 Final Context Sent To LLM:\n")
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

    print("\n🔎 Generating Reference Answer...")
    print("📝 Question:", question)

    # Retrieve context from indexed PDF
    retrieved_chunks, confidence, mode = rag_engine.retrieve(question)

    if len(retrieved_chunks) == 0:
        return {
            "status": "failed",
            "message": "No indexed book found."
        }

    context = "\n\n".join(retrieved_chunks)

    print("\n📄 Context Used for Reference Answer:\n")
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

# def evaluate_student_answer(question, student_answer, bloom_level="Understand"):

#     print("\n🔎 Evaluating Student Answer...")
#     print("📝 Question:", question)

#     # -------------------------------
#     # STEP 1: Retrieve Context
#     # -------------------------------
#     retrieved_chunks, confidence, mode = rag_engine.retrieve(question)

#     if len(retrieved_chunks) == 0:
#         return {
#             "status": "failed",
#             "message": "No indexed book found."
#         }

#     context = "\n\n".join(retrieved_chunks)

#     print("\n📄 Context Used:\n")
#     print(context[:800])
#     print("\n---------------------------")

#     # -------------------------------
#     # STEP 2: Generate Reference Answer
#     # -------------------------------
#     reference_answer = question_generator.generate_reference_answer(
#         context=context,
#         question=question,
#         bloom_level=bloom_level
#     )

#     print("\n📘 Generated Reference Answer:\n")
#     print(reference_answer[:500])
#     print("\n---------------------------")

#     # -------------------------------
#     # STEP 3: Semantic Similarity (SBERT)
#     # -------------------------------
#     # emb_student = sbert_model.encode(student_answer, convert_to_tensor=True)
#     # emb_reference = sbert_model.encode(reference_answer, convert_to_tensor=True)

#     # similarity = util.cos_sim(emb_student, emb_reference).item()
#     similarity = semantic_engine.semantic_similarity(
#     student_answer,
#     [reference_answer]
# )

#     print("\n📊 Semantic Similarity:", similarity)

#     # -------------------------------
#     # STEP 4: Score Calculation
#     # -------------------------------
#     max_score = 100
#     final_score = round(similarity * max_score, 2)

#     # -------------------------------
#     # STEP 5: Feedback Generation
#     # -------------------------------
#     if similarity >= 0.75:
#         feedback = "Excellent answer. Strong conceptual alignment with reference."
#     elif similarity >= 0.55:
#         feedback = "Good answer but lacks depth or precision in some areas."
#     elif similarity >= 0.35:
#         feedback = "Partial understanding shown. Key concepts missing."
#     else:
#         feedback = "Answer is largely incorrect or unrelated to the expected concept."

#     return {
#         "status": "success",
#         "score": final_score,
#         "similarity": round(similarity, 3),
#         "feedback": feedback,
#         "reference_answer": reference_answer,
#         "retrieval_confidence": confidence,
#         "retrieval_mode": mode
#     }


# def evaluate_student_answer(question, student_answer, bloom_level="Understand"):

#     print("\n🔎 Evaluating Student Answer...")
#     print("📝 Question:", question)

#     # -------------------------------
#     # STEP 1: Retrieve Context
#     # -------------------------------
#     retrieved_chunks, confidence, mode = rag_engine.retrieve(question)

#     if len(retrieved_chunks) == 0:
#         return {
#             "status": "failed",
#             "message": "No indexed book found."
#         }

#     context = "\n\n".join(retrieved_chunks)

#     # -------------------------------
#     # STEP 2: Generate Reference Answer
#     # -------------------------------
#     reference_answer = question_generator.generate_reference_answer(
#         context=context,
#         question=question,
#         bloom_level=bloom_level
#     )

#     # -------------------------------
#     # STEP 3: Semantic Similarity
#     # -------------------------------
#     emb_student = rag_engine.model.encode(student_answer, convert_to_tensor=True)
#     emb_reference = rag_engine.model.encode(reference_answer, convert_to_tensor=True)

#     semantic_similarity = util.cos_sim(emb_student, emb_reference).item()
#     semantic_similarity = max(0, semantic_similarity)

#     print("📊 Semantic Similarity:", round(semantic_similarity, 3))

#     # -------------------------------
#     # STEP 4: Keyword Coverage
#     # -------------------------------
#     # Auto extract keywords from reference
#     keywords = list(set(reference_answer.lower().split()))[:15]

#     keyword_score = keyword_coverage(student_answer, keywords)

#     print("🧠 Keyword Coverage:", keyword_score)

#     # -------------------------------
#     # STEP 5: Grammar / Length Score
#     # -------------------------------
#     grammar_quality = grammar_score(student_answer)

#     print("✍️ Grammar Score:", grammar_quality)

#     # -------------------------------
#     # STEP 6: Hybrid Weighted Score
#     # -------------------------------
#     base_score = (
#         0.6 * semantic_similarity +
#         0.25 * keyword_score +
#         0.15 * grammar_quality
#     )

#     # Adjust by retrieval confidence
#     weighted_score = base_score * confidence

#     # Bloom cap
#     capped_score = bloom_cap(weighted_score, bloom_level)

#     final_score = round(capped_score * 100, 2)

#     print("🎯 Final Score:", final_score)

#     # -------------------------------
#     # STEP 7: Feedback
#     # -------------------------------
#     if final_score >= 80:
#         feedback = "Excellent answer. Strong conceptual clarity and coverage."
#     elif final_score >= 60:
#         feedback = "Good answer but needs deeper explanation and refinement."
#     elif final_score >= 40:
#         feedback = "Partial understanding shown. Several key concepts missing."
#     else:
#         feedback = "Answer shows weak understanding or incorrect interpretation."

#     return {
#         "status": "success",
#         "score": final_score,
#         "semantic_similarity": round(semantic_similarity, 3),
#         "keyword_coverage": keyword_score,
#         "grammar_score": grammar_quality,
#         "retrieval_confidence": confidence,
#         "retrieval_mode": mode,
#         "feedback": feedback,
#         "reference_answer": reference_answer
#     }

# def evaluate_student_answer(question, student_answer, bloom_level="Understand"):

#     print("\n🔎 Evaluating Student Answer...")
#     print("📝 Question:", question)

#     # -------------------------------
#     # STEP 1: Retrieve Context
#     # -------------------------------
#     retrieved_chunks, confidence, mode = rag_engine.retrieve(question)

#     if len(retrieved_chunks) == 0:
#         return {
#             "status": "failed",
#             "message": "No indexed book found."
#         }

#     context = "\n\n".join(retrieved_chunks)

#     # -------------------------------
#     # STEP 2: Generate Reference Answer
#     # -------------------------------
#     reference_answer = question_generator.generate_reference_answer(
#         context=context,
#         question=question,
#         bloom_level=bloom_level
#     )

#     # -------------------------------
#     # STEP 3: Semantic Similarity
#     # -------------------------------
#     emb_student = rag_engine.model.encode(student_answer, convert_to_tensor=True)
#     emb_reference = rag_engine.model.encode(reference_answer, convert_to_tensor=True)

#     semantic_similarity = util.cos_sim(emb_student, emb_reference).item()
#     semantic_similarity = max(0.0, semantic_similarity)

#     print("📊 Semantic Similarity:", round(semantic_similarity, 3))

#     # -------------------------------
#     # STEP 4: Keyword Coverage
#     # -------------------------------
#     # Clean keyword extraction (remove very small words)
#     reference_words = [
#         word for word in reference_answer.lower().split()
#         if len(word) > 4
#     ]

#     keywords = list(set(reference_words))[:20]

#     keyword_score = keyword_coverage(student_answer, keywords)

#     print("🧠 Keyword Coverage:", keyword_score)

#     # -------------------------------
#     # STEP 5: Grammar / Length Score
#     # -------------------------------
#     grammar_quality = grammar_score(student_answer)
#     word_count = len(student_answer.split())

    
    
#     print("✍️ Grammar Score:", grammar_quality)

#     # -------------------------------
#     # STEP 6: Base Weighted Score
#     # -------------------------------
#     base_score = (
#         0.65 * semantic_similarity +
#         0.25 * keyword_score +
#         0.10 * grammar_quality
#     )

#     if word_count < 10:
#         base_score *= 0.6
#     elif word_count < 20:
#         base_score *= 0.8

#     # weighted_score = base_score * confidence
#     weighted_score = base_score
#     # -------------------------------
#     # STEP 7: Hard Penalties
#     # -------------------------------

#     # Off-topic guard
#     if semantic_similarity < 0.30:
#         weighted_score = semantic_similarity * 0.40

#     # Very short answer penalty
    

#     if word_count < 4:
#         weighted_score *= 0.4
#     elif word_count < 8:
#         weighted_score *= 0.6

#     # Weak keyword penalty
#     if keyword_score < 0.10:
#         weighted_score *= 0.75

#     # Bloom cap
#     capped_score = bloom_cap(weighted_score, bloom_level)

#     final_score = round(capped_score * 100, 2)

#     print("🎯 Final Score:", final_score)

#     # -------------------------------
#     # STEP 8: Feedback
#     # -------------------------------
#     if final_score >= 85:
#         feedback = "Excellent answer. Strong conceptual clarity and coverage."
#     elif final_score >= 65:
#         feedback = "Good answer but needs deeper explanation and refinement."
#     elif final_score >= 45:
#         feedback = "Partial understanding shown. Several key concepts missing."
#     else:
#         feedback = "Answer shows weak understanding or incorrect interpretation."

#     return {
#         "status": "success",
#         "score": final_score,
#         "semantic_similarity": round(semantic_similarity, 3),
#         "keyword_coverage": keyword_score,
#         "grammar_score": grammar_quality,
#         "retrieval_confidence": confidence,
#         "retrieval_mode": mode,
#         "feedback": feedback,
#         "reference_answer": reference_answer
#     }

# def evaluate_student_answer(question, student_answer, bloom_level="Understand"):

#     print("\n🔎 Evaluating Student Answer...")
#     print("📝 Question:", question)

#     # -------------------------------
#     # STEP 1: Retrieve Context
#     # -------------------------------
#     retrieved_chunks, confidence, mode = rag_engine.retrieve(question)

#     if len(retrieved_chunks) == 0:
#         return {
#             "status": "failed",
#             "message": "No indexed book found."
#         }

#     context = "\n\n".join(retrieved_chunks)

#     # -------------------------------
#     # STEP 2: Generate Reference Answer
#     # -------------------------------
#     reference_answer = question_generator.generate_reference_answer(
#         context=context,
#         question=question,
#         bloom_level=bloom_level
#     )

#     # -------------------------------
#     # STEP 3: Semantic Similarity
#     # -------------------------------
#     emb_student = rag_engine.model.encode(student_answer, convert_to_tensor=True)
#     emb_reference = rag_engine.model.encode(reference_answer, convert_to_tensor=True)

#     semantic_similarity = util.cos_sim(emb_student, emb_reference).item()
#     semantic_similarity = max(0.0, semantic_similarity)

#     print("📊 Semantic Similarity:", round(semantic_similarity, 3))

#     # -------------------------------
#     # STEP 4: Keyword Coverage
#     # -------------------------------
#     reference_words = [
#         word for word in reference_answer.lower().split()
#         if len(word) > 4
#     ]

#     keywords = list(set(reference_words))[:20]

#     keyword_score = keyword_coverage(student_answer, keywords)

#     print("🧠 Keyword Coverage:", keyword_score)

#     # -------------------------------
#     # STEP 5: Grammar Score
#     # -------------------------------
#     grammar_quality = grammar_score(student_answer)
#     word_count = len(student_answer.split())

#     print("✍️ Grammar Score:", grammar_quality)

#     # -------------------------------
#     # STEP 6: Base Weighted Score
#     # -------------------------------
#     base_score = (
#         0.65 * semantic_similarity +
#         0.25 * keyword_score +
#         0.10 * grammar_quality
#     )

#     # -------------------------------
#     # STEP 7: Depth Adjustment (Single Controlled Penalty)
#     # -------------------------------
#     if word_count < 5:
#         base_score *= 0.5
#     elif word_count < 12:
#         base_score *= 0.75

#     # -------------------------------
#     # STEP 8: Off-topic Guard (Safer Version)
#     # -------------------------------
#     if semantic_similarity < 0.60:
#         final_score = round(semantic_similarity * 30, 2)

#     # -------------------------------
#     # STEP 9: Bloom Cap
#     # -------------------------------
#     capped_score = bloom_cap(base_score, bloom_level)

#     final_score = round(capped_score * 100, 2)

#     print("🎯 Final Score:", final_score)

#     # -------------------------------
#     # STEP 10: Feedback
#     # -------------------------------
#     if final_score >= 85:
#         feedback = "Excellent answer. Strong conceptual clarity and coverage."
#     elif final_score >= 65:
#         feedback = "Good answer but needs deeper explanation and refinement."
#     elif final_score >= 45:
#         feedback = "Partial understanding shown. Several key concepts missing."
#     else:
#         feedback = "Answer shows weak understanding or incorrect interpretation."

#     return {
#         "status": "success",
#         "score": final_score,
#         "semantic_similarity": round(semantic_similarity, 3),
#         "keyword_coverage": keyword_score,
#         "grammar_score": grammar_quality,
#         "retrieval_confidence": confidence,
#         "retrieval_mode": mode,
#         "feedback": feedback,
#         "reference_answer": reference_answer
#     }

def evaluate_student_answer(question, student_answer, bloom_level="Understand"):

    print("\n🔎 Evaluating Student Answer...")
    print("📝 Question:", question)

    # -------------------------------
    # STEP 1: Retrieve Context
    # -------------------------------
    retrieved_chunks, confidence, mode = rag_engine.retrieve(question)

    if len(retrieved_chunks) == 0:
        return {
            "status": "failed",
            "message": "No indexed book found."
        }

    context = "\n\n".join(retrieved_chunks)

    # -------------------------------
    # STEP 2: Generate Reference Answer
    # -------------------------------
    reference_answer = question_generator.generate_reference_answer(
        context=context,
        question=question,
        bloom_level=bloom_level
    )

    # -------------------------------
    # STEP 3: Semantic Similarity
    # -------------------------------
    emb_student = rag_engine.model.encode(student_answer, convert_to_tensor=True)
    emb_reference = rag_engine.model.encode(reference_answer, convert_to_tensor=True)

    semantic_similarity = util.cos_sim(emb_student, emb_reference).item()
    semantic_similarity = max(0.0, semantic_similarity)

    print("📊 Semantic Similarity:", round(semantic_similarity, 3))

    # -------------------------------
    # HARD OFF-TOPIC GUARD
    # -------------------------------
    if semantic_similarity < 0.60:
        final_score = round(semantic_similarity * 30, 2)

        return {
            "status": "success",
            "score": final_score,
            "semantic_similarity": round(semantic_similarity, 3),
            "keyword_coverage": 0.0,
            "grammar_score": grammar_score(student_answer),
            "retrieval_confidence": confidence,
            "retrieval_mode": mode,
            "feedback": "Answer is off-topic or conceptually incorrect.",
            "reference_answer": reference_answer
        }

    # -------------------------------
    # STEP 4: Keyword Coverage
    # -------------------------------
    reference_words = [
        word for word in reference_answer.lower().split()
        if len(word) > 4
    ]

    keywords = list(set(reference_words))[:20]
    keyword_score = keyword_coverage(student_answer, keywords)

    print("🧠 Keyword Coverage:", keyword_score)

    # -------------------------------
    # STEP 5: Grammar Score
    # -------------------------------
    grammar_quality = grammar_score(student_answer)
    word_count = len(student_answer.split())

    print("✍️ Grammar Score:", grammar_quality)

    # -------------------------------
    # STEP 6: Weighted Score
    # -------------------------------
    base_score = (
        0.65 * semantic_similarity +
        0.25 * keyword_score +
        0.10 * grammar_quality
    )

    # Depth adjustment
    if word_count < 5:
        base_score *= 0.5
    elif word_count < 12:
        base_score *= 0.75

    # Bloom cap
    capped_score = bloom_cap(base_score, bloom_level)

    final_score = round(capped_score * 100, 2)

    print("🎯 Final Score:", final_score)

    # -------------------------------
    # Feedback
    # -------------------------------
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
