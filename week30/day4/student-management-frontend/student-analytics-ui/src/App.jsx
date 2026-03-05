import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

// --- Page Imports ---
import Register from './pages/Register';
import Login from './pages/Login';
import ForgotPassword from './pages/ForgotPassword';
import StudentDashboard from './pages/StudentDashBoard';
import AttemptTask from './pages/AttemptTask'; // 🔥 New Engine
import ResultView from './dashboard/ResultView';   // 🔥 New Engine
import AdminPortal from './pages/AdminPortal'; // 🔥 New Faculty Hub
import GlobalAnalytics from './dashboard/GlobalAnalytics'; // 🔥 New Admin Hub

// 🛡️ Private Route Guard: Validates both Authentication and Role
const RoleRoute = ({ children, allowedRoles }) => {
  const isAuthenticated = localStorage.getItem('token'); // Use token for better security check
  const userRole = parseInt(localStorage.getItem('userRole'));

  if (!isAuthenticated) return <Navigate to="/login" />;
  
  // Check if user's role is within the allowed roles array (1:Admin, 2:Teacher, 3:Student)
  if (allowedRoles && !allowedRoles.includes(userRole)) {
    // Redirect based on identity if unauthorized
    if (userRole === 3) return <Navigate to="/student-dashboard" />;
    if (userRole === 2 || userRole === 1) return <Navigate to="/admin-portal" />;
    return <Navigate to="/login" />;
  }

  return children;
};

function App() {
  return (
    <Router>
      <Routes>
        {/* --- 🔓 PUBLIC ROUTES --- */}
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />

        {/* --- 🎓 STUDENT SECURE ZONE (Role 3) --- */}
        <Route 
          path="/student-dashboard" 
          element={
            <RoleRoute allowedRoles={[3]}>
              <StudentDashboard />
            </RoleRoute>
          } 
        />
        
        {/* 🔥 TASK ENGINE: Dynamic type (quiz/midterm/assignment) and ID */}
        <Route 
          path="/attempt/:type/:id" 
          element={
            <RoleRoute allowedRoles={[3]}>
              <AttemptTask />
            </RoleRoute>
          } 
        />

        {/* 🔥 RESULT ENGINE: View AI feedback for a specific attempt */}
        <Route 
          path="/result/:type/:attemptId" 
          element={
            <RoleRoute allowedRoles={[3]}>
              <ResultView />
            </RoleRoute>
          } 
        />

        {/* --- 👨‍🏫 FACULTY & ADMIN ZONE (Roles 1 & 2) --- */}
        <Route 
          path="/admin-portal" 
          element={
            <RoleRoute allowedRoles={[1, 2]}>
              <AdminPortal />
            </RoleRoute>
          } 
        />

        <Route 
          path="/global-analytics" 
          element={
            <RoleRoute allowedRoles={[1]}>
              <GlobalAnalytics />
            </RoleRoute>
          } 
        />

        {/* --- 🚦 GLOBAL TRAFFIC CONTROL --- */}
        <Route path="/" element={<Navigate to="/login" />} />
        
        {/* 404 Guard */}
        <Route path="*" element={
          <div className="h-screen flex flex-col items-center justify-center bg-[#F8FAFC] dark:bg-[#0F172A] p-20 text-center transition-colors">
            <h1 className="text-9xl font-black text-indigo-600/20 mb-4">404</h1>
            <p className="text-slate-400 font-black uppercase tracking-[0.5em]">Protocol Not Found</p>
            <button 
              onClick={() => window.history.back()}
              className="mt-10 px-8 py-4 bg-indigo-600 text-white rounded-2xl font-bold hover:bg-indigo-700 transition-all shadow-xl shadow-indigo-200 dark:shadow-none"
            >
              Initialize Return Sequence
            </button>
          </div>
        } />
      </Routes>
    </Router>
  );
}

export default App;