import { useEffect, useState } from "react";
import API from "../../api/api";
import "./quizHistory.css";

export default function QuizHistory() {
  const [scores, setScores] = useState([]);

  const loadHistory = async () => {
    const res = await API.get("/student/myscores");
    setScores(res.data);
  };

  useEffect(() => {
    loadHistory();
  }, []);

  return (
    <div className="history-container">
      <h1 className="history-title">📊 My Quiz History</h1>

      <div className="history-list">
        {scores.map((s) => (
          <div key={s.id} className="history-card">
            <div className="history-info">
              <h3>Quiz #{s.quizId}</h3>
              <p>
                Score: <strong>{s.score} / {s.total}</strong>
              </p>
            </div>

            <a className="report-btn" href={s.reportUrl} target="_blank">
              📄 View Report
            </a>
          </div>
        ))}

        {scores.length === 0 && (
          <p className="no-history">No quiz attempts yet.</p>
        )}
      </div>
    </div>
  );
}
