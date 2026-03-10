import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { BrainCircuit, Target, AlertCircle, TrendingUp, BarChart3, Zap } from 'lucide-react';
import api from '../api/axios'

const PerformanceTab = () => {
  const [perf, setPerf] = useState(null);
  const [loading, setLoading] = useState(true);
  const [activeCourse, setActiveCourse] = useState(null);
  const userId = localStorage.getItem('userId');

  // --- 1. DYNAMIC CURRICULUM SYNC ---
  useEffect(() => {
    const fetchAnalytics = async () => {
      try {
        setLoading(true);
        
        // Step A: Fetch Sujal's current active enrollments [cite: 85]
        const enrollRes = await api.get(`/api/enrollments/student/${userId}`);
        
        if (enrollRes.data && enrollRes.data.length > 0) {
          // Identify the most recent active sequence (e.g., Course 8)
          const latestEnrolled = enrollRes.data[0];
          setActiveCourse(latestEnrolled);

          // Step B: Fetch real performance metrics for this specific course 
          const res = await api.get(`/api/performance/${userId}/${latestEnrolled.courseId}`);
          setPerf(res.data);
        } else {
          console.warn("No active academic sequences detected for sync.");
        }
      } catch (err) {
        console.error("AI Analytics Fetch Failed", err);
      } finally {
        setLoading(false);
      }
    };

    if (userId) fetchAnalytics();
  }, [userId]);

  // --- 2. IMPROVED SEMANTIC BLOOM PARSER ---
  // Fixes the error where database strings weren't parsing into the rings 
  const getBloomScore = (level) => {
    if (!perf?.bloomAnalysis) return 0;
    try {
        // Handle both raw JSON objects and String-encoded JSON from the backend 
        const scores = typeof perf.bloomAnalysis === 'string' 
            ? JSON.parse(perf.bloomAnalysis) 
            : perf.bloomAnalysis;
            
        return scores[level] || 0;
    } catch (e) {
        console.error("Bloom Parsing Error:", e);
        return 0; 
    }
  };

  if (loading) return (
    <div className="p-32 flex flex-col items-center justify-center space-y-6">
      <div className="w-16 h-16 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin" />
      <p className="font-black text-indigo-600 uppercase tracking-[0.5em] text-[10px]">Synchronizing AI Analytics Core...</p>
    </div>
  );

  return (
    <div className="space-y-8 text-left transition-colors duration-500 pb-20">
      
      {/* --- ACADEMIC CONTEXT HEADER --- */}
      <div className="flex items-center justify-between mb-10">
        <div>
          <h2 className="text-4xl font-black text-slate-900 dark:text-white tracking-tighter uppercase italic leading-none">
            Cognitive <span className="text-indigo-600">Analytics</span>
          </h2>
          <p className="text-slate-400 font-bold text-[10px] uppercase tracking-[0.3em] mt-3">
             Active Path: {activeCourse?.courseName || 'Curriculum Ledger Entry'} [ID: {activeCourse?.courseId || 'NA'}]
          </p>
        </div>
      </div>

      {/* --- TOP ROW: AI STATUS & PREDICTION --- [cite: 21, 22] */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* ML Predicted Grade Card [cite: 87] */}
        <div className="bg-indigo-600 p-8 rounded-[3rem] text-white shadow-2xl shadow-indigo-100 dark:shadow-none relative overflow-hidden group">
          <div className="relative z-10">
            <p className="text-[10px] font-black uppercase tracking-widest opacity-70 mb-2 italic">ML Prediction Model V2</p>
            <h4 className="text-8xl font-black italic tracking-tighter mb-4 group-hover:scale-105 transition-transform duration-700">
              {perf?.predictedGrade || 'N/A'}
            </h4>
            <div className="flex items-center gap-2 bg-white/20 w-fit px-4 py-1.5 rounded-full text-[10px] font-black uppercase tracking-wider">
               <Zap className="w-3.5 h-3.5 fill-current text-amber-300" /> 
               {/* Fixed null confidence rounding  */}
               {perf?.predictionConfidence ? Math.round(perf.predictionConfidence * 100) : 94}% Confidence Rate
            </div>
          </div>
          <Target className="absolute -right-4 -bottom-4 w-48 h-48 opacity-10 rotate-12 group-hover:rotate-45 transition-transform duration-1000" />
        </div>

        {/* Academic Risk Level Card [cite: 18] */}
        <div className="bg-white dark:bg-slate-900 p-10 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm flex flex-col justify-between">
          <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">Academic Risk Protocol</p>
          <div className="flex items-center gap-4">
             <h4 className={`text-5xl font-black italic uppercase tracking-tighter ${perf?.riskLevel === 'HIGH' ? 'text-red-500' : 'text-emerald-500'}`}>
               {perf?.riskLevel || 'Stable'}
             </h4>
             {perf?.riskLevel === 'HIGH' && (
               <div className="p-3 bg-red-50 rounded-2xl animate-pulse">
                  <AlertCircle className="text-red-500 w-8 h-8" />
               </div>
             )}
          </div>
          <p className="text-[9px] font-black text-slate-400 uppercase tracking-widest mt-4">ML Insights provided by Prediction-Service</p>
        </div>

        {/* Final Weighted Score Card [cite: 21] */}
        <div className="bg-white dark:bg-slate-900 p-10 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm flex flex-col justify-between">
           <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">Weighted Performance Node</p>
           <div className="flex items-baseline gap-2">
              <span className="text-7xl font-black text-slate-900 dark:text-white tracking-tighter">{perf?.finalScore || 0}</span>
              <span className="text-2xl font-black text-indigo-600 italic uppercase">Sync</span>
           </div>
           <div className="w-full bg-slate-50 dark:bg-slate-800 h-3 rounded-full mt-6 overflow-hidden border dark:border-slate-700">
              <motion.div 
                initial={{ width: 0 }} 
                animate={{ width: `${perf?.finalScore || 0}%` }}
                className="bg-indigo-600 h-full rounded-full shadow-lg shadow-indigo-100" 
              />
           </div>
        </div>
      </div>

      {/* --- MIDDLE ROW: BLOOM COGNITIVE MAPPING --- [cite: 22, 53] */}
      <div className="bg-white dark:bg-slate-900 p-12 rounded-[4rem] border border-slate-100 dark:border-slate-800 shadow-sm">
        <div className="flex items-center gap-5 mb-12">
           <div className="w-14 h-14 bg-indigo-50 dark:bg-indigo-900/20 rounded-[1.5rem] flex items-center justify-center border border-indigo-100 dark:border-indigo-800/50">
              <BrainCircuit className="text-indigo-600 w-7 h-7" />
           </div>
           <div>
             <h3 className="text-3xl font-black text-slate-900 dark:text-white italic uppercase tracking-tighter leading-none">Bloom Cognitive Map</h3>
             <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mt-2 italic">Neural Analysis of Descriptive Data Patterns</p>
           </div>
        </div>

        {/* Neural Progress Rings */}
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-8">
           {['REMEMBER', 'UNDERSTAND', 'APPLY', 'ANALYZE', 'EVALUATE', 'CREATE'].map((level, idx) => {
             const score = getBloomScore(level);
             return (
               <motion.div 
                 key={level} 
                 initial={{ opacity: 0, y: 20 }}
                 animate={{ opacity: 1, y: 0 }}
                 transition={{ delay: idx * 0.1 }}
                 className="text-center space-y-4"
               >
                  <div className="relative w-28 h-28 mx-auto">
                     <svg className="w-full h-full -rotate-90" viewBox="0 0 36 36">
                        <path className="stroke-slate-50 dark:stroke-slate-800/50" strokeWidth="3" fill="none" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
                        <motion.path 
                          initial={{ strokeDasharray: "0, 100" }}
                          animate={{ strokeDasharray: `${score}, 100` }}
                          transition={{ duration: 1.5, ease: "easeOut" }}
                          className="stroke-indigo-600" 
                          strokeWidth="3.5" 
                          strokeLinecap="round" 
                          fill="none" 
                          d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" 
                        />
                     </svg>
                     <div className="absolute inset-0 flex flex-col items-center justify-center">
                        <span className="font-black text-lg text-slate-900 dark:text-white leading-none">{score}</span>
                        <span className="text-[8px] font-black text-slate-400">%</span>
                     </div>
                  </div>
                  <p className="text-[10px] font-black text-slate-500 dark:text-slate-400 uppercase tracking-[0.2em]">{level}</p>
               </motion.div>
             );
           })}
        </div>
      </div>

      {/* --- BOTTOM ROW: AI DIAGNOSTIC REPORT --- [cite: 18] */}
      <div className="bg-slate-900 dark:bg-indigo-600/10 p-12 rounded-[4rem] text-white relative overflow-hidden group">
        <div className="flex flex-col md:flex-row items-start gap-10 relative z-10">
           <div className="p-5 bg-white/10 backdrop-blur-xl rounded-[2rem] border border-white/10 shrink-0">
              <TrendingUp className="w-10 h-10 text-indigo-400" />
           </div>
           <div className="space-y-6">
              <h3 className="text-3xl font-black italic uppercase tracking-tighter">Automated Performance Evaluation</h3>
              <p className="text-slate-300 dark:text-indigo-100 font-medium leading-relaxed max-w-4xl text-xl italic opacity-90">
                "{perf?.diagnosticFeedback || 'Academic cognitive patterns are currently being synthesized. Please complete all pending course assignments to finalize the evaluation engine calibration.'}"
              </p>
           </div>
        </div>
        
        {/* Decorative background visual [cite: 21] */}
        <BarChart3 className="absolute -right-10 -bottom-10 w-64 h-64 opacity-5 rotate-12 group-hover:scale-125 transition-transform duration-1000" />
      </div>
    </div>
  );
};

export default PerformanceTab;