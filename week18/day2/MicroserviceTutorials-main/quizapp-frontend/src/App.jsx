import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Home from "./pages/Home";
import QuizList from "./pages/QuizList";
import QuizQuestions from "./pages/QuizQuestions";

// AUTH
import Login from "./pages/auth/Login";
import Register from "./pages/auth/Register";
import OAuthSuccess from "./pages/auth/OAuthSuccess";
import AuthCallback from "./pages/auth/AuthCallback";

// STUDENT
import StudentDashboard from "./pages/student/StudentDashboard";
import QuizHistory from "./pages/student/QuizHistory";
import StudentProfile from "./pages/student/StudentProfile";
import QuizResult from "./pages/student/QuizResult";

// ADMIN
import AdminLayout from "./components/AdminLayout";
import AdminDashboard from "./pages/admin/AdminDashboard";
import AdminCreateQuiz from "./pages/admin/AdminCreateQuiz";
import AdminCreateQuestion from "./pages/admin/AdminCreateQuestion";
import AdminManageQuestions from "./pages/admin/AdminManageQuestions";
import AdminManageUsers from "./pages/admin/AdminManageUsers";
import AdminQuizScores from "./pages/admin/AdminQuizScores";

import AdminRoute from "./components/AdminRoute";
import StudentRoute from "./components/StudentRoute";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>

        {/* PUBLIC ROUTES */}
        <Route path="/" element={<Home />} />
        <Route path="/oauth/success" element={<OAuthSuccess />} />
        <Route path="/quizzes" element={<QuizList />} />
        <Route path="/auth/callback" element={<AuthCallback />} />

        {/* AUTH ROUTES (DO NOT auto redirect) */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* STUDENT ROUTES */}
        <Route
          path="/student/dashboard"
          element={
            <StudentRoute>
              <StudentDashboard />
            </StudentRoute>
          }
        />

        <Route
          path="/quiz/:id"
          element={
            <StudentRoute>
              <QuizQuestions />
            </StudentRoute>
          }
        />

        <Route
          path="/student/history"
          element={
            <StudentRoute>
              <QuizHistory />
            </StudentRoute>
          }
        />

        <Route
          path="/student/profile"
          element={
            <StudentRoute>
              <StudentProfile />
            </StudentRoute>
          }
        />

        <Route
          path="/result"
          element={
            <StudentRoute>
              <QuizResult />
            </StudentRoute>
          }
        />

        {/* ADMIN PROTECTED ROUTES */}
        <Route
          path="/admin"
          element={
            <AdminRoute>
              <AdminLayout />
            </AdminRoute>
          }
        >
          <Route index element={<Navigate to="/admin/dashboard" />} />
          <Route path="dashboard" element={<AdminDashboard />} />
          <Route path="create-quiz" element={<AdminCreateQuiz />} />
          <Route path="create-question" element={<AdminCreateQuestion />} />
          <Route path="questions" element={<AdminManageQuestions />} />
          <Route path="users" element={<AdminManageUsers />} />
          <Route path="scores" element={<AdminQuizScores />} />
        </Route>

        {/* CATCH-ALL */}
        <Route path="*" element={<Navigate to="/" replace />} />

      </Routes>
    </BrowserRouter>
  );
}
  