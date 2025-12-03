import { useEffect, useState } from "react";
import API from "../../api";
import "./studentProfile.css";

export default function StudentProfile() {
  const [profile, setProfile] = useState({
    fullName: "",
    email: "",
  });

  const [message, setMessage] = useState("");

  const loadProfile = async () => {
    try {
      const res = await API.get("/auth/me");
      setProfile({
        fullName: res.data.fullName,
        email: res.data.email,
      });
    } catch (err) {
      console.error("Failed to load profile", err);
    }
  };

  const handleSave = async () => {
    try {
      const res = await API.put("/student/profile/update", profile);
      setMessage("Profile updated successfully!");
      setTimeout(() => setMessage(""), 2000);
    } catch (error) {
      console.error("Update failed");
      setMessage("Update failed!");
    }
  };

  useEffect(() => {
    loadProfile();
  }, []);

  return (
    <div className="profile-container">
      <h1 className="profile-title">👤 My Profile</h1>

      <div className="profile-card">
        <label>Full Name</label>
        <input
          type="text"
          value={profile.fullName}
          onChange={(e) => setProfile({ ...profile, fullName: e.target.value })}
        />

        <label>Email</label>
        <input type="email" value={profile.email} disabled />

        <button className="btn save-btn" onClick={handleSave}>
          Save Changes
        </button>

        {message && <p className="update-msg">{message}</p>}
      </div>
    </div>
  );
}
