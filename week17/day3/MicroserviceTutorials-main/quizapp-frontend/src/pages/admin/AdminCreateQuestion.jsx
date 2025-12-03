import { useState } from "react";
import API from "../../api";
import "./admin.css";

export default function AdminCreateQuestion() {
  const [questionTitle, setQuestionTitle] = useState("");
  const [category, setCategory] = useState("");
  const [difficulty, setDifficulty] = useState("Easy");
  const [options, setOptions] = useState(["", "", "", ""]);
  const [rightAnswer, setRightAnswer] = useState("");

  const submitQuestion = async () => {
    if (!questionTitle || !category || !rightAnswer) {
      alert("Fill all fields!");
      return;
    }

    const payload = {
      questionTitle,
      option1: options[0],
      option2: options[1],
      option3: options[2],
      option4: options[3],
      rightAnswer,
      difficultylevel: difficulty,
      category,
    };

    await API.post("/question/add", payload);

    alert("Question Added Successfully!");

    setQuestionTitle("");
    setCategory("");
    setOptions(["", "", "", ""]);
    setRightAnswer("");
  };

  return (
    <div className="container admin-container">
      <h1 className="admin-title">➕ Add New Question</h1>

      <div className="card admin-card">
        <h2 className="card-title">📝 Question Details</h2>

        {/* Question Input */}
        <textarea
          className="admin-input"
          placeholder="Enter Question"
          rows={3}
          value={questionTitle}
          onChange={(e) => setQuestionTitle(e.target.value)}
        />

        {/* Category */}
        <select
          className="admin-input"
          value={category}
          onChange={(e) => setCategory(e.target.value)}
        >
          <option value="">Select Category</option>
          <option value="Java">Java</option>
          <option value="Spring">Spring</option>
          <option value="DSA">DSA</option>
        </select>

        {/* Difficulty */}
        <select
          className="admin-input"
          value={difficulty}
          onChange={(e) => setDifficulty(e.target.value)}
        >
          <option value="Easy">Easy</option>
          <option value="Medium">Medium</option>
          <option value="Hard">Hard</option>
        </select>

        {/* Options */}
        <div className="option-box">
          {options.map((opt, index) => (
            <input
              key={index}
              className="admin-input"
              placeholder={`Option ${index + 1}`}
              value={options[index]}
              onChange={(e) => {
                const newOptions = [...options];
                newOptions[index] = e.target.value;
                setOptions(newOptions);
              }}
            />
          ))}
        </div>

        {/* Right Answer */}
        <select
          className="admin-input"
          value={rightAnswer}
          onChange={(e) => setRightAnswer(e.target.value)}
        >
          <option value="">Select Correct Answer</option>
          {options.map(
            (o, i) =>
              o.trim() !== "" && (
                <option key={i} value={o}>
                  {`Option ${i + 1}: ${o}`}
                </option>
              )
          )}
        </select>

        <button className="btn" onClick={submitQuestion}>
          ✔ Add Question
        </button>
      </div>
    </div>
  );
}
