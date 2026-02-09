# app.py

from flask import Flask, request, jsonify
from config.hybrid_engine import evaluate_answer

app = Flask(__name__)


@app.route("/health", methods=["GET"])
def health():
    return jsonify({
        "status": "UP",
        "service": "hybrid-nlp-service"
    })


@app.route("/grade", methods=["POST"])
def grade_answer():
    data = request.json

    student_answer = data.get("student_answer", "")
    teacher_answer = data.get("teacher_answer", [])

    if isinstance(teacher_answer, str):
        reference_answers = [teacher_answer]
    else:
        reference_answers = teacher_answer

    keywords = data.get("keywords", [])
    bloom_level = data.get("bloom_level", "Understand")
    max_score = data.get("max_score", 10)

    result = evaluate_answer(
        student_answer=student_answer,
        reference_answers=reference_answers,
        keywords=keywords,
        bloom_level=bloom_level,
        max_score=max_score
    )

    return jsonify(result)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001, debug=True)
