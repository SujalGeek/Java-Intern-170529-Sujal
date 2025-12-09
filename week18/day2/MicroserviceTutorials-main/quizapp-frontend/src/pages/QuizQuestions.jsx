import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import API from "../api/api"; 
import DarkModeToggle from "../components/DarkModeToggle";

export default function QuizQuestions() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [questions, setQuestions] = useState([]);
  const [current, setCurrent] = useState(0);
  const [selected, setSelected] = useState({});
  const [submitted, setSubmitted] = useState(false);

  const progressKey = `quiz-progress-${id}`;

  // Fetch Questions
  useEffect(() => {
    const fetchQuestions = async () => {
      try {
        const response = await API.get(`/quiz/get/${id}`);
        setQuestions(response.data);
      } catch (error) {
        console.error("Error fetching questions:", error);
      }
    };
    fetchQuestions();
  }, [id]);

  // Save Progress
  useEffect(() => {
    const payload = { current, selected, timestamp: Date.now() };
    localStorage.setItem(progressKey, JSON.stringify(payload));
  }, [current, selected, progressKey]);

  if (questions.length === 0) {
    return (
      <div className="dashboard-container" style={{ textAlign: "center", marginTop: "50px" }}>
        <h2 className="section-title">Loading Quiz...</h2>
      </div>
    );
  }

  const question = questions[current];

  const handleSelect = (opt) => {
    if (submitted) return;
    setSelected((prev) => ({ ...prev, [question.id]: opt }));
  };

  const next = () => current < questions.length - 1 && setCurrent(current + 1);
  const prev = () => current > 0 && setCurrent(current - 1);

  const saveAndExit = () => navigate("/student/dashboard");

  // Submit Quiz
  const submitQuiz = async () => {
    try {
      setSubmitted(true);

      const payload = {
        quizId: parseInt(id),
        answers: selected
      };

      const res = await API.post(`/student/submit`, payload);

      localStorage.removeItem(progressKey);

      navigate("/result", { 
        state: { 
          score: res.data.score, 
          total: res.data.total, 
          reportUrl: res.data.reportUrl 
        }
      });

    } catch (err) {
      console.error("Submit failed", err);
      alert("Failed to submit. Check console.");
      setSubmitted(false);
    }
  };

  // Progress Bar
  const progressPercent = ((current + 1) / questions.length) * 100;

  return (
    <div className="dashboard-container">

      {/* HEADER */}
      <div className="dashboard-header"
        style={{ marginBottom: "30px", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        
        <div>
          <h1 className="dashboard-title" style={{ fontSize: "2rem" }}>Quiz Mode</h1>
          <p className="dashboard-subtitle">Stay focused and good luck!</p>
        </div>

        <DarkModeToggle />
      </div>

      {/* TOP CONTROLS */}
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 20 }}>
        
        <h2 className="section-title" style={{ margin: 0 }}>
          Question {current + 1} of {questions.length}
        </h2>

        <div style={{ display: "flex", gap: 10 }}>
          <button
            className="btn"
            style={{ background: "#ef4444" }}
            onClick={() => {
              localStorage.removeItem(progressKey);
              navigate("/student/dashboard");
            }}
          >
            Quit
          </button>

          <button 
            className="btn" 
            style={{ background: "#6366f1" }} 
            onClick={saveAndExit}
          >
            💾 Save & Exit
          </button>
        </div>
      </div>

      {/* PROGRESS BAR */}
      <div className="progress-container">
        <div 
          className="progress-bar"
          style={{ width: `${progressPercent}%` }}
        />
      </div>

      {/* QUESTION CARD */}
      <div className="card">
        <h2 className="card-title">{question.questionTitle}</h2>

        {[
          question.option1,
          question.option2,
          question.option3,
          question.option4
        ]
        .filter(Boolean)
        .map((opt, idx) => {
          const isSelected = selected[question.id] === opt;

          return (
            <div
              key={idx}
              className={`option ${isSelected ? "selected" : ""}`}
              onClick={() => handleSelect(opt)}
            >
              {opt}
            </div>
          );
        })}
      </div>

      {/* BOTTOM NAVIGATION */}
      <div style={{ marginTop: 30, display: "flex", justifyContent: "center", gap: 20 }}>
        
        <button className="btn" disabled={current === 0} onClick={prev}>
          Previous
        </button>

        {current < questions.length - 1 ? (
          <button className="btn" onClick={next}>Next</button>
        ) : (
          <button 
            className="btn" 
            style={{ background: "#16a34a" }}
            onClick={submitQuiz}
          >
            Submit Quiz
          </button>
        )}
      </div>
    </div>
  );
}
