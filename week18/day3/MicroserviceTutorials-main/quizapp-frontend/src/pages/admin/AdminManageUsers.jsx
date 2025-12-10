import { useEffect, useState } from "react";
import API from "../../api/api";
import "./admin.css";

export default function AdminManageUsers() {
  const [users, setUsers] = useState([]);
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState("All");

  const loadUsers = async () => {
    try {
      const res = await API.get("/admin/users");
      setUsers(res.data);
    } catch (err) {
      console.error("Failed to load users:", err);
    }
  };

  useEffect(() => {
    loadUsers();
  }, []);

  const promote = async (id) => {
    await API.put(`/admin/users/promote/${id}`);
    alert("User promoted to TEACHER");
    loadUsers();
  };

  const deleteUser = async (id) => {
    if (!window.confirm("Delete user permanently?")) return;
    await API.delete(`/admin/users/${id}`);
    loadUsers();
  };

  /** SAFELY FILTER USERS (Prevents null errors) */
  const filtered = users.filter((u) => {
    const name = u.fullName || "";           // SAFE
    const email = u.email || "";             // SAFE
    const role = u.role || "UNKNOWN";        // SAFE fallback

    const matchesSearch =
      name.toLowerCase().includes(search.toLowerCase()) ||
      email.toLowerCase().includes(search.toLowerCase());

    const matchesFilter =
      filter === "All" || role.toUpperCase() === filter.toUpperCase();

    return matchesSearch && matchesFilter;
  });

  return (
    <div className="container admin-container">
      <h1 className="admin-title">👥 Manage Users</h1>

      {/* Search & Filter */}
      <div className="filter-box">
        <input
          className="admin-input"
          placeholder="Search by name or email..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />

        <select
          className="admin-input"
          value={filter}
          onChange={(e) => setFilter(e.target.value)}
        >
          <option value="All">All Roles</option>
          <option value="STUDENT">Students</option>
          <option value="TEACHER">Teachers</option>
        </select>
      </div>

      {/* Users Table */}
      <div className="question-table">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Full Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Google User</th>
              <th>Actions</th>
            </tr>
          </thead>

          <tbody>
            {filtered.length === 0 && (
              <tr>
                <td colSpan="6" style={{ textAlign: "center", padding: "20px" }}>
                  No users found.
                </td>
              </tr>
            )}

            {filtered.map((u) => (
              <tr key={u.id}>
                <td>{u.id}</td>
                <td>{u.fullName || "Unknown"}</td>
                <td>{u.email}</td>
                <td>{u.role || "UNKNOWN"}</td>
                <td>{u.googleUser ? "Yes" : "No"}</td>
                <td>
                  {u.role === "STUDENT" && (
                    <button className="edit-btn" onClick={() => promote(u.id)}>
                      Promote
                    </button>
                  )}

                  <button className="delete-btn" onClick={() => deleteUser(u.id)}>
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
