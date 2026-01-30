import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../services/api';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // 1. Call Java Backend
      const response = await api.post('/auth/login', { email, password });
      
      console.log("Full Server Response:", response.data);

      // 2. Extract Data (Handle both casing just to be safe)
      const token = response.data.data.accesstoken || response.data.data.accessToken || response.data.data.token;
      const { studentId, teacherId } = response.data.data; 

      if (!token) {
         throw new Error("Token not found in response!");
      }

      // 3. Save Token & Clear Old IDs
      localStorage.setItem('token', token);
      localStorage.removeItem('studentId');
      localStorage.removeItem('teacherId');

      // 4. Smart Redirection Logic
      // Check which ID is present and route accordingly
      if (studentId) {
         localStorage.setItem('studentId', studentId);
         navigate('/student-dashboard');
      } 
      else if (teacherId) {
         localStorage.setItem('teacherId', teacherId);
         navigate('/teacher-dashboard'); 
      } 
      else {
         // Fallback for Admin or unknown roles
         navigate('/');
      }

    } catch (err) {
      console.error("Login failed", err);
      // Show backend error message if available, else generic message
      const msg = err.response?.data?.message || 'Invalid Credentials. Please check email/password.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-slate-100">
      <div className="w-full max-w-md p-8 bg-white rounded-xl shadow-lg border border-slate-200">
        
        <div className="text-center mb-8">
          <h2 className="text-3xl font-extrabold text-slate-800">Welcome Back</h2>
          <p className="text-slate-500 mt-2">Login to your dashboard</p>
        </div>
        
        {error && (
          <div className="p-4 mb-4 text-sm text-red-700 bg-red-100 rounded-lg border-l-4 border-red-500">
            {error}
          </div>
        )}

        <form onSubmit={handleLogin} className="space-y-5">
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-1">Email Address</label>
            <input 
              type="email" 
              required
              className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="admin@school.com"
            />
          </div>
          
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-1">Password</label>
            <input 
              type="password" 
              required
              className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
            />
          </div>

          <button 
            type="submit" 
            disabled={loading}
            className={`w-full py-3 font-bold text-white rounded-lg shadow-md transition duration-200 ${
              loading ? "bg-blue-400 cursor-not-allowed" : "bg-blue-600 hover:bg-blue-700 hover:shadow-lg"
            }`}
          >
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-slate-600">
          Don't have an account?{' '}
          <Link to="/signup" className="font-semibold text-blue-600 hover:text-blue-500 hover:underline">
            Register here
          </Link>
        </p>

      </div>
    </div>
  );
};

export default Login;