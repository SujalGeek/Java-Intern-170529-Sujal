import { NavLink, useNavigate } from "react-router-dom";
import "../pages/admin/admin.css";

export default function AdminSidebar() {
  const navigate = useNavigate();

  const logout = () => {
    localStorage.removeItem("authToken");
    navigate("/login");
  };

  return (
    <div className="admin-sidebar">
      <div className="admin-brand"> Admin Panel</div>

      <NavLink to="/admin/create-quiz" className="admin-link">Create Quiz</NavLink>
      <NavLink to="/admin/create-question" className="admin-link">Create Question</NavLink>
      <NavLink to="/admin/questions" className="admin-link">Manage Questions</NavLink>
      <NavLink to="/admin/users" className="admin-link">Manage Users</NavLink>
      <NavLink to="/admin/scores" className="admin-link">Scores</NavLink>

      {/* ---- LOGOUT BUTTON ---- */}
      <button className="admin-logout-btn" onClick={logout}>
         Logout
      </button>
    </div>
  );
}
