import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { 
  BookOpen, Clock, Star, ArrowUpRight, 
  Search, Filter, Sparkles, GraduationCap 
} from 'lucide-react';
import api from '../api/axios'

const OverviewTab = () => {
  const [myCourses, setMyCourses] = useState([]);
  const [stats, setStats] = useState({ gpa: 0, progress: 0, tasks: 0 });
  const [loading, setLoading] = useState(true);
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        // 1. Fetch Sujal's Enrollments from Enrollment-Service
        const enrollRes = await api.get(`/api/enrollments/student/${userId}`);
        
        // 2. Fetch Performance Stats from Analytics-Service
        const statsRes = await api.get(`/api/analytics/summary/${userId}`);
        
        setMyCourses(enrollRes.data); 
        setStats({
          gpa: statsRes.data.cgpa || 0,
          progress: statsRes.data.completionRate || 0,
          tasks: statsRes.data.pendingTasks || 0
        });
      } catch (err) {
        console.error("Nexus Sync Failed", err);
      } finally {
        setLoading(false);
      }
    };
    fetchDashboardData();
  }, [userId]);

  if (loading) return <div className="p-20 text-center font-black animate-pulse text-indigo-600 uppercase tracking-widest">Initialising Dashboard Data...</div>;

  return (
    <div className="space-y-10 text-left">
      {/* --- STATS HUD --- */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
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
            className="bg-white dark:bg-slate-900 p-8 rounded-[2.5rem] border border-slate-100 dark:border-slate-800 shadow-sm flex items-center gap-6"
          >
            <div className={`w-14 h-14 ${stat.bg} rounded-2xl flex items-center justify-center`}>
              <stat.icon className={`w-7 h-7 ${stat.color}`} />
            </div>
            <div>
              <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest leading-none mb-1">{stat.label}</p>
              <p className="text-3xl font-black text-slate-900 dark:text-white tracking-tighter italic">{stat.value}</p>
            </div>
          </motion.div>
        ))}
      </div>

      {/* --- ENROLLED COURSES ENGINE --- */}
      <section>
        <div className="flex items-center justify-between mb-8">
          <div className="flex items-center gap-4">
             <div className="w-10 h-10 bg-indigo-600 rounded-xl flex items-center justify-center text-white">
                <BookOpen className="w-5 h-5" />
             </div>
             <h2 className="text-3xl font-black text-slate-900 dark:text-white uppercase italic tracking-tighter">My Active Learning</h2>
          </div>
          <button className="flex items-center gap-2 text-xs font-black text-indigo-600 uppercase tracking-widest hover:gap-4 transition-all">
             Explore More <ArrowUpRight className="w-4 h-4" />
          </button>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {myCourses.length > 0 ? myCourses.map((course, idx) => (
            <motion.div 
              key={course.courseId}
              whileHover={{ scale: 1.02 }}
              className="group bg-white dark:bg-slate-900 p-10 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm relative overflow-hidden"
            >
              <div className="relative z-10 flex flex-col h-full">
                <div className="flex justify-between items-start mb-6">
                  <span className="px-4 py-1.5 bg-indigo-50 dark:bg-indigo-900/30 text-indigo-600 dark:text-indigo-400 rounded-full text-[10px] font-black uppercase tracking-widest">
                    {course.category || 'Tech Core'}
                  </span>
                  <Sparkles className="w-5 h-5 text-indigo-200 dark:text-slate-700" />
                </div>

                <h3 className="text-2xl font-black text-slate-900 dark:text-white mb-4 leading-tight">
                  {course.courseName}
                </h3>
                
                <p className="text-sm text-slate-400 font-bold mb-10 line-clamp-2 uppercase tracking-tighter">
                  Instructor: {course.instructorName || 'AI Generated System'}
                </p>

                <div className="mt-auto flex items-center justify-between">
                   <div className="space-y-1">
                      <p className="text-[10px] font-black text-slate-300 dark:text-slate-600 uppercase">Current Progress</p>
                      <p className="text-lg font-black text-slate-900 dark:text-white italic">{course.progress || 0}%</p>
                   </div>
                   <button className="px-8 py-4 bg-slate-900 dark:bg-indigo-600 text-white rounded-2xl font-black text-[10px] uppercase tracking-widest hover:shadow-xl transition-all">
                      Continue Learning
                   </button>
                </div>
              </div>
              {/* Subtle background ID tag */}
              <span className="absolute -right-4 -bottom-4 text-8xl font-black text-slate-50 dark:text-slate-800/20 -rotate-12 select-none group-hover:rotate-0 transition-transform duration-700">
                {course.courseCode || 'EDU'}
              </span>
            </motion.div>
          )) : (
            <div className="col-span-full py-20 bg-white dark:bg-slate-900 rounded-[3rem] border-2 border-dashed border-slate-100 dark:border-slate-800 text-center">
               <p className="text-slate-400 font-black uppercase tracking-widest italic">No active enrollments detected in Core Database.</p>
            </div>
          )}
        </div>
      </section>

      {/* --- AI ACTION CARDS --- */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div className="bg-slate-900 p-12 rounded-[3.5rem] text-white flex flex-col justify-between group">
           <div>
              <h4 className="text-3xl font-black uppercase italic tracking-tighter mb-4">Smart Exam Evaluation</h4>
              <p className="text-slate-400 font-medium text-sm leading-relaxed mb-10">Submit your midterms and let our AI evaluate your Bloom cognitive levels instantly. No manual grading required.</p>
           </div>
           <button className="w-fit flex items-center gap-4 text-[10px] font-black uppercase tracking-[0.3em] bg-indigo-600 px-8 py-4 rounded-2xl group-hover:bg-indigo-500 transition-colors">
              Access Midterms <ArrowUpRight className="w-4 h-4" />
           </button>
        </div>

        <div className="bg-indigo-600 p-12 rounded-[3.5rem] text-white flex flex-col justify-between group shadow-2xl shadow-indigo-200 dark:shadow-none">
           <div>
              <h4 className="text-3xl font-black uppercase italic tracking-tighter mb-4">Predictive Analytics</h4>
              <p className="text-indigo-100 font-medium text-sm leading-relaxed mb-10">Our ML engine has calculated your risk level for Semester {stats.semester}. Check your performance tab for details.</p>
           </div>
           <button className="w-fit flex items-center gap-4 text-[10px] font-black uppercase tracking-[0.3em] bg-white text-indigo-600 px-8 py-4 rounded-2xl hover:opacity-90 transition-all">
              View Insights <ArrowUpRight className="w-4 h-4" />
           </button>
        </div>
      </div>
    </div>
  );
};

export default OverviewTab;