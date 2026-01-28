import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Signup from './pages/SignUp';
import StudentDashboard from './pages/StudentDashboard';
import TeacherDashboard from './pages/TeacherDashboard';

// Placeholder Dashboards
// const StudentDashboard = () => <div className="p-10 text-2xl font-bold text-green-700"> Student Dashboard (Coming Soon)</div>;
// const TeacherDashboard = () => <div className="p-10 text-2xl font-bold text-blue-700">Teacher Dashboard (Coming Soon)</div>;

function App() {
  return (
    <Router>
      <Routes>
        {/* Default Redirect to Login */}
        <Route path="/" element={<Navigate to="/login" />} />
        
        {/* Auth Pages */}
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        
        {/* Dashboard Pages (Protected in future) */}
        <Route path="/student-dashboard" element={<StudentDashboard />} />
        <Route path="/teacher-dashboard" element={<TeacherDashboard />} />
      </Routes>
    </Router>
  );
}

export default App;