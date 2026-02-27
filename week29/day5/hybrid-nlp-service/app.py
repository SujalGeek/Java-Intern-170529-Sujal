from flask import Flask, request, jsonify
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

    rag_engine.index_pdf(course_id, file_path)

    return jsonify({"status": "success"})

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
    course_id = data.get("course_id")
    description = data.get("description")

    if not course_id or not description:
        return jsonify({"error": "course_id and Description required"}), 400

    # =====================================
    # 1️ RETRIEVE CONTEXT FROM INDEX
    # =====================================

    retrieved_chunks, confidence, mode = rag_engine.retrieve(course_id,description)

    if not retrieved_chunks:
        return jsonify({"error": "No relevant context found"}), 400

    context = "\n\n".join(retrieved_chunks[:3])

    # =====================================
    # 2️ QUIZ BLUEPRINT
    # =====================================

    TOTAL_MARKS = 10
    MCQ_MARK = 1
    MCQ_COUNT = TOTAL_MARKS // MCQ_MARK

    print("Quiz Blueprint:", MCQ_COUNT, "MCQs")

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
    # 5 VALIDATION FUNCTIONS
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
        "quiz": quiz 
    })


@app.route("/generate-reference", methods=["POST"])
def generate_reference():

    data = request.json
    # course_id = data.get("course_id")
    question = data.get("question", "")
    bloom_level = data.get("bloom_level", "Understand")

    if not question:
        return jsonify({"error": "Question is required"}), 400

    result = generate_reference_answer(
        question=question,
        bloom_level=bloom_level
    )

    return jsonify(result)

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


@app.route("/generate-dynamic-midterm", methods=["POST"])
def generate_dynamic_midterm():

    data = request.json
    course_id = data.get("course_id")
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

    print("Blueprint:", mcq_count, short_count, long_count, LONG_MARK)

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

        raw_output = rag_engine.generate(prompt)
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


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001, debug=True, use_reloader=False)