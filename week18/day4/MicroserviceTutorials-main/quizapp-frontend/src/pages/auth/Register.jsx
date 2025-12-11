import { useState } from "react";
import { Link } from "react-router-dom"; 
import API from "../../api/api";
import "./auth.css";
import googleIcon from "../../assets/google.svg"; 

export default function Register() {
  // 1. Add 'role' to the initial state (Default to STUDENT)
  const [form, setForm] = useState({ 
    fullName: "", 
    email: "", 
    password: "", 
    role: "STUDENT" 
  });
  
  const [error, setError] = useState("");
  const [successMsg, setSuccessMsg] = useState("");

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const registerUser = async () => {
    setError("");
    setSuccessMsg("");

    if (!form.fullName || !form.email || !form.password) {
        setError("Please fill in all fields.");
        return;
    }

    try {
      console.log("Sending Data:", form); // You will see 'role' here now
      const res = await API.post("/auth/register", form);
      setSuccessMsg(res.data); 
      setForm({ fullName: "", email: "", password: "", role: "STUDENT" });

    } catch (err) {
      console.error(err);
      if (err.response && err.response.data) {
          setError(typeof err.response.data === "string" ? err.response.data : "Registration failed.");
      } else {
          setError("Registration failed. Please try again.");
      }
    }
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">

        <h1>Create Account</h1>
        <p className="subtitle">Join our Quiz Platform today</p>

        {error && <p className="error-msg">{error}</p>}
        
        {successMsg ? (
            <div className="success-content" style={{ textAlign: "center" }}>
                <div style={{ fontSize: "3rem", margin: "10px 0" }}>📩</div>
                <h3 style={{ color: "green", marginBottom: "10px" }}>Success!</h3>
                <p className="success-msg" style={{ fontSize: "1rem" }}>{successMsg}</p>
                
                <div style={{ marginTop: "20px" }}>
                    <Link to="/login" className="auth-btn" style={{ textDecoration: 'none', display: 'inline-block' }}>
                        Proceed to Login
                    </Link>
                </div>
            </div>
        ) : (
            <>
                <input 
                    name="fullName" 
                    placeholder="Full Name" 
                    value={form.fullName}
                    onChange={handleChange} 
                />
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

                {/* --- ROLE SELECTION --- */}
                <div className="role-selection" style={{ margin: "15px 0", display: "flex", gap: "20px", justifyContent: "center" }}>
                    <label style={{ cursor: "pointer", display: "flex", alignItems: "center", gap: "5px" }}>
                        <input 
                            type="radio" 
                            name="role" 
                            value="STUDENT" 
                            checked={form.role === "STUDENT"} 
                            onChange={handleChange} 
                        />
                        Student
                    </label>
                    <label style={{ cursor: "pointer", display: "flex", alignItems: "center", gap: "5px" }}>
                        <input 
                            type="radio" 
                            name="role" 
                            value="TEACHER" 
                            checked={form.role === "TEACHER"} 
                            onChange={handleChange} 
                        />
                        Teacher
                    </label>
                </div>

                <button className="auth-btn" onClick={registerUser}>Register</button>

                <div className="divider">OR</div>

                <a className="google-btn" href="http://localhost:8765/oauth2/authorization/google">
                  <img src={googleIcon} alt="G" /> Continue with Google
                </a>

                <p className="switch-link">
                  Already have an account? <Link to="/login">Login</Link>
                </p>
            </>
        )}

      </div>
    </div>
  );
}