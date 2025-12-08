import { useNavigate } from "react-router-dom";

export default function Logout() {
  const navigate = useNavigate();

  const logout = () => {
    localStorage.removeItem("authToken");
    localStorage.removeItem("role"); 
    navigate("/login");
  };

  return (
    <button className="logout-btn" onClick={logout}>
       Logout
    </button>
  );
}
