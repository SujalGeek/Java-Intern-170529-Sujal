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
# from config.hybrid_engine import index_book
from config.hybrid_engine import generate_quiz, generate_reference_answer , evaluate_student_answer
from services.rag_engine import RAGEngine
from sentence_transformers import util
import json


app = Flask(__name__)
rag_engine = RAGEngine()
# REQUIRED_BLOOM_DISTRIBUTION = {
#     "REMEMBER": 2,
#     "UNDERSTAND": 2,
#     "APPLY": 2,
#     "ANALYZE": 2,
#     "EVALUATE": 1,
#     "CREATE": 1
# }

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

    # index_book(file_path)
    rag_engine.index_pdf(file_path)

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
# @app.route("/evaluate-answer", methods=["POST"])
# def evaluate_answer_api():

#     data = request.json

#     question = data.get("question")
#     student_answer = data.get("student_answer")
#     bloom_level = data.get("bloom_level", "Understand")

#     if not question or not student_answer:
#         return jsonify({
#             "status": "failed",
#             "message": "Question and student_answer are required"
#         }), 400

#     result = evaluate_student_answer(
#         question=question,
#         student_answer=student_answer,
#         bloom_level=bloom_level
#     )

#     return jsonify(result)


# @app.route("/evaluate-answer", methods=["POST"])
# def evaluate_score_api():

#     data = request.json

#     student_answer = data.get("student_answer")
#     reference_answer = data.get("reference_answer")
#     bloom_level = data.get("bloom_level", "Understand")

#     if not student_answer or not reference_answer:
#         return jsonify({"status": "failed", "message": "Missing data"}), 400

#     # SBERT similarity
#     emb_student = rag_engine.model.encode(student_answer, convert_to_tensor=True)
#     emb_reference = rag_engine.model.encode(reference_answer, convert_to_tensor=True)

#     similarity = util.cos_sim(emb_student, emb_reference).item()
#     similarity = max(0.0, similarity)

#     # Simple scoring
#     score = round(similarity * 100, 2)

#     # Feedback
#     if score >= 80:
#         feedback = "Excellent answer."
#     elif score >= 60:
#         feedback = "Good answer but needs refinement."
#     elif score >= 40:
#         feedback = "Partial understanding."
#     else:
#         feedback = "Weak or incorrect answer."

#     return jsonify({
#         "status": "success",
#         "score": score,
#         "feedback": feedback
#     })

@app.route("/evaluate-answer", methods=["POST"])
def evaluate_answer_api():

    data = request.json
    print("Evaluate answer hit")
    student_answer = data.get("student_answer")
    reference_answer = data.get("reference_answer")
    bloom_level = data.get("bloom_level", "Understand")

    if not student_answer or not reference_answer:
        return jsonify({"status": "failed", "message": "Missing data"}), 400

    # -------------------------------
    # SBERT Similarity
    # -------------------------------
    emb_student = rag_engine.model.encode(student_answer, convert_to_tensor=True)
    emb_reference = rag_engine.model.encode(reference_answer, convert_to_tensor=True)

    similarity = util.cos_sim(emb_student, emb_reference).item()
    similarity = max(0.0, float(similarity))

    word_count = len(student_answer.split())

    # -------------------------------
    # HARD OFF-TOPIC GUARD
    # -------------------------------
    if similarity < 0.55:
        final_score = round(similarity * 35, 2)

        return jsonify({
            "status": "success",
            "score": final_score,
            "feedback": "Answer is off-topic or conceptually incorrect."
        })

    # -------------------------------
    # Depth Adjustment
    # -------------------------------
    if word_count < 5:
        similarity *= 0.5
    elif word_count < 10:
        similarity *= 0.75

    # -------------------------------
    # Bloom Level Cap
    # -------------------------------
    bloom_caps = {
        "Remember": 0.7,
        "Understand": 0.85,
        "Apply": 1.0,
        "Analyze": 1.0,
        "Evaluate": 1.0,
        "Create": 1.0
    }

    similarity = min(similarity, bloom_caps.get(bloom_level, 1.0))

    final_score = round(similarity * 100, 2)

    # -------------------------------
    # Feedback
    # -------------------------------
    if final_score >= 85:
        feedback = "Excellent answer."
    elif final_score >= 65:
        feedback = "Good answer but needs refinement."
    elif final_score >= 45:
        feedback = "Partial understanding."
    else:
        feedback = "Weak or incorrect answer."

    return jsonify({
        "status": "success",
        "score": final_score,
        "feedback": feedback
    })


# @app.route("/generate-midterm-blueprint", methods=["POST"])
# def generate_midterm_blueprint():

#     data = request.json
#     course_description = data.get("description", "")
#     total_questions = data.get("total_questions", 10)

#     # Simple hybrid logic example
#     easy = int(total_questions * 0.4)
#     medium = int(total_questions * 0.4)
#     hard = total_questions - easy - medium

#     return jsonify({
#         "easy": easy,
#         "medium": medium,
#         "hard": hard
#     })

# @app.route("/generate-midterm-blueprint", methods=["POST"])
# def generate_midterm_blueprint():

#     data = request.json
#     description = data.get("description")
#     total_questions = data.get("total_questions")

#     # Use RAG to analyze topic depth
#     retrieved_chunks, confidence, mode = rag_engine.retrieve(description)

#     # Example hybrid logic
#     if confidence > 0.8:
#         easy = int(total_questions * 0.3)
#         medium = int(total_questions * 0.4)
#         hard = total_questions - easy - medium
#     else:
#         easy = int(total_questions * 0.5)
#         medium = int(total_questions * 0.3)
#         hard = total_questions - easy - medium

#     return jsonify({
#         "easy": easy,
#         "medium": medium,
#         "hard": hard,
#         "confidence": confidence
#     })

@app.route("/generate-midterm-blueprint", methods=["POST"])
def generate_midterm_blueprint():

    data = request.json
    description = data.get("description")
    total_questions = int(data.get("total_questions", 10))

    if not description:
        return jsonify({"error": "Description required"}), 400

    # 🔹 Step 1: Retrieve relevant content using RAG
    retrieved_chunks, confidence, mode = rag_engine.retrieve(description)

    if not retrieved_chunks:
        return jsonify({"error": "No relevant context found"}), 400

    retrieved_context = "\n\n".join(retrieved_chunks[:3])

    topic_complexity = len(" ".join(retrieved_chunks).split())

    # 🔹 Step 2: Intelligent Difficulty Logic
    if confidence > 0.85 and topic_complexity > 500:
        easy = int(total_questions * 0.3)
        medium = int(total_questions * 0.4)
        hard = total_questions - easy - medium
    else:
        easy = int(total_questions * 0.5)
        medium = int(total_questions * 0.3)
        hard = total_questions - easy - medium

    # 🔹 Step 3: Bloom Distribution
    bloom_distribution = {
        "Understand": int(total_questions * 0.4),
        "Apply": int(total_questions * 0.3),
        "Analyze": total_questions - int(total_questions * 0.4) - int(total_questions * 0.3)
    }

    return jsonify({
        "easy": easy,
        "medium": medium,
        "hard": hard,
        "bloom_distribution": bloom_distribution,
        "confidence": confidence
    })

@app.route("/generate-mcq", methods=["POST"])
def generate_mcq():

    data = request.json
    concept = data.get("concept")
    bloom_level = data.get("bloom_level", "Understand")
    count = data.get("count", 5)

    retrieved_chunks, confidence, mode = rag_engine.retrieve(concept)

    context = "\n\n".join(retrieved_chunks[:3])

    prompt = f"""
    Generate {count} multiple choice questions on {concept}.
    Bloom Level: {bloom_level}

    Use this context:
    {context}

    Each MCQ must have:
    - Question
    - 4 options
    - Correct answer
    """

    result = rag_engine.generate(prompt)

    return jsonify({
        "mcqs": result,
        "confidence": confidence
    })

# @app.route("/generate-dynamic-midterm", methods=["POST"])
# def generate_dynamic_midterm():

#     data = request.json
#     description = data.get("description")
#     total_questions = int(data.get("total_questions", 10))
#     total_marks = int(data.get("total_marks", 50))

#     if not description:
#         return jsonify({"error": "Description required"}), 400

#     # -----------------------------------
#     # STEP 1: Retrieve Context via RAG
#     # -----------------------------------
#     retrieved_chunks, confidence, mode = rag_engine.retrieve(description)
#     context = " ".join(retrieved_chunks[:5])

#     # -----------------------------------
#     # STEP 2: Intelligent Blueprint Logic
#     # -----------------------------------
#     if confidence > 0.85:
#         easy = int(total_questions * 0.3)
#         medium = int(total_questions * 0.4)
#         hard = total_questions - easy - medium
#     else:
#         easy = int(total_questions * 0.5)
#         medium = int(total_questions * 0.3)
#         hard = total_questions - easy - medium

#     # -----------------------------------
#     # STEP 3: Generate Sections
#     # -----------------------------------

#     # Section A — MCQs
#     mcq_prompt = f"""
#     Using the following academic context:

#     {context}

#     Generate {easy} MCQs (Bloom: Understand).
#     Each must contain:
#     - Question
#     - 4 options
#     - Correct answer
#     """

#     mcqs = rag_engine.generate(mcq_prompt)

#     # Section B — Short Answers
#     short_prompt = f"""
#     Using the context below:

#     {context}

#     Generate {medium} short answer questions (Bloom: Apply).
#     Each question should require explanation (5-7 lines).
#     """

#     short_questions = rag_engine.generate(short_prompt)

#     # Section C — Long Answers
#     long_prompt = f"""
#     Using the academic context:

#     {context}

#     Generate {hard} analytical/descriptive questions (Bloom: Analyze or Evaluate).
#     These should test deep conceptual understanding.
#     """

#     long_questions = rag_engine.generate(long_prompt)

#     # -----------------------------------
#     # FINAL STRUCTURED RESPONSE
#     # -----------------------------------
#     return jsonify({
#         "status": "success",
#         "confidence": confidence,
#         "blueprint": {
#             "easy": easy,
#             "medium": medium,
#             "hard": hard
#         },
#         "sections": {
#             "Section A - MCQ": mcqs,
#             "Section B - Short Answer": short_questions,
#             "Section C - Long Answer": long_questions
#         }
#     })
# @app.route("/generate-dynamic-midterm", methods=["POST"])
# def generate_dynamic_midterm():

#     data = request.json
#     description = data.get("description")
#     total_questions = int(data.get("total_questions", 10))
#     total_marks = int(data.get("total_marks", 50))

#     if not description:
#         return jsonify({"error": "Description required"}), 400

#     retrieved_chunks, confidence, mode = rag_engine.retrieve(description)
#     # context = " ".join(retrieved_chunks[:5])
#     # context = " ".join(retrieved_chunks[:3])[:1500]
#     context = " ".join(retrieved_chunks[:3])

#     # -------------------------------
#     # Blueprint Logic
#     # -------------------------------
#     if confidence > 0.85:
#         easy = int(total_questions * 0.3)
#         medium = int(total_questions * 0.4)
#         hard = total_questions - easy - medium
#     else:
#         easy = int(total_questions * 0.5)
#         medium = int(total_questions * 0.3)
#         hard = total_questions - easy - medium

#     # -------------------------------
#     # STRUCTURED PROMPT
#     # -------------------------------
    
#     # prompt = f"""
#     # Using the academic context below:

#     # {context}

#     # Generate a complete midterm exam in STRICT JSON format.

#     # Structure:

#     # {{
#     #     "sections": [
#     #         {{
#     #             "section_name": "Section A",
#     #             "type": "MCQ",
#     #             "questions": [
#     #                 {{
#     #                     "question": "...",
#     #                     "options": ["A", "B", "C", "D"],
#     #                     "correct_answer": "A",
#     #                     "bloom_level": "Understand",
#     #                     "difficulty": "Easy",
#     #                     "marks": 2
#     #                 }}
#     #             ]
#     #         }},
#     #         {{
#     #             "section_name": "Section B",
#     #             "type": "Short",
#     #             "questions": [
#     #                 {{
#     #                     "question": "...",
#     #                     "reference_answer": "...",
#     #                     "bloom_level": "Apply",
#     #                     "difficulty": "Medium",
#     #                     "marks": 5
#     #                 }}
#     #             ]
#     #         }},
#     #         {{
#     #             "section_name": "Section C",
#     #             "type": "Long",
#     #             "questions": [
#     #                 {{
#     #                     "question": "...",
#     #                     "reference_answer": "...",
#     #                     "bloom_level": "Analyze",
#     #                     "difficulty": "Hard",
#     #                     "marks": 10
#     #                 }}
#     #             ]
#     #         }}
#     #     ]
#     # }}


#     # Section A must contain {easy} MCQs.
#     # Section B must contain {medium} short questions.
#     # Section C must contain {hard} long questions.

#     # Output ONLY valid JSON.
#     # """
#     prompt = f"""
#     You are an academic exam generator.

# Using the context below, generate ONLY a valid JSON object.

# Context:
# {context}

# Return ONLY JSON.
# No explanation.
# No text before or after.

# JSON structure:

# {{
#   "sections": [
#     {{
#       "section_name": "Section A",
#       "type": "MCQ",
#       "questions": []
#     }},
#     {{
#       "section_name": "Section B",
#       "type": "Short",
#       "questions": []
#     }},
#     {{
#       "section_name": "Section C",
#       "type": "Long",
#       "questions": []
#     }}
#   ]
# }}

# Rules:
# - Section A → {easy} MCQs
# - Section B → {medium} short questions
# - Section C → {hard} long questions
# - Return STRICT JSON only
# """


#     # exam_json = rag_engine.generate(prompt)
#     raw_output = rag_engine.generate(prompt)


#     try:
#         exam_json = json.loads(raw_output)
#     except Exception as e:
#         print("❌ JSON Parsing Error:", str(e))
#         return jsonify({
#         "status": "error",
#         "message": "Model did not return valid JSON",
#         "raw_output": raw_output
#     }), 500


#     return jsonify({
#         "status": "success",
#         "confidence": confidence,
#         "blueprint": {
#             "easy": easy,
#             "medium": medium,
#             "hard": hard
#         },
#         "exam": exam_json
#     })




# if __name__ == "__main__":
#     # app.run(host="0.0.0.0", port=5001, debug=True)
#     app.run(host="0.0.0.0", port=5001, debug=True, use_reloader=False)


# import re

# @app.route("/generate-dynamic-midterm", methods=["POST"])
# def generate_dynamic_midterm():

#     data = request.json
#     description = data.get("description")
#     total_questions = int(data.get("total_questions", 10))

#     if not description:
#         return jsonify({"error": "Description required"}), 400

#     # ---------------------------------
#     # RETRIEVE CONTEXT
#     # ---------------------------------
#     retrieved_chunks, confidence, mode = rag_engine.retrieve(description)
#     context = " ".join(retrieved_chunks[:3])

#     # ---------------------------------
#     # BLUEPRINT LOGIC
#     # ---------------------------------
#     if confidence > 0.85:
#         easy = int(total_questions * 0.3)
#         medium = int(total_questions * 0.4)
#         hard = total_questions - easy - medium
#     else:
#         easy = int(total_questions * 0.5)
#         medium = int(total_questions * 0.3)
#         hard = total_questions - easy - medium

#     # ---------------------------------
#     # SIMPLE TEXT PROMPT (NO JSON!)
#     # ---------------------------------
#     prompt = f"""
#     Using the context below, generate a midterm exam.

#     Context:
#     {context}

#     Format EXACTLY like this:

#     SECTION A - MCQ
#     Q1: Question text
#     A) Option A
#     B) Option B
#     C) Option C
#     D) Option D
#     Answer: A

#     SECTION B - SHORT
#     Q1: Question text
#     Reference: Answer text

#     SECTION C - LONG
#     Q1: Question text
#     Reference: Answer text

#     Requirements:
#     - Section A must contain {easy} MCQs
#     - Section B must contain {medium} short questions
#     - Section C must contain {hard} long questions
#     """

#     raw_output = rag_engine.generate(prompt)
#     print("======== RAW MODEL OUTPUT ========")
#     print(raw_output)
#     print("===================================")


#     # ---------------------------------
#     # PARSE TEXT INTO STRUCTURED JSON
#     # ---------------------------------
#     sections = {
#         "Section A - MCQ": [],
#         "Section B - Short": [],
#         "Section C - Long": []
#     }

#     current_section = None
#     lines = raw_output.split("\n")

#     mcq_block = {}
#     short_block = {}
#     long_block = {}

#     for line in lines:
#         line = line.strip()

#         # Detect sections
#         if "SECTION A" in line.upper():
#             current_section = "Section A - MCQ"
#             continue
#         elif "SECTION B" in line.upper():
#             current_section = "Section B - Short"
#             continue
#         elif "SECTION C" in line.upper():
#             current_section = "Section C - Long"
#             continue

#         # MCQ Parsing
#         if current_section == "Section A - MCQ":
#             if line.startswith("Q"):
#                 mcq_block = {
#                     "question": line,
#                     "options": [],
#                     "correct_answer": ""
#                 }
#             elif re.match(r"^[A-D]\)", line):
#                 mcq_block["options"].append(line)
#             elif line.startswith("Answer"):
#                 mcq_block["correct_answer"] = line.replace("Answer:", "").strip()
#                 sections[current_section].append(mcq_block)

#         # Short / Long Parsing
#         elif current_section in ["Section B - Short", "Section C - Long"]:
#             if line.startswith("Q"):
#                 block = {
#                     "question": line,
#                     "reference_answer": ""
#                 }
#             elif line.startswith("Reference"):
#                 block["reference_answer"] = line.replace("Reference:", "").strip()
#                 sections[current_section].append(block)

#         print("FINAL STRUCTURED JSON:")
#         print(sections)

#     # ---------------------------------
#     # RETURN CLEAN STRUCTURE
#     # ---------------------------------
#     return jsonify({
#         "status": "success",
#         "confidence": confidence,
#         "blueprint": {
#             "easy": easy,
#             "medium": medium,
#             "hard": hard
#         },
#         "sections": sections
#     })

    # Using the context below, generate a midterm exam.

    # Context:
    # 

    # Format EXACTLY like this:

    # SECTION A - MCQ
    # Q1: Question text
    # A) Option A
    # B) Option B
    # C) Option C
    # D) Option D
    # Answer: A

    # SECTION B - SHORT
    # Q1: Question text
    # Reference: Answer text

    # SECTION C - LONG
    # Q1: Question text
    # Reference: Answer text

    # Requirements:
    # - Section A must contain {easy} MCQs
    # - Section B must contain {medium} short questions
    # - Section C must contain {hard} long questions


# import re

# @app.route("/generate-dynamic-midterm", methods=["POST"])
# def generate_dynamic_midterm():

#     data = request.json
#     description = data.get("description")
#     total_questions = int(data.get("total_questions", 10))

#     if not description:
#         return jsonify({"error": "Description required"}), 400

#     # ---------------------------------
#     # RETRIEVE CONTEXT
#     # ---------------------------------
#     retrieved_chunks, confidence, mode = rag_engine.retrieve(description)

#     if not retrieved_chunks:
#         return jsonify({"error": "No relevant context found"}), 400
    
#     context = " ".join(retrieved_chunks[:3])

#     # ---------------------------------
#     # BLUEPRINT LOGIC
#     # ---------------------------------
#     if confidence > 0.85:
#         easy = int(total_questions * 0.3)
#         medium = int(total_questions * 0.4)
#         hard = total_questions - easy - medium
#     else:
#         easy = int(total_questions * 0.5)
#         medium = int(total_questions * 0.3)
#         hard = total_questions - easy - medium

#     # ---------------------------------
#     # PROMPT
#     # ---------------------------------
#     prompt = f"""
#     Use ONLY the context below to generate the midterm.

#     CONTEXT:
#     {context}
#     Generate a university midterm exam using the context below.

#     Requirements:
#     - Total Questions: {total_questions}
#     - Follow Bloom distribution:
#     REMEMBER: 2
#     UNDERSTAND: 2
#     APPLY: 2
#     ANALYZE: 2
#     EVALUATE: 1
#     CREATE: 1

#     Return strictly in this JSON format:

#     {{
#     "sections": {{
#         "Section A - MCQ": [
#         {{
#             "question": "...",
#             "options": ["A) ...", "B) ...", "C) ...", "D) ..."],
#             "correct_answer": "A",
#             "bloom_level": "UNDERSTAND"
#         }}
#         ],
#         "Section B - Short": [
#         {{
#             "question": "...",
#             "reference_answer": "...",
#             "bloom_level": "APPLY"
#         }}
#         ],
#         "Section C - Long": [
#         {{
#             "question": "...",
#             "reference_answer": "...",
#             "bloom_level": "ANALYZE"
#         }}
#         ]
#     }}
#     }}
#     """

#     raw_output = rag_engine.generate(prompt)

#     print("======== RAW MODEL OUTPUT ========")
#     print(raw_output)
#     print("===================================")

#     # try:
#     #     start = raw_output.find("{")
#     #     end = raw_output.rfind("}") + 1
#     #     json_text = raw_output[start:end]

#     #     parsed = json.loads(json_text)
#     #     sections = parsed.get("sections", {})
    
#     # except Exception as e:
#     #     print("❌ JSON Parsing Failed:", str(e))
#     #     return jsonify({"error": "Invalid AI response format"}), 500

#     # ---------------------------------
#     # PARSE TEXT INTO STRUCTURED JSON
#     # ---------------------------------
#     sections = {
#         "Section A - MCQ": [],
#         "Section B - Short": [],
#         "Section C - Long": []
#     }

#     current_section = None
#     lines = raw_output.split("\n")
#     current_block = None

#     for line in lines:
#         line = line.strip()
#         if not line:
#             continue

#         # Detect sections
#         if "SECTION A" in line.upper():
#             current_section = "Section A - MCQ"
#             continue
#         elif "SECTION B" in line.upper():
#             current_section = "Section B - Short"
#             continue
#         elif "SECTION C" in line.upper():
#             current_section = "Section C - Long"
#             continue

#         # ---------------- MCQ ----------------
#         if current_section == "Section A - MCQ":

#             if line.startswith("Q"):
#                 current_block = {
#                     "question": line,
#                     "options": [],
#                     "correct_answer": ""
#                 }

#             elif re.match(r"^[A-D]\)", line):
#                 if current_block:
#                     current_block["options"].append(line)

#             elif line.startswith("Answer"):
#                 if current_block:
#                     current_block["correct_answer"] = line.replace("Answer:", "").strip()
#                     sections[current_section].append(current_block)
#                     current_block = None

#         # ---------------- SHORT / LONG ----------------
#         elif current_section in ["Section B - Short", "Section C - Long"]:

#             if line.startswith("Q"):
#                 current_block = {
#                     "question": line,
#                     "reference_answer": ""
#                 }

#             elif line.startswith("Reference"):
#                 if current_block:
#                     current_block["reference_answer"] = line.replace("Reference:", "").strip()
#                     sections[current_section].append(current_block)
#                     current_block = None

#     # print("FINAL STRUCTURED JSON:")
#     # print(sections)

#     # ---------------------------------
#     # RETURN CLEAN STRUCTURE
#     # ---------------------------------
#     # return jsonify({
#     #     "status": "success",
#     #     "confidence": confidence,
#     #     "blueprint": {
#     #         "easy": easy,
#     #         "medium": medium,
#     #         "hard": hard
#     #     },
#     #     "sections": sections
#     # })
# import json
# import re
# from flask import request, jsonify
# import random

# @app.route("/generate-dynamic-midterm", methods=["POST"])
# def generate_dynamic_midterm():

#     data = request.json
#     description = data.get("description")
#     total_questions = int(data.get("total_questions", 10))
#     total_marks = int(data.get("total_marks", 30))

#     if not description:
#         return jsonify({"error": "Description required"}), 400

#     # ---------------------------------
#     # RETRIEVE CONTEXT
#     # ---------------------------------
#     retrieved_chunks, confidence, mode = rag_engine.retrieve(description)

#     if not retrieved_chunks:
#         return jsonify({"error": "No relevant context found"}), 400

#     context = "\n\n".join(retrieved_chunks[:3])

#     # ---------------------------------
#     # SECTION BLUEPRINT
#     # ---------------------------------
#     if confidence > 0.85:
#         easy_count = int(total_questions * 0.3)
#         medium_count = int(total_questions * 0.4)
#         hard_count = total_questions - easy_count - medium_count
#     else:
#         easy_count = int(total_questions * 0.5)
#         medium_count = int(total_questions * 0.3)
#         hard_count = total_questions - easy_count - medium_count

#     # ---------------------------------
#     # PROMPT (NO BLOOM ENFORCEMENT)
#     # ---------------------------------
#     prompt = f"""
# Use ONLY the context below to generate the midterm.

# CONTEXT:
# {context}

# Requirements:
# - Total Questions: {total_questions}
# - Total Marks: {total_marks}
# - Section A must contain {easy_count} MCQs
# - Section B must contain {medium_count} short questions
# - Section C must contain {hard_count} long questions

# Each question MUST include:
# - "marks"

# Return strictly valid JSON:

# {{
#   "sections": {{
#     "Section A - MCQ": [
#       {{
#         "question": "...",
#         "marks": 2,
#         "options": [
#           "A) ...",
#           "B) ...",
#           "C) ...",
#           "D) ..."
#         ]
#       }}
#     ],
#     "Section B - Short": [
#       {{
#         "question": "...",
#         "marks": 5,
#         "reference_answer": "..."
#       }}
#     ],
#     "Section C - Long": [
#       {{
#         "question": "...",
#         "marks": 10,
#         "reference_answer": "..."
#       }}
#     ]
#   }}
# }}

# Section A MUST include exactly 4 options per question.
# Do NOT include bloom_level.
# Do NOT include correct_answer.
# """

#     # ---------------------------------
#     # VALIDATION FUNCTIONS
#     # ---------------------------------

#     def validate_section_sizes(sections):

#         if len(sections.get("Section A - MCQ", [])) != easy_count:
#             return False

#         if len(sections.get("Section B - Short", [])) != medium_count:
#             return False

#         if len(sections.get("Section C - Long", [])) != hard_count:
#             return False

#         return True


#     def validate_mcq_options(sections):

#         mcqs = sections.get("Section A - MCQ", [])

#         for q in mcqs:
#             if "options" not in q:
#                 return False
#             if not isinstance(q["options"], list) or len(q["options"]) != 4:
#                 return False

#         return True


#     # ---------------------------------
#     # GENERATION LOOP
#     # ---------------------------------

#     MAX_ATTEMPTS = 3
#     attempt = 0
#     sections = None
#     is_valid = False

#     while attempt < MAX_ATTEMPTS:

#         attempt += 1
#         print(f"🔁 Attempt {attempt}")

#         raw_output = rag_engine.generate(prompt)

#         try:
#             cleaned = re.sub(r'[\x00-\x1f\x7f]', '', raw_output)
#             parsed = json.loads(cleaned)
#             sections = parsed.get("sections", {})

#             if validate_section_sizes(sections) and validate_mcq_options(sections):
#                 is_valid = True
#                 break

#         except Exception as e:
#             print("JSON Parsing Failed:", str(e))
#             continue

#     if not sections:
#         return jsonify({"error": "AI failed to generate valid JSON"}), 500

#     if not is_valid:
#         return jsonify({"error": "Validation failed after retries"}), 400

#     # ---------------------------------
#     # SYSTEM-CONTROLLED BLOOM ASSIGNMENT
#     # ---------------------------------

#     def assign_bloom_by_marks(marks):
#         marks = int(marks)

#         if marks <= 2:
#             return random.choice(["REMEMBER", "UNDERSTAND"])
#         elif marks <= 5:
#             return random.choice(["APPLY", "ANALYZE"])
#         else:
#             return random.choice(["EVALUATE", "CREATE"])

#     for section in sections.values():
#         for q in section:
#             q["bloom_level"] = assign_bloom_by_marks(q.get("marks", 0))

#     # ---------------------------------
#     # FINAL RESPONSE
#     # ---------------------------------

#     return jsonify({
#         "status": "success",
#         "confidence": confidence,
#         "retrieval_mode": mode,
#         "blueprint": {
#             "easy": easy_count,
#             "medium": medium_count,
#             "hard": hard_count
#         },
#         "sections": sections
#     })


# if __name__ == "__main__":
#     app.run(host="0.0.0.0", port=5001, debug=True, use_reloader=False)
import json
import re
from flask import request, jsonify
import random

@app.route("/generate-dynamic-midterm", methods=["POST"])
def generate_dynamic_midterm():

    data = request.json
    description = data.get("description")
    total_marks = int(data.get("total_marks", 20))

    if not description:
        return jsonify({"error": "Description required"}), 400

    # ---------------------------------
    # RETRIEVE CONTEXT
    # ---------------------------------

    retrieved_chunks, confidence, mode = rag_engine.retrieve(description)

    if not retrieved_chunks:
        return jsonify({"error": "No relevant context found"}), 400

    context = "\n\n".join(retrieved_chunks[:3])

    # ---------------------------------
    # MARK CONTROLLED BLUEPRINT (FIXED)
    # ---------------------------------

    MCQ_MARK = 1
    SHORT_MARK = 4

    def build_blueprint(total_marks):

        easy_marks = round(total_marks * 0.2)
        medium_marks = round(total_marks * 0.4)
        hard_marks = total_marks - easy_marks - medium_marks

        mcq_count = easy_marks // MCQ_MARK
        short_count = medium_marks // SHORT_MARK

        # 🔥 HARD MARK FLEXIBLE
        long_count = 1
        LONG_MARK = hard_marks

        return mcq_count, short_count, long_count, LONG_MARK, easy_marks, medium_marks, hard_marks

    mcq_count, short_count, long_count, LONG_MARK, easy_marks, medium_marks, hard_marks = build_blueprint(total_marks)

    total_questions = mcq_count + short_count + long_count

    print("📘 Blueprint:")
    print("MCQs:", mcq_count)
    print("Short:", short_count)
    print("Long:", long_count)
    print("Long Mark:", LONG_MARK)

    # ---------------------------------
    # PROMPT
    # ---------------------------------

    prompt = f"""
Use ONLY the context below to generate the midterm.

CONTEXT:
{context}

Requirements:
- Total Marks: {total_marks}

Section Distribution:
- {mcq_count} MCQs ({MCQ_MARK} mark each)
- {short_count} Short Questions ({SHORT_MARK} marks each)
- {long_count} Long Questions ({LONG_MARK} marks each)

IMPORTANT:
- Each question must be UNIQUE.
- Do NOT repeat question stems.
- MCQs must test conceptual understanding.
- First identify 6 distinct key concepts from the context.
List them mentally.
Then generate exactly one MCQ per concept.
Do NOT repeat concepts.
Do NOT reuse question structure.
Each MCQ must test a different principle.
Avoid using the same phrasing template.
- Include 3 strong distractors.

Return strictly valid JSON:

{{
  "sections": {{
    "Section A - MCQ": [
      {{
        "question": "...",
        "marks": {MCQ_MARK},
        "options": [
          "A) ...",
          "B) ...",
          "C) ...",
          "D) ..."
        ],
        "correct_answer": "B",
        "bloom_level": "UNDERSTAND"

      }}
    ],
    "Section B - Short": [
      {{
        "question": "...",
        "marks": {SHORT_MARK},
        "reference_answer": "...",
        "bloom_level": "APPLY"
      }}
    ],
    "Section C - Long": [
      {{
        "question": "...",
        "marks": {LONG_MARK},
        "reference_answer": "...",
        "bloom_level": "CREATE"
      }}
    ]
  }}
}}

Section A MUST include exactly 4 options per question.

"""

    # ---------------------------------
    # VALIDATION HELPERS
    # ---------------------------------

    def has_duplicates(sections):
        seen = set()
        for section in sections.values():
            for q in section:
                text = q.get("question", "").strip().lower()
                if text in seen:
                    print("❌ Duplicate detected")
                    return True
                seen.add(text)
        return False


    def validate_mcq_options(sections):
        for q in sections.get("Section A - MCQ", []):
            if "options" not in q or not isinstance(q["options"], list) or len(q["options"]) != 4:
                print("❌ Invalid MCQ options")
                return False
        return True


    def validate_bloom_distribution(sections):

        bloom_tiers = {"EASY": 0, "MEDIUM": 0, "HARD": 0}

        def get_tier(level):
            level = level.upper()
            if level in ["REMEMBER", "UNDERSTAND"]:
                return "EASY"
            if level in ["APPLY", "ANALYZE"]:
                return "MEDIUM"
            if level in ["EVALUATE", "CREATE"]:
                return "HARD"
            return None

        for section in sections.values():
            for q in section:
                tier = get_tier(q.get("bloom_level", ""))
                if tier:
                    bloom_tiers[tier] += int(q.get("marks", 0))

        print("📊 Bloom Distribution:", bloom_tiers)

        return (
            bloom_tiers["EASY"] == easy_marks and
            bloom_tiers["MEDIUM"] == medium_marks and
            bloom_tiers["HARD"] == hard_marks
        )


    def validate_sections(sections):

        if has_duplicates(sections):
            return False

        if not validate_mcq_options(sections):
            return False

        if not validate_bloom_distribution(sections):
            print("❌ Bloom mismatch")
            return False

        print("✅ Validation Passed")
        return True


    # ---------------------------------
    # GENERATION LOOP
    # ---------------------------------

    MAX_ATTEMPTS = 3
    attempt = 0
    sections = None

    while attempt < MAX_ATTEMPTS:

        attempt += 1
        print(f"🔁 Attempt {attempt}")

        raw_output = rag_engine.generate(prompt)

        try:
            cleaned = re.sub(r'[\x00-\x1f\x7f]', '', raw_output)
            parsed = json.loads(cleaned)
            sections = parsed.get("sections", {})
        except Exception:
            continue

        if not sections:
            continue

        if validate_sections(sections):
            break

    if not sections or not validate_sections(sections):
        return jsonify({"error": "AI failed to generate valid paper"}), 500

    # ---------------------------------
    # FINAL RESPONSE
    # ---------------------------------

    return jsonify({
        "status": "success",
        "confidence": confidence,
        "retrieval_mode": mode,
        "blueprint": {
            "total_marks": total_marks,
            "mcq_count": mcq_count,
            "short_count": short_count,
            "long_count": long_count,
            "long_mark": LONG_MARK,
            "total_questions": total_questions
        },
        "sections": sections
    })


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001, debug=True, use_reloader=False)