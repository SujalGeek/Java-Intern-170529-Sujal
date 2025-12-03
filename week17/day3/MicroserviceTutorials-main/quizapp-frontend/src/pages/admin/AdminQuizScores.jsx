import { useEffect, useState } from "react";
import API from "../../api";
import "./admin.css";

export default function AdminQuizScores() {
  const [quizId, setQuizId] = useState("");
  const [scores, setScores] = useState([]);

  const fetchScores = async () => {
    if (!quizId) return;
    const res = await API.get(`/admin/scores/quiz/${quizId}`);
    setScores(res.data);
  };

  const sortedScores = [...scores].sort((a, b) => b.score - a.score);

  return (
    <div className="container admin-container">
      <h1 className="admin-title">🏆 Quiz Leaderboard</h1>

      <input
        className="admin-input"
        placeholder="Enter Quiz ID"
        value={quizId}
        onChange={(e) => setQuizId(e.target.value)}
      />

      <button className="btn" onClick={fetchScores}>
        Load Scores
      </button>

      {/* Score Table */}
      {sortedScores.length > 0 && (
        <div className="question-table" style={{ marginTop: "20px" }}>
          <table>
            <thead>
              <tr>
                <th>User</th>
                <th>Score</th>
                <th>Total</th>
              </tr>
            </thead>

            <tbody>
              {sortedScores.map((s) => (
                <tr key={s.id}>
                  <td>{s.user.fullName}</td>
                  <td>{s.score}</td>
                  <td>{s.total}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {sortedScores.length === 0 && (
        <p style={{ marginTop: "20px", fontSize: "18px" }}>
          No scores found for this Quiz ID.
        </p>
      )}
    </div>
  );
}
