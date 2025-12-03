import { useEffect, useState } from "react";
import API from "../../api";
import "./student.css";

export default function StudentDashboard() {
  const [quizzes, setQuizzes] = useState([]);
  const [scores, setScores] = useState([]);

  const loadQuizzes = async () => {
    const res = await API.get("/quiz");
    setQuizzes(res.data);
  };

  const loadMyScores = async () => {
    const res = await API.get("/student/myscores");
    setScores(res.data);
  };

  useEffect(() => {
    loadQuizzes();
    loadMyScores();
  }, []);

  return (
    <div className="dashboard-container">

      {/* HEADER */}
      <div className="dashboard-header">
        <h1 className="dashboard-title">👋 Welcome Student</h1>
        <p className="dashboard-subtitle">
          Start a quiz, improve your skills, and track your progress!
        </p>
      </div>

      {/* Available Quizzes */}
      <section>
        <h2 className="section-title">📘 Available Quizzes</h2>

        <div className="quiz-grid">
          {quizzes.map((q) => (
            <div className="quiz-card-student" key={q.id}>
              <h3>{q.title}</h3>

              <div className="badge-row">
                <span className="badge badge-blue">{q.categoryName}</span>
                <span className="badge badge-green">{q.numQuestions} Questions</span>
              </div>

              <button
                className="btn start-btn"
                onClick={() => (window.location.href = `/quiz/${q.id}`)}
              >
                Start Quiz →
              </button>
            </div>
          ))}
        </div>
      </section>

      {/* MY SCORES */}
      <section style={{ marginTop: "50px" }}>
        <h2 className="section-title">📊 Your Past Scores</h2>

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
                    <a className="download-link" href={s.reportUrl} target="_blank">
                      📄 Download
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
