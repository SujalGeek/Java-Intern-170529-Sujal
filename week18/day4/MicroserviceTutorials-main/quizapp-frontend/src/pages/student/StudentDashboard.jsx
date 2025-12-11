import { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import  API, {GATEWAY_URL} from "../../api/api";

import DarkModeToggle from "../../components/DarkModeToggle";
import Logout from "../../components/LogoutButton";

import "./student.css";

export default function StudentDashboard() {
  const [quizzes, setQuizzes] = useState([]);
  const [scores, setScores] = useState([]);
  const [isOAuthHandled, setIsOAuthHandled] = useState(false);

  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  // ---------------------------
  // 1. Handle OAuth redirect ONCE
  // ---------------------------
  useEffect(() => {
    const urlToken = searchParams.get("token");
    const urlRole = searchParams.get("role");

    if (urlToken) {
      localStorage.setItem("authToken", urlToken);
      if (urlRole) localStorage.setItem("role", urlRole);

      if (urlRole === "TEACHER") {
        window.location.replace("/admin");
        return;
      }

      window.history.replaceState({}, "", "/student/dashboard");
      setIsOAuthHandled(true);
      return;
    }

    setIsOAuthHandled(true);
  }, []);

  // ---------------------------
  // 2. Load quizzes + scores
  // ---------------------------
  useEffect(() => {
    if (!isOAuthHandled) return;

    async function load() {
      try {
        const quizRes = await API.get("/quiz");
        const scoreRes = await API.get("/student/myscores");

        setQuizzes(quizRes.data);
        setScores(scoreRes.data);
      } catch (err) {
        console.error("Error loading dashboard", err);
      }
    }

    load();
  }, [isOAuthHandled]);

  // ---------------------------
  // DOWNLOAD TXT REPORT
  // ---------------------------
  const handleDownload = (relativeUrl, quizId) => {
    // 2. Construct the full URL pointing to Quiz Service
    // Result: http://localhost:8765/quiz-service/reports/quiz_123.txt
    const fullUrl = `${GATEWAY_URL}/user-service${relativeUrl}`;

    const link = document.createElement("a");
    link.href = fullUrl;
    link.setAttribute("target", "_blank"); // Open in new tab if download fails
    link.download = `quiz-report-${quizId}.txt`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  // ---------------------------
  // Helpers
  // ---------------------------
  const hasProgress = (id) =>
    localStorage.getItem(`quiz-progress-${id}`) !== null;

  const getProgressPercent = (id, total) => {
    const saved = localStorage.getItem(`quiz-progress-${id}`);
    if (!saved) return 0;

    const parsed = JSON.parse(saved);
    const answered = Object.keys(parsed.selected || {}).length;

    return Math.round((answered / total) * 100);
  };

  if (!isOAuthHandled) return <div>Loading...</div>;

  return (
    <div className="dashboard-container">
      <div className="header-right">
        <DarkModeToggle />
        <Logout />
      </div>

      <div className="dashboard-header">
        <h1 className="dashboard-title">
          Welcome, <span className="highlight-text">Student</span>
        </h1>
        <p className="dashboard-subtitle">
          Start a quiz, improve your skills, and track your progress!
        </p>
      </div>

      {/* QUIZZES LIST */}
      <section>
        <h2 className="section-title">📘 Available Quizzes</h2>

        <div className="quiz-grid">
          {quizzes.map((q) => {
            const progress = getProgressPercent(q.id, q.numQuestions);
            const canResume = hasProgress(q.id);

            return (
              <div className="quiz-card-student" key={q.id}>
                <div>
                  <h3>{q.title}</h3>

                  <div className="badge-row">
                    <span className="badge badge-blue">{q.categoryName}</span>
                    <span className="badge badge-green">{q.numQuestions} Qs</span>
                  </div>

                  {canResume && (
                    <div className="progress-small">
                      <div
                        className="progress-bar-small"
                        style={{ width: progress + "%" }}
                      ></div>
                    </div>
                  )}
                </div>

                <div className="quiz-actions">
                  <button
                    className="btn start-btn"
                    onClick={() => navigate(`/quiz/${q.id}`)}
                  >
                    {canResume ? "Restart" : "Start Quiz →"}
                  </button>

                  {canResume && (
                    <button
                      className="btn resume-btn"
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

      {/* SCORE HISTORY */}
      <section>
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
                  <td>#{s.quizId}</td>
                  <td>{s.score}</td>
                  <td>{s.total}</td>
                  <td>{s.score >= s.total / 2 ? "Passed" : "Failed"}</td>
                  <td>
                    {s.reportUrl ? (
                      <button
                        className="download-btn"
                        onClick={() => handleDownload(s.reportUrl, s.quizId)}
                      >
                        ⬇ Download
                      </button>
                    ) : (
                      "Generating…"
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
