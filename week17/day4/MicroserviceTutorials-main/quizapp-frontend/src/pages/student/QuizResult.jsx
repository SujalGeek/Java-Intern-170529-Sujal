import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "../../pages/student/student.css"; // Reuse your student styles

export default function QuizResult() {
  const location = useLocation();
  const navigate = useNavigate();

  // Retrieve data sent from QuizQuestions.jsx
  // If user tries to type "/result" directly, data will be null, so default to empty
  const resultData = location.state || { score: 0, total: 0, reportUrl: "" };

  return (
    <div className="dashboard-container" style={{ textAlign: "center", marginTop: "50px" }}>
      <div className="auth-card" style={{ maxWidth: "500px", margin: "0 auto" }}>
        
        <h1 style={{ color: "#2563eb" }}>🎉 Quiz Completed!</h1>
        <p className="subtitle">Here is how you performed</p>

        <div style={{ margin: "30px 0", fontSize: "24px", fontWeight: "bold" }}>
          You scored <br />
          <span style={{ fontSize: "50px", color: "#10b981" }}>
            {resultData.score} / {resultData.total}
          </span>
        </div>

        <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
          {/* Download Button */}
          {resultData.reportUrl && (
            <a 
              href={`http://localhost:8765${resultData.reportUrl}`} 
              target="_blank" 
              rel="noreferrer"
              className="btn"
              style={{ background: "#6366f1", textAlign: "center", textDecoration: "none" }}
            >
              📄 Download Report
            </a>
          )}

          {/* Back to Dashboard */}
          <button 
            className="auth-btn" 
            onClick={() => navigate("/student/dashboard")}
          >
            Back to Dashboard
          </button>
        </div>

      </div>
    </div>
  );
}