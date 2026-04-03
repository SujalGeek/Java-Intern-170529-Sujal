# from flask import Flask, request, jsonify
# # from config.hybrid_engine import index_book
# from config.hybrid_engine import generate_quiz, generate_reference_answer , evaluate_student_answer
# from services.rag_engine import RAGEngine
# from sentence_transformers import util
# import json
# import re
# import random


# app = Flask(__name__)
# rag_engine = RAGEngine()
# # ------------------------------------------
# # HEALTH CHECK
# # ------------------------------------------
# @app.route("/health", methods=["GET"])
# def health():
#     return jsonify({"status": "UP"})

# # ------------------------------------------
# # PDF INDEX
# # ------------------------------------------
# # @app.route("/index-book", methods=["POST"])
# # def upload_book():
# #     file = request.files.get("file")

# #     if not file:
# #         return jsonify({"error": "No file uploaded"}), 400

# #     file_path = "uploaded_book.pdf"
# #     file.save(file_path)

# #     # index_book(file_path)
# #     rag_engine.index_pdf(file_path)

# #     return jsonify({"status": "success", "message": "Book indexed successfully"})
# @app.route("/index-book", methods=["POST"])
# def upload_book():

#     file = request.files.get("file")
#     course_id = request.form.get("course_id")

#     if not file or not course_id:
#         return jsonify({"error": "File and course_id required"}), 400

#     file_path = f"uploaded_{course_id}.pdf"
#     file.save(file_path)

#     rag_engine.index_pdf(str(course_id), file_path)
    
#     return jsonify({"status": "success"})

# # ------------------------------------------
# # GENERATE QUIZ
# # ------------------------------------------
# import json
# import re
# import random
# from flask import request, jsonify

# # @app.route("/generate-quiz", methods=["POST"])
# # def generate_quiz():

# #     data = request.json
# #     course_id = data.get("course_id")
# #     description = data.get("description")

# #     if not course_id or not description:
# #         return jsonify({"error": "course_id and Description required"}), 400

# #     # =====================================
# #     # 1️ RETRIEVE CONTEXT FROM INDEX
# #     # =====================================

# #     retrieved_chunks, confidence, mode = rag_engine.retrieve(course_id,description)

# #     if not retrieved_chunks:
# #         return jsonify({"error": "No relevant context found"}), 400

# #     context = "\n\n".join(retrieved_chunks[:3])

# #     # =====================================
# #     # 2️ QUIZ BLUEPRINT
# #     # =====================================

# #     TOTAL_MARKS = 10
# #     MCQ_MARK = 1
# #     MCQ_COUNT = TOTAL_MARKS // MCQ_MARK

# #     print("Quiz Blueprint:", MCQ_COUNT, "MCQs")

# #     # =====================================
# #     # 3 PROMPT
# #     # =====================================

# #     variation_seed = random.randint(1, 10000)

# #     prompt = f"""
# # Use ONLY the context below to generate a quiz.

# # Variation Seed: {variation_seed}

# # CONTEXT:
# # {context}

# # QUIZ REQUIREMENTS:
# # - EXACTLY {MCQ_COUNT} MCQs
# # - Each question must be 1 mark
# # - Total Marks = {TOTAL_MARKS}

# # COGNITIVE REQUIREMENTS:
# # - Questions MUST require conceptual reasoning.
# # - Avoid direct definition-based questions.
# # - Do NOT start questions with:
# #   * What is
# #   * Define
# #   * What is the purpose of
# #   * What is meant by
# # - Each question must test a DIFFERENT conceptual principle from the context.
# # - Do NOT repeat the same example structure.
# # - Do NOT generate two questions that differ only by replacing class names.
# # - Each question MUST describe a short practical scenario.
# # - The student must analyze the situation before choosing the answer.
# # - Do NOT ask "which of the following is" style conceptual recall questions.
# # - Every question must involve:
# #     • a situation
# #     • a design decision
# #     • or a behavioral outcome
# # - Avoid pure theory recall.
# # Every question must begin with a short real-world scenario (1–2 sentences) before asking the question.
# # STRUCTURE RULES:
# # - Exactly 4 options per question.
# # - Exactly 1 correct answer.
# # - Bloom level must be "UNDERSTAND".

# # Return STRICT JSON ONLY:

# # {{
# #   "quiz": {{
# #     "total_marks": {TOTAL_MARKS},
# #     "questions": [
# #       {{
# #         "question": "...",
# #         "marks": 1,
# #         "options": [
# #           "A) ...",
# #           "B) ...",
# #           "C) ...",
# #           "D) ..."
# #         ],
# #         "correct_answer": "B",
# #         "bloom_level": "UNDERSTAND"
# #       }}
# #     ]
# #   }}
# # }}
# # """

# #     # =====================================
# #     # 4 SAFE JSON EXTRACTOR
# #     # =====================================

# #     def extract_json(text):
# #         match = re.search(r'\{.*\}', text, re.DOTALL)
# #         return match.group(0) if match else None

# #     # =====================================
# #     # 5 VALIDATION FUNCTIONS
# #     # =====================================

# #     def validate_structure(quiz):

# #         if quiz.get("total_marks") != TOTAL_MARKS:
# #             return False

# #         questions = quiz.get("questions", [])

# #         if len(questions) != MCQ_COUNT:
# #             return False

# #         total = 0

# #         for q in questions:

# #             if q.get("marks") != 1:
# #                 return False

# #             if not isinstance(q.get("options"), list):
# #                 return False

# #             if len(q["options"]) != 4:
# #                 return False

# #             if q.get("bloom_level") not in ["REMEMBER", "UNDERSTAND"]:
# #                 return False

# #             if q.get("correct_answer") not in ["A", "B", "C", "D"]:
# #                 return False

# #             total += q.get("marks")

# #         return total == TOTAL_MARKS


# #     def validate_no_duplicates(quiz):
# #         seen = set()
# #         for q in quiz["questions"]:
# #             text = q.get("question", "").strip().lower()
# #             if text in seen:
# #                 return False
# #             seen.add(text)
# #         return True
    
# #     def validate_bloom_depth(quiz):

# #         banned_starters = [
# #             "what is",
# #             "define",
# #             "what is the purpose",
# #             "what is meant",
# #             "which term means"
# #         ]

# #         valid_count = 0

# #         for q in quiz["questions"]:
# #             question_text = q["question"].strip().lower()

# #             # Reject direct definitions
# #             if any(question_text.startswith(b) for b in banned_starters):
# #                 continue

# #             # Accept if sufficiently descriptive
# #             if len(question_text.split()) >= 8:
# #                 valid_count += 1

# #         # At least 70% must satisfy reasoning
# #         return valid_count >= int(len(quiz["questions"]) * 0.7)

# #     MAX_ATTEMPTS = 5

# #     for attempt in range(MAX_ATTEMPTS):

# #         print(f"Quiz Attempt {attempt+1}")

# #         raw_output = rag_engine.generate(prompt)

# #         cleaned = re.sub(r'[\x00-\x1f\x7f]', '', raw_output)

# #         json_block = extract_json(cleaned)

# #         if not json_block:
# #             print("No JSON detected")
# #             continue

# #         try:
# #             parsed = json.loads(json_block)
# #         except:
# #             print("JSON parsing failed")
# #             continue

# #         quiz = parsed.get("quiz")

# #         if not quiz:
# #             print("Missing quiz key")
# #             continue

# #         if not validate_structure(quiz):
# #             print("Structure validation failed")
# #             continue

# #         if not validate_no_duplicates(quiz):
# #             print("Duplicate questions detected")
# #             continue

# #         if not validate_bloom_depth(quiz):
# #             print("Bloom depth validation failed")
# #             continue

# #         print("VALID QUIZ GENERATED")

# #         return jsonify({
# #             "status": "success",
# #             "confidence": confidence,
# #             "retrieval_mode": mode,
# #             "blueprint": {
# #                 "total_marks": TOTAL_MARKS,
# #                 "mcq_count": MCQ_COUNT
# #             },
# #             "quiz": quiz
# #         })

# #     print("⚠ Returning last generated quiz despite validation softness.")

# #     return jsonify({
# #         "status": "soft_success",
# #         "confidence": confidence,
# #         "retrieval_mode": mode,
# #         "blueprint": {
# #             "total_marks": TOTAL_MARKS,
# #             "mcq_count": MCQ_COUNT
# #         },
# #         "quiz": quiz 
# #     })
# @app.route("/generate-quiz", methods=["POST"])
# def generate_quiz():
#     data = request.json
#     course_id = data.get("course_id")
#     description = data.get("description")
#     requested_count = int(data.get("count", 10))

#     if not course_id or not description:
#         return jsonify({"error": "course_id and description required"}), 400

#     # =====================================
#     # 1️ RETRIEVE CONTEXT FROM INDEX
#     # =====================================
#     retrieved_chunks, confidence, mode = rag_engine.retrieve(course_id, description)

#     if not retrieved_chunks:
#         return jsonify({"error": "No relevant context found"}), 400

#     context = "\n\n".join(retrieved_chunks[:3])

#     # =====================================
#     # 2️ QUIZ BLUEPRINT
#     # =====================================
#     MCQ_MARK = 1
#     # Now MCQ_COUNT is dynamic based on your Postman/Java request!
#     MCQ_COUNT = requested_count 
#     TOTAL_MARKS = MCQ_COUNT * MCQ_MARK

#     print(f"Quiz Blueprint: {MCQ_COUNT} MCQs (Total Marks: {TOTAL_MARKS})")

#     # print(f"Quiz Blueprint: {MCQ_COUNT} MCQs")

#     # =====================================
#     # 3️ PROMPT (Refined for Strictness)
#     # =====================================
#     variation_seed = random.randint(1, 10000)

# #     prompt = f"""
# # Return ONLY a valid JSON object. Do not include any text before or after the JSON.
# # Variation Seed: {variation_seed}

# # CONTEXT:
# # {context}

# # QUIZ REQUIREMENTS:
# # - Generate EXACTLY {MCQ_COUNT} multiple choice questions.
# # - Each question must be 1 mark. Total Marks = {TOTAL_MARKS}.
# # - Use ONLY the provided context.

# # COGNITIVE REQUIREMENTS:
# # - No direct definitions. No questions starting with "What is" or "Define".
# # - Each question MUST be a short 1-2 sentence practical scenario.
# # - Bloom level: UNDERSTAND.

# # STRICT JSON STRUCTURE:
# # {{
# #   "quiz": {{
# #     "total_marks": {TOTAL_MARKS},
# #     "questions": [
# #       {{
# #         "question": "scenario description + question",
# #         "marks": 1,
# #         "options": ["A) ...", "B) ...", "C) ...", "D) ..."],
# #         "correct_answer": "A",
# #         "bloom_level": "UNDERSTAND"
# #       }}
# #     ]
# #   }}
# # }}
# # """
#     prompt = f"""
# Return ONLY a valid JSON object. Do not include any text before or after the JSON.
# Variation Seed: {variation_seed}

# CONTEXT:
# {context}

# QUIZ REQUIREMENTS:
# - Generate EXACTLY {MCQ_COUNT} multiple choice questions about: {description}
# - Each question must be 1 mark. Total Marks = {TOTAL_MARKS}.
# - Extract the core academic concepts from the context, but DO NOT copy the specific textbook examples. 
# - Use generic, standard programming scenarios (e.g., 'Vehicle/Car', 'Employee/Manager') to test the concepts. Do NOT use specific domain names from the text.
# - Use the provided context as your primary source. 
# - CRITICAL: If the context does not contain information about all the topics requested by the user ({description}), you MUST use your own expert programming knowledge to generate questions for the missing topics. 
# - Ensure EVERY topic requested in the prompt has at least one question.

# COGNITIVE REQUIREMENTS:
# - No direct definitions. No questions starting with "What is" or "Define".
# - Each question MUST be a short 1-2 sentence practical scenario.
# - Bloom level: UNDERSTAND.

# STRICT JSON STRUCTURE:
# {{
#   "quiz": {{
#     "total_marks": {TOTAL_MARKS},
#     "questions": [
#       {{
#         "question": "scenario description + question",
#         "marks": 1,
#         "options": ["A) ...", "B) ...", "C) ...", "D) ..."],
#         "correct_answer": "A",
#         "bloom_level": "UNDERSTAND"
#       }}
#     ]
#   }}
# }}
# """

#     # =====================================
#     # 4 SAFE JSON EXTRACTOR & CLEANER
#     # =====================================
#     def extract_json(text):
#         # Remove markdown backticks if present
#         text = re.sub(r'```json\s*|```', '', text).strip()
#         # Find the first { and the last }
#         match = re.search(r'\{.*\}', text, re.DOTALL)
#         return match.group(0) if match else None

#     # =====================================
#     # 5 VALIDATION FUNCTIONS (Untouched Logic)
#     # =====================================
#     def validate_structure(quiz):
#         if quiz.get("total_marks") != TOTAL_MARKS:
#             print(f"DEBUG: Marks mismatch. Expected {TOTAL_MARKS}, got {quiz.get('total_marks')}")
#             return False
#         questions = quiz.get("questions", [])
#         if len(questions) != MCQ_COUNT:
#             print(f"DEBUG: Count mismatch. Expected {MCQ_COUNT}, got {len(questions)}")
#             return False
#         for q in questions:
#             if q.get("marks") != 1 or len(q.get("options", [])) != 4:
#                 return False
#             if q.get("correct_answer") not in ["A", "B", "C", "D"]:
#                 return False
#         return True

#     def validate_no_duplicates(quiz):
#         seen = set()
#         for q in quiz["questions"]:
#             text = q.get("question", "").strip().lower()
#             if text in seen: return False
#             seen.add(text)
#         return True
    
#     def validate_bloom_depth(quiz):
#         banned = ["what is", "define", "what is the purpose", "what is meant"]
#         valid_count = 0
#         for q in quiz["questions"]:
#             txt = q["question"].strip().lower()
#             if any(txt.startswith(b) for b in banned): continue
#             if len(txt.split()) >= 8: valid_count += 1
#         return valid_count >= int(len(quiz["questions"]) * 0.7)

#     # =====================================
#     # 6 GENERATION LOOP
#     # =====================================
#     MAX_ATTEMPTS = 5
#     last_quiz = None

#     for attempt in range(MAX_ATTEMPTS):
#         print(f"Quiz Attempt {attempt+1}")
#         raw_output = rag_engine.generate(prompt)
        
#         # Basic cleaning of non-printable characters
#         cleaned = re.sub(r'[\x00-\x1f\x7f]', '', raw_output)
#         json_block = extract_json(cleaned)

#         if not json_block:
#             print("No JSON detected")
#             continue

#         try:
#             parsed = json.loads(json_block)
#             quiz = parsed.get("quiz")
#             last_quiz = quiz # Store in case of soft_success
#         except Exception as e:
#             print(f"JSON parsing failed: {e}")
#             continue

#         if not quiz:
#             print("Missing quiz key")
#             continue

#         # Sequential Validations
#         if not validate_structure(quiz):
#             print("Structure validation failed")
#             continue
#         if not validate_no_duplicates(quiz):
#             print("Duplicate questions detected")
#             continue
#         if not validate_bloom_depth(quiz):
#             print("Bloom depth validation failed")
#             continue

#         print("VALID QUIZ GENERATED")
#         return jsonify({
#             # "status": "success",
#             # "confidence": confidence,
#             # "retrieval_mode": mode,
#             # "blueprint": {"total_marks": TOTAL_MARKS, "mcq_count": MCQ_COUNT},
#             # # "quiz":  quiz
#             "status": "success",
#             "concept": description,
#             "bloom_level": "UNDERSTAND",
#             "question_count": len(quiz.get("questions", [])),
#             "generated_questions": quiz.get("questions", []), # MUST MATCH JAVA NAME
#             "confidence": confidence,
#             "retrieval_mode": mode
#         })

#     # =====================================
#     # 7 FALLBACK
#     # =====================================
#     print("⚠ Returning last generated quiz despite validation issues.")
#     return jsonify({
#         "status": "soft_success",
#         "confidence": confidence,
#         "retrieval_mode": mode,
#         "blueprint": {"total_marks": TOTAL_MARKS, "mcq_count": MCQ_COUNT},
#         "quiz": last_quiz if last_quiz else {}
#     })


# @app.route("/generate-reference", methods=["POST"])
# def generate_reference():

#     data = request.json
#     course_id = data.get("course_id")
#     question = data.get("question", "")
#     bloom_level = data.get("bloom_level", "Understand")

#     if not question:
#         return jsonify({"error": "Question is required"}), 400

#     result = generate_reference_answer(
#         course_id=course_id,
#         question=question,
#         bloom_level=bloom_level
#     )

#     return jsonify(result)

# @app.route("/evaluate-answer", methods=["POST"])
# def evaluate_answer_api():

#     data = request.json
#     print("Evaluate answer hit")
#     student_answer = data.get("student_answer")
#     reference_answer = data.get("reference_answer")
#     bloom_level = data.get("bloom_level", "Understand")

#     print("Student Answer:", student_answer)
#     print("Reference Answer:", reference_answer)

#     if not student_answer or not reference_answer:
#         return jsonify({"status": "failed", "message": "Missing data"}), 400

#     # -------------------------------
#     # SBERT Similarity
#     # -------------------------------
#     emb_student = rag_engine.model.encode(student_answer, convert_to_tensor=True)
#     emb_reference = rag_engine.model.encode(reference_answer, convert_to_tensor=True)

#     similarity = util.cos_sim(emb_student, emb_reference).item()
#     similarity = max(0.0, float(similarity))

#     word_count = len(student_answer.split())

#     # -------------------------------
#     # HARD OFF-TOPIC GUARD
#     # -------------------------------
#     if similarity < 0.55:
#         final_score = round(similarity * 35, 2)

#         return jsonify({
#             "status": "success",
#             "score": final_score,
#             "feedback": "Answer is off-topic or conceptually incorrect."
#         })

#     # -------------------------------
#     # Depth Adjustment
#     # -------------------------------
#     if word_count < 5:
#         similarity *= 0.5
#     elif word_count < 10:
#         similarity *= 0.75

#     # -------------------------------
#     # Bloom Level Cap
#     # -------------------------------
#     bloom_caps = {
#         "Remember": 0.7,
#         "Understand": 0.85,
#         "Apply": 1.0,
#         "Analyze": 1.0,
#         "Evaluate": 1.0,
#         "Create": 1.0
#     }

#     similarity = min(similarity, bloom_caps.get(bloom_level, 1.0))

#     final_score = round(similarity * 100, 2)

#     # -------------------------------
#     # Feedback
#     # -------------------------------
#     if final_score >= 85:
#         feedback = "Excellent answer."
#     elif final_score >= 65:
#         feedback = "Good answer but needs refinement."
#     elif final_score >= 45:
#         feedback = "Partial understanding."
#     else:
#         feedback = "Weak or incorrect answer."

#     return jsonify({
#         "status": "success",
#         "score": final_score,
#         "feedback": feedback
#     })

# @app.route("/generate-midterm-blueprint", methods=["POST"])
# def generate_midterm_blueprint():

#     data = request.json
#     course_id = data.get("course_id")
#     description = data.get("description")
#     total_questions = int(data.get("total_questions", 10))

#     if not course_id or not description:
#         return jsonify({"error": "course_id and Description required"}), 400

#     # 🔹 Step 1: Retrieve relevant content using RAG
#     retrieved_chunks, confidence, mode = rag_engine.retrieve(course_id,description)

#     if not retrieved_chunks:
#         return jsonify({"error": "No relevant context found"}), 400

#     retrieved_context = "\n\n".join(retrieved_chunks[:3])

#     topic_complexity = len(" ".join(retrieved_chunks).split())

#     # 🔹 Step 2: Intelligent Difficulty Logic
#     if confidence > 0.85 and topic_complexity > 500:
#         easy = int(total_questions * 0.3)
#         medium = int(total_questions * 0.4)
#         hard = total_questions - easy - medium
#     else:
#         easy = int(total_questions * 0.5)
#         medium = int(total_questions * 0.3)
#         hard = total_questions - easy - medium

#     # 🔹 Step 3: Bloom Distribution
#     bloom_distribution = {
#         "Understand": int(total_questions * 0.4),
#         "Apply": int(total_questions * 0.3),
#         "Analyze": total_questions - int(total_questions * 0.4) - int(total_questions * 0.3)
#     }

#     return jsonify({
#         "easy": easy,
#         "medium": medium,
#         "hard": hard,
#         "bloom_distribution": bloom_distribution,
#         "confidence": confidence
#     })

# @app.route("/generate-mcq", methods=["POST"])
# def generate_mcq():

#     data = request.json
#     concept = data.get("concept")
#     course_id = data.get("course_id")
#     bloom_level = data.get("bloom_level", "Understand")
#     count = data.get("count", 5)

#     retrieved_chunks, confidence, mode = rag_engine.retrieve(course_id,concept)

#     context = "\n\n".join(retrieved_chunks[:3])

#     prompt = f"""
#     Generate {count} multiple choice questions on {concept}.
#     Bloom Level: {bloom_level}

#     Use this context:
#     {context}

#     Each MCQ must have:
#     - Question
#     - 4 options
#     - Correct answer
#     """

#     result = rag_engine.generate(prompt)

#     return jsonify({
#         "mcqs": result,
#         "confidence": confidence
#     })

# @app.route("/submit-quiz", methods=["POST"])
# def submit_quiz():

#     data = request.json

#     if not data:
#         return jsonify({"error": "Invalid request body"}), 400

#     quiz = data.get("quiz")
#     answers = data.get("answers")
#     student_id = data.get("student_id")

#     if not quiz or not answers or not student_id:
#         return jsonify({"error": "quiz, answers, and student_id are required"}), 400

#     questions = quiz.get("questions")

#     if not questions or not isinstance(questions, list):
#         return jsonify({"error": "Invalid quiz structure"}), 400

#     if len(answers) != len(questions):
#         return jsonify({"error": "Answers count mismatch"}), 400

#     total_score = 0
#     total_marks = quiz.get("total_marks", 0)

#     def calculate_grade(percentage):

#         if percentage >= 90:
#             return "A+"
#         elif percentage >= 80:
#             return "A"
#         elif percentage >= 70:
#             return "B"
#         elif percentage >= 60:
#             return "C"
#         elif percentage >= 50:
#             return "D"
#         else:
#             return "F"
    
#     detailed_results = []

#     for idx, question in enumerate(questions):

#         correct_answer = question.get("correct_answer")
#         student_answer = answers[idx]

#         is_correct = student_answer == correct_answer
#         marks = question.get("marks", 1)

#         score_awarded = marks if is_correct else 0
#         total_score += score_awarded

#         detailed_results.append({
#             "question_index": idx + 1,
#             "student_answer": student_answer,
#             "correct_answer": correct_answer,
#             "is_correct": is_correct,
#             "marks_awarded": score_awarded
#         })

#     if total_marks == 0:
#         percentage = 0
#     else:
#         percentage = round((total_score / total_marks) * 100, 2)

#     grade = calculate_grade(percentage)

#     return jsonify({
#         "status": "evaluated",
#         "student_id": student_id,
#         "total_score": total_score,
#         "total_marks": total_marks,
#         "percentage": percentage,
#         "grade": grade,
#         "results": detailed_results
#     })


# @app.route("/generate-assignment", methods=["POST"])
# def generate_assignment():

#     data = request.json
#     course_id = data.get("course_id")
#     description = data.get("description")
#     total_marks = data.get("total_marks", 20)
#     difficulty = data.get("difficulty", "medium")
#     teacher_distribution = data.get("bloom_distribution")

#     if not course_id or not description:
#         return jsonify({"error": "course_id and Description required"}), 400

#     retrieved_chunks, confidence, mode = rag_engine.retrieve(course_id,description)

#     if not retrieved_chunks:
#         return jsonify({"error": "No relevant context found"}), 400

#     context = "\n\n".join(retrieved_chunks[:4])

#     if teacher_distribution:
#         bloom_distribution = teacher_distribution
#     else:
#         if difficulty == "easy":
#             bloom_distribution = {
#                 "REMEMBER": 2,
#                 "UNDERSTAND": 2,
#                 "APPLY": 1
#             }
#         elif difficulty == "medium":
#             bloom_distribution = {
#                 "UNDERSTAND": 1,
#                 "APPLY": 1,
#                 "ANALYZE": 1,
#                 "EVALUATE": 1
#             }
#         else:  # hard
#             bloom_distribution = {
#                 "APPLY": 1,
#                 "ANALYZE": 2,
#                 "EVALUATE": 2
#             }

#     # ================================
#     #  Auto Marks Distribution
#     # ================================

#     num_questions = sum(bloom_distribution.values())
#     marks_per_question = total_marks // num_questions

#     # ================================
#     #  Build Prompt
#     # ================================

#     bloom_instruction = "\n".join(
#         [f"{count} question(s) at {level} level"
#          for level, count in bloom_distribution.items()]
#     )

# #     prompt = f"""
# # Use ONLY the context below.

# # CONTEXT:
# # {context}

# # Generate a descriptive assignment.

# # Total Marks: {total_marks}

# # Bloom Distribution:
# # {bloom_instruction}

# # Each question carries {marks_per_question} marks.

# # Rules:
# # - No MCQs.
# # - Descriptive only.
# # - Strictly follow Bloom distribution.
# # - Include bloom_level in JSON.
# # - Return STRICT JSON only.

# # Return format:

# # {{
# #   "assignment": {{
# #     "total_marks": {total_marks},
# #     "questions": [
# #       {{
# #         "question": "...",
# #         "marks": {marks_per_question},
# #         "bloom_level": "ANALYZE"
# #       }}
# #     ]
# #   }}
# # }}
# # """
#     prompt = f"""
# Extract the core academic concepts from the context below about: {description}

# CONTEXT:
# {context}

# Generate a descriptive assignment testing these concepts.

# Total Marks: {total_marks}

# Bloom Distribution:
# {bloom_instruction}

# Each question carries {marks_per_question} marks.

# Rules:
# - No MCQs.
# - Descriptive only.
# - Strictly follow Bloom distribution.
# - Include bloom_level in JSON.
# - Return STRICT JSON only.
# - GENERALIZATION RULE: Test the concepts, NOT the textbook's specific examples. Do NOT use specific names from the text (e.g., 'CashTill', 'Publication'). Use standard universal examples.
# - TOPIC COVERAGE: If the context is missing some of the requested topics from '{description}', use your expert knowledge to include questions about them anyway.

# Return format:

# {{
#   "assignment": {{
#     "total_marks": {total_marks},
#     "questions": [
#       {{
#         "question": "...",
#         "marks": {marks_per_question},
#         "bloom_level": "ANALYZE"
#       }}
#     ]
#   }}
# }}
# """

#     raw_output = rag_engine.generate(prompt)

#     try:
#         parsed = json.loads(re.search(r'\{.*\}', raw_output, re.DOTALL).group(0))
#     except:
#         return jsonify({"error": "Invalid JSON from model"}), 500

#     assignment = parsed.get("assignment")

#     if not assignment:
#         return jsonify({"error": "Invalid structure"}), 500

#     # ================================
#     #  Validate Bloom Compliance
#     # ================================

#     def validate_distribution(questions, expected_distribution):
#         found = {}
#         for q in questions:
#             level = q["bloom_level"].upper()
#             found[level] = found.get(level, 0) + 1
#         return found == expected_distribution

#     if not validate_distribution(assignment["questions"], bloom_distribution):
#         return jsonify({"error": "Bloom distribution mismatch"}), 500

#     return jsonify({
#         "status": "success",
#         "confidence": confidence,
#         "retrieval_mode": mode,
#         "assignment": assignment
#     })

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

# #         prompt = f"""
# # Evaluate strictly.

# # Question:
# # {q['question']}

# # Student Answer:
# # {student_answer}

# # Max Marks:
# # {q['marks']}

# # Return JSON:
# # {{
# #   "score": X,
# #   "feedback": "..."
# # }}
# # """
#         prompt = f"""
# Evaluate strictly.

# Question:
# {q['question']}

# Student Answer:
# {student_answer}

# Max Marks:
# {q['marks']}

# Return JSON:
# {{
#   "score": X,
#   "feedback": "..."
# }}
# """

#         raw = rag_engine.generate(prompt)

#         try:
#             parsed = json.loads(re.search(r'\{.*\}', raw, re.DOTALL).group(0))
#         except:
#             return jsonify({"error": "Invalid evaluation JSON"}), 500

#         score = min(parsed["score"], q["marks"])  #   cap score

#         total_score += score

#         results.append({
#             "question": q["question"],
#             "score": score,
#             "feedback": parsed["feedback"]
#         })

#     total_marks = sum(q["marks"] for q in questions)
#     percentage = round((total_score / total_marks) * 100, 2)

#     return jsonify({
#         "total_score": total_score,
#         "percentage": percentage,
#         "results": results
#     })


# @app.route("/generate-dynamic-midterm", methods=["POST"])
# def generate_dynamic_midterm():

#     data = request.json
#     course_id = str(data.get("course_id"))
#     description = data.get("description")
#     total_marks = int(data.get("total_marks", 20))

#     if not course_id or not description:
#         return jsonify({"error": "course_id and Description required"}), 400

#     # ---------------------------------
#     # RETRIEVE CONTEXT
#     # ---------------------------------

#     retrieved_chunks, confidence, mode = rag_engine.retrieve(course_id,description)

#     if not retrieved_chunks:
#         return jsonify({"error": "No relevant context found"}), 400

#     context = "\n\n".join(retrieved_chunks[:3])

#     # ---------------------------------
#     # BLUEPRINT
#     # ---------------------------------

#     MCQ_MARK = 1
#     SHORT_MARK = 4

#     easy_marks = round(total_marks * 0.2)
#     medium_marks = round(total_marks * 0.4)
#     hard_marks = total_marks - easy_marks - medium_marks

#     mcq_count = easy_marks // MCQ_MARK
#     short_count = medium_marks // SHORT_MARK
#     long_count = 1
#     LONG_MARK = hard_marks

#     total_questions = mcq_count + short_count + long_count

#     print("Blueprint:", mcq_count, short_count, long_count, LONG_MARK)

#     # ---------------------------------
#     # PROMPT
#     # ---------------------------------

#     variation_seed = random.randint(1, 10000)

# #     prompt = f"""
# # You are an academic question paper generator.

# # Use ONLY the context below to generate a midterm paper.

# # CONTEXT:
# # {context}

# # ========================
# # PAPER REQUIREMENTS
# # ========================

# # Total Marks: {total_marks}

# # Section Distribution:
# # - Section A: {mcq_count} MCQs ({MCQ_MARK} mark each)
# # - Section B: {short_count} Short Questions ({SHORT_MARK} marks each)
# # - Section C: {long_count} Long Question ({LONG_MARK} marks)

# # IMPORTANT RULES:

# # 1. STRICTLY follow the section names:
# #    - "Section A - MCQ"
# #    - "Section B - Short"
# #    - "Section C - Long"

# # 2. Do NOT rename sections.
# # 3. Do NOT create extra keys.
# # 4. Do NOT wrap output inside "midterm".
# # 5. Do NOT include total_marks in output.
# # 6. Do NOT include explanation or commentary.
# # 7. Return ONLY valid JSON.
# # 8. All questions must be UNIQUE.
# # 9. MCQs must include EXACTLY 4 options.
# # 10. Marks must match exactly:
# #     - MCQ = {MCQ_MARK}
# #     - Short = {SHORT_MARK}
# #     - Long = {LONG_MARK}

# # ========================
# # OUTPUT FORMAT (STRICT)
# # ========================

# # Return ONLY this JSON structure:

# # {{
# #   "sections": {{
# #     "Section A - MCQ": [
# #       {{
# #         "question": "...",
# #         "marks": {MCQ_MARK},
# #         "options": [
# #           "A) ...",
# #           "B) ...",
# #           "C) ...",
# #           "D) ..."
# #         ],
# #         "correct_answer": "A",
# #         "bloom_level": "UNDERSTAND"
# #       }}
# #     ],
# #     "Section B - Short": [
# #       {{
# #         "question": "...",
# #         "marks": {SHORT_MARK},
# #         "reference_answer": "...",
# #         "bloom_level": "APPLY"
# #       }}
# #     ],
# #     "Section C - Long": [
# #       {{
# #         "question": "...",
# #         "marks": {LONG_MARK},
# #         "reference_answer": "...",
# #         "bloom_level": "CREATE"
# #       }}
# #     ]
# #   }}
# # }}

# # NO markdown.
# # NO backticks.
# # NO extra text.
# # ONLY valid JSON.
# # """
#     prompt = f"""
# You are an academic question paper generator.

# Extract the core academic concepts from the context below to generate a midterm paper about: {description}

# CONTEXT:
# {context}

# ========================
# PAPER REQUIREMENTS
# ========================

# Total Marks: {total_marks}

# Section Distribution:
# - Section A: {mcq_count} MCQs ({MCQ_MARK} mark each)
# - Section B: {short_count} Short Questions ({SHORT_MARK} marks each)
# - Section C: {long_count} Long Question ({LONG_MARK} marks)

# IMPORTANT RULES:

# 1. STRICTLY follow the section names:
#    - "Section A - MCQ"
#    - "Section B - Short"
#    - "Section C - Long"

# 2. Do NOT rename sections.
# 3. Do NOT create extra keys.
# 4. Do NOT wrap output inside "midterm".
# 5. Do NOT include total_marks in output.
# 6. Do NOT include explanation or commentary.
# 7. Return ONLY valid JSON.
# 8. TOPIC COVERAGE: Ensure all core topics from '{description}' are equally represented in the questions. If the context is missing a topic, use your expert CS knowledge to fill the gap.
# 9. GENERALIZATION RULE: Test the concepts, NOT the textbook's specific examples. Do NOT use specific names from the text (e.g., 'CashTill', 'Publication'). Create new, generic examples (like 'Animal/Dog' or 'Employee/Manager').
# 10. All questions must be UNIQUE.
# 11. MCQs must include EXACTLY 4 options.
# 12. Marks must match exactly:
#     - MCQ = {MCQ_MARK}
#     - Short = {SHORT_MARK}
#     - Long = {LONG_MARK}

# ========================
# OUTPUT FORMAT (STRICT)
# ========================

# Return ONLY this JSON structure:

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
#         "correct_answer": "A",
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

# NO markdown.
# NO backticks.
# NO extra text.
# ONLY valid JSON.
# """

#     def extract_json(text):
#         match = re.search(r'\{.*\}', text, re.DOTALL)
#         return match.group(0) if match else None

#     # ---------------------------------
#     # VALIDATIONS
#     # ---------------------------------

#     def validate_structure(sections):

#         if not all(k in sections for k in [
#             "Section A - MCQ",
#             "Section B - Short",
#             "Section C - Long"
#         ]):
#             return False

#         if len(sections["Section A - MCQ"]) != mcq_count:
#             return False

#         if len(sections["Section B - Short"]) != short_count:
#             return False

#         if len(sections["Section C - Long"]) != long_count:
#             return False

#         return True


#     def validate_no_duplicates(sections):
#         seen = set()
#         for section in sections.values():
#             for q in section:
#                 text = q.get("question", "").strip().lower()
#                 if text in seen:
#                     return False
#                 seen.add(text)
#         return True


#     def validate_mcq_options(sections):
#         for q in sections["Section A - MCQ"]:
#             if "options" not in q:
#                 return False
#             if not isinstance(q["options"], list):
#                 return False
#             if len(q["options"]) != 4:
#                 return False
#         return True

#     def force_bloom_distribution(sections):

#     # MCQ = EASY
#         for q in sections["Section A - MCQ"]:
#             q["bloom_level"] = "UNDERSTAND"

#         # Short = MEDIUM
#         for q in sections["Section B - Short"]:
#             q["bloom_level"] = "APPLY"

#         # Long = HARD
#         for q in sections["Section C - Long"]:
#             q["bloom_level"] = "CREATE"

#         return sections


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

#         return (
#             bloom_tiers["EASY"] == easy_marks and
#             bloom_tiers["MEDIUM"] == medium_marks and
#             bloom_tiers["HARD"] == hard_marks
#         )


#     def validate_total_marks(sections):
#         total = 0
#         for section in sections.values():
#             for q in section:
#                 total += int(q.get("marks", 0))
#         return total == total_marks


#     # ---------------------------------
#     # GENERATION LOOP
#     # ---------------------------------

#     MAX_ATTEMPTS = 5
#     sections = None

#     for attempt in range(MAX_ATTEMPTS):

#         print(f"Attempt {attempt+1}")

#         raw_output = rag_engine.generate(prompt)
#         print("RAW OUTPUT:\n", raw_output)

#         cleaned = re.sub(r'[\x00-\x1f\x7f]', '', raw_output)

#         json_block = extract_json(cleaned)

#         if not json_block:
#             continue

#         try:
#             parsed = json.loads(json_block)
#         except:
#             print(" JSON Parse Failed")
#             continue

#         sections = parsed.get("sections")

#         if not sections:
#             continue

#         if not validate_structure(sections):
#             print(" Structure mismatch")
#             continue

#         if not validate_no_duplicates(sections):
#             print(" Duplicate questions")
#             continue

#         if not validate_mcq_options(sections):
#             print(" MCQ options invalid")
#             continue

#         # sections = auto_correct_bloom(sections)
#         sections = force_bloom_distribution(sections)

#         if not validate_bloom_distribution(sections):
#             print(" Bloom mismatch")
#             continue

#         if not validate_total_marks(sections):
#             print(" Total marks mismatch")
#             continue

#         print(" VALID MIDTERM GENERATED")
#         break

#     if not sections:
#         return jsonify({"error": "AI failed after retries"}), 500

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


# if __name__ == "__main__":
#     app.run(host="0.0.0.0", port=5001, debug=True, use_reloader=False)

from flask import Flask, request, jsonify
import os
# from config.hybrid_engine import index_book
from config.hybrid_engine import generate_quiz, generate_reference_answer , evaluate_student_answer
from services.rag_engine import RAGEngine
from sentence_transformers import util
import json
import re
import random


app = Flask(__name__)
rag_engine = RAGEngine()
# ------------------------------------------
# HEALTH CHECK
# ------------------------------------------
@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "UP"})

# ------------------------------------------
# PDF INDEX
# ------------------------------------------
# @app.route("/index-book", methods=["POST"])
# def upload_book():
#     file = request.files.get("file")

#     if not file:
#         return jsonify({"error": "No file uploaded"}), 400

#     file_path = "uploaded_book.pdf"
#     file.save(file_path)

#     # index_book(file_path)
#     rag_engine.index_pdf(file_path)

#     return jsonify({"status": "success", "message": "Book indexed successfully"})
@app.route("/index-book", methods=["POST"])
def upload_book():

    file = request.files.get("file")
    course_id = request.form.get("course_id")

    if not file or not course_id:
        return jsonify({"error": "File and course_id required"}), 400

    file_path = f"uploaded_{course_id}.pdf"
    file.save(file_path)

    rag_engine.index_pdf(str(course_id), file_path)
    
    return jsonify({"status": "success"})

# ------------------------------------------
# GENERATE QUIZ
# ------------------------------------------
import json
import re
import random
from flask import request, jsonify

# @app.route("/generate-quiz", methods=["POST"])
# def generate_quiz():

#     data = request.json
#     course_id = data.get("course_id")
#     description = data.get("description")

#     if not course_id or not description:
#         return jsonify({"error": "course_id and Description required"}), 400

#     # =====================================
#     # 1️ RETRIEVE CONTEXT FROM INDEX
#     # =====================================

#     retrieved_chunks, confidence, mode = rag_engine.retrieve(course_id,description)

#     if not retrieved_chunks:
#         return jsonify({"error": "No relevant context found"}), 400

#     context = "\n\n".join(retrieved_chunks[:3])

#     # =====================================
#     # 2️ QUIZ BLUEPRINT
#     # =====================================

#     TOTAL_MARKS = 10
#     MCQ_MARK = 1
#     MCQ_COUNT = TOTAL_MARKS // MCQ_MARK

#     print("Quiz Blueprint:", MCQ_COUNT, "MCQs")

#     # =====================================
#     # 3 PROMPT
#     # =====================================

#     variation_seed = random.randint(1, 10000)

#     prompt = f"""
# Use ONLY the context below to generate a quiz.

# Variation Seed: {variation_seed}

# CONTEXT:
# {context}

# QUIZ REQUIREMENTS:
# - EXACTLY {MCQ_COUNT} MCQs
# - Each question must be 1 mark
# - Total Marks = {TOTAL_MARKS}

# COGNITIVE REQUIREMENTS:
# - Questions MUST require conceptual reasoning.
# - Avoid direct definition-based questions.
# - Do NOT start questions with:
#   * What is
#   * Define
#   * What is the purpose of
#   * What is meant by
# - Each question must test a DIFFERENT conceptual principle from the context.
# - Do NOT repeat the same example structure.
# - Do NOT generate two questions that differ only by replacing class names.
# - Each question MUST describe a short practical scenario.
# - The student must analyze the situation before choosing the answer.
# - Do NOT ask "which of the following is" style conceptual recall questions.
# - Every question must involve:
#     • a situation
#     • a design decision
#     • or a behavioral outcome
# - Avoid pure theory recall.
# Every question must begin with a short real-world scenario (1–2 sentences) before asking the question.
# STRUCTURE RULES:
# - Exactly 4 options per question.
# - Exactly 1 correct answer.
# - Bloom level must be "UNDERSTAND".

# Return STRICT JSON ONLY:

# {{
#   "quiz": {{
#     "total_marks": {TOTAL_MARKS},
#     "questions": [
#       {{
#         "question": "...",
#         "marks": 1,
#         "options": [
#           "A) ...",
#           "B) ...",
#           "C) ...",
#           "D) ..."
#         ],
#         "correct_answer": "B",
#         "bloom_level": "UNDERSTAND"
#       }}
#     ]
#   }}
# }}
# """

#     # =====================================
#     # 4 SAFE JSON EXTRACTOR
#     # =====================================

#     def extract_json(text):
#         match = re.search(r'\{.*\}', text, re.DOTALL)
#         return match.group(0) if match else None

#     # =====================================
#     # 5 VALIDATION FUNCTIONS
#     # =====================================

#     def validate_structure(quiz):

#         if quiz.get("total_marks") != TOTAL_MARKS:
#             return False

#         questions = quiz.get("questions", [])

#         if len(questions) != MCQ_COUNT:
#             return False

#         total = 0

#         for q in questions:

#             if q.get("marks") != 1:
#                 return False

#             if not isinstance(q.get("options"), list):
#                 return False

#             if len(q["options"]) != 4:
#                 return False

#             if q.get("bloom_level") not in ["REMEMBER", "UNDERSTAND"]:
#                 return False

#             if q.get("correct_answer") not in ["A", "B", "C", "D"]:
#                 return False

#             total += q.get("marks")

#         return total == TOTAL_MARKS


#     def validate_no_duplicates(quiz):
#         seen = set()
#         for q in quiz["questions"]:
#             text = q.get("question", "").strip().lower()
#             if text in seen:
#                 return False
#             seen.add(text)
#         return True
    
#     def validate_bloom_depth(quiz):

#         banned_starters = [
#             "what is",
#             "define",
#             "what is the purpose",
#             "what is meant",
#             "which term means"
#         ]

#         valid_count = 0

#         for q in quiz["questions"]:
#             question_text = q["question"].strip().lower()

#             # Reject direct definitions
#             if any(question_text.startswith(b) for b in banned_starters):
#                 continue

#             # Accept if sufficiently descriptive
#             if len(question_text.split()) >= 8:
#                 valid_count += 1

#         # At least 70% must satisfy reasoning
#         return valid_count >= int(len(quiz["questions"]) * 0.7)

#     MAX_ATTEMPTS = 5

#     for attempt in range(MAX_ATTEMPTS):

#         print(f"Quiz Attempt {attempt+1}")

#         raw_output = rag_engine.generate(prompt)

#         cleaned = re.sub(r'[\x00-\x1f\x7f]', '', raw_output)

#         json_block = extract_json(cleaned)

#         if not json_block:
#             print("No JSON detected")
#             continue

#         try:
#             parsed = json.loads(json_block)
#         except:
#             print("JSON parsing failed")
#             continue

#         quiz = parsed.get("quiz")

#         if not quiz:
#             print("Missing quiz key")
#             continue

#         if not validate_structure(quiz):
#             print("Structure validation failed")
#             continue

#         if not validate_no_duplicates(quiz):
#             print("Duplicate questions detected")
#             continue

#         if not validate_bloom_depth(quiz):
#             print("Bloom depth validation failed")
#             continue

#         print("VALID QUIZ GENERATED")

#         return jsonify({
#             "status": "success",
#             "confidence": confidence,
#             "retrieval_mode": mode,
#             "blueprint": {
#                 "total_marks": TOTAL_MARKS,
#                 "mcq_count": MCQ_COUNT
#             },
#             "quiz": quiz
#         })

#     print("⚠ Returning last generated quiz despite validation softness.")

#     return jsonify({
#         "status": "soft_success",
#         "confidence": confidence,
#         "retrieval_mode": mode,
#         "blueprint": {
#             "total_marks": TOTAL_MARKS,
#             "mcq_count": MCQ_COUNT
#         },
#         "quiz": quiz 
#     })
@app.route("/generate-quiz", methods=["POST"])
def generate_quiz():
    data = request.json
    course_id = data.get("course_id")
    description = data.get("description")
    requested_count = int(data.get("count", 10))

    if not course_id or not description:
        return jsonify({"error": "course_id and description required"}), 400

    # =====================================
    # 1️ RETRIEVE CONTEXT FROM INDEX
    # =====================================
    retrieved_chunks, confidence, mode = rag_engine.retrieve(course_id, description)

    if not retrieved_chunks:
        return jsonify({"error": "No relevant context found"}), 400

    context = "\n\n".join(retrieved_chunks[:3])

    # =====================================
    # 2️ QUIZ BLUEPRINT
    # =====================================
    MCQ_MARK = 1
    # Now MCQ_COUNT is dynamic based on your Postman/Java request!
    MCQ_COUNT = requested_count 
    TOTAL_MARKS = MCQ_COUNT * MCQ_MARK

    print(f"Quiz Blueprint: {MCQ_COUNT} MCQs (Total Marks: {TOTAL_MARKS})")

    # =====================================
    # 3️ PROMPT (Refined for Strictness & FEW-SHOT EXAMPLE)
    # =====================================
    variation_seed = random.randint(1, 10000)

    #   GOD-TIER SCENARIO PROMPT INJECTED HERE
    prompt = f"""
Return ONLY a valid JSON object. Do not include any text before or after the JSON.
Variation Seed: {variation_seed}

CONTEXT:
{context}

QUIZ REQUIREMENTS:
- Generate EXACTLY {MCQ_COUNT} multiple choice questions about: {description}
- Each question must be 1 mark. Total Marks = {TOTAL_MARKS}.
- Extract the core academic concepts from the context, but DO NOT copy the specific textbook examples. 
- Use generic, standard programming scenarios to test the concepts. Do NOT use specific domain names from the text.
- Use the provided context as your primary source. 
- CRITICAL: If the context does not contain information about all the topics requested by the user ({description}), you MUST use your own expert programming knowledge to generate questions for the missing topics. 
- Ensure EVERY topic requested in the prompt has at least one question.

COGNITIVE REQUIREMENTS:
- No direct definitions. No questions starting with "What is" or "Define".
- Each question MUST be a short 1-2 sentence practical scenario.
- Bloom level: UNDERSTAND.

EXAMPLE OF A PERFECT SCENARIO QUESTION:
{{
  "question": "A developer is designing a logging system where only one instance of the logger should exist across the entire application. Which design pattern should they implement?",
  "marks": 1,
  "options": [
    "A) Factory Pattern, to create multiple instances safely.",
    "B) Singleton Pattern, to restrict instantiation to one object.",
    "C) Observer Pattern, to notify classes of log events.",
    "D) Decorator Pattern, to wrap the logger with new features."
  ],
  "correct_answer": "B",
  "bloom_level": "UNDERSTAND"
}}

STRICT JSON STRUCTURE REQUIRED:
{{
  "quiz": {{
    "total_marks": {TOTAL_MARKS},
    "questions": [
      // Insert your {MCQ_COUNT} generated questions here matching the structure exactly.
    ]
  }}
}}
"""

    # =====================================
    # 4 SAFE JSON EXTRACTOR & CLEANER
    # =====================================
    def extract_json(text):
        # Remove markdown backticks if present
        text = re.sub(r'```json\s*|```', '', text).strip()
        # Find the first { and the last }
        match = re.search(r'\{.*\}', text, re.DOTALL)
        return match.group(0) if match else None

    # =====================================
    # 5 VALIDATION FUNCTIONS (Upgraded)
    # =====================================
    def validate_structure(quiz):
        questions = quiz.get("questions", [])

        # if quiz.get("total_marks") != TOTAL_MARKS:
        #     print(f"DEBUG: Marks mismatch. Expected {TOTAL_MARKS}, got {quiz.get('total_marks')}")
        #     return False

        if len(questions) < MCQ_COUNT:
            print(f"DEBUG: Count mismatch. Expected at least {MCQ_COUNT}, got {len(questions)}")
            return False
        
        # questions = quiz.get("questions", [])
        # if len(questions) != MCQ_COUNT:
        #     print(f"DEBUG: Count mismatch. Expected {MCQ_COUNT}, got {len(questions)}")
        #     return False

        # for q in questions:
        #     if q.get("marks") != 1 or len(q.get("options", [])) != 4:
        #         return False
        #     if q.get("correct_answer") not in ["A", "B", "C", "D"]:
        #         return False
        # return True
        for q in questions[:MCQ_COUNT]:
            if q.get("marks") != 1 or len(q.get("options", [])) != 4:
                print("DEBUG: Options length mismatch")
                return False
            # Ensure correct_answer is just a clean letter A, B, C, or D
            ans = str(q.get("correct_answer")).strip().upper()
            if ans not in ["A", "B", "C", "D"]:
                print("DEBUG: Correct answer format mismatch")
                return False
        return True

    def validate_no_duplicates(quiz):
        seen = set()
        for q in quiz["questions"]:
            text = q.get("question", "").strip().lower()
            if text in seen: return False
            seen.add(text)
        return True
    
    def validate_bloom_depth(quiz):
        banned = ["what is", "define", "what is the purpose", "what is meant"]
        valid_count = 0
        for q in quiz["questions"]:
            txt = q["question"].strip().lower()
            if any(txt.startswith(b) for b in banned): continue
            #   RELAXED CHOKEHOLD: Lowered from 8 words to 5 words so smart questions pass
            if len(txt.split()) >= 5: valid_count += 1
        #   RELAXED CHOKEHOLD: Lowered from 0.7 (70%) to 0.6 (60%)
        return valid_count >= int(len(quiz["questions"]) * 0.6)

    # =====================================
    # 6 GENERATION LOOP
    # =====================================
    MAX_ATTEMPTS = 5
    last_quiz = None

    for attempt in range(MAX_ATTEMPTS):
        print(f"Quiz Attempt {attempt+1}")
        
        #   THE SILVER BULLET APPLIED HERE: require_json=True
        raw_output = rag_engine.generate(prompt, require_json=True)
        
        # Basic cleaning of non-printable characters
        cleaned = re.sub(r'[\x00-\x1f\x7f]', '', raw_output)
        json_block = extract_json(cleaned)

        if not json_block:
            print("No JSON detected")
            continue

        try:
            parsed = json.loads(json_block)
            quiz = parsed.get("quiz")
            last_quiz = quiz # Store in case of soft_success
        except Exception as e:
            print(f"JSON parsing failed: {e}")
            continue

        if not quiz:
            print("Missing quiz key")
            continue

        # Sequential Validations
        if not validate_structure(quiz):
            print("Structure validation failed")
            continue
        if not validate_no_duplicates(quiz):
            print("Duplicate questions detected")
            continue
        if not validate_bloom_depth(quiz):
            print("Bloom depth validation failed")
            continue

        print("VALID QUIZ GENERATED")

        final_questions = quiz.get("questions", [])[:MCQ_COUNT]

        return jsonify({
            "status": "success",
            "concept": description,
            "bloom_level": "UNDERSTAND",
            # "question_count": len(quiz.get("questions", [])),
            "question_count": len(final_questions),
            # "generated_questions": quiz.get("questions", []), # MUST MATCH JAVA NAME
            "generated_questions": final_questions, # Matches Java DTO!
            "confidence": confidence,
            "retrieval_mode": mode
        })

    # =====================================
    # 7 FALLBACK
    # =====================================
    print("⚠ Returning last generated quiz despite validation issues.")

    fallback_questions = last_quiz.get("questions", [])[:MCQ_COUNT] if last_quiz else []


    return jsonify({
        "status": "soft_success",
        "concept": description,
        "bloom_level": "UNDERSTAND",
        "question_count": len(fallback_questions),
        "generated_questions": fallback_questions, # Matches Java DTO!
        "confidence": confidence,
        "retrieval_mode": mode
    })

    # return jsonify({
    #     "status": "soft_success",
    #     "confidence": confidence,
    #     "retrieval_mode": mode,
    #     "blueprint": {"total_marks": TOTAL_MARKS, "mcq_count": MCQ_COUNT},
    #     "quiz": last_quiz if last_quiz else {}
    # })


@app.route("/generate-reference", methods=["POST"])
def generate_reference():

    data = request.json
    course_id = data.get("course_id")
    question = data.get("question", "")
    bloom_level = data.get("bloom_level", "Understand")

    if not question:
        return jsonify({"error": "Question is required"}), 400

    result = generate_reference_answer(
        course_id=course_id,
        question=question,
        bloom_level=bloom_level
    )

    return jsonify(result)

# @app.route("/evaluate-answer", methods=["POST"])
# def evaluate_answer_api():

#     data = request.json
#     print("Evaluate answer hit")
#     student_answer = data.get("student_answer")
#     reference_answer = data.get("reference_answer")
#     bloom_level = data.get("bloom_level", "Understand")

#     print("Student Answer:", student_answer)
#     print("Reference Answer:", reference_answer)

#     if not student_answer or not reference_answer:
#         return jsonify({"status": "failed", "message": "Missing data"}), 400

#     # -------------------------------
#     # SBERT Similarity
#     # -------------------------------
#     emb_student = rag_engine.model.encode(student_answer, convert_to_tensor=True)
#     emb_reference = rag_engine.model.encode(reference_answer, convert_to_tensor=True)

#     similarity = util.cos_sim(emb_student, emb_reference).item()
#     similarity = max(0.0, float(similarity))

#     word_count = len(student_answer.split())

#     # -------------------------------
#     # HARD OFF-TOPIC GUARD
#     # -------------------------------
#     if similarity < 0.55:
#         final_score = round(similarity * 35, 2)

#         return jsonify({
#             "status": "success",
#             "score": final_score,
#             "feedback": "Answer is off-topic or conceptually incorrect."
#         })

#     # -------------------------------
#     # Depth Adjustment
#     # -------------------------------
#     if word_count < 5:
#         similarity *= 0.5
#     elif word_count < 10:
#         similarity *= 0.75

#     # -------------------------------
#     # Bloom Level Cap
#     # -------------------------------
#     bloom_caps = {
#         "Remember": 0.7,
#         "Understand": 0.85,
#         "Apply": 1.0,
#         "Analyze": 1.0,
#         "Evaluate": 1.0,
#         "Create": 1.0
#     }

#     similarity = min(similarity, bloom_caps.get(bloom_level, 1.0))

#     final_score = round(similarity * 100, 2)

#     # -------------------------------
#     # Feedback
#     # -------------------------------
#     if final_score >= 85:
#         feedback = "Excellent answer."
#     elif final_score >= 65:
#         feedback = "Good answer but needs refinement."
#     elif final_score >= 45:
#         feedback = "Partial understanding."
#     else:
#         feedback = "Weak or incorrect answer."

#     return jsonify({
#         "status": "success",
#         "score": final_score,
#         "feedback": feedback
#     })

# @app.route("/evaluate-answer", methods=["POST"])
# def evaluate_answer_api():
#     data = request.json
#     student_answer = data.get("student_answer", "").strip()
#     reference_answer = data.get("reference_answer", "").strip()
    
#     # 1. THE SCIENTIFIC PART: Cosine Similarity (SBERT)
#     emb_student = rag_engine.model.encode(student_answer, convert_to_tensor=True)
#     emb_reference = rag_engine.model.encode(reference_answer, convert_to_tensor=True)
#     similarity = util.cos_sim(emb_student, emb_reference).item()
    
#     # 2. THE LOGIC PART: AI Verification
#     # If the math says "Low Score" (e.g., < 0.7), ask the AI if the LOGIC is actually correct.
#     if similarity < 0.7 and len(student_answer) > 5:
#         verify_prompt = f"""
#         Compare these two coding solutions.
#         Reference: {reference_answer}
#         Student: {student_answer}
        
#         Are they logically identical even if the variable names/spacing are different?
#         Return ONLY 'YES' or 'NO'.
#         """
#         ai_verification = rag_engine.generate(verify_prompt).strip().upper()
        
#         if "YES" in ai_verification:
#             # If AI says logic is good, "Boost" the similarity score!
#             similarity = 0.95 

#     # 3. Final Scoring
#     final_score = round(similarity * 100, 2) # Scale to 10 for your Java logic
    
#     return jsonify({
#         "status": "success",
#         "score": final_score,
#         "feedback": "Logic verified via Hybrid NLP-LLM Engine." if similarity > 0.8 else "Partial match."
#     })
# @app.route("/evaluate-answer", methods=["POST"])
# def evaluate_answer_api():
#     data = request.json
#     student_answer = data.get("student_answer", "").strip()
#     reference_answer = data.get("reference_answer", "").strip()
    
#     if not student_answer or not reference_answer:
#         return jsonify({"status": "failed", "message": "Missing data"}), 400

#     # 1. MATHEMATICAL SIMILARITY (The Foundation)
#     emb_student = rag_engine.model.encode(student_answer, convert_to_tensor=True)
#     emb_reference = rag_engine.model.encode(reference_answer, convert_to_tensor=True)
#     similarity = util.cos_sim(emb_student, emb_reference).item()
    
#     # 2. LOGICAL VERIFICATION (The "Brain")
#     # We trigger this for any substantial answer to ensure the student isn't punished 
#     # for using different words or modern logic.
#     if len(student_answer) > 5:
#         # THE UNIVERSAL PROFESSOR PROMPT: Works for Code, Theory, or Math.
#         verify_prompt = f"""
#         [ROLE: Senior Academic Professor]
#         Compare the STUDENT ANSWER against the REFERENCE ANSWER.
        
#         REFERENCE: {reference_answer}
#         STUDENT: {student_answer}
        
#         GRADING CRITERIA:
#         1. If the student's answer is LOGICALLY correct and achieves the same goal as the reference, say 'YES'.
#         2. Be lenient: Accept different terminology, different coding styles (Streams vs Loops), or different sentence structures.
#         3. If the student answer is a valid alternative explanation or solution, say 'YES'.
#         4. ONLY say 'NO' if the answer is factually wrong, conceptually broken, or completely unrelated.
        
#         Is the student's answer logically correct? 
#         Return ONLY 'YES' or 'NO'.
#         """
        
#         ai_verification = rag_engine.generate(verify_prompt).strip().upper()
        
#         # 🕵️ CRITICAL DEBUG LOG: This will show you exactly why a score failed in your terminal
#         print(f"\n--- Grading Check ---")
#         print(f"Math Score: {similarity:.4f}")
#         print(f"AI Decision: {ai_verification}")
#         print(f"Student: {student_answer[:50]}...")
        
#         #  THE "OVERRIDE" RULE: If the Professor says YES, the Math doesn't matter!
#         if "YES" in ai_verification:
#             # We jump the score to 96% immediately.
#             similarity = max(similarity, 0.96)
#         else:
#             # If the AI says NO, but SBERT similarity was high, we still give the math score
#             # but we don't boost it.
#             pass

#     # 3. FINAL CALCULATION (Scaled for Java 0-100)
#     final_score = round(similarity * 100, 2)
    
#     # Professional Feedback Logic
#     if similarity >= 0.95:
#         feedback = "Excellent! Your logic is perfectly correct."
#     elif similarity >= 0.75:
#         feedback = "Good answer. It matches the core concepts well."
#     elif similarity >= 0.45:
#         feedback = "Partial understanding. Some technical points were missed."
#     else:
#         feedback = "Incorrect or insufficient. Please review the reference material."

#     return jsonify({
#         "status": "success",
#         "score": final_score,
#         "feedback": feedback
#     })

@app.route("/evaluate-answer", methods=["POST"])
def evaluate_answer_api():
    data = request.json
    student_answer = data.get("student_answer", "").strip()
    reference_answer = data.get("reference_answer", "").strip()
    # We need the max marks to calculate depth fairly
    max_marks = float(data.get("marks", 4)) 
    
    if not student_answer or not reference_answer:
        return jsonify({"status": "failed", "message": "Missing data"}), 400

    # 1. THE SCIENTIFIC FOUNDATION (SBERT)
    emb_student = rag_engine.model.encode(student_answer, convert_to_tensor=True)
    emb_reference = rag_engine.model.encode(reference_answer, convert_to_tensor=True)
    similarity = util.cos_sim(emb_student, emb_reference).item()
    
    original_math_score = similarity

    # 2. THE LOGIC BRAIN (AI Verification)
    if len(student_answer) > 5:
        verify_prompt = f"""
        [ROLE: Senior Academic Professor]
        Compare STUDENT vs REFERENCE for LOGICAL correctness.
        REFERENCE: {reference_answer}
        STUDENT: {student_answer}
        
        RULES:
        - If logic is correct/equivalent, say 'YES'.
        - If logic is wrong/off-topic, say 'NO'.
        Return ONLY 'YES' or 'NO'.
        """
        
        ai_verification = rag_engine.generate(verify_prompt).strip().upper()
        
        if "YES" in ai_verification:
            # 🔥 SMART DEPTH SCALING 🔥
            # Calculate how many words the student wrote
            word_count = len(student_answer.split())
            
            # Define "Expected Depth": 
            # For 1 mark, we expect ~10 words. For 12 marks, we expect ~120 words.
            expected_words = max_marks * 10 
            
            # Depth Ratio (capped at 1.0)
            depth_ratio = min(1.0, word_count / expected_words)
            
            # NEW CALCULATION:
            # Base logic score is 70% (0.70). 
            # Remaining 30% (0.30) depends on how detailed the answer is.
            similarity = 0.70 + (0.28 * depth_ratio) 
            
            # Ensure we don't punish someone who actually had a high SBERT score
            similarity = max(similarity, original_math_score)
        else:
            # If AI says NO, we stick to the low math score
            similarity = min(similarity, 0.40) 

    # 3. FINAL CALCULATION (0-100)
    final_score = round(similarity * 100, 2)
    
    # 4. PROFESSIONAL FEEDBACK
    if "YES" in locals().get('ai_verification', '') and similarity < 0.85:
        feedback = "Logic is correct, but more detailed explanation is required for full marks."
    elif similarity >= 0.90:
        feedback = "Excellent! Correct logic and sufficient depth."
    elif similarity >= 0.70:
        feedback = "Good logic, matches core concepts."
    else:
        feedback = "Logic mismatch or insufficient technical detail."

    return jsonify({
        "status": "success",
        "score": final_score,
        "feedback": feedback
    })


@app.route("/generate-midterm-blueprint", methods=["POST"])
def generate_midterm_blueprint():

    data = request.json
    course_id = data.get("course_id")
    description = data.get("description")
    total_questions = int(data.get("total_questions", 10))

    if not course_id or not description:
        return jsonify({"error": "course_id and Description required"}), 400

    # 🔹 Step 1: Retrieve relevant content using RAG
    retrieved_chunks, confidence, mode = rag_engine.retrieve(course_id,description)

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
    course_id = data.get("course_id")
    bloom_level = data.get("bloom_level", "Understand")
    count = data.get("count", 5)

    retrieved_chunks, confidence, mode = rag_engine.retrieve(course_id,concept)

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
    
    Return ONLY a valid JSON object.
    """
    #   Applied JSON safety here too just in case
    result = rag_engine.generate(prompt, require_json=True)

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


@app.route("/generate-assignment", methods=["POST"])
def generate_assignment():

    data = request.json
    course_id = data.get("course_id")
    description = data.get("description")
    total_marks = data.get("total_marks", 20)
    difficulty = data.get("difficulty", "medium")
    teacher_distribution = data.get("bloom_distribution")

    if not course_id or not description:
        return jsonify({"error": "course_id and Description required"}), 400

    retrieved_chunks, confidence, mode = rag_engine.retrieve(course_id,description)

    if not retrieved_chunks:
        return jsonify({"error": "No relevant context found"}), 400

    context = "\n\n".join(retrieved_chunks[:4])

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
    #  Auto Marks Distribution
    # ================================

    num_questions = sum(bloom_distribution.values())
    marks_per_question = total_marks // num_questions

    # ================================
    #  Build Prompt
    # ================================

    bloom_instruction = "\n".join(
        [f"{count} question(s) at {level} level"
         for level, count in bloom_distribution.items()]
    )

    prompt = f"""
Extract the core academic concepts from the context below about: {description}

CONTEXT:
{context}

Generate a descriptive assignment testing these concepts.

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
- GENERALIZATION RULE: Test the concepts, NOT the textbook's specific examples. Do NOT use specific names from the text (e.g., 'CashTill', 'Publication'). Use standard universal examples.
- TOPIC COVERAGE: If the context is missing some of the requested topics from '{description}', use your expert knowledge to include questions about them anyway.

Return format:

{{
  "assignment": {{
    "total_marks": {total_marks},
    "questions": [
      {{
        "question": "...",
        "marks": {marks_per_question},
        "bloom_level": "ANALYZE",
        "reference_answer": "A detailed ideal answer for the grading engine to use as a rubric..."
      }}
    ]
  }}
}}
"""

    #   APPLIED require_json=True
    raw_output = rag_engine.generate(prompt, require_json=True)

    try:
        parsed = json.loads(re.search(r'\{.*\}', raw_output, re.DOTALL).group(0))
    except:
        return jsonify({"error": "Invalid JSON from model"}), 500

    assignment = parsed.get("assignment")

    if not assignment:
        return jsonify({"error": "Invalid structure"}), 500

    # ================================
    #  Validate Bloom Compliance
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
# Evaluate strictly.

# Question:
# {q['question']}

# Student Answer:
# {student_answer}

# Max Marks:
# {q['marks']}

# Return JSON:
# {{
#   "score": X,
#   "feedback": "..."
# }}
# """

#         #   APPLIED require_json=True
#         raw = rag_engine.generate(prompt, require_json=True)

#         try:
#             parsed = json.loads(re.search(r'\{.*\}', raw, re.DOTALL).group(0))
#         except:
#             return jsonify({"error": "Invalid evaluation JSON"}), 500

#         score = min(parsed["score"], q["marks"])  #   cap score

#         total_score += score

#         results.append({
#             "question": q["question"],
#             "score": score,
#             "feedback": parsed["feedback"]
#         })

#     total_marks = sum(q["marks"] for q in questions)
#     percentage = round((total_score / total_marks) * 100, 2)

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

    results = []
    total_score = 0

    # Using zip to pair question objects with student strings
    for q, student_answer in zip(questions, answers):
        # 🔥 THE "PROFESSOR" PROMPT (Ensures consistency)
        prompt = f"""
        [ROLE: Senior Academic Grader]
        Question: {q['question']}
        Reference/Ideal: {q.get('ideal_answer', 'Use general domain knowledge')}
        Student Answer: {student_answer}
        Max Marks: {q['marks']}

        Task: Evaluate the student answer for logical correctness. 
        If the logic is correct but the wording is different, give full marks.
        If partially correct, give partial marks.
        
        Return ONLY valid JSON:
        {{
          "score": float,
          "feedback": "string"
        }}
        """

        raw = rag_engine.generate(prompt, require_json=True)
        
        try:
            # Cleaning the string to ensure only JSON is parsed
            clean_json = re.search(r'\{.*\}', raw, re.DOTALL).group(0)
            parsed = json.loads(clean_json)
            
            score = float(parsed.get("score", 0))
            score = min(score, q["marks"]) # Cap it
            
        except Exception as e:
            print(f"Error parsing AI response: {e}")
            score = 0
            parsed = {"feedback": "Error in AI evaluation."}

        total_score += score
        results.append({
            "question": q["question"],
            "score": score,
            "feedback": parsed.get("feedback", "No feedback provided.")
        })

    total_marks = sum(q["marks"] for q in questions)
    percentage = round((total_score / total_marks) * 100, 2) if total_marks > 0 else 0

    return jsonify({
        "total_score": total_score,
        "percentage": percentage,
        "results": results
    })

@app.route("/generate-dynamic-midterm", methods=["POST"])
def generate_dynamic_midterm():

    data = request.json
    course_id = str(data.get("course_id"))
    description = data.get("description")
    total_marks = int(data.get("total_marks", 20))

    if not course_id or not description:
        return jsonify({"error": "course_id and Description required"}), 400

    # ---------------------------------
    # RETRIEVE CONTEXT
    # ---------------------------------

    retrieved_chunks, confidence, mode = rag_engine.retrieve(course_id,description)

    if not retrieved_chunks:
        return jsonify({"error": "No relevant context found"}), 400

    random.shuffle(retrieved_chunks)

    context = "\n\n".join(retrieved_chunks[:5])

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

    print("Blueprint:", mcq_count, short_count, long_count, LONG_MARK)

    # ---------------------------------
    # PROMPT
    # ---------------------------------

    variation_seed = random.randint(1, 10000)

    #   GOD-TIER SCENARIO PROMPT INJECTED HERE FOR MIDTERMS
#     prompt = f"""
# You are an academic question paper generator.

# Extract the core academic concepts from the context below to generate a midterm paper about: {description}

# CONTEXT:
# {context}

# ========================
# PAPER REQUIREMENTS
# ========================

# Total Marks: {total_marks}

# Section Distribution:
# - Section A: {mcq_count} MCQs ({MCQ_MARK} mark each)
# - Section B: {short_count} Short Questions ({SHORT_MARK} marks each)
# - Section C: {long_count} Long Question ({LONG_MARK} marks)

# IMPORTANT RULES:

# 1. STRICTLY follow the section names:
#    - "Section A - MCQ"
#    - "Section B - Short"
#    - "Section C - Long"

# 2. Do NOT rename sections.
# 3. Do NOT create extra keys.
# 4. Do NOT wrap output inside "midterm".
# 5. Do NOT include total_marks in output.
# 6. Do NOT include explanation or commentary.
# 7. Return ONLY valid JSON.
# 8. TOPIC COVERAGE: Ensure all core topics from '{description}' are equally represented in the questions. If the context is missing a topic, use your expert CS knowledge to fill the gap.
# 9. GENERALIZATION RULE: Test the concepts, NOT the textbook's specific examples. Do NOT use specific names from the text (e.g., 'CashTill', 'Publication'). Create new, generic examples (like 'Animal/Dog' or 'Employee/Manager').
# 10. All questions must be UNIQUE.
# 11. VARIETY RULE: Every question in Section A must test a DIFFERENT concept of {description}.
# 12. NO REPETITION: Do NOT use the same scenario (like 'Vehicle' or 'Animal') more than once.
# 13. MCQs must include EXACTLY 4 options.
# 14. Marks must match exactly:
#     - MCQ = {MCQ_MARK}
#     - Short = {SHORT_MARK}
#     - Long = {LONG_MARK}

# ========================
# OUTPUT FORMAT (STRICT)
# ========================

# Return ONLY this JSON structure:

# {{
#   "sections": {{
#     "Section A - MCQ": [
#       {{
#         "question": "A developer needs to ensure only one instance of a database connection exists. Which pattern is appropriate?",
#         "marks": {MCQ_MARK},
#         "options": [
#           "A) Factory Pattern, to create multiple instances safely.",
#           "B) Singleton Pattern, to restrict instantiation to one object.",
#           "C) Observer Pattern, to notify classes of events.",
#           "D) Decorator Pattern, to wrap the connection."
#         ],
#         "correct_answer": "B",
#         "bloom_level": "UNDERSTAND"
#       }}
#     ],
#     "Section B - Short": [
#       {{
#         "question": "Explain how the Singleton pattern differs from a standard static class, using a scenario involving thread safety.",
#         "marks": {SHORT_MARK},
#         "reference_answer": "The Singleton pattern allows for lazy initialization and can implement interfaces, unlike static classes...",
#         "bloom_level": "APPLY"
#       }}
#     ],
#     "Section C - Long": [
#       {{
#         "question": "Design a complete class structure for a University Library system that handles both Physical Books and Digital Audiobooks...",
#         "marks": {LONG_MARK},
#         "reference_answer": "The system should use an abstract base class 'LibraryItem' with attributes for ID and Title...",
#         "bloom_level": "CREATE"
#       }}
#     ]
#   }}
# }}

# NO markdown.
# NO backticks.
# NO extra text.
# ONLY valid JSON.
# """

    prompt = f"""
[SYSTEM RULE: You are an Elite Academic Professor creating a HIGH-INTEGRITY professional exam.]
[CRITICAL: DO NOT GENERATE DEFINITIONS. DO NOT ASK 'WHAT IS' OR 'DIFFERENCE BETWEEN'.]
[CRITICAL: EVERY MCQ MUST BE A 2-SENTENCE PROBLEM-SOLVING SCENARIO.]
[ANTI-REPETITION RULE: DO NOT use generic examples like 'Library', 'Student', or 'Employee'.]

You are generating a unique midterm for the topic: {description}

CONTEXT FROM UPLOADED MATERIAL:
{context}

========================
SECTION C - LONG QUESTION VARIETY RULE
========================
For Section C, you MUST identify a 'Large-Scale Challenge' from the context. 
To ensure variety across different runs, pick ONE of these meta-perspectives to build the design challenge:
1. THE INTEGRATION PERSPECTIVE: How multiple sub-components from the context interact.
2. THE FAILURE PERSPECTIVE: Designing a system to handle extreme edge cases or errors mentioned in the text.
3. THE OPTIMIZATION PERSPECTIVE: Refactoring a complex process for efficiency or scalability based on the book's rules.
4. THE SECURITY/INTEGRITY PERSPECTIVE: Ensuring data or logic remains consistent under heavy use.

[CRITICAL: If Run 1 focused on 'Integration', Run 2 MUST focus on 'Failure' or 'Optimization'. Rotate the focus!]

========================
SECTION RULES
========================
- Section A - MCQ: {mcq_count} unique technical scenarios requiring a professional decision.
- Section B - Short: {short_count} implementation tasks (code logic, formula application, or bug fixing).
- Section C - Long: 1 comprehensive System/Process Design challenge (150+ word grading rubric).

========================
STRICT SOURCE INTEGRITY
========================
- Use technical keywords and specific methodologies found ONLY in the provided CONTEXT.
- If the context mentions specific algorithms, class names, or business rules, use them.
- Ensure the "reference_answer" is detailed enough for a strict AI grader.

========================
OUTPUT FORMAT (STRICT JSON)
========================
{{
  "sections": {{
    "Section A - MCQ": [
      {{
        "question": "...",
        "marks": {MCQ_MARK},
        "options": ["A) ...", "B) ...", "C) ...", "D) ..."],
        "correct_answer": "...",
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

NO markdown. NO backticks. NO extra text. ONLY valid JSON.
"""

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

        print(f"Attempt {attempt+1}")

        #  APPLIED require_json=True
        raw_output = rag_engine.generate(prompt, require_json=True)
        print("RAW OUTPUT:\n", raw_output)

        cleaned = re.sub(r'[\x00-\x1f\x7f]', '', raw_output)

        json_block = extract_json(cleaned)

        if not json_block:
            continue

        try:
            parsed = json.loads(json_block)
        except:
            print(" JSON Parse Failed")
            continue

        sections = parsed.get("sections")

        if not sections:
            continue

        if not validate_structure(sections):
            print(" Structure mismatch")
            continue

        if not validate_no_duplicates(sections):
            print(" Duplicate questions")
            continue

        if not validate_mcq_options(sections):
            print(" MCQ options invalid")
            continue

        # sections = auto_correct_bloom(sections)
        sections = force_bloom_distribution(sections)

        if not validate_bloom_distribution(sections):
            print(" Bloom mismatch")
            continue

        if not validate_total_marks(sections):
            print(" Total marks mismatch")
            continue

        print(" VALID MIDTERM GENERATED")
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


# if __name__ == "__main__":
#     app.run(host="0.0.0.0", port=5001, debug=True, use_reloader=False)
if __name__ == "__main__":
    # K8s will pass the port, or default to 5001
    port = int(os.environ.get("PORT", 5001))
    # Production mein debug=False rakho safety ke liye
    debug_mode = os.environ.get("FLASK_DEBUG", "False").lower() == "true"
    
    app.run(host="0.0.0.0", port=port, debug=debug_mode, use_reloader=False)