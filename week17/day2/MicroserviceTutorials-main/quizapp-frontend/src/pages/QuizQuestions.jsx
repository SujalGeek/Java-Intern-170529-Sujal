import { useParams } from "react-router-dom";
import { useState, useEffect } from "react";
import { getQuizById, getQuestionById } from "../api/api";

export default function QuizQuestions() {
  const { id } = useParams();

  const [questions, setQuestions] = useState([]);
  const [index, setIndex] = useState(0);
  const [selected, setSelected] = useState(null);
  const [score, setScore] = useState(0);

  // Load quiz + questions
  useEffect(() => {
    async function loadQuiz() {
      const quizRes = await getQuizById(id);
      const questionIds = quizRes.data.questionIds;

      const fullQuestions = await Promise.all(
        questionIds.map((qid) =>
          getQuestionById(qid).then((res) => res.data)
        )
      );

      setQuestions(fullQuestions);
    }

    loadQuiz();
  }, [id]);

  if (!questions.length) return <h2 style={{ textAlign: "center" }}>Loading...</h2>;

  const q = questions[index];

  // When user clicks an option
  function handleAnswer(option) {
    if (selected !== null) return; // prevent clicking multiple times

    setSelected(option);

    if (option === q.rightAnswer) {
      setScore((prev) => prev + 1);
    }

    setTimeout(() => {
      setSelected(null);
      setIndex((prev) => prev + 1);
    }, 1000);
  }

  // End of quiz screen
  if (index >= questions.length) {
    return (
      <div className="container" style={{ textAlign: "center" }}>
        <h1 style={{ fontSize: "45px", marginBottom: "20px" }}>Quiz Completed 🎉</h1>

        <div className="card" style={{ textAlign: "center" }}>
          <h2>Your Score</h2>
          <h1 style={{ fontSize: "60px", margin: "10px 0" }}>
            {score}/{questions.length}
          </h1>
        </div>

        <button
          className="btn"
          style={{ marginTop: "30px" }}
          onClick={() => (window.location.href = "/quizzes")}
        >
          Back to Quizzes
        </button>
      </div>
    );
  }

  // Progress percentage
  const progress = ((index + 1) / questions.length) * 100;

  return (
    <div className="container">

      {/* Progress Bar */}
      <div
        style={{
          width: "100%",
          height: "12px",
          background: "#e2e8f0",
          borderRadius: "10px",
          marginBottom: "30px",
          overflow: "hidden"
        }}
      >
        <div
          style={{
            width: `${progress}%`,
            height: "100%",
            background: "#2563eb",
            transition: "0.3s"
          }}
        ></div>
      </div>

      <div className="card">

        <div className="card-title" style={{ marginBottom: "20px" }}>
          {q.questionTitle}
        </div>

        {/* Options */}
        {["option1", "option2", "option3", "option4"].map((optKey) => {
          const option = q[optKey];

          let className = "option";

          if (selected !== null) {
            if (option === q.rightAnswer) className = "option correct";
            else if (option === selected) className = "option wrong";
          }

          return (
            <div
              key={optKey}
              className={className}
              onClick={() => handleAnswer(option)}
            >
              {option}
            </div>
          );
        })}
      </div>
    </div>
  );
}
