import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import API from "../api";

function QuizQuestions() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [questions, setQuestions] = useState([]);
  const [current, setCurrent] = useState(0);
  const [selected, setSelected] = useState({});
  const [submitted, setSubmitted] = useState(false);
  const [score, setScore] = useState(null);

  useEffect(() => {
    API.get(`/question/quiz/${id}`).then((res) => {
      setQuestions(res.data);
    });
  }, [id]);

  if (questions.length === 0) {
    return <div className="container"><h2>Loading...</h2></div>;
  }

  const question = questions[current];

  // Handle selecting an option
  const handleSelect = (option) => {
    if (submitted) return;
    setSelected({ ...selected, [question.id]: option });
  };

  // Handle next
  const next = () => {
    if (current < questions.length - 1) {
      setCurrent(current + 1);
    }
  };

  // Handle previous
  const prev = () => {
    if (current > 0) {
      setCurrent(current - 1);
    }
  };

  // Submit quiz
  const submitQuiz = async () => {
    const answers = {};

    questions.forEach((q) => {
      answers[q.id] = selected[q.id] || "";
    });

    const res = await API.post(`/student/submit`, {
      quizId: Number(id),
      answers,
    });

    setScore(res.data.score);
    setSubmitted(true);

    // Show result page after 1 sec (UX smooth)
    setTimeout(() => {
      navigate(`/result`, {
        state: {
          score: res.data.score,
          total: res.data.total,
          reportUrl: res.data.reportUrl
        },
      });
    }, 1000);
  };

  /* PROGRESS BAR PERCENTAGE */
  const progress = ((current + 1) / questions.length) * 100;

  return (
    <div className="container">

      {/* DARK MODE BUTTON */}
      <div className="toggle-dark">
        <div
          className="toggle-btn"
          onClick={() => document.body.classList.toggle("dark")}
        >
          🌙/☀️
        </div>
      </div>

      <h1>Quiz</h1>

      {/* PROGRESS BAR */}
      <div className="progress-container">
        <div className="progress-bar" style={{ width: `${progress}%` }}></div>
      </div>

      {/* QUESTION CARD */}
      <div className="card">
        <h2 className="card-title">{question.questionTitle}</h2>

        {/* OPTIONS */}
        {[question.option1, question.option2, question.option3, question.option4]
          .filter(Boolean)
          .map((opt, index) => {
            const isSelected = selected[question.id] === opt;

            // After submitting
            const isCorrect = submitted && opt === question.rightAnswer;
            const isWrong = submitted && isSelected && opt !== question.rightAnswer;

            return (
              <div
                key={index}
                className={`option 
                  ${isSelected ? "selected" : ""} 
                  ${isCorrect ? "correct" : ""} 
                  ${isWrong ? "wrong" : ""}
                `}
                onClick={() => handleSelect(opt)}
              >
                {opt}
              </div>
            );
          })}
      </div>

      {/* NAV BUTTONS */}
      <div style={{ marginTop: "20px" }}>
        {current > 0 && (
          <button onClick={prev} style={{ marginRight: "10px" }}>
            Previous
          </button>
        )}

        {current < questions.length - 1 && (
          <button onClick={next}>Next</button>
        )}

        {current === questions.length - 1 && (
          <button
            onClick={submitQuiz}
            style={{ background: "green", marginLeft: "10px" }}
          >
            Submit Quiz
          </button>
        )}
      </div>
    </div>
  );
}

export default QuizQuestions;
