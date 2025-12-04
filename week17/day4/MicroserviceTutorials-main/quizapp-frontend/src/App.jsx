import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Home from "./pages/Home";
import QuizList from "./pages/QuizList";
import QuizQuestions from "./pages/QuizQuestions";

// AUTH
import Login from "./pages/auth/Login";
import Register from "./pages/auth/Register";

// STUDENT
import StudentDashboard from "./pages/student/StudentDashboard";
import QuizHistory from "./pages/student/QuizHistory";
import StudentProfile from "./pages/student/StudentProfile";
import QuizResult from "./pages/student/QuizResult"; // <--- Import it
// ADMIN
import AdminLayout from "./components/AdminLayout";
import AdminCreateQuiz from "./pages/admin/AdminCreateQuiz";
import AdminCreateQuestion from "./pages/admin/AdminCreateQuestion";
import AdminManageQuestions from "./pages/admin/AdminManageQuestions";
import AdminManageUsers from "./pages/admin/AdminManageUsers";
import AdminQuizScores from "./pages/admin/AdminQuizScores";

import AdminRoute from "./components/AdminRoute";
import StudentRoute from "./components/StudentRoute";

// Helper to check login
const isLoggedIn = () => !!localStorage.getItem("authToken");

export default function App() {
  return (
    <BrowserRouter>
      <Routes>

        {/* PUBLIC ROUTES */}
        <Route path="/" element={<Home />} />
        <Route path="/quizzes" element={<QuizList />} />

        {/* AUTH ROUTES */}
        <Route
          path="/login"
          element={isLoggedIn() ? <Navigate to="/student/dashboard" /> : <Login />}
        />

        <Route
          path="/register"
          element={isLoggedIn() ? <Navigate to="/login" /> : <Register />}
        />


        {/* PROTECTED STUDENT ROUTES */}
        <Route path="/quiz/:id" element={
          <StudentRoute>
            <QuizQuestions />
          </StudentRoute>
        } />

        <Route path="/student/dashboard" element={
          <StudentRoute>
            <StudentDashboard />
          </StudentRoute>
        } />

        <Route path="/student/history" element={
          <StudentRoute>
            <QuizHistory />
          </StudentRoute>
        } />

        <Route path="/student/profile" element={
          <StudentRoute>
            <StudentProfile />
          </StudentRoute>
        } />


        <Route path="/result" element={
          <StudentRoute>
            <QuizResult />
          </StudentRoute>
        } />

        
        {/* PROTECTED ADMIN ROUTES */}
        <Route
          path="/admin"
          element={
            <AdminRoute>
              <AdminLayout />
            </AdminRoute>
          }
        >
          <Route index element={<Navigate to="/admin/create-quiz" />} />
          <Route path="create-quiz" element={<AdminCreateQuiz />} />
          <Route path="create-question" element={<AdminCreateQuestion />} />
          <Route path="questions" element={<AdminManageQuestions />} />
          <Route path="users" element={<AdminManageUsers />} />
          <Route path="scores" element={<AdminQuizScores />} />
        </Route>

      </Routes>
    </BrowserRouter>
  );
}
