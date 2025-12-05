import { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import API from "../../api/api";
import DarkModeToggle from "../../components/DarkModeToggle";
import "./student.css";

export default function StudentDashboard() {
  const [quizzes, setQuizzes] = useState([]);
  const [scores, setScores] = useState([]);
  
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const loadQuizzes = async () => {
    try {
      const res = await API.get("/quiz");
      setQuizzes(res.data);
    } catch (error) {
      console.error("Error loading quizzes", error);
    }
  };

  const loadMyScores = async () => {
    try {
      const response = await API.get('/student/myscores');
      setScores(response.data);
    } catch (error) {
       console.error("Error loading scores:", error);
    }
  };

 useEffect(() => {
    // 1. Get Token AND Role from URL
    const urlToken = searchParams.get("token");
    const urlRole = searchParams.get("role");

    if (urlToken) {
      // Save Token
      localStorage.setItem("authToken", urlToken);
      
      // Save Role (CRITICAL FIX)
      if (urlRole) {
        localStorage.setItem("role", urlRole);
      }

      // Check if user is actually a TEACHER
      if (urlRole === "TEACHER") {
        // Redirect to Admin Dashboard immediately
        window.location.href = "/admin"; 
        return; 
      }

      // If Student, clean URL and stay here
      navigate("/student/dashboard", { replace: true });
    }

    loadQuizzes();
    loadMyScores();
    
  }, [searchParams, navigate]);
  // --- NEW: SECURE DOWNLOAD FUNCTION ---
  const handleDownload = async (reportUrl, quizId) => {
    try {
      // 1. Fetch file as a "blob" (binary data) using our secure API client
      const response = await API.get(reportUrl, {
        responseType: 'blob', 
      });

      // 2. Create a temporary download link
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `Quiz-Report-${quizId}.txt`);
      
      // 3. Trigger download and cleanup
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
      
    } catch (error) {
      console.error("Download failed", error);
      alert("Failed to download. Please try again.");
    }
  };

  const hasProgress = (quizId) => {
    return localStorage.getItem(`quiz-progress-${quizId}`) !== null;
  };

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
      
      <div className="dashboard-header">
        <div className="header-actions">
          <DarkModeToggle />
        </div>
        <h1 className="dashboard-title">
          Welcome, <span className="highlight-text">Student</span> 👋
        </h1>
        <p className="dashboard-subtitle">
          Start a quiz, improve your skills, and track your progress!
        </p>
      </div>

      <section>
        <h2 className="section-title">📘 Available Quizzes</h2>
        <div className="quiz-grid">
          {quizzes.map((q) => {
            const continueAvailable = hasProgress(q.id);
            const progress = getProgressPercent(q.id, q.numQuestions);

            return (
              <div className="quiz-card-student" key={q.id}>
                <div>
                    <h3>{q.title}</h3>
                    <div className="badge-row">
                      <span className="badge badge-blue">{q.categoryName}</span>
                      <span className="badge badge-green">{q.numQuestions} Qs</span>
                    </div>

                    {continueAvailable && (
                      <div className="progress-small">
                        <div className="progress-bar-small" style={{ width: progress + "%" }}></div>
                      </div>
                    )}
                </div>

                <div style={{ display: "flex", gap: 10, marginTop: "20px" }}>
                  <button
                    className="btn start-btn"
                    onClick={() => navigate(`/quiz/${q.id}`)}
                  >
                    {continueAvailable ? "Restart" : "Start Quiz →"}
                  </button>

                  {continueAvailable && (
                    <button
                      className="btn"
                      style={{ background: "#f59e0b", color: "white" }}
                      onClick={() => navigate(`/quiz/${q.id}`)}
                    >
                      Resume ({progress}%)
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </section>

      <section style={{ marginTop: "50px" }}>
        <h2 className="section-title">📊 Your Performance History</h2>
        
        <div className="score-table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Quiz ID</th>
                <th>Score</th>
                <th>Total</th>
                <th>Status</th>
                <th>Report</th>
              </tr>
            </thead>
            <tbody>
              {scores.map((s) => (
                <tr key={s.id}>
                  <td><span style={{ fontFamily: 'monospace', fontWeight: 'bold' }}>#{s.quizId}</span></td>
                  <td>
                    <span style={{ fontWeight: "700", color: s.score >= s.total / 2 ? "#16a34a" : "#dc2626" }}>
                      {s.score}
                    </span>
                  </td>
                  <td>{s.total}</td>
                  <td>
                    <span className={`status-badge ${s.score >= s.total / 2 ? 'status-pass' : 'status-fail'}`}
                          style={{ padding: "4px 8px", borderRadius: "6px", background: s.score >= s.total / 2 ? "#dcfce7" : "#fee2e2", color: s.score >= s.total / 2 ? "#166534" : "#991b1b", fontSize: "0.85rem", fontWeight: "600" }}>
                        {s.score >= s.total / 2 ? "Passed" : "Failed"}
                    </span>
                  </td>
                  <td>
                    {/* CHANGED TO BUTTON WITH ONCLICK */}
                    {s.reportUrl ? (
                        <button 
                            className="download-btn" 
                            onClick={() => handleDownload(s.reportUrl, s.quizId)}
                            style={{ cursor: "pointer", border: "none", fontFamily: "inherit" }}
                        >
                           <span>⬇</span> Download
                        </button>
                    ) : (
                        <span style={{ color: "#94a3b8", fontSize: "0.85rem" }}>Generating...</span>
                    )}
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