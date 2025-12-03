import { useEffect, useState } from "react";
import API from "../../api";
import "./admin.css";

export default function AdminManageUsers() {
  const [users, setUsers] = useState([]);
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState("All");

  const loadUsers = async () => {
    const res = await API.get("/admin/users");
    setUsers(res.data);
  };

  useEffect(() => {
    loadUsers();
  }, []);

  const promote = async (id) => {
    await API.put(`/admin/users/promote/${id}`);
    alert("User promoted to Teacher");
    loadUsers();
  };

  const deleteUser = async (id) => {
    if (!window.confirm("Delete user permanently?")) return;
    await API.delete(`/admin/users/${id}`);
    loadUsers();
  };

  const filtered = users.filter((u) => {
    const matchesSearch =
      u.fullName.toLowerCase().includes(search.toLowerCase()) ||
      u.email.toLowerCase().includes(search.toLowerCase());

    const matchesFilter = filter === "All" || u.role === filter;

    return matchesSearch && matchesFilter;
  });

  return (
    <div className="container admin-container">
      <h1 className="admin-title">👥 Manage Users</h1>

      {/* Search + Filter */}
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

      {/* User Table */}
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
            {filtered.map((u) => (
              <tr key={u.id}>
                <td>{u.id}</td>
                <td>{u.fullName}</td>
                <td>{u.email}</td>
                <td>{u.role}</td>
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

        {filtered.length === 0 && (
          <p style={{ marginTop: "20px", fontSize: "18px" }}>
            No users found.
          </p>
        )}
      </div>
    </div>
  );
}
