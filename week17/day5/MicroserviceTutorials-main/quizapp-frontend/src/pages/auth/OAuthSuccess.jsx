import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";

export default function OAuthSuccess() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const token = searchParams.get("token");
    if (token) {
      localStorage.setItem("authToken", token);
      localStorage.setItem("role", "STUDENT"); // Default role if not passed
      
      // Clean URL and go to dashboard
      navigate("/student/dashboard", { replace: true });
    } else {
      navigate("/login");
    }
  }, [searchParams, navigate]);

  return <div style={{textAlign: "center", marginTop: "50px"}}>Logging you in...</div>;
}