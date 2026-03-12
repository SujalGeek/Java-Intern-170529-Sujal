import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Mail, KeyRound, ArrowLeft, ShieldCheck, CheckCircle2 } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

const ForgotPassword = () => {
  const [step, setStep] = useState(1); // 1: Request, 2: Reset
  const [email, setEmail] = useState('');
  const [token, setToken] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  // Phase 1: Request Reset Link/OTP
  const handleRequest = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      // Hits your Auth Service
      await api.post('/api/auth/forgot-password', { email });
      setStep(2);
    } catch (err) {
      alert(err.response?.data?.message || 'Email not found');
    } finally {
      setIsLoading(false);
    }
  };

  // Phase 2: Reset with Token
  const handleReset = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      await api.post('/api/auth/reset-password', { token, newPassword });
      alert('Password Reset Successful! Redirecting to login...');
      setStep(3);
      setTimeout(() => {
      navigate('/login');
    }, 3000);
  } catch (err) {
    alert('Invalid or expired token. Please request a new one.');
  } finally {
    setIsLoading(false);
  }
  };

  return (
    <div className="min-h-screen bg-[#F0F2F5] flex items-center justify-center p-6 font-sans">
      <motion.div 
        initial={{ opacity: 0, y: 20 }} 
        animate={{ opacity: 1, y: 0 }}
        className="max-w-md w-full bg-white rounded-[2.5rem] shadow-2xl p-10 border border-white relative overflow-hidden"
      >
        {/* Subtle Background Glow */}
        <div className="absolute -top-10 -right-10 w-32 h-32 bg-indigo-500/10 rounded-full blur-3xl"></div>

        <button 
          onClick={() => navigate('/login')} 
          className="flex items-center gap-2 text-slate-400 hover:text-indigo-600 font-bold text-xs uppercase tracking-widest mb-8 transition-colors"
        >
          <ArrowLeft className="w-4 h-4" /> Back to Login
        </button>

        <header className="mb-10 text-left">
          <h2 className="text-3xl font-black text-slate-900 tracking-tight">
            {step === 1 ? 'Reset Password' : 'New Credentials'}
          </h2>
          <p className="text-slate-400 text-sm mt-2 font-medium">
            {step === 1 
              ? 'Enter your email to receive a secure recovery token.' 
              : 'Enter the token sent to your email and choose a new password.'}
          </p>
        </header>

        <AnimatePresence mode="wait">
          {step === 1 ? (
            <motion.form 
              key="request"
              initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: 20 }}
              onSubmit={handleRequest} className="space-y-6"
            >
              <div className="space-y-2 text-left">
                <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Email Address</label>
                <div className="relative group">
                  <Mail className="w-5 h-5 text-slate-300 absolute left-4 top-3.5 group-focus-within:text-indigo-500 transition-colors" />
                  <input 
                    type="email" required 
                    className="w-full pl-12 pr-5 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:border-indigo-500 outline-none transition-all font-medium text-slate-700" 
                    placeholder="sujal@gmail.com"
                    onChange={(e) => setEmail(e.target.value)}
                  />
                </div>
              </div>

              <motion.button 
                whileHover={{ scale: 1.01 }} whileTap={{ scale: 0.99 }}
                type="submit" disabled={isLoading}
                className="w-full bg-[#0F172A] text-white font-bold py-4 rounded-2xl shadow-xl hover:bg-indigo-600 transition-all flex items-center justify-center gap-3"
              >
                {isLoading ? <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" /> : 'Send Recovery Token'}
              </motion.button>
            </motion.form>
          ) : (
            <motion.form 
              key="reset"
              initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: 20 }}
              onSubmit={handleReset} className="space-y-6"
            >
              <div className="space-y-2 text-left">
                <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Recovery Token</label>
                <div className="relative group">
                  <ShieldCheck className="w-5 h-5 text-slate-300 absolute left-4 top-3.5 group-focus-within:text-indigo-500 transition-colors" />
                  <input 
                    type="text" required 
                    className="w-full pl-12 pr-5 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:border-indigo-500 outline-none transition-all font-medium text-slate-700" 
                    placeholder="Check your email..."
                    onChange={(e) => setToken(e.target.value)}
                  />
                </div>
              </div>

              <div className="space-y-2 text-left">
                <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">New Password</label>
                <div className="relative group">
                  <KeyRound className="w-5 h-5 text-slate-300 absolute left-4 top-3.5 group-focus-within:text-indigo-500 transition-colors" />
                  <input 
                    type="password" required 
                    className="w-full pl-12 pr-5 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:border-indigo-500 outline-none transition-all font-medium text-slate-700" 
                    placeholder="••••••••"
                    onChange={(e) => setNewPassword(e.target.value)}
                  />
                </div>
              </div>

              <motion.button 
                whileHover={{ scale: 1.01 }} whileTap={{ scale: 0.99 }}
                type="submit" disabled={isLoading}
                className="w-full bg-indigo-600 text-white font-bold py-4 rounded-2xl shadow-xl hover:bg-indigo-700 transition-all flex items-center justify-center gap-3"
              >
                {isLoading ? <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" /> : 'Update Password'}
              </motion.button>
            </motion.form>
          )}

          {step === 3 && (
          <motion.div 
            key="success"
            initial={{ opacity: 0, scale: 0.9 }} animate={{ opacity: 1, scale: 1 }}
            className="text-center py-8"
          >
            <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
              <CheckCircle2 className="w-10 h-10 text-green-600" />
            </div>
            <h2 className="text-2xl font-black text-slate-900">All Set!</h2>
            <p className="text-slate-500 mt-2">Your password has been updated. <br/> Redirecting you to login...</p>
          </motion.div>
        )}
        </AnimatePresence>
      </motion.div>
    </div>
  );
};

export default ForgotPassword;