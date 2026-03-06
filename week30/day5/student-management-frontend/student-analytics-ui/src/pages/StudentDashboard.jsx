import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  LayoutDashboard, BookOpen, BrainCircuit, 
  ClipboardCheck, User, LogOut, Bell, ChevronRight, Sun, Moon , BookPlus
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios.js'

// --- INTEGRATED TAB COMPONENTS ---
import OverviewTab from '../dashboard/OverviewTab.jsx';
import ExamsTab from '../dashboard/ExamsTab.jsx';
import AssignmentsTab from '../dashboard/AssignmentsTab.jsx';
import PerformanceTab from '../dashboard/PerformanceTab.jsx';
import ProfileTab from '../dashboard/ProfileTab.jsx';
import CourseRegistryTab from '../dashboard/CourseRegistryTab.jsx';

const StudentDashboard = () => {
  const [activeTab, setActiveTab] = useState('overview');
  const [isDark, setIsDark] = useState(localStorage.getItem('theme') === 'dark');
  const [studentInfo, setStudentInfo] = useState(null);
  const [notifications, setNotifications] = useState(2);
  const navigate = useNavigate();
  
  // 🔥 CRITICAL IDENTITY FETCH (X-User-Id derived from localStorage)
  const userId = localStorage.getItem('userId');
  

  // --- 1. SYSTEM-WIDE THEME ENGINE ---
  useEffect(() => {
    if (isDark) {
      document.documentElement.classList.add('dark');
      localStorage.setItem('theme', 'dark');
    } else {
      document.documentElement.classList.remove('dark');
      localStorage.setItem('theme', 'light');
    }
  }, [isDark]);

  // --- 2. IDENTITY SYNC (UserController @GetMapping("/{id}")) ---
  useEffect(() => {
    const fetchProfile = async () => {
      try {
        // This hits your specific User-Service endpoint through the Gateway
        const res = await api.get(`/api/users/${userId}`); 
        setStudentInfo(res.data);
      } catch (err) {
        console.error("CRITICAL: Identity Synchronization Failed", err);
        // If the token is invalid (401), force a clean state
        if (err.response?.status === 401) {
          localStorage.clear();
          navigate('/login');
        }
      }
    };
    if (userId) fetchProfile();
  }, [userId, navigate]);

  // --- 3. STATELESS LOGOUT (Token Blacklisting Integration) ---
  const handleLogout = async () => {
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      // Hits your TokenService to blacklist the session
      await api.post('/api/auth/logout', { refreshToken });
    } catch (err) {
      console.warn("Auth: Soft Logout initiated");
    } finally {
      localStorage.clear();
      navigate('/login');
    }
  };

  const navItems = [
    { id: 'overview', label: 'Overview', icon: LayoutDashboard },
    { id: 'registry', label: 'Course Catalog', icon: BookPlus }, 
    { id: 'exams', label: 'Quizzes & Midterms', icon: BookOpen },
    { id: 'assignments', label: 'Assignments', icon: ClipboardCheck },
    { id: 'performance', label: 'AI Analytics', icon: BrainCircuit },
    { id: 'profile', label: 'My Identity', icon: User },
  ];

  return (
    <div className="flex h-screen bg-[#F8FAFC] dark:bg-[#0F172A] font-sans antialiased text-slate-900 dark:text-slate-100 transition-colors duration-300 overflow-hidden">
      
      {/* --- SIDEBAR: NAVIGATIONAL CORE --- */}
      <aside className="w-80 bg-white dark:bg-slate-900 border-r border-slate-200 dark:border-slate-800 flex flex-col shadow-sm z-20 transition-colors">
        <div className="p-8">
          <div className="flex items-center gap-4 mb-12">
            <div className="w-12 h-12 bg-indigo-600 rounded-2xl flex items-center justify-center shadow-2xl shadow-indigo-200 dark:shadow-none animate-pulse-slow">
              <BrainCircuit className="text-white w-7 h-7" />
            </div>
            <div className="flex flex-col">
              <span className="text-2xl font-black tracking-tighter text-slate-800 dark:text-white uppercase italic">
                EduPulse<span className="text-indigo-600">.</span>
              </span>
              <span className="text-[9px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest leading-none">Intelligence Engine</span>
            </div>
          </div>

          <nav className="space-y-2.5">
            {navItems.map((item) => (
              <button
                key={item.id}
                onClick={() => setActiveTab(item.id)}
                className={`w-full flex items-center gap-4 px-6 py-4 rounded-[1.5rem] font-black text-sm transition-all group relative overflow-hidden ${
                  activeTab === item.id 
                    ? 'bg-indigo-600 text-white shadow-xl shadow-indigo-100 dark:shadow-none translate-x-2' 
                    : 'text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800/50 hover:text-slate-600 dark:hover:text-slate-200'
                }`}
              >
                <item.icon className={`w-5 h-5 transition-transform ${activeTab === item.id ? 'scale-125' : 'group-hover:scale-110'}`} />
                <span className="relative z-10 uppercase tracking-tight">{item.label}</span>
                {activeTab === item.id && (
                  <motion.div layoutId="navMarker" className="absolute left-0 w-1 h-8 bg-white rounded-full" />
                )}
              </button>
            ))}
          </nav>
        </div>

        {/* TERMINATE SESSION ENGINE */}
        <div className="mt-auto p-8 border-t border-slate-100 dark:border-slate-800 bg-slate-50/30 dark:bg-slate-900/50">
          <button 
            onClick={handleLogout} 
            className="w-full flex items-center gap-3 px-6 py-4 text-slate-400 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/10 rounded-2xl font-black text-xs transition-all group uppercase tracking-widest"
          >
            <LogOut className="w-5 h-5 group-hover:-translate-x-1 transition-transform" /> 
            <span>Terminate Identity</span>
          </button>
        </div>
      </aside>

      {/* --- MAIN CONTENT STACK --- */}
      <div className="flex-1 flex flex-col min-w-0">
        
        {/* EXECUTIVE NAVBAR */}
        <header className="h-24 bg-white dark:bg-slate-900 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between px-12 shrink-0 transition-colors">
          <div className="flex items-center gap-4 text-slate-400 dark:text-slate-500 text-[11px] font-black uppercase tracking-[0.3em]">
             <span className="hover:text-indigo-600 cursor-pointer transition-colors">Nexus Platform</span> 
             <ChevronRight className="w-3 h-3 text-indigo-400" /> 
             <span className="text-indigo-600 dark:text-indigo-400 bg-indigo-50 dark:bg-indigo-900/20 px-3 py-1 rounded-lg">
                {activeTab}
             </span>
          </div>

          <div className="flex items-center gap-8">
            {/* THEME TOGGLE: DUAL STACK */}
            <div className="flex items-center bg-slate-50 dark:bg-slate-800 p-1.5 rounded-[1.25rem] border border-slate-100 dark:border-slate-700">
               <button 
                onClick={() => setIsDark(false)}
                className={`p-2.5 rounded-xl transition-all ${!isDark ? 'bg-white text-indigo-600 shadow-md' : 'text-slate-500'}`}
               >
                 <Sun className="w-4 h-4" />
               </button>
               <button 
                onClick={() => setIsDark(true)}
                className={`p-2.5 rounded-xl transition-all ${isDark ? 'bg-slate-700 text-indigo-400 shadow-md' : 'text-slate-500'}`}
               >
                 <Moon className="w-4 h-4" />
               </button>
            </div>

            <button className="p-3.5 bg-slate-50 dark:bg-slate-800 text-slate-400 hover:text-indigo-600 transition-all relative rounded-2xl border border-slate-100 dark:border-slate-700 hover:rotate-12">
               <Bell className="w-6 h-6" />
               {notifications > 0 && (
                 <span className="absolute top-0 right-0 w-6 h-6 bg-red-500 text-white text-[10px] font-black flex items-center justify-center rounded-full border-4 border-white dark:border-slate-900 shadow-xl">
                   {notifications}
                 </span>
               )}
            </button>

            {/* IDENTITY PREVIEW */}
            <div className="flex items-center gap-5 border-l dark:border-slate-800 pl-8 border-slate-200">
              <div className="text-right hidden lg:block">
                <p className="text-sm font-black text-slate-900 dark:text-white leading-none uppercase tracking-tight mb-1">
                  {studentInfo?.fullName || 'SYNCING...'}
                </p>
                <p className="text-[10px] font-bold text-slate-400 dark:text-slate-500 tracking-[0.2em] uppercase">
                  STU-{userId} | SEM-{studentInfo?.semester || '0'}
                </p>
              </div>
              <motion.div 
                whileHover={{ scale: 1.05 }}
                className="w-14 h-14 rounded-[1.5rem] border-2 border-indigo-500/30 p-1 overflow-hidden shadow-sm cursor-pointer bg-gradient-to-br from-indigo-50 to-white"
              >
                <img 
                  className="w-full h-full object-cover rounded-[1.1rem]"
                  src={`https://ui-avatars.com/api/?name=${studentInfo?.fullName}&background=6366f1&color=fff&bold=true&size=256`} 
                  alt="Student Avatar" 
                />
              </motion.div>
            </div>
          </div>
        </header>

        {/* --- VIEWPORT ENGINE --- */}
        <main className="flex-1 overflow-y-auto p-12 bg-[#F8FAFC] dark:bg-[#0F172A] transition-colors duration-500">
          <AnimatePresence mode="wait">
            <motion.div
              key={activeTab}
              initial={{ opacity: 0, y: 30, scale: 0.98 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              exit={{ opacity: 0, y: -30, scale: 0.98 }}
              transition={{ duration: 0.35, ease: "circOut" }}
              className="max-w-7xl mx-auto"
            >
              {activeTab === 'overview' && <OverviewTab userId={userId} />}
              {activeTab === 'registry' && <CourseRegistryTab />} 
              {activeTab === 'exams' && <ExamsTab userId={userId} />}
              {activeTab === 'assignments' && <AssignmentsTab userId={userId} />}
              {activeTab === 'performance' && <PerformanceTab userId={userId} />}
              {activeTab === 'profile' && <ProfileTab studentInfo={studentInfo} />}
            </motion.div>
          </AnimatePresence>
        </main>

        {/* SENIOR FOOTER: SYSTEM HEALTH */}
        <footer className="h-16 bg-white dark:bg-slate-900 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between px-12 text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-[0.4em] shrink-0">
           <div className="flex items-center gap-3">
              <div className="flex gap-1">
                 <span className="w-1.5 h-1.5 bg-green-500 rounded-full animate-pulse"></span>
                 <span className="w-1.5 h-1.5 bg-indigo-500 rounded-full animate-pulse delay-75"></span>
              </div>
              <span>Platform Runtime v1.1.0-STABLE</span>
           </div>
           <div className="flex gap-10">
              <div className="flex items-center gap-2">
                 <span className="text-indigo-600 dark:text-indigo-400">Microservice Gateway:</span>
                 <span className="text-slate-800 dark:text-white">Active [8091]</span>
              </div>
              <div className="flex items-center gap-2">
                 <span className="text-indigo-600 dark:text-indigo-400">NLP Core:</span>
                 <span className="text-slate-800 dark:text-white">Sync [5001]</span>
              </div>
           </div>
        </footer>
      </div>
    </div>
  );
};

export default StudentDashboard;