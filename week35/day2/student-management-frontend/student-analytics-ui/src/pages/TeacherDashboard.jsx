import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  LayoutDashboard, Database, FileSignature, 
  CheckSquare, LogOut, ShieldCheck, BookPlus, LayoutList,
  Menu, X, Sun, Moon, ChevronRight
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios.js';

// --- INDUSTRIAL TAB COMPONENTS ---
import TeacherOverviewTab from '../dashboard/TeacherOverviewTab.jsx';
import KnowledgeBaseTab from '../dashboard/KnowledgeBaseTab.jsx';
import AssessmentBuilderTab from '../dashboard/AssessmentBuilderTab';
import GradingHubTab from '../dashboard/GradingHubTab';
import CourseRegistryTab from '../dashboard/CourseRegistryTab.jsx';
import QuestionBankTab from '../dashboard/QuestionBankTab.jsx';

const TeacherDashboard = () => {
  const [activeTab, setActiveTab] = useState('overview');
  const [isDark, setIsDark] = useState(localStorage.getItem('theme') === 'dark');
  const [activeCourseId, setActiveCourseId] = useState(null);
  const [myCourses, setMyCourses] = useState([]);
  const [teacherInfo, setTeacherInfo] = useState(null);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const navigate = useNavigate();
  
  const userId = localStorage.getItem('userId');

  // --- 1. THEME SYNCHRONIZATION ENGINE ---
  useEffect(() => {
    if (isDark) {
      document.documentElement.classList.add('dark');
      localStorage.setItem('theme', 'dark');
    } else {
      document.documentElement.classList.remove('dark');
      localStorage.setItem('theme', 'light');
    }
  }, [isDark]);

  // --- 2. MULTI-SERVICE DATA INITIALIZATION ---
  useEffect(() => {
    const fetchTeacherEnvironment = async () => {
      try {
        const profileRes = await api.get(`/api/users/${userId}`); 
        setTeacherInfo(profileRes.data);

        const coursesRes = await api.get('/api/course/my');
        setMyCourses(coursesRes.data);
        
        if (coursesRes.data.length > 0 && !activeCourseId) {
          setActiveCourseId(coursesRes.data[0].courseId);
        }
      } catch (err) {
        console.error("Environment Sync Failed", err);
        if (err.response?.status === 401) {
          localStorage.clear();
          navigate('/login');
        }
      }
    };
    if (userId) fetchTeacherEnvironment();
  }, [userId, navigate, activeCourseId]);

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  const navItems = [
    { id: 'overview', label: 'Class Analytics', icon: LayoutDashboard },
    { id: 'registry', label: 'Course Management', icon: BookPlus },
    { id: 'knowledge', label: 'Knowledge Base', icon: Database },
    { id: 'builder', label: 'Assessment Builder', icon: FileSignature },
    { id: 'bank', label: 'Question Bank', icon: LayoutList },
    { id: 'grading', label: 'Grading Hub', icon: CheckSquare },
  ];

  return (
    <div className="flex h-screen bg-slate-50 dark:bg-[#050a18] font-sans antialiased text-slate-900 dark:text-slate-100 transition-colors duration-300 overflow-hidden relative">
      
      {/* MOBILE OVERLAY */}
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

      {/* --- SIDEBAR: FACULTY CONTROL PLANE --- */}
      <aside className={`
        fixed lg:static inset-y-0 left-0 w-80 bg-white dark:bg-slate-900 border-r border-slate-200 dark:border-slate-800 flex flex-col shadow-xl lg:shadow-none z-50 transition-transform duration-300 ease-in-out
        ${isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}
      `}>
        <div className="p-8">
          <div className="flex items-center justify-between mb-12">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 bg-emerald-600 rounded-2xl flex items-center justify-center shadow-2xl shadow-emerald-200 dark:shadow-none">
                <ShieldCheck className="text-white w-7 h-7" />
              </div>
              <div className="flex flex-col">
                <span className="text-2xl font-black tracking-tighter text-slate-800 dark:text-white uppercase italic leading-none">
                  EduPulse<span className="text-emerald-600">.</span>
                </span>
                <span className="text-[9px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-widest mt-1">Faculty Plane v1.1</span>
              </div>
            </div>
            <button onClick={() => setIsMobileMenuOpen(false)} className="lg:hidden text-slate-400 hover:text-slate-600 dark:hover:text-white">
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
                    ? 'bg-emerald-600 text-white shadow-xl translate-x-2' 
                    : 'text-slate-400 dark:text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800/50 hover:text-slate-600 dark:hover:text-slate-200'
                }`}
              >
                <item.icon className={`w-5 h-5 transition-transform ${activeTab === item.id ? 'scale-125' : 'group-hover:scale-110'}`} />
                <span className="relative z-10 uppercase tracking-tight">{item.label}</span>
                {activeTab === item.id && (
                  <motion.div layoutId="navMarkerTeacher" className="absolute left-0 w-1 h-8 bg-white rounded-full" />
                )}
              </button>
            ))}
          </nav>
        </div>

        <div className="mt-auto p-8 border-t border-slate-100 dark:border-slate-800 bg-slate-50/30 dark:bg-slate-900/50">
          <button onClick={handleLogout} className="w-full flex items-center gap-3 px-6 py-4 text-slate-400 dark:text-slate-500 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/10 rounded-2xl font-black text-xs transition-all group uppercase tracking-widest">
            <LogOut className="w-5 h-5 group-hover:-translate-x-1 transition-transform" /> 
            <span>Secure Logout</span>
          </button>
        </div>
      </aside>

      {/* --- MAIN INTERFACE STACK --- */}
      <div className="flex-1 flex flex-col min-w-0">
        
        {/* EXECUTIVE HEADER */}
        <header className="h-24 bg-white dark:bg-slate-900 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between px-6 lg:px-12 shrink-0">
          <div className="flex items-center gap-4 lg:gap-6">
            <button onClick={() => setIsMobileMenuOpen(true)} className="lg:hidden p-2 bg-slate-100 dark:bg-slate-800 rounded-xl text-slate-600 dark:text-slate-300">
              <Menu className="w-6 h-6" />
            </button>

            <div className="hidden sm:flex items-center gap-4 text-slate-400 dark:text-slate-500 text-[11px] font-black uppercase tracking-[0.3em]">
                <span>Faculty nexus</span> <ChevronRight className="w-3 h-3 text-emerald-400" /> 
                <span className="text-emerald-600 dark:text-emerald-400 bg-emerald-50 dark:bg-emerald-900/20 px-3 py-1 rounded-lg">{activeTab}</span>
            </div>

            {/* GLOBAL COURSE SELECTOR */}
            {/* GLOBAL COURSE SELECTOR: Updates all tabs simultaneously */}
            <div className="flex items-center gap-2 bg-slate-100 dark:bg-slate-800 p-1.5 rounded-2xl border border-slate-200 dark:border-slate-700 ml-2 lg:ml-4 shadow-inner">
              <Database className="w-4 h-4 text-emerald-600 dark:text-emerald-400 ml-2" />
              <select 
                value={activeCourseId || ""} 
                onChange={(e) => setActiveCourseId(e.target.value)}
                className="bg-transparent text-[11px] font-black uppercase tracking-widest text-slate-700 dark:text-white outline-none cursor-pointer pr-4 focus:ring-0"
              >
                {myCourses.length > 0 ? (
                  myCourses.map(course => (
                    <option 
                      key={course.courseId} 
                      value={course.courseId} 
                      className="bg-white dark:bg-slate-900 text-slate-900 dark:text-white font-bold"
                    >
                      {course.courseName}
                    </option>
                  ))
                ) : (
                  <option disabled className="bg-white dark:bg-slate-900 text-slate-400">
                    No Courses Found
                  </option>
                )}
              </select>
            </div>
         </div>

          <div className="flex items-center gap-4 lg:gap-8">
                    {/* DARK MODE SWITCHER */}
          <div className="flex items-center bg-slate-100 dark:bg-slate-800 p-1.5 rounded-[1.25rem] border border-slate-200 dark:border-slate-700">
          <button 
            onClick={() => setIsDark(false)} 
            className={`p-2.5 rounded-xl transition-all ${!isDark ? 'bg-white text-emerald-600 shadow-sm' : 'text-slate-400'}`}
          >
            <Sun className="w-4 h-4" />
          </button>
          <button 
            onClick={() => setIsDark(true)} 
            className={`p-2.5 rounded-xl transition-all ${isDark ? 'bg-slate-700 text-emerald-400 shadow-sm' : 'text-slate-400'}`}
          >
            <Moon className="w-4 h-4" />
          </button>
        </div>
            
            <div className="flex items-center gap-3 lg:gap-5 border-l dark:border-slate-800 pl-4 lg:pl-8 border-slate-200">
              <div className="text-right hidden xl:block">
                <p className="text-sm font-black text-slate-900 dark:text-white leading-none uppercase tracking-tight mb-1">
                  Prof. {teacherInfo?.fullName || 'Nand Sharma'}
                </p>
                <p className="text-[10px] font-bold text-slate-400 dark:text-slate-500 tracking-[0.2em] uppercase">
                  Faculty ID: {userId} | Status: Online
                </p>
              </div>
              <motion.div 
                whileHover={{ scale: 1.05 }}
                className="w-12 h-12 lg:w-14 lg:h-14 rounded-[1.5rem] border-2 border-emerald-500/30 p-1 overflow-hidden shadow-sm bg-white"
              >
                <img 
                  className="w-full h-full object-cover rounded-[1.1rem]" 
                  src={`https://ui-avatars.com/api/?name=${teacherInfo?.fullName}&background=10b981&color=fff&bold=true&size=256`} 
                  alt="Avatar" 
                />
              </motion.div>
            </div>
          </div>
        </header>

        {/* --- DYNAMIC WORKSPACE --- */}
        <main className="flex-1 overflow-y-auto p-6 lg:p-12 bg-slate-50 dark:bg-[#050a18] transition-colors duration-500 custom-scrollbar">
          <AnimatePresence mode="wait">
            <motion.div
              key={activeTab}
              initial={{ opacity: 0, y: 30, scale: 0.98 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              exit={{ opacity: 0, y: -30, scale: 0.98 }}
              transition={{ duration: 0.35, ease: "circOut" }}
              className="max-w-7xl mx-auto"
            >
              {activeTab === 'overview' && <TeacherOverviewTab courseId={activeCourseId} />}
              {activeTab === 'registry' && <CourseRegistryTab />}
              {activeTab === 'knowledge' && <KnowledgeBaseTab courseId={activeCourseId} />}
              {activeTab === 'builder' && <AssessmentBuilderTab courseId={activeCourseId} />}
              {activeTab === 'bank' && <QuestionBankTab courseId={activeCourseId} />}
              {activeTab === 'grading' && <GradingHubTab courseId={activeCourseId} />}
            </motion.div>
          </AnimatePresence>
        </main>
        
        <footer className="h-14 bg-white dark:bg-slate-900 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between px-6 lg:px-12 text-[10px] font-black text-slate-400 dark:text-slate-500 uppercase tracking-[0.4em] shrink-0 overflow-hidden">
            <div className="flex items-center gap-3">
              <span className="w-2 h-2 bg-emerald-500 rounded-full animate-pulse"></span>
              <span className="hidden sm:inline">Gateway: Protocol Linked [8091]</span>
            </div>
            <div className="flex gap-4 lg:gap-10">
              <span className="text-emerald-600 dark:text-emerald-400">Knowledge Base Vectorized</span>
              <span className="hidden md:inline">Latency: 0.02ms</span>
            </div>
        </footer>
      </div>
    </div>
  );
};

export default TeacherDashboard;