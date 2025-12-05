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

  // 1. Fetch Questions
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

  // 2. Save Progress
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

  // 3. Handle Answer Selection
  const handleSelect = (opt) => {
    if (submitted) return;
    setSelected((prev) => ({ ...prev, [question.id]: opt }));
  };

  const next = () => {
    if (current < questions.length - 1) setCurrent(current + 1);
  };
  const prev = () => {
    if (current > 0) setCurrent(current - 1);
  };

  const saveAndExit = () => {
    navigate("/student/dashboard");
  };

  // 4. Submit Quiz
  const submitQuiz = async () => {
    try {
      setSubmitted(true);

      const payload = {
        quizId: parseInt(id),
        answers: selected
      };

      console.log("🚀 Submitting Payload:", payload);

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
      console.error("❌ Submit failed", err);
      alert("Failed to submit. Please check console for errors.");
      setSubmitted(false);
    }
  };

  const progressPercent = ((current + 1) / questions.length) * 100;

  return (
    <div className="dashboard-container">
      {/* HEADER: Clean and Aligned */}
      <div className="dashboard-header" style={{ marginBottom: '30px', textAlign: 'left', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
         <div>
            <h1 className="dashboard-title" style={{ fontSize: '2rem' }}>Quiz Mode</h1>
            <p className="dashboard-subtitle">Stay focused and good luck!</p>
         </div>
         <DarkModeToggle />
      </div>

      {/* QUIZ CONTROLS */}
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: '20px' }}>
        <h2 className="section-title" style={{ margin: 0, fontSize: '1.2rem' }}>
            {questions.length > 0 ? `Question ${current + 1} of ${questions.length}` : "Quiz"}
        </h2>

        <div style={{ display: "flex", gap: 10 }}>
          <button
            className="btn"
            style={{ background: "#ef4444", color: "white", width: 'auto' }}
            onClick={() => {
              localStorage.removeItem(progressKey);
              navigate("/student/dashboard");
            }}
          >
            Quit
          </button>

          <button className="btn" style={{ background: "#6366f1", color: "white", width: 'auto' }} onClick={saveAndExit}>
            💾 Save & Exit
          </button>
        </div>
      </div>

      {/* PROGRESS BAR */}
      <div className="progress-small">
        <div
          className="progress-bar-small"
          style={{ width: `${progressPercent}%`, transition: "width 0.3s ease" }}
        ></div>
      </div>

      {/* QUESTION CARD - Uses CSS classes for Dark Mode support */}
      <div className="card">
        <h2 style={{ fontSize: '1.4rem', marginBottom: '20px', lineHeight: '1.5' }}>
            {question.questionTitle}
        </h2>

        <div className="options-grid">
          {[question.option1, question.option2, question.option3, question.option4]
            .filter(Boolean)
            .map((opt, idx) => {
              const isSelected = selected[question.id] === opt;
              
              return (
                <div
                  key={idx}
                  className={`option-card ${isSelected ? "selected" : ""}`}
                  onClick={() => handleSelect(opt)}
                >
                  {opt}
                </div>
              );
            })}
        </div>
      </div>

      {/* NAVIGATION BUTTONS */}
      <div style={{ marginTop: 30, display: "flex", justifyContent: "center", gap: 20 }}>
        <button 
            className="btn" 
            onClick={prev} 
            disabled={current === 0}
            style={{ width: '150px', opacity: current === 0 ? 0.5 : 1 }}
        >
            Previous
        </button>
        
        {current < questions.length - 1 ? (
            <button className="btn start-btn" onClick={next} style={{ width: '150px' }}>
                Next
            </button>
        ) : (
            <button className="btn" onClick={submitQuiz} style={{ background: "#16a34a", color: "white", width: '150px' }}>
              Submit Quiz
            </button>
        )}
      </div>
    </div>
  );
}