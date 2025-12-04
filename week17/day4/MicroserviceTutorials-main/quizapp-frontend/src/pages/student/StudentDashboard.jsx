import { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom"; // <--- ADDED THESE IMPORTS
import API from "../../api/api";
import DarkModeToggle from "../../components/DarkModeToggle";
import "./student.css";

export default function StudentDashboard() {
  const [quizzes, setQuizzes] = useState([]);
  const [scores, setScores] = useState([]);
  
  // Hooks for URL handling
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  // // Helper to get token securely
  // const getAuthHeader = () => {
  //   const token = localStorage.getItem("authToken");
  //   return token ? { Authorization: `Bearer ${token}` } : {};
  // };

const loadQuizzes = async () => {
    try {
      // CLEANER: Let api.js handle the headers automatically!
      const res = await API.get("/quiz"); 
      setQuizzes(res.data);
    } catch (error) {
      console.error("Error loading quizzes", error);
    }
  };

  // In StudentDashboard.jsx

const loadMyScores = async () => {
    try {
      // API.js now handles the token automatically!
      // No need for { headers: ... } here anymore.
      const response = await API.get('/student/myscores');
      
      setScores(response.data);
    } catch (error) {
       console.error("❌ Error loading scores:", error);
    }
  };

  useEffect(() => {
    // 1. CHECK IF GOOGLE SENT US BACK WITH A TOKEN
    const urlToken = searchParams.get("token");

    if (urlToken) {
      // Save Google Token
      localStorage.setItem("authToken", urlToken);
      
      // Remove token from URL so it looks clean (no page reload)
      navigate("/student/dashboard", { replace: true });
    }

    // 2. LOAD DATA (Now that token is definitely in LocalStorage)
    loadQuizzes();
    loadMyScores();
    
  }, [searchParams, navigate]); // Dependencies are correct

  /** Check if quiz progress exists */
  const hasProgress = (quizId) => {
    return localStorage.getItem(`quiz-progress-${quizId}`) !== null;
  };

  /** Calculate % progress */
  const getProgressPercent = (quizId, numQuestions) => {
    const saved = localStorage.getItem(`quiz-progress-${quizId}`);
    if (!saved) return 0;
    try {
      const parsed = JSON.parse(saved);
      const answeredCount = Object.keys(parsed.selected || {}).length;
      return Math.round((answeredCount / numQuestions) * 100);
    } catch {
      return 0;
    }
  };

  return (
    <div className="dashboard-container">
      {/* DASHBOARD HEADER */}
      <div className="dashboard-header" style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <div>
          <h1 className="dashboard-title"> Welcome Student</h1>
          <p className="dashboard-subtitle">
            Start a quiz, improve your skills, and track your progress!
          </p>
        </div>
        <DarkModeToggle />
      </div>

      {/* AVAILABLE QUIZZES */}
      <section>
        <h2 className="section-title">Available Quizzes</h2>
        <div className="quiz-grid">
          {quizzes.map((q) => {
            const continueAvailable = hasProgress(q.id);
            const progress = getProgressPercent(q.id, q.numQuestions);

            return (
              <div className="quiz-card-student" key={q.id}>
                <h3>{q.title}</h3>
                <div className="badge-row">
                  <span className="badge badge-blue">{q.categoryName}</span>
                  <span className="badge badge-green">{q.numQuestions} Questions</span>
                </div>

                {continueAvailable && (
                  <div className="progress-small">
                    <div className="progress-bar-small" style={{ width: progress + "%" }}></div>
                  </div>
                )}

                <div style={{ display: "flex", gap: 10 }}>
                  <button
                    className="btn start-btn"
                    onClick={() => navigate(`/quiz/${q.id}`)} // Use navigate
                  >
                    Start Quiz →
                  </button>

                  {continueAvailable && (
                    <button
                      className="btn"
                      style={{ background: "#f59e0b" }}
                      onClick={() => navigate(`/quiz/${q.id}`)} // Use navigate
                    >
                      Continue ({progress}%)
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </section>

      {/* SCORE HISTORY */}
      <section style={{ marginTop: "50px" }}>
        <h2 className="section-title">Your Past Scores</h2>
        <div className="score-table">
          <table>
            <thead>
              <tr>
                <th>Quiz ID</th>
                <th>Score</th>
                <th>Total</th>
                <th>Report</th>
              </tr>
            </thead>
            <tbody>
              {scores.length === 0 && (
                <tr>
                  <td colSpan="4" style={{ textAlign: "center", padding: "20px" }}>
                    No scores yet – take your first quiz!
                  </td>
                </tr>
              )}
              {scores.map((s) => (
                <tr key={s.id}>
                  <td>{s.quizId}</td>
                  <td>{s.score}</td>
                  <td>{s.total}</td>
                  <td>
                    <a className="download-link" href={s.reportUrl} target="_blank" rel="noreferrer">
                        Download
                    </a>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}