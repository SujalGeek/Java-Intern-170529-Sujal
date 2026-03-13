import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  BookOpen, GraduationCap, LayoutDashboard, LogOut, 
  Sparkles, TrendingUp, AlertTriangle, BrainCircuit, 
  ChevronRight, Loader2, CheckCircle2, ShieldAlert
} from 'lucide-react';
import api from '../api/axios';

const StudentPortal = () => {
  const navigate = useNavigate();
  
  const studentId = localStorage.getItem('userId');
  const studentRole = 3;

  const [activeTab, setActiveTab] = useState('MY_COURSES');
  const [availableCourses, setAvailableCourses] = useState([]);
  const [myEnrollments, setMyEnrollments] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  // AI Prediction State
  const [isPredicting, setIsPredicting] = useState(false);
  const [predictionResult, setPredictionResult] = useState(null);
  const [viewingCourse, setViewingCourse] = useState(null);

  // --- 1. FETCH DATA ON LOAD ---
  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setIsLoading(true);
    try {
      // 1. Get all courses to browse
      const coursesRes = await api.get('/api/course/all', {
        headers: { 'X-User-Role': studentRole }
      });
      
      // 2. Get my personal enrollments
      // Note: Ensure this matches your EnrollmentController mapping!
      const enrollRes = await api.get(`/api/enrollment/student/${studentId}`, {
        headers: { 'X-User-Role': studentRole }
      });

      setAvailableCourses(coursesRes.data || []);
      setMyEnrollments(enrollRes.data || []);
    } catch (error) {
      console.error("Failed to load student data", error);
    } finally {
      setIsLoading(false);
    }
  };

  // --- 2. ENROLL IN COURSE ---
  const handleEnroll = async (courseId) => {
    try {
      await api.post('/api/enrollment', { courseId: courseId }, {
        headers: { 'X-User-Role': studentRole, 'X-User-Id': studentId }
      });
      alert("Successfully enrolled in the course!");
      fetchDashboardData(); // Refresh lists
    } catch (error) {
      alert(error.response?.data?.message || "Failed to enroll. You might be enrolled already or the class is full.");
    }
  };

  // --- 3. THE MAGIC ROBOT (AI FORECAST) 🔥 ---
  const handleGenerateForecast = async (course) => {
    setViewingCourse(course);
    setIsPredicting(true);
    setPredictionResult(null);

    try {
      // Hits the Java ML Pipeline we just built!
      const res = await api.post(`/api/performance/predict/${studentId}/${course.courseId}`, {}, {
        headers: { 'X-User-Role': studentRole }
      });
      
      // Add a tiny artificial delay so the loading animation looks cool
      setTimeout(() => {
        setPredictionResult(res.data);
        setIsPredicting(false);
      }, 1500);

    } catch (error) {
      alert("The AI Engine is currently resting or encountered an error.");
      setIsPredicting(false);
      setViewingCourse(null);
    }
  };

  // --- 4. SECURE LOGOUT ---
  const handleLogout = async () => {
    if (window.confirm("Are you sure you want to log out?")) {
      try {
        const token = localStorage.getItem('refreshToken') || "null";
        await api.post('/api/users/logout', { refreshToken: token });
      } catch (err) {
        console.log("Logged out locally");
      }
      localStorage.clear();
      navigate('/login');
    }
  };

  // UI Helpers
  const getRiskColor = (riskLevel) => {
    if (riskLevel === 'High') return 'text-rose-500 bg-rose-500/10 border-rose-500/20';
    if (riskLevel === 'Medium') return 'text-amber-500 bg-amber-500/10 border-amber-500/20';
    return 'text-emerald-500 bg-emerald-500/10 border-emerald-500/20';
  };

  return (
    <div className="flex min-h-screen bg-[#0F172A] text-slate-200 selection:bg-indigo-500/30">
      
      {/* --- SIDEBAR --- */}
      <aside className="w-72 bg-slate-900 border-r border-slate-800 p-6 flex flex-col shrink-0 z-10 hidden lg:flex">
        <div className="flex items-center gap-3 mb-12 px-2 mt-4">
          <GraduationCap className="w-10 h-10 text-indigo-500" />
          <div>
            <h2 className="text-xl font-black text-white tracking-tighter uppercase italic leading-none">Nexus</h2>
            <h2 className="text-xl font-black text-indigo-500 tracking-tighter uppercase italic leading-none">Student</h2>
          </div>
        </div>

        <nav className="flex-1 space-y-2">
          <button onClick={() => setActiveTab('MY_COURSES')} className={`w-full flex items-center gap-4 px-4 py-4 rounded-2xl font-black uppercase tracking-widest text-[10px] transition-all ${activeTab === 'MY_COURSES' ? 'bg-indigo-500/10 text-indigo-400' : 'text-slate-400 hover:bg-slate-800 hover:text-slate-300'}`}>
            <LayoutDashboard className="w-5 h-5" /> My Classroom
          </button>
          <button onClick={() => setActiveTab('DIRECTORY')} className={`w-full flex items-center gap-4 px-4 py-4 rounded-2xl font-black uppercase tracking-widest text-[10px] transition-all ${activeTab === 'DIRECTORY' ? 'bg-indigo-500/10 text-indigo-400' : 'text-slate-400 hover:bg-slate-800 hover:text-slate-300'}`}>
            <BookOpen className="w-5 h-5" /> Course Directory
          </button>
        </nav>

        <button onClick={handleLogout} className="mt-4 mb-4 w-full flex items-center gap-4 px-4 py-4 rounded-2xl font-black uppercase tracking-widest text-[10px] text-rose-400 hover:bg-rose-500/10 hover:text-rose-500 transition-all">
          <LogOut className="w-5 h-5" /> Logout
        </button>
      </aside>

      {/* --- MAIN CONTENT --- */}
      <main className="flex-1 p-8 lg:p-12 overflow-y-auto custom-scrollbar relative">
        <AnimatePresence mode="wait">

          {/* 🟢 MY CLASSROOM TAB */}
          {activeTab === 'MY_COURSES' && (
            <motion.div key="my-courses" initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -10 }} className="space-y-8 max-w-7xl mx-auto">
              <header>
                 <h1 className="text-4xl font-black text-white uppercase italic tracking-tighter">My <span className="text-indigo-500">Classroom</span></h1>
                 <p className="text-slate-400 font-bold uppercase text-[10px] tracking-[0.4em] mt-2">Active Enrollments & AI Analytics</p>
              </header>

              {isLoading ? (
                 <div className="flex items-center justify-center py-20"><Loader2 className="w-10 h-10 animate-spin text-indigo-500" /></div>
              ) : myEnrollments.length === 0 ? (
                 <div className="text-center py-20 bg-slate-900/50 rounded-[3rem] border border-slate-800">
                    <BookOpen className="w-16 h-16 mx-auto text-slate-700 mb-4" />
                    <h3 className="text-xl font-bold text-slate-300">No Active Courses</h3>
                    <p className="text-slate-500 mt-2 mb-6">Head over to the Course Directory to enroll in classes.</p>
                    <button onClick={() => setActiveTab('DIRECTORY')} className="px-6 py-3 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl font-black uppercase tracking-widest text-xs">Browse Courses</button>
                 </div>
              ) : (
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                  {myEnrollments.map(course => (
                    <div key={course.enrollmentId} className="bg-slate-900 rounded-[2.5rem] p-8 border border-slate-800 relative overflow-hidden group hover:border-indigo-500/30 transition-colors">
                      
                      {/* Course Header */}
                      <div className="flex justify-between items-start mb-6">
                        <div>
                          <span className="px-3 py-1 bg-indigo-500/20 text-indigo-400 rounded-lg text-[10px] font-black uppercase tracking-widest mb-3 inline-block">
                            {course.courseCode}
                          </span>
                          <h3 className="text-2xl font-bold text-white leading-tight">{course.courseName}</h3>
                        </div>
                      </div>

                      {/* Course Stats */}
                      <div className="flex items-center gap-6 mb-8 text-sm font-bold text-slate-400">
                        <span className="flex items-center gap-2"><CheckCircle2 className="w-4 h-4 text-emerald-500"/> Enrolled</span>
                        <span>Semester: {course.semester}</span>
                      </div>

                      {/* Action Buttons */}
                      <div className="flex gap-4">
                        <button className="flex-1 py-4 bg-slate-800 hover:bg-slate-700 rounded-xl font-black uppercase tracking-widest text-xs flex items-center justify-center gap-2 transition-colors">
                          <BookOpen className="w-4 h-4" /> Go to Class
                        </button>
                        
                        {/* 🔥 THE AI PREDICTION TRIGGER BUTTON */}
                        <button onClick={() => handleGenerateForecast(course)} className="flex-1 py-4 bg-indigo-600 hover:bg-indigo-500 shadow-lg shadow-indigo-900/20 rounded-xl font-black uppercase tracking-widest text-xs flex items-center justify-center gap-2 transition-all">
                          <Sparkles className="w-4 h-4" /> AI Forecast
                        </button>
                      </div>

                    </div>
                  ))}
                </div>
              )}
            </motion.div>
          )}

          {/* 🟢 COURSE DIRECTORY TAB */}
          {activeTab === 'DIRECTORY' && (
            <motion.div key="directory" initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -10 }} className="space-y-8 max-w-7xl mx-auto">
              <header>
                 <h1 className="text-4xl font-black text-white uppercase italic tracking-tighter">Course <span className="text-indigo-500">Directory</span></h1>
                 <p className="text-slate-400 font-bold uppercase text-[10px] tracking-[0.4em] mt-2">Available Infrastructure</p>
              </header>

              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {availableCourses.map(course => {
                  // Check if already enrolled to disable the button
                  const isEnrolled = myEnrollments.some(e => e.courseId === (course.courseId || course.id));

                  return (
                    <div key={course.courseId || course.id} className="bg-slate-900 p-6 rounded-[2rem] border border-slate-800 flex flex-col h-full">
                      <div className="mb-4">
                        <span className="px-2.5 py-1 bg-slate-800 text-slate-300 rounded-md text-[10px] font-black uppercase tracking-widest">{course.courseCode || course.code}</span>
                      </div>
                      <h4 className="font-bold text-white text-lg mb-2">{course.courseName || course.name}</h4>
                      <p className="text-xs text-slate-400 mb-6 flex-1">{course.description || "No description provided by instructor."}</p>
                      
                      <div className="flex items-center justify-between mt-auto">
                        <span className="text-xs font-bold text-slate-500">{course.credits || 3} Credits</span>
                        <button 
                          onClick={() => handleEnroll(course.courseId || course.id)} 
                          disabled={isEnrolled}
                          className={`px-4 py-2 rounded-lg text-[10px] font-black uppercase tracking-widest transition-colors ${isEnrolled ? 'bg-emerald-500/20 text-emerald-500 cursor-not-allowed' : 'bg-indigo-500/20 text-indigo-400 hover:bg-indigo-500 hover:text-white'}`}
                        >
                          {isEnrolled ? 'Enrolled' : 'Enroll Now'}
                        </button>
                      </div>
                    </div>
                  );
                })}
              </div>
            </motion.div>
          )}

        </AnimatePresence>
      </main>

      {/* ========================================================= */}
      {/* 🔥 THE MAGIC ROBOT AI PREDICTION MODAL 🔥 */}
      {/* ========================================================= */}
      <AnimatePresence>
        {viewingCourse && (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md">
            <motion.div initial={{ scale: 0.9, y: 20 }} animate={{ scale: 1, y: 0 }} exit={{ scale: 0.9, y: 20 }} className="bg-slate-900 rounded-[3rem] w-full max-w-md overflow-hidden shadow-2xl border border-indigo-500/20 relative">
              
              {/* Close Button */}
              <button onClick={() => { setViewingCourse(null); setPredictionResult(null); }} className="absolute top-6 right-6 p-2 bg-slate-800 text-slate-400 rounded-full hover:bg-rose-500/20 hover:text-rose-400 transition-colors z-10">
                <X className="w-5 h-5" />
              </button>

              <div className="p-8 text-center relative overflow-hidden">
                {/* Background Glow */}
                <div className="absolute top-0 left-1/2 -translate-x-1/2 w-64 h-64 bg-indigo-500/20 rounded-full blur-[80px] pointer-events-none"></div>

                <h2 className="text-2xl font-black text-white uppercase italic tracking-tight relative z-10">Nexus <span className="text-indigo-400">Oracle</span></h2>
                <p className="text-xs font-bold text-slate-400 relative z-10 mb-8">{viewingCourse.courseName}</p>

                {isPredicting ? (
                  <div className="py-12 flex flex-col items-center justify-center relative z-10">
                    <div className="relative">
                      <BrainCircuit className="w-16 h-16 text-indigo-500 animate-pulse relative z-10" />
                      <div className="absolute inset-0 bg-indigo-500 rounded-full blur-xl animate-pulse opacity-50"></div>
                    </div>
                    <p className="mt-6 text-sm font-bold text-indigo-400 animate-pulse uppercase tracking-widest">Analyzing Historical Data...</p>
                    <p className="text-xs text-slate-500 mt-2">Running Machine Learning Models</p>
                  </div>
                ) : predictionResult ? (
                  <motion.div initial={{ opacity: 0, scale: 0.9 }} animate={{ opacity: 1, scale: 1 }} className="relative z-10 space-y-6">
                    
                    {/* THE GRADE DISPLAY */}
                    <div className="bg-slate-950 rounded-3xl p-6 border border-slate-800 shadow-inner">
                      <p className="text-[10px] font-black uppercase tracking-widest text-slate-500 mb-2">Predicted Final Grade</p>
                      <div className="text-7xl font-black text-white tracking-tighter drop-shadow-[0_0_15px_rgba(99,102,241,0.5)]">
                        {predictionResult.predictedGrade}
                      </div>
                      <p className="text-sm font-bold text-slate-400 mt-2">Estimated Score: <span className="text-indigo-400">{predictionResult.predictedScore}%</span></p>
                    </div>

                    {/* RISK AND PROBABILITY */}
                    <div className="grid grid-cols-2 gap-4">
                      <div className={`p-4 rounded-2xl border flex flex-col items-center justify-center ${getRiskColor(predictionResult.riskLevel)}`}>
                        <AlertTriangle className="w-6 h-6 mb-2 opacity-80" />
                        <p className="text-[10px] font-black uppercase tracking-widest opacity-70 mb-1">Risk Level</p>
                        <p className="text-lg font-bold">{predictionResult.riskLevel}</p>
                      </div>
                      
                      <div className="p-4 rounded-2xl bg-slate-800/50 border border-slate-700 flex flex-col items-center justify-center">
                        <TrendingUp className="w-6 h-6 mb-2 text-emerald-400" />
                        <p className="text-[10px] font-black uppercase tracking-widest text-slate-500 mb-1">Pass Probability</p>
                        <p className="text-lg font-bold text-white">{(predictionResult.passProbability * 100).toFixed(1)}%</p>
                      </div>
                    </div>

                    <p className="text-[10px] text-slate-500 italic mt-4 px-4">
                      *This prediction is generated dynamically based on your current quiz scores, assignment averages, and attendance footprint.
                    </p>

                  </motion.div>
                ) : null}
              </div>

            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

    </div>
  );
};

export default StudentPortal;