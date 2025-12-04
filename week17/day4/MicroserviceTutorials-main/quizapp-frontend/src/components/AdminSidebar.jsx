import { NavLink } from "react-router-dom";
import "../pages/admin/admin.css";

export default function AdminSidebar() {
  const linkClass = ({ isActive }) =>
    isActive ? "admin-link active" : "admin-link";

  return (
    <aside className="admin-sidebar">
      <div className="admin-brand">🧑‍🏫 Admin</div>

      <nav>
        <NavLink to="/admin/dashboard" className={linkClass}>Dashboard</NavLink>
        <NavLink to="/admin/create-quiz" className={linkClass}>Create Quiz</NavLink>
        <NavLink to="/admin/create-question" className={linkClass}>Add Question</NavLink>
        <NavLink to="/admin/questions" className={linkClass}>Manage Questions</NavLink>
        <NavLink to="/admin/users" className={linkClass}>Manage Users</NavLink>
        <NavLink to="/admin/scores" className={linkClass}>Scores / Leaderboard</NavLink>
      </nav>
    </aside>
  );
}
