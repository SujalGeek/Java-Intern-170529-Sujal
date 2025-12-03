import { useEffect, useState } from "react";
import API from "../../api";

export default function AdminDashboard() {
  const [users, setUsers] = useState([]);
  const [quizzes, setQuizzes] = useState([]);
  const [scores, setScores] = useState([]);

  const [selectedQuiz, setSelectedQuiz] = useState(null);
  const [selectedUser, setSelectedUser] = useState(null);

  useEffect(() => {
    API.get("/admin/users").then((res) => setUsers(res.data));
    API.get("/quiz").then((res) => setQuizzes(res.data));
  }, []);

  const loadScoresByQuiz = (quizId) => {
    setSelectedUser(null);
    API.get(`/admin/scores/quiz/${quizId}`).then((res) => setScores(res.data));
  };

  const loadScoresByUser = (userId) => {
    setSelectedQuiz(null);
    API.get(`/admin/scores/user/${userId}`).then((res) => setScores(res.data));
  };

  return (
    <div className="container">
      <h1 style={{ marginBottom: "20px" }}>👨‍🏫 Admin Dashboard</h1>

      {/* QUIZ LIST */}
      <div className="card">
        <h2 className="card-title">📘 Quizzes</h2>

        {quizzes.map((quiz) => (
          <div
            key={quiz.id}
            className="quiz-card"
            onClick={() => {
              setSelectedQuiz(quiz.id);
              loadScoresByQuiz(quiz.id);
            }}
            style={{ cursor: "pointer" }}
          >
            <h3>{quiz.title}</h3>
            <p>{quiz.questionIds.length} Questions</p>
          </div>
        ))}
      </div>

      {/* USER LIST */}
      <div className="card" style={{ marginTop: "30px" }}>
        <h2 className="card-title">👥 Users</h2>

        {users.map((user) => (
          <div
            key={user.id}
            className="quiz-card"
            onClick={() => {
              setSelectedUser(user.id);
              loadScoresByUser(user.id);
            }}
            style={{ cursor: "pointer" }}
          >
            <h3>{user.fullName}</h3>
            <p>{user.email}</p>
          </div>
        ))}
      </div>

      {/* SCORE TABLE */}
      <div className="card" style={{ marginTop: "30px" }}>
        <h2 className="card-title">📊 Scores</h2>

        {scores.length === 0 && <p>No scores available...</p>}

        {scores.length > 0 && (
          <table
            style={{
              width: "100%",
              borderCollapse: "collapse",
              marginTop: "20px",
            }}
          >
            <thead>
              <tr style={{ background: "#e2e8f0" }}>
                <th style={th}>User</th>
                <th style={th}>Quiz</th>
                <th style={th}>Score</th>
                <th style={th}>Total</th>
              </tr>
            </thead>

            <tbody>
              {scores.map((s, idx) => (
                <tr key={idx}>
                  <td style={td}>{s.user.fullName}</td>
                  <td style={td}>{s.quizId}</td>
                  <td style={td}>{s.score}</td>
                  <td style={td}>{s.total}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}

const th = {
  padding: "12px",
  borderBottom: "2px solid #cbd5e1",
  textAlign: "left",
};

const td = {
  padding: "12px",
  borderBottom: "1px solid #e2e8f0",
};
