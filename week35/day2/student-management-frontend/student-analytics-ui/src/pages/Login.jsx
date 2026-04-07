import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion'; // 🔥 Full imports
import { LogIn, Mail, Lock, ArrowRight, ShieldCheck } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(''); 
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');

    // --- Validation Logic ---
    if (!/\S+@\S+\.\S+/.test(email)) {
      setError('Please enter a valid email address.');
      return;
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }
    
    setIsLoading(true);

    try {
      // --- API Call ---
      const response = await api.post('/api/auth/login', { 
        email: email, 
        password: password 
      });
      
      // --- Session Persistence ---
      
      localStorage.setItem('token', response.data.token); 
    // localStorage.setItem('userId', response.data.userId);
    // localStorage.setItem('userRole', response.data.role);
      localStorage.setItem('userId', response.data.userId);
      localStorage.setItem('userRole', response.data.role);
      localStorage.setItem('userName', response.data.username);

      // --- Role-Based Navigation ---
      if (response.data.role === 1) {
        navigate('/admin-portal');
      } else if (response.data.role === 2) {
        navigate('/teacher-dashboard');
      } else {
        navigate('/student-dashboard');
      }
      
    } catch (err) {
        setError(err.response?.data?.message || 'Invalid Credentials');    
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#F0F2F5] flex items-center justify-center p-6 font-sans">
      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="max-w-4xl w-full bg-white rounded-[2.5rem] shadow-[0_20px_60px_rgba(0,0,0,0.08)] flex overflow-hidden border border-white"
      >
        {/* Left Branding Panel */}
        <div className="hidden lg:flex w-1/3 bg-[#0F172A] p-12 flex-col justify-between text-white relative">
          <div className="absolute top-0 left-0 w-full h-full opacity-10 pointer-events-none">
            <div className="absolute top-10 left-10 w-40 h-40 bg-indigo-500 rounded-full blur-[80px]"></div>
          </div>
          
          <div className="relative z-10 text-left">
            <motion.div 
              whileHover={{ rotate: 10, scale: 1.1 }}
              className="w-12 h-12 bg-indigo-600 rounded-2xl flex items-center justify-center mb-8 shadow-xl shadow-indigo-500/20"
            >
              <LogIn className="w-6 h-6 text-white" />
            </motion.div>
            <h1 className="text-3xl font-black leading-tight">Welcome <br/>Back.</h1>
            <p className="text-slate-400 mt-4 text-sm font-medium">Log in to resume tracking your intelligent student analytics.</p>
          </div>

          <div className="relative z-10">
            <div className="flex items-center space-x-2 text-[10px] font-bold uppercase tracking-[0.2em] text-slate-500">
              <ShieldCheck className="w-3 h-3" />
              <span>Secure Session Active</span>
            </div>
          </div>
        </div>

        {/* Login Form Section */}
        <div className="flex-1 p-10 lg:p-16 bg-white">
          <header className="mb-12 text-left">
            <h2 className="text-3xl font-black text-slate-900 tracking-tight">Sign In</h2>
            <p className="text-slate-400 text-sm mt-2 font-semibold text-left">Enter your credentials to access your dashboard.</p>
          </header>

          <form onSubmit={handleLogin} className="space-y-8">
            
            {/* 🔥 Error Message Animation Block */}
            <AnimatePresence mode="wait">
              {error && (
                <motion.div 
                  initial={{ opacity: 0, height: 0 }}
                  animate={{ opacity: 1, height: 'auto' }}
                  exit={{ opacity: 0, height: 0 }}
                  className="bg-red-50 text-red-500 text-xs font-bold p-4 rounded-xl border border-red-100 text-left"
                >
                  ⚠ {error}
                </motion.div>
              )}
            </AnimatePresence>

            <div className="space-y-6">
              {/* Email Input */}
              <div className="space-y-2 text-left">
                <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Email Address</label>
                <div className="relative group">
                  <Mail className="w-5 h-5 text-slate-300 absolute left-4 top-3.5 group-focus-within:text-indigo-500 transition-colors" />
                  <input 
                    type="email" 
                    required 
                    className="w-full pl-12 pr-5 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:ring-4 focus:ring-indigo-500/10 focus:border-indigo-500 outline-none transition-all font-medium text-slate-700" 
                    placeholder="name@university.com"
                    onChange={(e) => setEmail(e.target.value)} 
                  />
                </div>
              </div>

              {/* Password Input */}
              <div className="space-y-2 text-left">
                <div className="flex justify-between items-center px-1">
                  <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Password</label>
                <button 
  type="button" 
  onClick={() => navigate('/forgot-password')} // 🔥 Added navigation
  className="text-[10px] font-bold text-indigo-600 uppercase tracking-widest hover:text-indigo-700 transition-colors"
>
  Forgot?
</button>
                </div>
                <div className="relative group">
                  <Lock className="w-5 h-5 text-slate-300 absolute left-4 top-3.5 group-focus-within:text-indigo-500 transition-colors" />
                  <input 
                    type="password" 
                    required 
                    className="w-full pl-12 pr-5 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:ring-4 focus:ring-indigo-500/10 focus:border-indigo-500 outline-none transition-all font-medium text-slate-700" 
                    placeholder="••••••••"
                    onChange={(e) => setPassword(e.target.value)} 
                  />
                </div>
              </div>
            </div>

            {/* Submit Button */}
            <motion.button 
              whileHover={{ scale: 1.01 }}
              whileTap={{ scale: 0.99 }}
              type="submit" 
              disabled={isLoading}
              className="w-full bg-[#0F172A] text-white font-bold py-4 rounded-2xl shadow-xl hover:bg-indigo-600 disabled:bg-slate-400 transition-all flex items-center justify-center gap-3 group mt-4"
            >
              {isLoading ? (
                <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              ) : (
                <>
                  Access Dashboard
                  <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
                </>
              )}
            </motion.button>

            {/* Footer Link */}
            <p className="text-center text-sm text-slate-500 font-medium">
              Don't have an account? {' '}
              <button 
                type="button" 
                onClick={() => navigate('/register')}
                className="text-indigo-600 font-bold hover:underline"
              >
                Create one now
              </button>
            </p>
          </form>
        </div>
      </motion.div>
    </div>
  );
};

export default Login;