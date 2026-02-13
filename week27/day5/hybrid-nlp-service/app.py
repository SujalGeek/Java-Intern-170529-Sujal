# from flask import Flask, request, jsonify

# from config.hybrid_engine import evaluate_answer
# from services.question_generator import QuestionGenerator
# from services.rag_engine import RAGEngine
# import os


# app = Flask(__name__)

# # -------------------------------------------------
# # Load AI services ONCE (important for performance)
# # -------------------------------------------------
# # This will initialize the Groq API connection
# question_generator = QuestionGenerator()
# rag_engine = RAGEngine()



# # -------------------------------------------------
# # Health Check
# # -------------------------------------------------
# @app.route("/health", methods=["GET"])
# def health():
#     return jsonify({
#         "status": "UP",
#         "service": "hybrid-nlp-service"
#     })


# # -------------------------------------------------
# # Hybrid Answer Evaluation API
# # -------------------------------------------------
# @app.route("/grade", methods=["POST"])
# def grade_answer():
#     try:
#         data = request.json or {}

#         student_answer = data.get("student_answer", "").strip()
#         teacher_answer = data.get("teacher_answer", [])
#         keywords = data.get("keywords", [])
#         bloom_level = data.get("bloom_level", "Understand")
#         max_score = int(data.get("max_score", 10))

#         if not student_answer:
#             return jsonify({"error": "student_answer is required"}), 400

#         # Normalize teacher answers
#         reference_answers = (
#             [teacher_answer] if isinstance(teacher_answer, str) else teacher_answer
#         )

#         result = evaluate_answer(
#             student_answer=student_answer,
#             reference_answers=reference_answers,
#             keywords=keywords,
#             bloom_level=bloom_level,
#             max_score=max_score
#         )

#         return jsonify(result)

#     except Exception as e:
#         return jsonify({"error": str(e)}), 500

# above one -> this will remain as it is after aborting the new code (chatgpt 1st time bro)
# -------------------------------------------------
# AI Question Generation API (Groq/LangChain)
# -------------------------------------------------
# @app.route("/generate-quiz", methods=["POST"])
# def generate_quiz():
#     try:
#         data = request.json or {}

#         # Extract fields from the request
#         concept = data.get("concept", "").strip()
#         description = data.get("description", "").strip()
#         keywords = data.get("keywords", [])
#         bloom_level = data.get("bloom_level", "Understand")
#         count = int(data.get("count", 3))

#         # ---------- VALIDATION ----------
#         if not concept and not description:
#             return jsonify({"error": "concept and description are required"}), 400

#         # ---------- CONTEXT BUILDING ----------
#         # Combine the inputs into a single rich text block for the LLM
#         keywords_str = ", ".join(keywords) if keywords else "None"
#         context_text = f"Topic: {concept}. \nDescription: {description}. \nKey terms to include: {keywords_str}."

#         # ---------- GENERATE ----------
#         # Call the new Groq-powered generate_quiz method
#         questions = question_generator.generate_quiz(
#             context=context_text,
#             count=count,
#             bloom_level=bloom_level
#         )

#         # ---------- RETURN ----------
#         return jsonify({
#             "status": "success",
#             "concept": concept,
#             "bloom_level": bloom_level,
#             "question_count": len(questions),
#             "generated_questions": questions
#         })

#     except Exception as e:
#         return jsonify({"error": str(e)}), 500
# main code is above one if we are not able to generate the quiz or generate the proper questions according to the bloom taxnomy bro


# this is the updated 1st time code from the llm(chatgpt se kiya tha)
# @app.route("/generate-quiz", methods=["POST"])
# def generate_quiz():
#     data = request.json

#     concept = data.get("concept")
#     bloom_level = data.get("bloom_level", "Understand")
#     count = data.get("count", 1)

#     if not concept:
#         return {"error": "Concept required"}, 400

#     # -----------------------------
#     # RAG Retrieval
#     # -----------------------------
#     context, confidence = rag_engine.retrieve(concept)

#     # -----------------------------
#     # Multi Query Fallback
#     # -----------------------------
#     if context is None:
#         alt_queries = [
#             f"Explain {concept} in Java",
#             f"{concept} OOP concept",
#             f"{concept} superclass subclass"
#         ]

#         for q in alt_queries:
#             context, confidence = rag_engine.retrieve(q)
#             if context:
#                 break

#     # -----------------------------
#     # Final LLM Fallback
#     # -----------------------------
#     if context is None:
#         context = f"Java concept: {concept}"

#     questions = question_generator.generate_quiz(
#         context=context,
#         concept=concept,
#         count=count,
#         bloom_level=bloom_level
#     )

#     return {
#         "status": "success",
#         "bloom_level": bloom_level,
#         "retrieval_confidence": round(confidence, 3),
#         "questions": questions
#     }


# @app.route("/upload-book", methods=["POST"])
# def upload_book():
#     if "file" not in request.files:
#         return {"error": "No file uploaded"}, 400

#     file = request.files["file"]
#     file_path = os.path.join("uploads", file.filename)

#     os.makedirs("uploads", exist_ok=True)
#     file.save(file_path)

#     text = rag_engine.extract_text_from_pdf(file_path)
#     rag_engine.build_vector_store(text)

#     return {
#         "status": "success",
#         "message": "Book indexed successfully"
#     }


# -------------------------------------------------
# Application Entry Point
# -------------------------------------------------


from flask import Flask, request, jsonify
from config.hybrid_engine import index_book, generate_quiz, generate_reference_answer , evaluate_student_answer

app = Flask(__name__)

# ------------------------------------------
# HEALTH CHECK
# ------------------------------------------
@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "UP"})

# ------------------------------------------
# PDF INDEX
# ------------------------------------------
@app.route("/index-book", methods=["POST"])
def upload_book():
    file = request.files.get("file")

    if not file:
        return jsonify({"error": "No file uploaded"}), 400

    file_path = "uploaded_book.pdf"
    file.save(file_path)

    index_book(file_path)

    return jsonify({"status": "success", "message": "Book indexed successfully"})

# ------------------------------------------
# GENERATE QUIZ
# ------------------------------------------
@app.route("/generate-quiz", methods=["POST"])
def generate():
    data = request.json

    concept = data.get("concept", "")
    description = data.get("description", "")
    bloom_level = data.get("bloom_level", "Understand")
    count = data.get("count", 3)

    if not concept or not description:
        return jsonify({"error": "Concept and description required"}), 400

    result = generate_quiz(
        concept=concept,
        description=description,
        bloom_level=bloom_level,
        count=count
    )

    return jsonify(result)

@app.route("/generate-reference", methods=["POST"])
def generate_reference():

    data = request.json
    question = data.get("question", "")
    bloom_level = data.get("bloom_level", "Understand")

    if not question:
        return jsonify({"error": "Question is required"}), 400

    result = generate_reference_answer(
        question=question,
        bloom_level=bloom_level
    )

    return jsonify(result)

# @app.route("/evaluate-answer", methods=["POST"])
# def evaluate_answer_api():
#     try:
#         data = request.json

#         question = data.get("question")
#         student_answer = data.get("student_answer")
#         bloom_level = data.get("bloom_level", "Understand")

#         if not question or not student_answer:
#             return jsonify({
#                 "status": "failed",
#                 "message": "Question and student_answer are required"
#             }), 400

#         # 🔹 Step 1: Generate Reference Answer using RAG
#         ref_result = generate_reference_answer(
#             question=question,
#             bloom_level=bloom_level
#         )

#         reference_answer = ref_result.get("reference_answer", "")

#         # 🔹 Step 2: Evaluate using existing evaluate_answer() logic
#         evaluation = evaluate_answer(
#             student_answer=student_answer,
#             reference_answers=[reference_answer],
#             keywords=[],
#             bloom_level=bloom_level,
#             max_score=100
#         )

#         return jsonify({
#             "status": "success",
#             "score": evaluation.get("score"),
#             "feedback": evaluation.get("feedback"),
#             "reference_answer": reference_answer
#         })

#     except Exception as e:
#         print("❌ Evaluation Error:", str(e))
#         return jsonify({
#             "status": "error",
#             "message": str(e)
#         }), 500
@app.route("/evaluate-answer", methods=["POST"])
def evaluate_answer_api():

    data = request.json

    question = data.get("question")
    student_answer = data.get("student_answer")
    bloom_level = data.get("bloom_level", "Understand")

    if not question or not student_answer:
        return jsonify({
            "status": "failed",
            "message": "Question and student_answer are required"
        }), 400

    result = evaluate_student_answer(
        question=question,
        student_answer=student_answer,
        bloom_level=bloom_level
    )

    return jsonify(result)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001, debug=True)

