import { Link } from "react-router-dom";

export default function Home() {
  return (
    <div className="container" style={{ textAlign: "center" }}>
      <h1 style={{ fontSize: "50px", fontWeight: 800 }}>
        Welcome to Quiz App
      </h1>

      <p style={{ fontSize: "20px", marginBottom: "30px", color: "#475569" }}>
        Test your knowledge with interactive quizzes!
      </p>

      <button className="btn" onClick={() => window.location.href = "/quizzes"}>
        Start Quiz
      </button>
    </div>
  );
}
