import { useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import API from "../../api/api";
import "./auth.css";

export default function ResetPassword() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = searchParams.get("token");

    if (!token) {
      setError("Invalid or Missing Token.");
      return;
    }

    try {
      // Backend expects raw string body for the new password
      await API.post(`/auth/reset-password?token=${token}`, password, {
        headers: { "Content-Type": "text/plain" }
      });
      
      setMessage("Password Reset Successfully!");
      setTimeout(() => navigate("/login"), 3000); // Redirect after 3s
    } catch (err) {
      setError("Failed to reset. Token might be expired.");
    }
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <h1>Reset Password</h1>
        
        {message && <p className="success-msg">{message}</p>}
        {error && <p className="error-msg">{error}</p>}

        {!message && (
          <>
            <input
              type="password"
              placeholder="Enter new password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <button className="auth-btn" onClick={handleSubmit}>
              Set New Password
            </button>
          </>
        )}
      </div>
    </div>
  );
}