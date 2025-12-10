import { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import API from "../../api/api";
import "./auth.css";

export default function VerifyEmail() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [status, setStatus] = useState("Verifying...");
  const [isSuccess, setIsSuccess] = useState(false);

  useEffect(() => {
    const verify = async () => {
      const code = searchParams.get("code");
      if (!code) {
        setStatus("Invalid Verification Link.");
        return;
      }

      try {
        // Call the backend endpoint
        const res = await API.get(`/auth/verify?code=${code}`);
        setStatus(res.data); // "Account Verified Successfully!"
        setIsSuccess(true);
      } catch (err) {
        setStatus("Verification Failed. The link might be expired or invalid.");
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