import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { 
  BookOpen, Clock, Star, ArrowUpRight, 
  Sparkles, GraduationCap, AlertCircle 
} from 'lucide-react';
import api from '../api/axios';

const OverviewTab = () => {
  const [myCourses, setMyCourses] = useState([]);
  const [stats, setStats] = useState({ gpa: 0, progress: 0, tasks: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    const fetchDashboardData = async () => {
      setLoading(true);
      setError(false);
      try {
        const statsRes = await api.get(`/api/performance/summary/${userId}`);
        const enrollRes = await api.get(`/api/enrollments/student/${userId}`);

        const avgProgress = enrollRes.data.length > 0 
          ? Math.round(enrollRes.data.reduce((acc, curr) => acc + (curr.progress || 0), 0) / enrollRes.data.length)
          : 0;

        setMyCourses(enrollRes.data || []); 
        setStats({
          gpa: statsRes.data.gpa ? Number(statsRes.data.gpa).toFixed(2) : "0.00",
          tasks: statsRes.data.totalTasks || 0,
          progress: avgProgress,
          semester: statsRes.data.semester || '6'
        });
      } catch (err) {
        console.error("Nexus Sync Failed", err);
        setError(true);
      } finally {
        setLoading(false);
      }
    };

    if (userId) fetchDashboardData();
  }, [userId]);

  if (loading) return (
    <div className="p-10 lg:p-20 text-center font-black animate-pulse text-indigo-600 uppercase tracking-[0.5em]">
      Syncing Academic Core...
    </div>
  );

  return (
    <div className="space-y-6 lg:space-y-10 text-left transition-colors duration-500 pb-10">
      
      {/* --- STATS HUD --- */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 lg:gap-6">
        {[
          { label: 'Academic Standing', value: `${stats.gpa} GPA`, icon: Star, color: 'text-amber-500', bg: 'bg-amber-50 dark:bg-amber-900/10' },
          { label: 'Curriculum Progress', value: `${stats.progress}%`, icon: GraduationCap, color: 'text-indigo-600', bg: 'bg-indigo-50 dark:bg-indigo-900/10' },
          { label: 'Active Tasks', value: stats.tasks, icon: Clock, color: 'text-emerald-500', bg: 'bg-emerald-50 dark:bg-emerald-900/10' },
        ].map((stat, i) => (
          <motion.div 
            key={i}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.1 }}
            className="bg-white dark:bg-slate-900 p-6 lg:p-8 rounded-[1.5rem] lg:rounded-[2.5rem] border border-slate-100 dark:border-slate-800 shadow-sm flex items-center gap-4 lg:gap-6"
          >
            <div className={`w-12 h-12 lg:w-14 lg:h-14 shrink-0 ${stat.bg} rounded-xl lg:rounded-2xl flex items-center justify-center`}>
              <stat.icon className={`w-6 h-6 lg:w-7 lg:h-7 ${stat.color}`} />
            </div>
            <div className="min-w-0">
              <p className="text-[9px] lg:text-[10px] font-black text-slate-400 uppercase tracking-widest leading-none mb-1">{stat.label}</p>
              <p className="text-xl lg:text-3xl font-black text-slate-900 dark:text-white tracking-tighter italic truncate">{stat.value}</p>
            </div>
          </motion.div>
        ))}
      </div>

      {/* --- ENROLLED COURSES --- */}
      <section>
        <div className="flex items-center justify-between mb-6 lg:mb-8">
          <div className="flex items-center gap-3 lg:gap-4">
             <div className="w-8 h-8 lg:w-10 lg:h-10 bg-indigo-600 rounded-lg lg:rounded-xl flex items-center justify-center text-white">
                <BookOpen className="w-4 h-4 lg:w-5 lg:h-5" />
             </div>
             <h2 className="text-xl lg:text-3xl font-black text-slate-900 dark:text-white uppercase italic tracking-tighter">My Active Learning</h2>
          </div>
        </div>

        {error ? (
          <div className="py-10 lg:py-20 bg-red-50 dark:bg-red-900/10 rounded-[2rem] lg:rounded-[3rem] border border-red-100 dark:border-red-900/30 text-center">
             <AlertCircle className="w-8 h-8 lg:w-10 lg:h-10 text-red-500 mx-auto mb-4" />
             <p className="text-red-800 dark:text-red-200 font-black uppercase tracking-widest text-[10px] lg:text-xs">Gateway Data Fetch Failure</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 lg:gap-8">
            {myCourses.length > 0 ? myCourses.map((course) => (
              <motion.div 
                key={course.enrollmentId}
                whileHover={{ scale: 1.01 }}
                className="group bg-white dark:bg-slate-900 p-6 lg:p-10 rounded-[2rem] lg:rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm relative overflow-hidden transition-colors"
              >
                <div className="relative z-10 flex flex-col h-full">
                  <div className="flex justify-between items-start mb-4 lg:mb-6">
                    <span className="px-3 py-1 lg:px-4 lg:py-1.5 bg-indigo-50 dark:bg-indigo-900/30 text-indigo-600 dark:text-indigo-400 rounded-full text-[9px] lg:text-[10px] font-black uppercase tracking-widest">
                      {course.status || 'ACTIVE'}
                    </span>
                    <Sparkles className="w-4 h-4 lg:w-5 lg:h-5 text-indigo-200 dark:text-slate-700" />
                  </div>
                  <h3 className="text-xl lg:text-2xl font-black text-slate-900 dark:text-white mb-2 lg:mb-4 leading-tight group-hover:text-indigo-600 transition-colors truncate">{course.courseName}</h3>
                  <p className="text-xs lg:text-sm text-slate-400 font-bold mb-6 lg:mb-10 line-clamp-1 uppercase tracking-tighter">{course.instructorName || 'AI Generated System'}</p>
                  <div className="mt-auto flex items-center justify-between gap-4">
                      <div className="space-y-0.5 lg:space-y-1">
                        <p className="text-[8px] lg:text-[10px] font-black text-slate-300 dark:text-slate-600 uppercase">Progress</p>
                        <p className="text-base lg:text-lg font-black text-slate-900 dark:text-white italic">{course.progress || 0}%</p>
                      </div>
                      <button className="px-4 py-3 lg:px-8 lg:py-4 bg-slate-900 dark:bg-indigo-600 text-white rounded-xl lg:rounded-2xl font-black text-[9px] lg:text-[10px] uppercase tracking-widest hover:shadow-xl transition-all shrink-0">Continue</button>
                  </div>
                </div>
                <span className="absolute -right-4 -bottom-4 text-6xl lg:text-8xl font-black text-slate-50 dark:text-slate-800/20 -rotate-12 select-none group-hover:rotate-0 transition-transform duration-700 pointer-events-none">{course.courseCode || 'EDU'}</span>
              </motion.div>
            )) : (
              <div className="col-span-full py-10 lg:py-20 bg-white dark:bg-slate-900 rounded-[2rem] border-2 border-dashed border-slate-100 dark:border-slate-800 text-center">
                 <p className="text-slate-400 font-black uppercase tracking-widest italic leading-relaxed text-xs lg:text-sm">No active enrollments detected.</p>
              </div>
            )}
          </div>
        )}
      </section>

      {/* --- AI ACTION CARDS --- */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 lg:gap-8">
        <div className="bg-slate-900 p-8 lg:p-12 rounded-[2rem] lg:rounded-[3.5rem] text-white flex flex-col justify-between group shadow-xl">
            <div>
              <h4 className="text-2xl lg:text-3xl font-black uppercase italic tracking-tighter mb-2 lg:mb-4">Smart Exam Evaluation</h4>
              <p className="text-slate-400 font-medium text-xs lg:text-sm leading-relaxed mb-6 lg:mb-10 opacity-70">Submit midterms for instant Bloom cognitive evaluation.</p>
            </div>
            <button className="w-full sm:w-fit flex items-center justify-center gap-3 lg:gap-4 text-[9px] lg:text-[10px] font-black uppercase tracking-[0.2em] bg-indigo-600 px-6 py-3 lg:px-8 lg:py-4 rounded-xl lg:rounded-2xl hover:bg-indigo-500 transition-colors">
              Access Midterms <ArrowUpRight className="w-3 h-3 lg:w-4 lg:h-4" />
            </button>
        </div>
        <div className="bg-indigo-600 p-8 lg:p-12 rounded-[2rem] lg:rounded-[3.5rem] text-white flex flex-col justify-between group shadow-2xl shadow-indigo-100 dark:shadow-none">
            <div>
              <h4 className="text-2xl lg:text-3xl font-black uppercase italic tracking-tighter mb-2 lg:mb-4">Predictive Analytics</h4>
              <p className="text-indigo-100 font-medium text-xs lg:text-sm leading-relaxed mb-6 lg:mb-10 opacity-80">ML risk projection for Semester {stats.semester}.</p>
            </div>
            <button className="w-full sm:w-fit flex items-center justify-center gap-3 lg:gap-4 text-[9px] lg:text-[10px] font-black uppercase tracking-[0.2em] bg-white text-indigo-600 px-6 py-3 lg:px-8 lg:py-4 rounded-xl lg:rounded-2xl hover:scale-105 transition-all">
              View Insights <ArrowUpRight className="w-3 h-3 lg:w-4 lg:h-4" />
            </button>
        </div>
      </div>
    </div>
  );
};

export default OverviewTab;