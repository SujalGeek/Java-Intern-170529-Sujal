import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../api";

export default function QuizList() {
  const [quizzes, setQuizzes] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    API.get("/quiz").then((res) => {
      setQuizzes(res.data);
    });
  }, []);

  return (
    <div className="container">
      <h1 style={{ fontSize: "30px", marginBottom: "20px" }}>📚 Available Quizzes</h1>

      {quizzes.length === 0 && <h3>Loading quizzes...</h3>}

      {quizzes.map((quiz) => (
        <div
          key={quiz.id}
          className="quiz-card"
          style={{
            cursor: "pointer",
            transition: "0.2s",
          }}
          onClick={() => navigate(`/quiz/${quiz.id}`)}
        >
          <h2 className="quiz-card-title">{quiz.title}</h2>

          <p style={{ opacity: 0.7 }}>
            {quiz.questionIds.length} Questions
          </p>

          <button className="btn" style={{ marginTop: "10px" }}>
            Start Quiz →
          </button>
        </div>
      ))}
    </div>
  );
}
