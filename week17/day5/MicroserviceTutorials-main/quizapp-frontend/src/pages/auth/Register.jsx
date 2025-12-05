import { useState } from "react";
import API from "../../api/api";
import "./auth.css";


export default function Register() {
  const [form, setForm] = useState({ fullName: "", email: "", password: "" });

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const registerUser = async () => {
    try {
      console.log("Sending Data:", form);
      await API.post("/auth/register", form);
      alert("Account created!");
      window.location.href = "/login";
    } catch (err) {
      alert("Registration failed");
    }
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">

        <h1>Create Account</h1>
        <p className="subtitle">Join our Quiz Platform today</p>

        <input name="fullName" placeholder="Full Name" onChange={handleChange} />
        <input name="email" placeholder="Email" onChange={handleChange} />
        <input name="password" type="password" placeholder="Password" onChange={handleChange} />

        <button className="auth-btn" onClick={registerUser}>Register</button>

        <div className="divider">OR</div>

        {/* GOOGLE LOGIN */}
        <a className="google-btn" href="http://localhost:8765/oauth2/authorization/google">
          <img src="/google.svg" alt="" /> Continue with Google
        </a>

        <p className="switch-link">
          Already have an account? <a href="/login">Login</a>
        </p>

      </div>
    </div>
  );
}
