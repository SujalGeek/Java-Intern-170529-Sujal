import { useLocation, useNavigate } from "react-router-dom";

export default function Result() {
  const { state } = useLocation();
  const navigate = useNavigate();

  if (!state) return <h2>No result found</h2>;

  const { score, total, reportUrl } = state;

  return (
    <div className="container">

      <div className="card" style={{ marginTop: "50px" }}>
        <h1 className="card-title"> Quiz Completed!</h1>

        {/* SCORE BADGE */}
        <div
          style={{
            fontSize: "50px",
            marginBottom: "20px",
            fontWeight: "800",
            color: score / total >= 0.5 ? "#16a34a" : "#dc2626",
          }}
        >
          {score}/{total}
        </div>

        <p style={{ fontSize: "20px", marginBottom: "30px" }}>
          {score / total >= 0.5 ? "Well done! 🎯" : "Keep practicing! 💪"}
        </p>

        {/* DOWNLOAD REPORT */}
        <a
          href={`http://localhost:8765${reportUrl}`}
          download
          className="btn"
          style={{
            background: "#0f172a",
            marginBottom: "20px",
            display: "inline-block",
          }}
        >
          📄 Download Quiz Report
        </a>

        {/* RETAKE BUTTON */}
        <button className="btn" onClick={() => navigate("/")}>
          🔁 Take Another Quiz
        </button>
      </div>
    </div>
  );
}
