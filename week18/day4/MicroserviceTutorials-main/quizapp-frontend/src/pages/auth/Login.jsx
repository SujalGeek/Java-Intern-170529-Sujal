import { useState } from "react";
import API from "../../api/api";
import "./auth.css";

import googleIcon from "../../assets/google.svg";

export default function Login() {
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState(""); // To show messages on screen, not alerts

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const loginUser = async (e) => {

    e.preventDefault();
    if (!form.email || !form.password) {
      setError("Please fill in both fields.");
      return;
    }

    try {
      const res = await API.post("/auth/login", form);

      // --- DEBUGGING ---
      console.log("SERVER RESPONSE:", res.data);

      // 1. EXTRACT TOKEN (Handle both Object and String cases)
      let token = "";

      if (res.data && typeof res.data === 'object' && res.data.token) {
        // Case A: Backend sent JSON { "token": "ey..." }
        token = res.data.token;
      } else if (typeof res.data === 'string') {
        // Case B: Backend sent just a String "ey..."
        token = res.data;
      }

      // 2. VALIDATE TOKEN
      if (!token) {
        console.error("Token extraction failed. Response was:", res.data);
        setError("Login failed: Server sent invalid data.");
        return;
      }

      // 3. SAVE CLEAN TOKEN
      localStorage.setItem("authToken", token);
      console.log("Saved Token to LocalStorage:", token);
      // const userRole = res.data.role || "STUDENT"; // Default to student if missing
      
      // 4. GO TO DASHBOARD (Using window.location is fine, but verify token first!)
      const userRole = res.data.role; // Backend sends "STUDENT" or "TEACHER"
      localStorage.setItem("role", userRole);
      console.log(" Saved Token & Role:", token, userRole);
      
      if (userRole === "TEACHER") {
          window.location.href = "/admin/dashboard"; // or just /admin
      } else {
          window.location.href = "/student/dashboard";
      }

    } catch (err) {
      // FIX: Check if the backend sent a specific error message
      console.error(err);
     if (err.response && err.response.data) {
          // If backend sends a string like "Account not verified..."
          if (typeof err.response.data === "string") {
              setError(err.response.data); 
          } 
          // If backend sends JSON object like { message: "..." }
          else if (err.response.data.message) {
              setError(err.response.data.message);
          } else {
              setError("Login failed. Please try again.");
          }
      } else {
          setError("Invalid credentials or Server Error");
      }
    }
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <h1>Welcome Back</h1>
        <p className="subtitle">Login to continue</p>

        {/* Show Error Message in Red */}
        {error && <p style={{ color: "red", marginBottom: "10px" }}>{error}</p>}

        <input
          name="email"
          placeholder="Email"
          value={form.email}
          onChange={handleChange}
        />
        <input
          name="password"
          type="password"
          placeholder="Password"
          value={form.password}
          onChange={handleChange}
        />

        <div className="flex justify-end mt-2 mb-4">
          <a
            href="/forgot-password"
            className="text-sm text-blue-600 hover:text-blue-800 hover:underline"
          >
            Forgot password?
          </a>
        </div>

        <button className="auth-btn" onClick={(e) => loginUser(e)}>Login</button>

        <div className="divider">OR</div>

        {/* GOOGLE LOGIN */}
        <a className="google-btn" href="http://localhost:8765/oauth2/authorization/google">
          <img src={googleIcon} alt="G" /> Sign in with Google
        </a>

        <p className="switch-link">
          New user? <a href="/register">Create account</a>
        </p>
      </div>
    </div>
  );
}