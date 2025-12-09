import { useEffect, useState } from "react";
import API from "../../api/api";
import "./admin.css";

export default function AdminCreateQuiz() {
  const [title, setTitle] = useState("");
  const [category, setCategory] = useState("");
  const [difficulty, setDifficulty] = useState("");

  const [questions, setQuestions] = useState([]);
  const [selectedQuestions, setSelectedQuestions] = useState([]);
  const [step, setStep] = useState(1);

  const fetchQuestions = async () => {
    const res = await API.get(
      `/question/filter?category=${category}&difficulty=${difficulty}`
    );
    setQuestions(res.data);
  };

  const toggleQuestion = (q) => {
    if (selectedQuestions.some((item) => item.id === q.id)) {
      setSelectedQuestions(selectedQuestions.filter((item) => item.id !== q.id));
    } else {
      setSelectedQuestions([...selectedQuestions, q]);
    }
  };

  const submitQuiz = async () => {
    const payload = {
      categoryName: category,
      numQuestions: selectedQuestions.length,
      title: title,
      questionIds: selectedQuestions.map((q) => q.id),
    };

    await API.post("/quiz/create", payload);

    alert("Quiz Created Successfully! 🎉");
    setTitle("");
    setCategory("");
    setDifficulty("");
    setQuestions([]);
    setSelectedQuestions([]);
    setStep(1);
  };

  return (
    <div className="container admin-container">
      <h1 className="admin-title">📝 Create New Quiz</h1>

      {/* STEP INDICATOR */}
      <div className="steps">
        <div className={step === 1 ? "step active" : "step"}>1. Quiz Info</div>
        <div className={step === 2 ? "step active" : "step"}>2. Pick Questions</div>
        <div className={step === 3 ? "step active" : "step"}>3. Review & Save</div>
      </div>

      {/* STEP 1 — BASIC INFO */}
      {step === 1 && (
        <div className="card admin-card">
          <h2 className="card-title">📘 Quiz Details</h2>

          <input
            className="admin-input"
            placeholder="Quiz Title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />

          <select
            className="admin-input"
            value={category}
            onChange={(e) => setCategory(e.target.value)}
          >
            <option value="">Select Category</option>
            <option value="Java">Java</option>
            <option value="Spring">Spring</option>
            <option value="DSA">DSA</option>
          </select>

          <select
            className="admin-input"
            value={difficulty}
            onChange={(e) => setDifficulty(e.target.value)}
          >
            <option value="">Difficulty</option>
            <option value="Easy">Easy</option>
            <option value="Medium">Medium</option>
            <option value="Hard">Hard</option>
          </select>

          <button
            className="btn"
            onClick={() => setStep(2)}
            disabled={!title || !category}
          >
            Next →
          </button>
        </div>
      )}

      {/* STEP 2 — SELECT QUESTIONS */}
      {step === 2 && (
        <div className="card admin-card">
          <h2 className="card-title">📚 Select Questions</h2>

          <button className="btn" onClick={fetchQuestions}>
            Load Questions
          </button>

          <div className="question-list">
            {questions.map((q) => (
              <div
                key={q.id}
                className={`question-item ${
                  selectedQuestions.some((item) => item.id === q.id)
                    ? "selected"
                    : ""
                }`}
                onClick={() => toggleQuestion(q)}
              >
                <strong>{q.questionTitle}</strong>
                <p style={{ opacity: 0.7, fontSize: "14px" }}>
                  Difficulty: {q.difficultylevel}
                </p>
              </div>
            ))}
          </div>

          <button className="btn" onClick={() => setStep(3)}>
            Review →
          </button>
        </div>
      )}

      {/* STEP 3 — REVIEW */}
      {step === 3 && (
        <div className="card admin-card">
          <h2 className="card-title">🔍 Review Quiz</h2>

          <h3>Title: {title}</h3>
          <h4>Category: {category}</h4>
          <h4>Total Questions: {selectedQuestions.length}</h4>

          <ul style={{ textAlign: "left" }}>
            {selectedQuestions.map((q) => (
              <li key={q.id} style={{ marginBottom: "10px" }}>
                {q.questionTitle}
              </li>
            ))}
          </ul>

          <button className="btn" onClick={submitQuiz}>
            ✔ Save Quiz
          </button>

          <button className="btn" style={{ background: "#64748b" }} onClick={() => setStep(2)}>
            ← Back
          </button>
        </div>
      )}
    </div>
  );
}
