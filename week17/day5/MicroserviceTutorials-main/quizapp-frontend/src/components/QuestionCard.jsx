export default function QuestionCard({ question, onAnswer }) {
  return (
    <div style={{
      border: "1px solid #ccc",
      padding: "20px",
      borderRadius: "10px",
      marginBottom: "20px",
    }}>
      <h2>{question.questionTitle}</h2>

      <div style={{ display: "flex", flexDirection: "column", gap: "10px", marginTop: "20px" }}>
        <button onClick={() => onAnswer(question.option1)}>
          {question.option1}
        </button>

        <button onClick={() => onAnswer(question.option2)}>
          {question.option2}
        </button>

        <button onClick={() => onAnswer(question.option3)}>
          {question.option3}
        </button>

        <button onClick={() => onAnswer(question.option4)}>
          {question.option4}
        </button>
      </div>
    </div>
  );
}
