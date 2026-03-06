import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Users, BarChart3, TrendingUp, ShieldAlert, BookOpen } from 'lucide-react';
import api from '../api/axios';

const TeacherOverviewTab = () => {
  const [courseStats, setCourseStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [courseId, setCourseId] = useState(101); // Hardcoded for demo, normally selected from a dropdown

  useEffect(() => {
    const fetchCourseAnalytics = async () => {
      try {
        // Hits AnalyticsController @GetMapping("/course/{courseId}")
        const res = await api.get(`/api/analytics/course/${courseId}`);
        setCourseStats(res.data);
      } catch (err) {
        console.error("Failed to load course analytics", err);
      } finally {
        setLoading(false);
      }
    };
    fetchCourseAnalytics();
  }, [courseId]);

  if (loading) return <div className="p-20 text-center font-black animate-pulse text-emerald-600 uppercase tracking-widest">Aggregating Course Data...</div>;

  return (
    <div className="space-y-10 text-left">
      <header className="flex justify-between items-end">
        <div>
          <h2 className="text-4xl font-black text-slate-900 dark:text-white uppercase italic tracking-tighter">
            Class <span className="text-emerald-600">Analytics</span>
          </h2>
          <p className="text-slate-400 font-bold text-xs uppercase tracking-widest mt-2">
            Real-time performance metrics for Course {courseId}
          </p>
        </div>
        <div className="flex items-center gap-2 bg-white dark:bg-slate-800 p-2 rounded-xl shadow-sm border border-slate-100 dark:border-slate-700">
          <BookOpen className="w-5 h-5 text-emerald-600 ml-2" />
          <select 
            className="bg-transparent text-sm font-bold text-slate-700 dark:text-white outline-none cursor-pointer pr-4"
            value={courseId}
            onChange={(e) => setCourseId(e.target.value)}
          >
            <option value="101">CS101 - Intro to Programming</option>
            <option value="102">CS102 - Data Structures</option>
          </select>
        </div>
      </header>

      {/* --- KPI CARDS --- */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {[
          { label: 'Total Enrolled', value: courseStats?.totalStudents || 0, icon: Users, color: 'text-blue-500', bg: 'bg-blue-50 dark:bg-blue-900/20' },
          { label: 'Avg Exam Score', value: `${courseStats?.averageScore || 0}%`, icon: BarChart3, color: 'text-emerald-500', bg: 'bg-emerald-50 dark:bg-emerald-900/20' },
          { label: 'High Risk Students', value: courseStats?.highRiskCount || 0, icon: ShieldAlert, color: 'text-red-500', bg: 'bg-red-50 dark:bg-red-900/20' },
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

      {/* --- RECENT AI EVALUATIONS --- */}
      <div className="bg-white dark:bg-slate-900 p-10 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm">
        <h3 className="text-2xl font-black text-slate-900 dark:text-white uppercase italic tracking-tighter mb-8">Recent AI Evaluations</h3>
        <div className="space-y-4">
          {/* Mock data mapped here for visual representation until hooked to specific submissions */}
          {[1, 2, 3].map((item) => (
            <div key={item} className="flex items-center justify-between p-6 bg-slate-50 dark:bg-slate-800/50 rounded-3xl border border-slate-100 dark:border-slate-700">
              <div className="flex items-center gap-4">
                <div className="w-10 h-10 bg-emerald-100 dark:bg-emerald-900/30 text-emerald-600 rounded-full flex items-center justify-center font-black">
                  {item}
                </div>
                <div>
                  <p className="font-bold text-slate-800 dark:text-white">Midterm Exam Submission</p>
                  <p className="text-[10px] text-slate-400 uppercase tracking-widest font-bold">Processed by Hybrid NLP Engine</p>
                </div>
              </div>
              <button className="px-6 py-2 bg-white dark:bg-slate-700 text-emerald-600 dark:text-emerald-400 font-bold text-xs uppercase tracking-widest rounded-xl shadow-sm hover:scale-105 transition-all">
                Review Script
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default TeacherOverviewTab;