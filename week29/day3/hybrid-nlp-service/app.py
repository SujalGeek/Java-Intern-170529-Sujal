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
import json
import re
import random
from flask import request, jsonify

@app.route("/generate-quiz", methods=["POST"])
def generate_quiz():

    data = request.json
    description = data.get("description")

    if not description:
        return jsonify({"error": "Description required"}), 400

    # =====================================
    # 1️ RETRIEVE CONTEXT FROM INDEX
    # =====================================

    retrieved_chunks, confidence, mode = rag_engine.retrieve(description)

    if not retrieved_chunks:
        return jsonify({"error": "No relevant context found"}), 400

    context = "\n\n".join(retrieved_chunks[:3])

    # =====================================
    # 2️ QUIZ BLUEPRINT
    # =====================================

    TOTAL_MARKS = 10
    MCQ_MARK = 1
    MCQ_COUNT = TOTAL_MARKS // MCQ_MARK

    print("📘 Quiz Blueprint:", MCQ_COUNT, "MCQs")

    # =====================================
    # 3 PROMPT
    # =====================================

    variation_seed = random.randint(1, 10000)

    prompt = f"""
Use ONLY the context below to generate a quiz.

Variation Seed: {variation_seed}

CONTEXT:
{context}

QUIZ REQUIREMENTS:
- EXACTLY {MCQ_COUNT} MCQs
- Each question must be 1 mark
- Total Marks = {TOTAL_MARKS}

COGNITIVE REQUIREMENTS:
- Questions MUST require conceptual reasoning.
- Avoid direct definition-based questions.
- Do NOT start questions with:
  * What is
  * Define
  * What is the purpose of
  * What is meant by
- Each question must test a DIFFERENT conceptual principle from the context.
- Do NOT repeat the same example structure.
- Do NOT generate two questions that differ only by replacing class names.
- Each question MUST describe a short practical scenario.
- The student must analyze the situation before choosing the answer.
- Do NOT ask "which of the following is" style conceptual recall questions.
- Every question must involve:
    • a situation
    • a design decision
    • or a behavioral outcome
- Avoid pure theory recall.
Every question must begin with a short real-world scenario (1–2 sentences) before asking the question.
STRUCTURE RULES:
- Exactly 4 options per question.
- Exactly 1 correct answer.
- Bloom level must be "UNDERSTAND".

Return STRICT JSON ONLY:

{{
  "quiz": {{
    "total_marks": {TOTAL_MARKS},
    "questions": [
      {{
        "question": "...",
        "marks": 1,
        "options": [
          "A) ...",
          "B) ...",
          "C) ...",
          "D) ..."
        ],
        "correct_answer": "B",
        "bloom_level": "UNDERSTAND"
      }}
    ]
  }}
}}
"""

    # =====================================
    # 4 SAFE JSON EXTRACTOR
    # =====================================

    def extract_json(text):
        match = re.search(r'\{.*\}', text, re.DOTALL)
        return match.group(0) if match else None

    # =====================================
    # 5️ VALIDATION FUNCTIONS
    # =====================================

    def validate_structure(quiz):

        if quiz.get("total_marks") != TOTAL_MARKS:
            return False

        questions = quiz.get("questions", [])

        if len(questions) != MCQ_COUNT:
            return False

        total = 0

        for q in questions:

            if q.get("marks") != 1:
                return False

            if not isinstance(q.get("options"), list):
                return False

            if len(q["options"]) != 4:
                return False

            if q.get("bloom_level") not in ["REMEMBER", "UNDERSTAND"]:
                return False

            if q.get("correct_answer") not in ["A", "B", "C", "D"]:
                return False

            total += q.get("marks")

        return total == TOTAL_MARKS


    def validate_no_duplicates(quiz):
        seen = set()
        for q in quiz["questions"]:
            text = q.get("question", "").strip().lower()
            if text in seen:
                return False
            seen.add(text)
        return True

    # def validate_bloom_depth(quiz):

    #     banned_starters = [
    #         "what is",
    #         "define",
    #         "what is the purpose",
    #         "what is meant",
    #         "what does",
    #         "which term means"
    #     ]

    #     for q in quiz["questions"]:
    #         question_text = q["question"].strip().lower()

    #         # Reject direct definition style questions
    #         for banned in banned_starters:
    #             if question_text.startswith(banned):
    #                 return False

    #         # Require minimum complexity (at least 12 words)
    #         if len(question_text.split()) < 10:
    #             return False

    #     return True
    def validate_bloom_depth(quiz):

        banned_starters = [
            "what is",
            "define",
            "what is the purpose",
            "what is meant",
            "which term means"
        ]

        valid_count = 0

        for q in quiz["questions"]:
            question_text = q["question"].strip().lower()

            # Reject direct definitions
            if any(question_text.startswith(b) for b in banned_starters):
                continue

            # Accept if sufficiently descriptive
            if len(question_text.split()) >= 8:
                valid_count += 1

        # At least 70% must satisfy reasoning
        return valid_count >= int(len(quiz["questions"]) * 0.7)
    
    # def validate_concept_diversity(quiz):

    #     questions = [q["question"].strip().lower() for q in quiz["questions"]]

    #     for i in range(len(questions)):
    #         for j in range(i + 1, len(questions)):

    #             # If questions are almost identical length and share first 6 words → reject
    #             first_words_i = " ".join(questions[i].split()[:6])
    #             first_words_j = " ".join(questions[j].split()[:6])

    #             if first_words_i == first_words_j:
    #                 return False

    #             # If entire question similarity very high (simple ratio)
    #             if questions[i] in questions[j] or questions[j] in questions[i]:
    #                 return False

        # return True

    # =====================================
    # 6  GENERATION LOOP (RETRY LOGIC)
    # =====================================

    MAX_ATTEMPTS = 5

    for attempt in range(MAX_ATTEMPTS):

        print(f"Quiz Attempt {attempt+1}")

        raw_output = rag_engine.generate(prompt)

        cleaned = re.sub(r'[\x00-\x1f\x7f]', '', raw_output)

        json_block = extract_json(cleaned)

        if not json_block:
            print("No JSON detected")
            continue

        try:
            parsed = json.loads(json_block)
        except:
            print("JSON parsing failed")
            continue

        quiz = parsed.get("quiz")

        if not quiz:
            print("Missing quiz key")
            continue

        if not validate_structure(quiz):
            print("Structure validation failed")
            continue

        if not validate_no_duplicates(quiz):
            print("Duplicate questions detected")
            continue

        if not validate_bloom_depth(quiz):
            print("Bloom depth validation failed")
            continue

        # if not validate_concept_diversity(quiz):
        #     print("Concept diversity failed")
        #     continue
        
        print("VALID QUIZ GENERATED")

        return jsonify({
            "status": "success",
            "confidence": confidence,
            "retrieval_mode": mode,
            "blueprint": {
                "total_marks": TOTAL_MARKS,
                "mcq_count": MCQ_COUNT
            },
            "quiz": quiz
        })

    print("⚠ Returning last generated quiz despite validation softness.")

    return jsonify({
        "status": "soft_success",
        "confidence": confidence,
        "retrieval_mode": mode,
        "blueprint": {
            "total_marks": TOTAL_MARKS,
            "mcq_count": MCQ_COUNT
        },
        "quiz": quiz  # last parsed quiz
    })


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

    print("Student Answer:", student_answer)
    print("Reference Answer:", reference_answer)

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

@app.route("/submit-quiz", methods=["POST"])
def submit_quiz():

    data = request.json

    if not data:
        return jsonify({"error": "Invalid request body"}), 400

    quiz = data.get("quiz")
    answers = data.get("answers")
    student_id = data.get("student_id")

    if not quiz or not answers or not student_id:
        return jsonify({"error": "quiz, answers, and student_id are required"}), 400

    questions = quiz.get("questions")

    if not questions or not isinstance(questions, list):
        return jsonify({"error": "Invalid quiz structure"}), 400

    if len(answers) != len(questions):
        return jsonify({"error": "Answers count mismatch"}), 400

    total_score = 0
    total_marks = quiz.get("total_marks", 0)

    def calculate_grade(percentage):

        if percentage >= 90:
            return "A+"
        elif percentage >= 80:
            return "A"
        elif percentage >= 70:
            return "B"
        elif percentage >= 60:
            return "C"
        elif percentage >= 50:
            return "D"
        else:
            return "F"
    
    detailed_results = []

    for idx, question in enumerate(questions):

        correct_answer = question.get("correct_answer")
        student_answer = answers[idx]

        is_correct = student_answer == correct_answer
        marks = question.get("marks", 1)

        score_awarded = marks if is_correct else 0
        total_score += score_awarded

        detailed_results.append({
            "question_index": idx + 1,
            "student_answer": student_answer,
            "correct_answer": correct_answer,
            "is_correct": is_correct,
            "marks_awarded": score_awarded
        })

    if total_marks == 0:
        percentage = 0
    else:
        percentage = round((total_score / total_marks) * 100, 2)

    grade = calculate_grade(percentage)

    return jsonify({
        "status": "evaluated",
        "student_id": student_id,
        "total_score": total_score,
        "total_marks": total_marks,
        "percentage": percentage,
        "grade": grade,
        "results": detailed_results
    })


# @app.route("/generate-assignment", methods=["POST"])
# def generate_assignment():

#     data = request.json
#     description = data.get("description")
#     total_marks = data.get("total_marks", 20)

#     if not description:
#         return jsonify({"error": "Description required"}), 400

#     retrieved_chunks, confidence, mode = rag_engine.retrieve(description)

#     if not retrieved_chunks:
#         return jsonify({"error": "No relevant context found"}), 400

#     context = "\n\n".join(retrieved_chunks[:4])

# #     prompt = f"""
# # Use ONLY the context below to generate a descriptive assignment.

# # CONTEXT:
# # {context}

# # Generate:
# # - Total Marks = {total_marks}
# # - 5 descriptive questions
# # - Mix of 5, 5, 5, 3, 2 marks distribution
# # - Questions must require explanation and reasoning
# # - No MCQs

# # Return STRICT JSON:

# # {{
# #   "assignment": {{
# #     "total_marks": {total_marks},
# #     "questions": [
# #       {{
# #         "question": "...",
# #         "marks": 5,
# #         "bloom_level": "ANALYZE"
# #       }}
# #     ]
# #   }}
# # }}
# # """

#     prompt = f"""
# Use ONLY the context below.

# CONTEXT:
# {context}

# Generate a 20-mark assignment with:

# 1 question – REMEMBER – 2 marks  
# 1 question – UNDERSTAND – 3 marks  
# 1 question – APPLY – 5 marks  
# 1 question – ANALYZE – 5 marks  
# 1 question – EVALUATE – 5 marks  

# Rules:
# - Questions must be descriptive.
# - No MCQs.
# - Each question must clearly reflect its Bloom level.
# - Include bloom_level field in JSON.
# - Marks must match distribution exactly.

# Return STRICT JSON:

# {{
#   "assignment": {{
#     "total_marks": 20,
#     "questions": [
#       {{
#         "question": "...",
#         "marks": 5,
#         "bloom_level": "ANALYZE"
#       }}
#     ]
#   }}
# }}
# """

#     raw_output = rag_engine.generate(prompt)
#     parsed = json.loads(re.search(r'\{.*\}', raw_output, re.DOTALL).group(0))

#     return jsonify(parsed)

@app.route("/generate-assignment", methods=["POST"])
def generate_assignment():

    data = request.json
    description = data.get("description")
    total_marks = data.get("total_marks", 20)
    difficulty = data.get("difficulty", "medium")
    teacher_distribution = data.get("bloom_distribution")

    if not description:
        return jsonify({"error": "Description required"}), 400

    retrieved_chunks, confidence, mode = rag_engine.retrieve(description)

    if not retrieved_chunks:
        return jsonify({"error": "No relevant context found"}), 400

    context = "\n\n".join(retrieved_chunks[:4])

    # ================================
    # 1️⃣ Determine Bloom Distribution
    # ================================

    if teacher_distribution:
        bloom_distribution = teacher_distribution
    else:
        if difficulty == "easy":
            bloom_distribution = {
                "REMEMBER": 2,
                "UNDERSTAND": 2,
                "APPLY": 1
            }
        elif difficulty == "medium":
            bloom_distribution = {
                "UNDERSTAND": 1,
                "APPLY": 1,
                "ANALYZE": 1,
                "EVALUATE": 1
            }
        else:  # hard
            bloom_distribution = {
                "APPLY": 1,
                "ANALYZE": 2,
                "EVALUATE": 2
            }

    # ================================
    # 2️⃣ Auto Marks Distribution
    # ================================

    num_questions = sum(bloom_distribution.values())
    marks_per_question = total_marks // num_questions

    # ================================
    # 3️⃣ Build Prompt
    # ================================

    bloom_instruction = "\n".join(
        [f"{count} question(s) at {level} level"
         for level, count in bloom_distribution.items()]
    )

    prompt = f"""
Use ONLY the context below.

CONTEXT:
{context}

Generate a descriptive assignment.

Total Marks: {total_marks}

Bloom Distribution:
{bloom_instruction}

Each question carries {marks_per_question} marks.

Rules:
- No MCQs.
- Descriptive only.
- Strictly follow Bloom distribution.
- Include bloom_level in JSON.
- Return STRICT JSON only.

Return format:

{{
  "assignment": {{
    "total_marks": {total_marks},
    "questions": [
      {{
        "question": "...",
        "marks": {marks_per_question},
        "bloom_level": "ANALYZE"
      }}
    ]
  }}
}}
"""

    raw_output = rag_engine.generate(prompt)

    try:
        parsed = json.loads(re.search(r'\{.*\}', raw_output, re.DOTALL).group(0))
    except:
        return jsonify({"error": "Invalid JSON from model"}), 500

    assignment = parsed.get("assignment")

    if not assignment:
        return jsonify({"error": "Invalid structure"}), 500

    # ================================
    # 4️⃣ Validate Bloom Compliance
    # ================================

    def validate_distribution(questions, expected_distribution):
        found = {}
        for q in questions:
            level = q["bloom_level"].upper()
            found[level] = found.get(level, 0) + 1
        return found == expected_distribution

    if not validate_distribution(assignment["questions"], bloom_distribution):
        return jsonify({"error": "Bloom distribution mismatch"}), 500

    return jsonify({
        "status": "success",
        "confidence": confidence,
        "retrieval_mode": mode,
        "assignment": assignment
    })

# @app.route("/evaluate-assignment", methods=["POST"])
# def evaluate_assignment():

#     data = request.json
#     questions = data.get("questions")
#     answers = data.get("answers")

#     if not questions or not answers:
#         return jsonify({"error": "Invalid request"}), 400

#     total_score = 0
#     results = []

#     for q, student_answer in zip(questions, answers):

#         prompt = f"""
# Evaluate the student's answer strictly based on context.

# Question:
# {q['question']}

# Student Answer:
# {student_answer}

# Marks:
# {q['marks']}

# Return JSON:
# {{
#   "score": X,
#   "feedback": "..."
# }}
# """

#         raw = rag_engine.generate(prompt)
#         parsed = json.loads(re.search(r'\{.*\}', raw, re.DOTALL).group(0))

#         total_score += parsed["score"]

#         results.append({
#             "question": q["question"],
#             "score": parsed["score"],
#             "feedback": parsed["feedback"]
#         })

#     percentage = round((total_score / sum(q["marks"] for q in questions)) * 100, 2)

#     return jsonify({
#         "total_score": total_score,
#         "percentage": percentage,
#         "results": results
#     })
@app.route("/evaluate-assignment", methods=["POST"])
def evaluate_assignment():

    data = request.json
    questions = data.get("questions")
    answers = data.get("answers")

    if not questions or not answers:
        return jsonify({"error": "Invalid request"}), 400

    total_score = 0
    results = []

    for q, student_answer in zip(questions, answers):

        prompt = f"""
Evaluate strictly.

Question:
{q['question']}

Student Answer:
{student_answer}

Max Marks:
{q['marks']}

Return JSON:
{{
  "score": X,
  "feedback": "..."
}}
"""

        raw = rag_engine.generate(prompt)

        try:
            parsed = json.loads(re.search(r'\{.*\}', raw, re.DOTALL).group(0))
        except:
            return jsonify({"error": "Invalid evaluation JSON"}), 500

        score = min(parsed["score"], q["marks"])  # 🔥 cap score

        total_score += score

        results.append({
            "question": q["question"],
            "score": score,
            "feedback": parsed["feedback"]
        })

    total_marks = sum(q["marks"] for q in questions)
    percentage = round((total_score / total_marks) * 100, 2)

    return jsonify({
        "total_score": total_score,
        "percentage": percentage,
        "results": results
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
# import json
# import re
# from flask import request, jsonify
# import random

# @app.route("/generate-dynamic-midterm", methods=["POST"])
# def generate_dynamic_midterm():

#     data = request.json
#     description = data.get("description")
#     total_marks = int(data.get("total_marks", 20))

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
#     # MARK CONTROLLED BLUEPRINT (FIXED)
#     # ---------------------------------

#     MCQ_MARK = 1
#     SHORT_MARK = 4

#     def build_blueprint(total_marks):

#         easy_marks = round(total_marks * 0.2)
#         medium_marks = round(total_marks * 0.4)
#         hard_marks = total_marks - easy_marks - medium_marks

#         mcq_count = easy_marks // MCQ_MARK
#         short_count = medium_marks // SHORT_MARK

#         # 🔥 HARD MARK FLEXIBLE
#         long_count = 1
#         LONG_MARK = hard_marks

#         return mcq_count, short_count, long_count, LONG_MARK, easy_marks, medium_marks, hard_marks

#     mcq_count, short_count, long_count, LONG_MARK, easy_marks, medium_marks, hard_marks = build_blueprint(total_marks)

#     total_questions = mcq_count + short_count + long_count

#     print("📘 Blueprint:")
#     print("MCQs:", mcq_count)
#     print("Short:", short_count)
#     print("Long:", long_count)
#     print("Long Mark:", LONG_MARK)

#     # ---------------------------------
#     # PROMPT
#     # ---------------------------------

#     prompt = f"""
# Use ONLY the context below to generate the midterm.

# CONTEXT:
# {context}

# Requirements:
# - Total Marks: {total_marks}

# Section Distribution:
# - {mcq_count} MCQs ({MCQ_MARK} mark each)
# - {short_count} Short Questions ({SHORT_MARK} marks each)
# - {long_count} Long Questions ({LONG_MARK} marks each)

# IMPORTANT:
# - Each question must be UNIQUE.
# - Do NOT repeat question stems.
# - MCQs must test conceptual understanding.
# - First identify 6 distinct key concepts from the context.
# List them mentally.
# Then generate exactly one MCQ per concept.
# Do NOT repeat concepts.
# Do NOT reuse question structure.
# Each MCQ must test a different principle.
# Avoid using the same phrasing template.
# - Include 3 strong distractors.

# Return strictly valid JSON:

# {{
#   "sections": {{
#     "Section A - MCQ": [
#       {{
#         "question": "...",
#         "marks": {MCQ_MARK},
#         "options": [
#           "A) ...",
#           "B) ...",
#           "C) ...",
#           "D) ..."
#         ],
#         "correct_answer": "B",
#         "bloom_level": "UNDERSTAND"

#       }}
#     ],
#     "Section B - Short": [
#       {{
#         "question": "...",
#         "marks": {SHORT_MARK},
#         "reference_answer": "...",
#         "bloom_level": "APPLY"
#       }}
#     ],
#     "Section C - Long": [
#       {{
#         "question": "...",
#         "marks": {LONG_MARK},
#         "reference_answer": "...",
#         "bloom_level": "CREATE"
#       }}
#     ]
#   }}
# }}

# Section A MUST include exactly 4 options per question.

# """

#     # ---------------------------------
#     # VALIDATION HELPERS
#     # ---------------------------------

#     def has_duplicates(sections):
#         seen = set()
#         for section in sections.values():
#             for q in section:
#                 text = q.get("question", "").strip().lower()
#                 if text in seen:
#                     print("❌ Duplicate detected")
#                     return True
#                 seen.add(text)
#         return False


#     def validate_mcq_options(sections):
#         for q in sections.get("Section A - MCQ", []):
#             if "options" not in q or not isinstance(q["options"], list) or len(q["options"]) != 4:
#                 print("❌ Invalid MCQ options")
#                 return False
#         return True


#     def validate_bloom_distribution(sections):

#         bloom_tiers = {"EASY": 0, "MEDIUM": 0, "HARD": 0}

#         def get_tier(level):
#             level = level.upper()
#             if level in ["REMEMBER", "UNDERSTAND"]:
#                 return "EASY"
#             if level in ["APPLY", "ANALYZE"]:
#                 return "MEDIUM"
#             if level in ["EVALUATE", "CREATE"]:
#                 return "HARD"
#             return None

#         for section in sections.values():
#             for q in section:
#                 tier = get_tier(q.get("bloom_level", ""))
#                 if tier:
#                     bloom_tiers[tier] += int(q.get("marks", 0))

#         print("📊 Bloom Distribution:", bloom_tiers)

#         return (
#             bloom_tiers["EASY"] == easy_marks and
#             bloom_tiers["MEDIUM"] == medium_marks and
#             bloom_tiers["HARD"] == hard_marks
#         )


#     def validate_sections(sections):

#         if has_duplicates(sections):
#             return False

#         if not validate_mcq_options(sections):
#             return False

#         if not validate_bloom_distribution(sections):
#             print("❌ Bloom mismatch")
#             return False

#         print("✅ Validation Passed")
#         return True


#     # ---------------------------------
#     # GENERATION LOOP
#     # ---------------------------------

#     MAX_ATTEMPTS = 3
#     attempt = 0
#     sections = None

#     while attempt < MAX_ATTEMPTS:

#         attempt += 1
#         print(f"🔁 Attempt {attempt}")

#         raw_output = rag_engine.generate(prompt)

#         try:
#             cleaned = re.sub(r'[\x00-\x1f\x7f]', '', raw_output)
#             parsed = json.loads(cleaned)
#             sections = parsed.get("sections", {})
#         except Exception:
#             continue

#         if not sections:
#             continue

#         if validate_sections(sections):
#             break

#     if not sections or not validate_sections(sections):
#         return jsonify({"error": "AI failed to generate valid paper"}), 500

#     # ---------------------------------
#     # FINAL RESPONSE
#     # ---------------------------------

#     return jsonify({
#         "status": "success",
#         "confidence": confidence,
#         "retrieval_mode": mode,
#         "blueprint": {
#             "total_marks": total_marks,
#             "mcq_count": mcq_count,
#             "short_count": short_count,
#             "long_count": long_count,
#             "long_mark": LONG_MARK,
#             "total_questions": total_questions
#         },
#         "sections": sections
#     })
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
    # BLUEPRINT
    # ---------------------------------

    MCQ_MARK = 1
    SHORT_MARK = 4

    easy_marks = round(total_marks * 0.2)
    medium_marks = round(total_marks * 0.4)
    hard_marks = total_marks - easy_marks - medium_marks

    mcq_count = easy_marks // MCQ_MARK
    short_count = medium_marks // SHORT_MARK
    long_count = 1
    LONG_MARK = hard_marks

    total_questions = mcq_count + short_count + long_count

    print("📘 Blueprint:", mcq_count, short_count, long_count, LONG_MARK)

    # ---------------------------------
    # PROMPT
    # ---------------------------------

    variation_seed = random.randint(1, 10000)

    prompt = f"""
You are an academic question paper generator.

Use ONLY the context below to generate a midterm paper.

CONTEXT:
{context}

========================
PAPER REQUIREMENTS
========================

Total Marks: {total_marks}

Section Distribution:
- Section A: {mcq_count} MCQs ({MCQ_MARK} mark each)
- Section B: {short_count} Short Questions ({SHORT_MARK} marks each)
- Section C: {long_count} Long Question ({LONG_MARK} marks)

IMPORTANT RULES:

1. STRICTLY follow the section names:
   - "Section A - MCQ"
   - "Section B - Short"
   - "Section C - Long"

2. Do NOT rename sections.
3. Do NOT create extra keys.
4. Do NOT wrap output inside "midterm".
5. Do NOT include total_marks in output.
6. Do NOT include explanation or commentary.
7. Return ONLY valid JSON.
8. All questions must be UNIQUE.
9. MCQs must include EXACTLY 4 options.
10. Marks must match exactly:
    - MCQ = {MCQ_MARK}
    - Short = {SHORT_MARK}
    - Long = {LONG_MARK}

========================
OUTPUT FORMAT (STRICT)
========================

Return ONLY this JSON structure:

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
        "correct_answer": "A",
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

NO markdown.
NO backticks.
NO extra text.
ONLY valid JSON.
"""
    # prompt = f"""
# Use ONLY the context below to generate the midterm.

# Variation Seed: {variation_seed}

# CONTEXT:
# {context}

# Requirements:
# - EXACTLY {mcq_count} MCQs (1 mark each)
# - EXACTLY {short_count} Short Questions (4 marks each)
# - EXACTLY {long_count} Long Question ({LONG_MARK} marks)
# - Total marks must equal {total_marks}

# Return STRICTLY this JSON format:

# {{
#   "sections": {{
#     "Section A - MCQ": [
#       {{
#         "question": "...",
#         "marks": 1,
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
#         "marks": 4
#       }}
#     ],
#     "Section C - Long": [
#       {{
#         "question": "...",
#         "marks": {LONG_MARK}
#       }}
#     ]
#   }}
# }}

# DO NOT return "midterm".
# DO NOT change field names.
# DO NOT add extra keys.
# DO NOT add total_marks.
# ONLY return "sections".
# No explanation.
# No markdown.
# """

    # ---------------------------------
    # SAFE JSON EXTRACTOR
    # ---------------------------------

    # def extract_json(text):
    #     match = re.search(r'\{{.*\}}', text, re.DOTALL)
    #     return match.group(0) if match else None

    def extract_json(text):
        match = re.search(r'\{.*\}', text, re.DOTALL)
        return match.group(0) if match else None

    # ---------------------------------
    # VALIDATIONS
    # ---------------------------------

    def validate_structure(sections):

        if not all(k in sections for k in [
            "Section A - MCQ",
            "Section B - Short",
            "Section C - Long"
        ]):
            return False

        if len(sections["Section A - MCQ"]) != mcq_count:
            return False

        if len(sections["Section B - Short"]) != short_count:
            return False

        if len(sections["Section C - Long"]) != long_count:
            return False

        return True


    def validate_no_duplicates(sections):
        seen = set()
        for section in sections.values():
            for q in section:
                text = q.get("question", "").strip().lower()
                if text in seen:
                    return False
                seen.add(text)
        return True


    def validate_mcq_options(sections):
        for q in sections["Section A - MCQ"]:
            if "options" not in q:
                return False
            if not isinstance(q["options"], list):
                return False
            if len(q["options"]) != 4:
                return False
        return True


    # def auto_correct_bloom(sections):

    #     def assign(marks):
    #         if marks == 1:
    #             return random.choice(["REMEMBER", "UNDERSTAND"])
    #         if marks == 4:
    #             return random.choice(["APPLY", "ANALYZE"])
    #         return random.choice(["EVALUATE", "CREATE"])

    #     for section in sections.values():
    #         for q in section:
    #             q["bloom_level"] = assign(int(q.get("marks", 0)))

    #     return sections
    def force_bloom_distribution(sections):

    # MCQ = EASY
        for q in sections["Section A - MCQ"]:
            q["bloom_level"] = "UNDERSTAND"

        # Short = MEDIUM
        for q in sections["Section B - Short"]:
            q["bloom_level"] = "APPLY"

        # Long = HARD
        for q in sections["Section C - Long"]:
            q["bloom_level"] = "CREATE"

        return sections


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

        return (
            bloom_tiers["EASY"] == easy_marks and
            bloom_tiers["MEDIUM"] == medium_marks and
            bloom_tiers["HARD"] == hard_marks
        )


    def validate_total_marks(sections):
        total = 0
        for section in sections.values():
            for q in section:
                total += int(q.get("marks", 0))
        return total == total_marks


    # ---------------------------------
    # GENERATION LOOP
    # ---------------------------------

    MAX_ATTEMPTS = 5
    sections = None

    for attempt in range(MAX_ATTEMPTS):

        print(f"🔁 Attempt {attempt+1}")

        raw_output = rag_engine.generate(prompt)
        print("RAW OUTPUT:\n", raw_output)

        cleaned = re.sub(r'[\x00-\x1f\x7f]', '', raw_output)

        json_block = extract_json(cleaned)

        if not json_block:
            continue

        try:
            parsed = json.loads(json_block)
        except:
            print("❌ JSON Parse Failed")
            continue
        
        # try:
        #     parsed = json.loads(cleaned)
        # except:
        #     print("❌ JSON Parse Failed")
        #     continue

        sections = parsed.get("sections")

        if not sections:
            continue

        if not validate_structure(sections):
            print("❌ Structure mismatch")
            continue

        if not validate_no_duplicates(sections):
            print("❌ Duplicate questions")
            continue

        if not validate_mcq_options(sections):
            print("❌ MCQ options invalid")
            continue

        # sections = auto_correct_bloom(sections)
        sections = force_bloom_distribution(sections)

        if not validate_bloom_distribution(sections):
            print("❌ Bloom mismatch")
            continue

        if not validate_total_marks(sections):
            print("❌ Total marks mismatch")
            continue

        print("✅ VALID MIDTERM GENERATED")
        break

    if not sections:
        return jsonify({"error": "AI failed after retries"}), 500

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