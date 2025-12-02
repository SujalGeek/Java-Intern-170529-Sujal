import { Link, useLocation } from "react-router-dom";

export default function Result({ score, total }) {
  return (
    <div className="container" style={{ textAlign: "center" }}>
      <h1 style={{ fontSize: "45px" }}>Quiz Completed 🎉</h1>

      <div className="card" style={{ textAlign: "center" }}>
        <h2>Your Score</h2>
        <h1 style={{ fontSize: "60px", margin: "10px 0" }}>
          {score}/{total}
        </h1>
      </div>

      <button
        className="btn"
        onClick={() => (window.location.href = "/quizzes")}
        style={{ marginTop: "30px" }}
      >
        Try Another Quiz
      </button>
    </div>
  );
}
