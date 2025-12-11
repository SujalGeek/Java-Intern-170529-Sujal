import { useEffect, useState, useRef } from "react"; // Import useRef
import { useSearchParams, useNavigate } from "react-router-dom";
import API from "../../api/api";
import "./auth.css";

export default function VerifyEmail() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [status, setStatus] = useState("Verifying...");
  const [isSuccess, setIsSuccess] = useState(false);
  
  // Ref to track if we already called the API
  const hasCalledAPI = useRef(false);

  useEffect(() => {
    const verify = async () => {
      const code = searchParams.get("code");
      if (!code) {
        setStatus("Invalid Verification Link.");
        return;
      }

      // Prevent double-call in React Strict Mode
      if (hasCalledAPI.current) return;
      hasCalledAPI.current = true;

      try {
        const res = await API.get(`/auth/verify?code=${code}`);
        setStatus(res.data);
        setIsSuccess(true);
      } catch (err) {
        setStatus("Verification Failed. The link might be expired or already used.");
      }
    };

    verify();
  }, [searchParams]);

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <h1>Email Verification</h1>
        <p className={isSuccess ? "success-msg" : "error-msg"}>{status}</p>
        
        {isSuccess && (
          <button className="auth-btn" onClick={() => navigate("/login")}>
            Go to Login
          </button>
        )}
      </div>
    </div>
  );
}