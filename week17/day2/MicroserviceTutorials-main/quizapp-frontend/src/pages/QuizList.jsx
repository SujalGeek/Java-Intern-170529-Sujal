import { useEffect, useState } from "react";
import { getAllQuizzes } from "../api/api";
import { Link } from "react-router-dom";

export default function QuizList() {
  const [quizzes, setQuizzes] = useState([]);

  useEffect(() => {
    getAllQuizzes().then((res) => setQuizzes(res.data));
  }, []);

  return (
    <div className="container">
      <h1 style={{ fontSize: "40px", fontWeight: 700 }}>Available Quizzes</h1>

      {quizzes.map((quiz) => (
        <div className="card" key={quiz.id}>
          <div className="card-title">{quiz.title}</div>

          <button
            className="btn"
            onClick={() => (window.location.href = `/quiz/${quiz.id}`)}
          >
            Start Quiz →
          </button>
        </div>
      ))}
    </div>
  );
}

