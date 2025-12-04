import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import API from "../api/api"; // ✅ Use your configured API (sends token automatically)
import DarkModeToggle from "../components/DarkModeToggle";
// Removed 'axios' import because we should use API

export default function QuizQuestions() {
  const { id } = useParams(); // quiz ID
  const navigate = useNavigate();

  const [questions, setQuestions] = useState([]);
  const [current, setCurrent] = useState(0);
  const [selected, setSelected] = useState({});
  const [submitted, setSubmitted] = useState(false);

  const progressKey = `quiz-progress-${id}`;

  // -----------------------------------------
  // 🔹 FETCH QUESTIONS
  // -----------------------------------------
  useEffect(() => {
    const fetchQuestions = async () => {
      try {
        // ✅ FIX 1: Use API.get() so it sends the 'Authorization' header
        // URL matches QuizService: /quiz/get/{id}
        const response = await API.get(`/quiz/get/${id}`);
        setQuestions(response.data);
      } catch (error) {
        console.error("Error fetching questions:", error);
      }
    };
    fetchQuestions();
  }, [id]);

  // -----------------------------------------
  // SAVE PROGRESS TO LOCAL STORAGE
  // -----------------------------------------
  useEffect(() => {
    const payload = { current, selected, timestamp: Date.now() };
    localStorage.setItem(progressKey, JSON.stringify(payload));
  }, [current, selected, progressKey]);

  // Loading State
  if (questions.length === 0) {
    return (
      <div className="container" style={{ textAlign: "center", marginTop: "50px" }}>
        <h2>Loading Quiz...</h2>
      </div>
    );
  }

  const question = questions[current];

  // -----------------------------------------
  // 🔹 SELECT ANSWER
  // -----------------------------------------
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
    // Just saving to local storage (handled by useEffect) and leaving
    navigate("/student/dashboard");
  };

  // -----------------------------------------
  // 🔹 SUBMIT QUIZ (CRITICAL FIXES HERE)
  // -----------------------------------------
  const submitQuiz = async () => {
    try {
      setSubmitted(true);

      //  FIX 2: Prepare the Payload for Java
      // Java DTO expects: { quizId: Integer, answers: Map }
      const payload = {
        quizId: parseInt(id), // Ensure it is an Integer
        answers: selected     // Matches the Map<Integer, String>
      };

      console.log("🚀 Submitting Payload:", payload);

      //  FIX 3: Correct URL
      // We send this to USER-SERVICE because that is where UserQuizService lives
      // Assuming your StudentController is mapped to /student/submit
      const res = await API.post(`/student/submit`, payload); 
      
      // FIX 4: Clear local progress after success
      localStorage.removeItem(progressKey);

      // Navigate to result page with data
      navigate("/result", { 
        state: { 
            score: res.data.score, 
            total: res.data.total,
            reportUrl: res.data.reportUrl 
        } 
      });

    } catch (err) {
      console.error(" Submit failed", err);
      alert("Failed to submit. Please check console for errors.");
      setSubmitted(false);
    }
  };

  const progressPercent = ((current + 1) / questions.length) * 100;

  // -----------------------------------------
  // RENDER UI
  // -----------------------------------------
  return (
    <>
      <div className="dashboard-header" style={{ display: 'flex', justifyContent: 'space-between', padding: '20px' }}>
         <h1 style={{margin: 0}}>Quiz Mode</h1>
         <DarkModeToggle />
      </div>

      <div className="container">
        {/* HEADER BAR */}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <h2 style={{ color: "#2563eb" }}>{questions.length > 0 ? `Q${current + 1}: ${question.questionTitle}` : "Quiz"}</h2>

          <div style={{ display: "flex", gap: 10 }}>
            <button
              className="btn"
              onClick={() => {
                localStorage.removeItem(progressKey);
                navigate("/student/dashboard");
              }}
            >
              Exit & Clear
            </button>

            <button className="btn" onClick={saveAndExit}>
              💾 Save & Exit
            </button>
          </div>
        </div>

        {/* PROGRESS BAR */}
        <div className="progress-container" style={{ margin: "14px 0" }}>
          <div
            className="progress-bar"
            style={{
              width: `${progressPercent}%`,
              height: "8px",
              background: "#2563eb",
              borderRadius: "6px",
              transition: "width 0.3s ease"
            }}
          ></div>
        </div>

        {/* QUESTION CARD */}
        <div className="card">
          <div className="options-grid">
            {[question.option1, question.option2, question.option3, question.option4]
              .filter(Boolean)
              .map((opt, idx) => {
                const isSelected = selected[question.id] === opt;
                
                return (
                  <div
                    key={idx}
                    className={`option ${isSelected ? "selected" : ""}`}
                    onClick={() => handleSelect(opt)}
                    style={{
                        padding: "15px",
                        border: isSelected ? "2px solid #2563eb" : "1px solid #ddd",
                        borderRadius: "8px",
                        margin: "10px 0",
                        cursor: "pointer",
                        backgroundColor: isSelected ? "#eff6ff" : "white"
                    }}
                  >
                    {opt}
                  </div>
                );
              })}
          </div>
        </div>

        {/* NAV BUTTONS */}
        <div style={{ marginTop: 18, display: "flex", justifyContent: "center", gap: 12 }}>
          {current > 0 && <button className="btn" onClick={prev}>Previous</button>}
          
          {current < questions.length - 1 ? (
              <button className="btn" onClick={next}>Next</button>
          ) : (
              <button className="btn" onClick={submitQuiz} style={{ background: "#16a34a", color: "white" }}>
                Submit Quiz
              </button>
          )}
        </div>
      </div>
    </>
  );
}