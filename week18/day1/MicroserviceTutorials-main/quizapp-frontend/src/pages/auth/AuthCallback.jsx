import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";

export default function AuthCallback() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const token = searchParams.get("token");
    const role = searchParams.get("role");

    if (token) {
      // 1. Save Credentials
      localStorage.setItem("authToken", token);
      if (role) localStorage.setItem("role", role);

      // 2. Decide where to go
      if (role === "TEACHER") {
        window.location.href = "/admin"; // Force reload for admin
      } else {
        window.location.href = "/student/dashboard"; // Force reload for student
      }
    } else {
      // Failed? Go back to login
      navigate("/login");
    }
  }, [searchParams, navigate]);

  return (
    <div style={{ 
      height: "100vh", 
      display: "flex", 
      justifyContent: "center", 
      alignItems: "center", 
      fontSize: "1.5rem", 
      fontFamily: "sans-serif" 
    }}>
       Logging you in...
    </div>
  );
}