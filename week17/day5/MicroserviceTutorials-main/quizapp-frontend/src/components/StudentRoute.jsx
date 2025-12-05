import { Navigate } from "react-router-dom";

export default function StudentRoute({ children }) {
  const token = localStorage.getItem("authToken");
  const role = localStorage.getItem("role");

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  // If user is TEACHER, send them to admin (don't let them see student dashboard)
  if (role === "TEACHER") {
    return <Navigate to="/admin" replace />;
  }

  return children;
}