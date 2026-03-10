import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Users, BarChart3, ShieldAlert, FileText, Download, Loader2 } from 'lucide-react';
import api from '../api/axios';

// 🔥 Receive courseId as a prop from the global TeacherDashboard header!
const TeacherOverviewTab = ({ courseId }) => {
  const [courseStats, setCourseStats] = useState(null);
  const [recentExams, setRecentExams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [downloadingId, setDownloadingId] = useState(null);

  useEffect(() => {
    // Wait until a course is selected
    if (!courseId) return;

    const fetchCourseAnalytics = async () => {
      setLoading(true);
      try {
        // 1. Fetch Real KPI Data
        const statsRes = await api.get(`/api/analytics/course/${courseId}`);
        setCourseStats(statsRes.data);

        // 2. Fetch Real Midterm Papers for this course
        const examRes = await api.get(`/api/v1/exams/course/${courseId}`);
        
        // If the backend returns a single object, wrap it in an array for mapping.
        // If it returns an array, use it directly.
        const examsData = Array.isArray(examRes.data) ? examRes.data : [examRes.data];
        setRecentExams(examsData);

      } catch (err) {
        console.error("Failed to load course analytics or exams", err);
        setRecentExams([]); // Clear if none found
      } finally {
        setLoading(false);
      }
    };
    
    fetchCourseAnalytics();
  }, [courseId]);

  // 🔥 PDF DOWNLOAD HANDLER
  const handleDownloadPDF = async (midtermId) => {
    setDownloadingId(midtermId);
    try {
      // Must specify responseType 'blob' to handle binary data from Java
      const res = await api.get(`/api/v1/exams/${midtermId}/pdf`, {
        responseType: 'blob', 
        headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
      });
      
      // Create a temporary link to force the browser download
      const blob = new Blob([res.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `EduPulse_Midterm_${midtermId}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
    } catch (err) {
      console.error("Failed to download PDF", err);
      alert("Error generating PDF. Please ensure the backend is connected.");
    } finally {
      setDownloadingId(null);
    }
  };

  if (!courseId) {
    return <div className="p-20 text-center font-black animate-pulse text-emerald-600 uppercase tracking-widest">Waiting for Course Selection...</div>;
  }

  if (loading) {
    return <div className="p-20 flex justify-center text-emerald-600"><Loader2 className="w-10 h-10 animate-spin" /></div>;
  }

  return (
    <div className="space-y-10 text-left transition-all duration-500">
      <header>
        <h2 className="text-4xl font-black text-slate-900 dark:text-white uppercase italic tracking-tighter">
          Class <span className="text-emerald-600">Analytics</span>
        </h2>
        <p className="text-slate-400 font-bold text-xs uppercase tracking-widest mt-2 flex items-center gap-2">
          Real-time performance metrics for <span className="text-emerald-600 bg-emerald-50 dark:bg-emerald-900/20 px-3 py-1 rounded-lg">Course {courseId}</span>
        </p>
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

      {/* --- RECENT AI EVALUATIONS / PAPERS --- */}
      <div className="bg-white dark:bg-slate-900 p-10 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm">
        <h3 className="text-2xl font-black text-slate-900 dark:text-white uppercase italic tracking-tighter mb-8">Generated Midterm Papers</h3>
        
        <div className="space-y-4">
          <AnimatePresence>
            {recentExams.length > 0 ? (
              recentExams.map((exam) => (
                <motion.div 
                  key={exam.midtermId} 
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  className="flex items-center justify-between p-6 bg-slate-50 dark:bg-slate-800/50 rounded-3xl border border-slate-100 dark:border-slate-700"
                >
                  <div className="flex items-center gap-5">
                    <div className="w-12 h-12 bg-emerald-100 dark:bg-emerald-900/30 text-emerald-600 rounded-full flex items-center justify-center font-black">
                      <FileText className="w-5 h-5" />
                    </div>
                    <div>
                      <p className="font-bold text-slate-800 dark:text-white text-lg">AI Generated Midterm #{exam.midtermId}</p>
                      <div className="flex items-center gap-3 mt-1">
                        <span className="text-[10px] text-slate-400 uppercase tracking-widest font-bold">Total Marks: {exam.totalMarks}</span>
                        <span className="text-[10px] text-slate-400 uppercase tracking-widest font-bold">Questions: {exam.totalQuestions}</span>
                      </div>
                    </div>
                  </div>
                  
                  {/* 🔥 DOWNLOAD PDF BUTTON */}
                  <button 
                    onClick={() => handleDownloadPDF(exam.midtermId)}
                    disabled={downloadingId === exam.midtermId}
                    className="flex items-center gap-2 px-6 py-3 bg-emerald-600 text-white font-black text-xs uppercase tracking-widest rounded-xl shadow-lg shadow-emerald-200 dark:shadow-none hover:bg-emerald-700 hover:scale-105 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {downloadingId === exam.midtermId ? (
                      <Loader2 className="w-4 h-4 animate-spin" />
                    ) : (
                      <Download className="w-4 h-4" />
                    )}
                    {downloadingId === exam.midtermId ? 'Exporting...' : 'Export PDF'}
                  </button>
                </motion.div>
              ))
            ) : (
              <div className="text-center p-8 text-slate-400 font-bold text-sm uppercase tracking-widest">
                No midterms generated for this course yet.
              </div>
            )}
          </AnimatePresence>
        </div>
      </div>
    </div>
  );
};

export default TeacherOverviewTab;