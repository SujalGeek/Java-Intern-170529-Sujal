import { useEffect, useState } from "react";
import API from "../../api";
import "./admin.css";

export default function AdminManageQuestions() {
  const [questions, setQuestions] = useState([]);
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState("All");

  const loadQuestions = async () => {
    const res = await API.get("/question/all");
    setQuestions(res.data);
  };

  useEffect(() => {
    loadQuestions();
  }, []);

  const deleteQuestion = async (id) => {
    if (!window.confirm("Are you sure you want to delete this question?")) return;

    await API.delete(`/question/${id}`);
    setQuestions(questions.filter((q) => q.id !== id));
  };

  const filteredQuestions = questions.filter((q) => {
    const matchesSearch = q.questionTitle.toLowerCase().includes(search.toLowerCase());
    const matchesFilter = filter === "All" || q.difficultylevel === filter;

    return matchesSearch && matchesFilter;
  });

  return (
    <div className="container admin-container">
      <h1 className="admin-title">📚 Manage All Questions</h1>

      {/* Search + Filter */}
      <div className="filter-box">
        <input
          className="admin-input"
          placeholder="Search question..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />

        <select
          className="admin-input"
          value={filter}
          onChange={(e) => setFilter(e.target.value)}
        >
          <option value="All">All Difficulty</option>
          <option value="Easy">Easy</option>
          <option value="Medium">Medium</option>
          <option value="Hard">Hard</option>
        </select>
      </div>

      <div className="question-table">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Question</th>
              <th>Category</th>
              <th>Level</th>
              <th>Right Answer</th>
              <th>Actions</th>
            </tr>
          </thead>

          <tbody>
            {filteredQuestions.map((q) => (
              <tr key={q.id}>
                <td>{q.id}</td>
                <td>{q.questionTitle}</td>
                <td>{q.category}</td>
                <td>{q.difficultylevel}</td>
                <td>{q.rightAnswer}</td>
                <td>
                  <button className="delete-btn" onClick={() => deleteQuestion(q.id)}>
                    🗑 Delete
                  </button>

                  <button className="edit-btn" onClick={() => alert("Edit panel coming soon!")}>
                    ✏ Edit
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {filteredQuestions.length === 0 && (
          <p style={{ marginTop: "20px", fontSize: "18px" }}>No questions found.</p>
        )}
      </div>
    </div>
  );
}
