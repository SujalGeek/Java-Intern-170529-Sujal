import { useEffect, useState } from "react";
import API from "../../api/api";

export default function AdminDashboard() {
  const [users, setUsers] = useState([]);
  const [quizzes, setQuizzes] = useState([]);
  const [scores, setScores] = useState([]);

  useEffect(() => {
    API.get("/admin/users").then((res) => setUsers(res.data));
    API.get("/quiz").then((res) => setQuizzes(res.data));
  }, []);

  const loadScoresByQuiz = (quizId) => {
    API.get(`/admin/scores/quiz/${quizId}`).then((res) => setScores(res.data));
  };

  const loadScoresByUser = (userId) => {
    API.get(`/admin/scores/user/${userId}`).then((res) => setScores(res.data));
  };

  return (
    <div className="admin-grid">

      {/* ---------- COLUMN 1: QUIZZES ---------- */}
      <div className="admin-column">
        <h2>📘 Quizzes</h2>

        <div className="scroll-box">
          {quizzes.map((quiz) => (
            <div
              key={quiz.id}
              className="admin-card"
              onClick={() => loadScoresByQuiz(quiz.id)}
            >
              <h3>{quiz.title}</h3>
              <p>{quiz.questionIds.length} Questions</p>
            </div>
          ))}
        </div>
      </div>

      {/* ---------- COLUMN 2: USERS ---------- */}
      <div className="admin-column">
        <h2>👥 Users</h2>

        <div className="scroll-box">
          {users.map((user) => (
            <div
              key={user.id}
              className="admin-card"
              onClick={() => loadScoresByUser(user.id)}
            >
              <h3>{user.fullName}</h3>
              <p>{user.email}</p>
            </div>
          ))}
        </div>
      </div>

      {/* ---------- COLUMN 3: SCORES ---------- */}
      <div className="admin-column">
        <h2>📊 Scores</h2>

        <div className="scroll-box">
          {scores.length === 0 ? (
            <p>No scores available...</p>
          ) : (
            <table className="score-table">
              <thead>
                <tr>
                  <th>User</th>
                  <th>Quiz</th>
                  <th>Score</th>
                  <th>Total</th>
                </tr>
              </thead>
              <tbody>
                {scores.map((s, i) => (
                  <tr key={i}>
                    <td>{s.user.fullName}</td>
                    <td>{s.quizId}</td>
                    <td>{s.score}</td>
                    <td>{s.total}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

    </div>
  );
}
