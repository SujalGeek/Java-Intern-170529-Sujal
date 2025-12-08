import { Navigate } from "react-router-dom";

export default function StudentRoute({ children }) {
  const token = localStorage.getItem("authToken");
  const role = localStorage.getItem("role");

  // Not logged in
  if (!token) return <Navigate to="/login" replace />;

  // Teacher cannot access student pages
  if (role === "TEACHER") return <Navigate to="/admin" replace />;

  return children;
}
