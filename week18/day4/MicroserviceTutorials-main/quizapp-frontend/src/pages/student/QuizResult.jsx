import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import API from "../../api/api"; // Import API
import "../../pages/student/student.css";

export default function QuizResult() {
  const location = useLocation();
  const navigate = useNavigate();

  const resultData = location.state || { score: 0, total: 0, reportUrl: "" };

  // --- DOWNLOAD FUNCTION ---
  const handleDownload = async () => {
    if (!resultData.reportUrl) return;
    
    try {
      const response = await API.get(resultData.reportUrl, {
        responseType: 'blob',
      });

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `Quiz-Result.txt`);
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
      
    } catch (error) {
      console.error("Download failed", error);
      alert("Failed to download report.");
    }
  };

  return (
    <div className="dashboard-container" style={{ textAlign: "center", marginTop: "80px" }}>
      <div className="quiz-card-student" style={{ maxWidth: "500px", margin: "0 auto", padding: "40px" }}>
        
        <h1 style={{ color: "#2563eb", marginBottom: "10px" }}>🎉 Quiz Completed!</h1>
        <p className="dashboard-subtitle">Here is how you performed</p>

        <div style={{ margin: "40px 0", fontSize: "24px", fontWeight: "bold" }}>
          You scored <br />
          <span style={{ fontSize: "60px", color: resultData.score >= resultData.total/2 ? "#10b981" : "#ef4444" }}>
            {resultData.score} / {resultData.total}
          </span>
        </div>

        <div style={{ display: "flex", flexDirection: "column", gap: "15px" }}>
          
          {/* Download Button (Fixed) */}
          {resultData.reportUrl && (
            <button 
              onClick={handleDownload}
              className="btn"
              style={{ background: "#6366f1", color: "white" }}
            >
              Download Report
            </button>
          )}

          <button 
            className="btn" 
            style={{ background: "#2563eb", color: "white" }}
            onClick={() => navigate("/student/dashboard")}
          >
            Back to Dashboard
          </button>
        </div>

      </div>
    </div>
  );
}