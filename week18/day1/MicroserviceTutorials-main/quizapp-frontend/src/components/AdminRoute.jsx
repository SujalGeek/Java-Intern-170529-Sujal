import { Navigate } from "react-router-dom";

export default function AdminRoute({ children }) {
  // 1. Get the Role directly (just like StudentRoute)
  const role = localStorage.getItem("role");
  const token = localStorage.getItem("authToken");

  // 2. Check if logged in AND is TEACHER
  if (!token || role !== "TEACHER") {
    return <Navigate to="/" replace />;
  }

  return children;
}