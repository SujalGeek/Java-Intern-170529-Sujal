import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { BookPlus, CheckCircle, Search, AlertCircle, Sparkles, Filter, ShieldCheck } from 'lucide-react';
import api from '../api/axios';

const CourseRegistryTab = () => {
  const [availableCourses, setAvailableCourses] = useState([]);
  const [myEnrollments, setMyEnrollments] = useState([]); // 🔥 PREVENT DUPLICATES
  const [loading, setLoading] = useState(true);
  const [enrollingId, setEnrollingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");

  const userId = localStorage.getItem('userId');

  // --- 1. MULTI-SERVICE DATA SYNC ---
  useEffect(() => {
    const fetchCatalogData = async () => {
      try {
        // Step A: Fetch Global Course List
        const catalogRes = await api.get('/api/course/all'); 
        setAvailableCourses(catalogRes.data);

        // Step B: Fetch Sujal's Existing IDs to block re-enrollment
        const myRes = await api.get(`/api/enrollments/student/${userId}`);
        const enrolledIds = myRes.data.map(e => e.courseId);
        setMyEnrollments(enrolledIds);

      } catch (err) {
        console.error("Catalog Sync Failed", err);
      } finally {
        setLoading(false);
      }
    };
    if(userId) fetchCatalogData();
  }, [userId]);

  const handleEnroll = async (courseId) => {
    setEnrollingId(courseId);
    try {
      // Hits: EnrollmentController @PostMapping
      // Body: { "courseId": 9 }
      await api.post('/api/enrollments', { courseId: parseInt(courseId) });
      
      alert("Nexus Handshake Successful! Curriculum initialized.");
      window.location.reload(); // Hard refresh to update UI state
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "Enrollment protocol rejected.");
    } finally {
      setEnrollingId(null);
    }
  };

  const filteredCourses = availableCourses.filter(c => 
    c.courseName.toLowerCase().includes(searchTerm.toLowerCase()) || 
    c.courseCode.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) return (
    <div className="p-20 text-center flex flex-col items-center justify-center space-y-4">
      <div className="w-12 h-12 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin" />
      <p className="font-black text-indigo-600 uppercase tracking-[0.4em] text-[10px]">Scanning Academic Ledger...</p>
    </div>
  );

  return (
    <div className="space-y-12 text-left transition-all duration-500">
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6">
        <div>
          <div className="flex items-center gap-3 text-indigo-600 mb-2">
            <Sparkles className="w-6 h-6" />
            <span className="text-[10px] font-black uppercase tracking-[0.3em]">Curriculum discovery</span>
          </div>
          <h2 className="text-5xl font-black text-slate-900 dark:text-white uppercase italic tracking-tighter leading-none">
            Course <span className="text-indigo-600">Registry</span>
          </h2>
          <p className="text-slate-400 font-bold text-xs uppercase tracking-widest mt-4">
            Initialize your cognitive learning paths via indexed curricula.
          </p>
        </div>

        {/* SEARCH BAR */}
        <div className="relative group w-full md:w-96">
          <Search className="w-4 h-4 text-slate-400 absolute left-5 top-5 group-focus-within:text-indigo-600 transition-colors" />
          <input 
            type="text"
            placeholder="Search Database..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-12 pr-6 py-4 bg-white dark:bg-slate-800 border border-slate-100 dark:border-slate-700 rounded-2xl text-sm font-black focus:ring-4 focus:ring-indigo-500/10 outline-none transition-all dark:text-white shadow-sm"
          />
        </div>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10">
        {filteredCourses.map((course) => {
          // 🔥 PREVENTION LOGIC: Check if already enrolled in this specific ID
          const isEnrolled = myEnrollments.includes(course.courseId);

          return (
            <motion.div 
              key={course.courseId}
              whileHover={{ y: -12 }}
              className="group bg-white dark:bg-slate-900 p-10 rounded-[3.5rem] border border-slate-100 dark:border-slate-800 shadow-sm flex flex-col justify-between relative overflow-hidden transition-all"
            >
              <div>
                <div className="flex justify-between items-start mb-8">
                  <span className="bg-indigo-50 dark:bg-indigo-900/30 text-indigo-600 dark:text-indigo-400 px-5 py-2 rounded-2xl text-[10px] font-black uppercase tracking-widest border border-indigo-100/50 dark:border-indigo-800/50">
                    {course.courseCode}
                  </span>
                  {isEnrolled ? (
                    <ShieldCheck className="w-7 h-7 text-emerald-500" />
                  ) : (
                    <BookPlus className="w-7 h-7 text-slate-200 dark:text-slate-700 group-hover:text-indigo-300 transition-colors" />
                  )}
                </div>
                
                <h3 className="text-2xl font-black text-slate-900 dark:text-white mb-4 tracking-tight leading-tight group-hover:text-indigo-600 transition-colors">
                  {course.courseName}
                </h3>
                
                <p className="text-xs text-slate-400 font-bold mb-10 line-clamp-3 uppercase tracking-tighter leading-relaxed">
                  {course.description || "Synthesizing foundational concepts for advanced intelligence engineering and descriptive analysis."}
                </p>
              </div>

              {/* ENROLLMENT ACTION */}
              <button
                onClick={() => handleEnroll(course.courseId)}
                disabled={enrollingId === course.courseId || isEnrolled}
                className={`w-full py-5 rounded-[1.5rem] font-black text-[11px] uppercase tracking-[0.3em] transition-all flex items-center justify-center gap-3 ${
                  isEnrolled
                    ? 'bg-emerald-50 dark:bg-emerald-900/20 text-emerald-600 cursor-default border border-emerald-100 dark:border-emerald-800/50'
                    : enrollingId === course.courseId
                      ? 'bg-slate-100 dark:bg-slate-800 text-slate-400 cursor-not-allowed italic'
                      : 'bg-indigo-600 text-white hover:bg-indigo-700 hover:scale-[1.02] shadow-xl shadow-indigo-100 dark:shadow-none'
                }`}
              >
                {isEnrolled ? (
                  <>Active Sequence <CheckCircle className="w-4 h-4" /></>
                ) : enrollingId === course.courseId ? (
                  'Synchronizing...'
                ) : (
                  <>Initialize Learning <Sparkles className="w-4 h-4 fill-current" /></>
                )}
              </button>
              
              {/* Decorative accent */}
              <div className="absolute -right-8 -bottom-8 w-32 h-32 bg-indigo-600/5 rounded-full blur-2xl group-hover:bg-indigo-600/10 transition-all duration-700" />
            </motion.div>
          );
        })}
      </div>

      {filteredCourses.length === 0 && (
        <div className="py-20 text-center bg-white dark:bg-slate-900 rounded-[3rem] border-2 border-dashed border-slate-100 dark:border-slate-800">
           <AlertCircle className="w-12 h-12 text-slate-200 dark:text-slate-700 mx-auto mb-4" />
           <p className="text-slate-400 font-black uppercase tracking-widest italic">No matching curriculum segments found.</p>
        </div>
      )}
    </div>
  );
};

export default CourseRegistryTab;