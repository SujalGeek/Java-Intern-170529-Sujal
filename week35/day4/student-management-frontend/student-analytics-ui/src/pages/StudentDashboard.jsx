import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  LayoutDashboard, BookOpen, BrainCircuit, 
  ClipboardCheck, User, LogOut, Bell, ChevronRight, Sun, Moon, BookPlus, Menu, X 
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios.js';

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
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false); // Added for responsiveness
  const navigate = useNavigate();
  
  const userId = localStorage.getItem('userId');

  // --- 1. SYSTEM-WIDE THEME ENGINE ---
  useEffect(() => {
    const root = window.document.documentElement;
    if (isDark) {
      root.classList.add('dark');
      localStorage.setItem('theme', 'dark');
    } else {
      root.classList.remove('dark');
      localStorage.setItem('theme', 'light');
    }
  }, [isDark]);

  // --- 2. IDENTITY SYNC ---
  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const res = await api.get(`/api/users/${userId}`); 
        setStudentInfo(res.data);
      } catch (err) {
        console.error("CRITICAL: Identity Synchronization Failed", err);
        if (err.response?.status === 401) {
          localStorage.clear();
          navigate('/login');
        }
      }
    };
    if (userId) fetchProfile();
  }, [userId, navigate]);

  // --- 3. STATELESS LOGOUT ---
  const handleLogout = async () => {
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      await api.post('/api/users/logout', { refreshToken });
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
    <div className="flex h-screen bg-[#F8FAFC] dark:bg-[#050a18] font-sans antialiased text-slate-900 dark:text-slate-100 transition-colors duration-300 overflow-hidden relative">
      
      {/* 📱 MOBILE OVERLAY */}
      <AnimatePresence>
        {isMobileMenuOpen && (
          <motion.div 
            initial={{ opacity: 0 }} 
            animate={{ opacity: 1 }} 
            exit={{ opacity: 0 }}
            onClick={() => setIsMobileMenuOpen(false)}
            className="fixed inset-0 bg-black/60 backdrop-blur-sm z-40 lg:hidden"
          />
        )}
      </AnimatePresence>

      {/* --- SIDEBAR --- */}
      <aside className={`
        fixed lg:static inset-y-0 left-0 w-80 bg-white dark:bg-slate-900 border-r border-slate-200 dark:border-slate-800 flex flex-col z-50 transition-transform duration-300 ease-in-out
        ${isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}
      `}>
        <div className="p-8">
          <div className="flex items-center justify-between mb-12">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-indigo-600 rounded-2xl flex items-center justify-center shadow-2xl shadow-indigo-200 dark:shadow-none animate-pulse-slow">
                <BrainCircuit className="text-white w-7 h-7" />
              </div>
              <div className="flex flex-col">
                <span className="text-2xl font-black tracking-tighter text-slate-800 dark:text-white uppercase italic leading-none">EduPulse</span>
                <span className="text-[9px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest mt-1 text-center">Intelligence Engine</span>
              </div>
            </div>
            <button onClick={() => setIsMobileMenuOpen(false)} className="lg:hidden text-slate-400 hover:text-indigo-600">
              <X className="w-6 h-6" />
            </button>
          </div>

          <nav className="space-y-2.5">
            {navItems.map((item) => (
              <button
                key={item.id}
                onClick={() => { setActiveTab(item.id); setIsMobileMenuOpen(false); }}
                className={`w-full flex items-center gap-4 px-6 py-4 rounded-[1.5rem] font-black text-sm transition-all group relative overflow-hidden ${
                  activeTab === item.id 
                    ? 'bg-indigo-600 text-white shadow-xl translate-x-2' 
                    : 'text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800/50 hover:text-slate-600'
                }`}
              >
                <item.icon className={`w-5 h-5 transition-transform ${activeTab === item.id ? 'scale-125' : 'group-hover:scale-110'}`} />
                <span className="relative z-10 uppercase tracking-tight">{item.label}</span>
                {activeTab === item.id && (
                  <motion.div layoutId="navMarkerStudent" className="absolute left-0 w-1 h-8 bg-white rounded-full" />
                )}
              </button>
            ))}
          </nav>
        </div>

        <div className="mt-auto p-8 border-t border-slate-100 dark:border-slate-800 bg-slate-50/30 dark:bg-slate-900/50">
          <button onClick={handleLogout} className="w-full flex items-center gap-3 px-6 py-4 text-slate-400 hover:text-red-500 rounded-2xl font-black text-xs transition-all group uppercase tracking-widest">
            <LogOut className="w-5 h-5 group-hover:-translate-x-1 transition-transform" /> 
            <span>Terminate Identity</span>
          </button>
        </div>
      </aside>

      {/* --- MAIN CONTENT AREA --- */}
      <div className="flex-1 flex flex-col min-w-0">
        
        {/* EXECUTIVE NAVBAR */}
        <header className="h-24 bg-white dark:bg-slate-900 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between px-6 lg:px-12 shrink-0 transition-colors">
          <div className="flex items-center gap-4">
            <button onClick={() => setIsMobileMenuOpen(true)} className="lg:hidden p-2 bg-slate-100 dark:bg-slate-800 rounded-xl">
              <Menu className="w-6 h-6 text-slate-600 dark:text-slate-300" />
            </button>
            <div className="hidden sm:flex items-center gap-4 text-slate-400 dark:text-slate-500 text-[11px] font-black uppercase tracking-[0.3em]">
                <span className="hover:text-indigo-600 cursor-pointer transition-colors">Nexus Platform</span> 
                <ChevronRight className="w-3 h-3 text-indigo-400" /> 
                <span className="text-indigo-600 dark:text-indigo-400 bg-indigo-50 dark:bg-indigo-900/20 px-3 py-1 rounded-lg">{activeTab}</span>
            </div>
          </div>

          <div className="flex items-center gap-3 lg:gap-8">
            <div className="flex items-center bg-slate-50 dark:bg-slate-800 p-1.5 rounded-[1.25rem] border border-slate-100 dark:border-slate-700">
               <button onClick={() => setIsDark(false)} className={`p-2.5 rounded-xl transition-all ${!isDark ? 'bg-white text-indigo-600 shadow-md' : 'text-slate-500'}`}><Sun className="w-4 h-4" /></button>
               <button onClick={() => setIsDark(true)} className={`p-2.5 rounded-xl transition-all ${isDark ? 'bg-slate-700 text-indigo-400 shadow-md' : 'text-slate-500'}`}><Moon className="w-4 h-4" /></button>
            </div>

            <button className="hidden sm:block p-3.5 bg-slate-50 dark:bg-slate-800 text-slate-400 hover:text-indigo-600 transition-all relative rounded-2xl border border-slate-100 dark:border-slate-700">
               <Bell className="w-6 h-6" />
               {notifications > 0 && <span className="absolute top-0 right-0 w-6 h-6 bg-red-500 text-white text-[10px] font-black flex items-center justify-center rounded-full border-4 border-white dark:border-slate-900 shadow-xl">{notifications}</span>}
            </button>
            
            <div className="flex items-center gap-5 border-l dark:border-slate-800 pl-4 lg:pl-8 border-slate-200">
              <div className="text-right hidden lg:block">
                <p className="text-sm font-black text-slate-900 dark:text-white leading-none uppercase">{studentInfo?.fullName || 'SYNCING...'}</p>
                <p className="text-[10px] font-bold text-slate-400 mt-1 uppercase">STU-{userId} | SEM-{studentInfo?.semester || '0'}</p>
              </div>
              <div className="w-12 h-12 lg:w-14 lg:h-14 rounded-[1.5rem] border-2 border-indigo-500/30 p-1 overflow-hidden bg-white shadow-sm">
                <img className="w-full h-full object-cover rounded-[1.1rem]" src={`https://ui-avatars.com/api/?name=${studentInfo?.fullName}&background=6366f1&color=fff&bold=true&size=256`} alt="Avatar" />
              </div>
            </div>
          </div>
        </header>

        <main className="flex-1 overflow-y-auto p-4 lg:p-12 bg-[#F8FAFC] dark:bg-[#050a18] transition-colors duration-500 custom-scrollbar">
          <AnimatePresence mode="wait">
            <motion.div
              key={activeTab}
              initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -20 }}
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

        <footer className="h-16 bg-white dark:bg-slate-900 border-t border-slate-100 dark:border-slate-800 hidden sm:flex items-center justify-between px-12 text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-[0.4em] shrink-0">
            <div className="flex items-center gap-3">
              <span className="flex gap-1">
                 <span className="w-1.5 h-1.5 bg-green-500 rounded-full animate-pulse"></span>
                 <span className="w-1.5 h-1.5 bg-indigo-500 rounded-full animate-pulse delay-75"></span>
              </span>
              <span>Platform Runtime v1.1.0-STABLE</span>
            </div>
            <div className="flex gap-10">
              <span className="hidden md:inline"><span className="text-indigo-600 dark:text-indigo-400">Microservice Gateway:</span> ACTIVE [8091]</span>
              <span className="hidden lg:inline"><span className="text-indigo-600 dark:text-indigo-400">NLP Core:</span> SYNC [5001]</span>
            </div>
        </footer>
      </div>
    </div>
  );
};

export default StudentDashboard;