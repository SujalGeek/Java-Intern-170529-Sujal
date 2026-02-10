from flask import Flask, request, jsonify

from config.hybrid_engine import evaluate_answer
from services.question_generator import QuestionGenerator

app = Flask(__name__)

# -------------------------------------------------
# Load AI services ONCE (important for performance)
# -------------------------------------------------
question_generator = QuestionGenerator()


# -------------------------------------------------
# Health Check
# -------------------------------------------------
@app.route("/health", methods=["GET"])
def health():
    return jsonify({
        "status": "UP",
        "service": "hybrid-nlp-service"
    })


# -------------------------------------------------
# Hybrid Answer Evaluation API
# -------------------------------------------------
@app.route("/grade", methods=["POST"])
def grade_answer():
    try:
        data = request.json or {}

        student_answer = data.get("student_answer", "").strip()
        teacher_answer = data.get("teacher_answer", [])
        keywords = data.get("keywords", [])
        bloom_level = data.get("bloom_level", "Understand")
        max_score = int(data.get("max_score", 10))

        if not student_answer:
            return jsonify({"error": "student_answer is required"}), 400

        # Normalize teacher answers
        reference_answers = (
            [teacher_answer] if isinstance(teacher_answer, str) else teacher_answer
        )

        result = evaluate_answer(
            student_answer=student_answer,
            reference_answers=reference_answers,
            keywords=keywords,
            bloom_level=bloom_level,
            max_score=max_score
        )

        return jsonify(result)

    except Exception as e:
        return jsonify({"error": str(e)}), 500


# -------------------------------------------------
# AI Question Generation API (Bloom-based)
# -------------------------------------------------
@app.route("/generate-quiz", methods=["POST"])
def generate_quiz():
    try:
        data = request.json or {}

        concept = data.get("concept", "").strip()
        description = data.get("description", "").strip()
        keywords = data.get("keywords", [])
        bloom_level = data.get("bloom_level", "Understand")
        count = int(data.get("count", 3))

        # ---------- VALIDATION ----------
        if not concept:
            return jsonify({"error": "concept is required"}), 400

        if not description:
            return jsonify({"error": "description is required"}), 400

        if not isinstance(keywords, list):
            return jsonify({"error": "keywords must be a list"}), 400

        # ---------- GENERATE ----------
        questions = question_generator.generate_questions(
            concept=concept,
            description=description,
            keywords=keywords,
            bloom_level=bloom_level,
            count=count
        )

        return jsonify({
            "concept": concept,
            "bloom_level": bloom_level,
            "question_count": len(questions),
            "questions": questions
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500


# -------------------------------------------------
# Application Entry Point
# -------------------------------------------------
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001, debug=True)


# @app.route("/generate-quiz", methods=["POST"])
# def generate_quiz_endpoint():
#     try:
#         data = request.json
        
#         # 1. Get Inputs
#         context_text = data.get('context_text', '')
#         bloom_level = data.get('bloom_level', 'Remember')
#         num_questions = data.get('count', 1)

#         if not context_text or len(context_text) < 10:
#             return jsonify({"error": "Context text is too short or missing."}), 400

#         # 2. Generate
#         # We use a set to avoid duplicates if the model repeats itself
#         generated_questions = []
        
#         # If they want multiple questions, we can split text or just ask repeatedly
#         # For simplicity in this demo, we generate 1 high-quality question per request usually
#         # But let's try to fetch 'count'
        
#         questions = exam_generator.generate_quiz(context_text, count=num_questions, bloom_level=bloom_level)
        
#         return jsonify({
#             "status": "success",
#             "bloom_level": bloom_level,
#             "generated_questions": questions
#         })

#     except Exception as e:
#         return jsonify({"error": str(e)}), 500

# if __name__ == "__main__":
#     app.run(host="0.0.0.0", port=5001, debug=True)

