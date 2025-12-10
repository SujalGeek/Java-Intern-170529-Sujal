import { useState } from "react";
import API from "../../api/api";
import "./auth.css";

export default function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");

    try {
      // Call backend
      const res = await API.post(`/auth/forgot-password?email=${email}`);
      setMessage(res.data); // "Reset link sent..."
    } catch (err) {
      setError("User not found or Server Error.");
    }
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <h1>Forgot Password</h1>
        <p className="subtitle">Enter your email to receive a reset link.</p>

        {message && <p className="success-msg">{message}</p>}
        {error && <p className="error-msg">{error}</p>}

        <input
          type="email"
          placeholder="Enter your email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <button className="auth-btn" onClick={handleSubmit}>
          Send Reset Link
        </button>

        <p className="switch-link">
          <a href="/login">Back to Login</a>
        </p>
      </div>
    </div>
  );
}