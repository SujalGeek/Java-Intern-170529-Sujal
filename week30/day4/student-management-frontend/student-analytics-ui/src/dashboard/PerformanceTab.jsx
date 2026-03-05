import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { BrainCircuit, Target, AlertCircle, TrendingUp, BarChart3, Zap } from 'lucide-react';
import api from '../api/axios'

const PerformanceTab = () => {
  const [perf, setPerf] = useState(null);
  const [loading, setLoading] = useState(true);
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    const fetchStats = async () => {
      try {
        // Mapped to PerformanceController @GetMapping("/{studentId}/{courseId}")
        // Hardcoded CourseID 101 for testing, but you can make this dynamic
        const res = await api.get(`/api/performance/${userId}/101`);
        setPerf(res.data);
      } catch (err) {
        console.error("AI Analytics Fetch Failed", err);
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, [userId]);

  if (loading) return <div className="p-20 text-center animate-pulse text-indigo-600 font-black uppercase">Syncing AI Core...</div>;

  return (
    <div className="space-y-8 text-left transition-colors duration-500">
      {/* --- TOP ROW: AI STATUS & RISK --- */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* ML Predicted Grade */}
        <div className="bg-indigo-600 p-8 rounded-[2.5rem] text-white shadow-2xl shadow-indigo-200 dark:shadow-none relative overflow-hidden group">
          <div className="relative z-10">
            <p className="text-[10px] font-black uppercase tracking-widest opacity-70 mb-2">ML Predicted Grade</p>
            <h4 className="text-8xl font-black italic tracking-tighter mb-4 group-hover:scale-110 transition-transform duration-500">
              {perf?.predictedGrade || 'N/A'}
            </h4>
            <div className="flex items-center gap-2 bg-white/20 w-fit px-3 py-1 rounded-full text-[10px] font-bold">
               <Zap className="w-3 h-3 fill-current" /> {Math.round(perf?.predictionConfidence * 100 || 0)}% Confidence
            </div>
          </div>
          <Target className="absolute -right-4 -bottom-4 w-40 h-40 opacity-10 rotate-12" />
        </div>

        {/* Academic Risk Level */}
        <div className="bg-white dark:bg-slate-900 p-8 rounded-[2.5rem] border border-slate-100 dark:border-slate-800 shadow-sm flex flex-col justify-between">
          <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">Academic Risk Status</p>
          <div className="flex items-center gap-4">
             <h4 className={`text-4xl font-black italic uppercase tracking-tighter ${perf?.riskLevel === 'HIGH' ? 'text-red-500' : 'text-green-500'}`}>
               {perf?.riskLevel || 'Stable'}
             </h4>
             {perf?.riskLevel === 'HIGH' && <AlertCircle className="text-red-500 w-8 h-8 animate-bounce" />}
          </div>
          <p className="text-xs text-slate-400 font-bold mt-4">Calculated via Prediction Service Engine</p>
        </div>

        {/* Final weighted Score */}
        <div className="bg-white dark:bg-slate-900 p-8 rounded-[2.5rem] border border-slate-100 dark:border-slate-800 shadow-sm flex flex-col justify-between">
           <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">Weighted Performance</p>
           <div className="flex items-baseline gap-2">
              <span className="text-6xl font-black text-slate-900 dark:text-white tracking-tighter">{perf?.finalScore || 0}</span>
              <span className="text-xl font-bold text-indigo-600">%</span>
           </div>
           <div className="w-full bg-slate-100 dark:bg-slate-800 h-2 rounded-full mt-4">
              <div className="bg-indigo-600 h-full rounded-full" style={{ width: `${perf?.finalScore}%` }}></div>
           </div>
        </div>
      </div>

      {/* --- MIDDLE ROW: BLOOM ANALYTICS (Real Logic) --- */}
      <div className="bg-white dark:bg-slate-900 p-10 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm">
        <div className="flex items-center gap-4 mb-10">
           <div className="w-12 h-12 bg-indigo-50 dark:bg-indigo-900/20 rounded-2xl flex items-center justify-center">
              <BrainCircuit className="text-indigo-600 w-6 h-6" />
           </div>
           <div>
              <h3 className="text-2xl font-black text-slate-900 dark:text-white italic uppercase tracking-tighter leading-none">Bloom Cognitive Map</h3>
              <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest mt-1">Cross-Service Analytics</p>
           </div>
        </div>

        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-6">
           {['REMEMBER', 'UNDERSTAND', 'APPLY', 'ANALYZE', 'EVALUATE', 'CREATE'].map((level) => {
             // Logic to extract score from your bloomAnalysis string or map
             const score = 75; // Replace with logic to parse perf?.bloomAnalysis
             return (
               <div key={level} className="text-center space-y-3">
                  <div className="relative w-24 h-24 mx-auto">
                     <svg className="w-full h-full" viewBox="0 0 36 36">
                        <path className="stroke-slate-100 dark:stroke-slate-800" strokeWidth="3" fill="none" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
                        <path className="stroke-indigo-600" strokeWidth="3" strokeDasharray={`${score}, 100`} strokeLinecap="round" fill="none" d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831" />
                     </svg>
                     <div className="absolute inset-0 flex items-center justify-center font-black text-xs dark:text-white">{score}%</div>
                  </div>
                  <p className="text-[9px] font-black text-slate-400 uppercase tracking-widest">{level}</p>
               </div>
             );
           })}
        </div>
      </div>

      {/* --- BOTTOM ROW: DIAGNOSTIC FEEDBACK --- */}
      <div className="bg-slate-900 dark:bg-indigo-900/10 p-10 rounded-[3rem] text-white">
        <div className="flex items-start gap-6">
           <TrendingUp className="w-10 h-10 text-indigo-400 shrink-0" />
           <div className="space-y-4">
              <h3 className="text-2xl font-black italic uppercase tracking-tighter">AI Diagnostic Report</h3>
              <p className="text-slate-300 font-medium leading-relaxed max-w-4xl text-lg">
                "{perf?.diagnosticFeedback || 'Your cognitive patterns are currently being analyzed. Please attempt more assignments to calibrate the diagnostic engine.'}"
              </p>
           </div>
        </div>
      </div>
    </div>
  );
};

export default PerformanceTab;