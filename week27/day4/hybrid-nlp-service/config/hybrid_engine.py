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

rag_engine = RAGEngine()
question_generator = QuestionGenerator()

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
