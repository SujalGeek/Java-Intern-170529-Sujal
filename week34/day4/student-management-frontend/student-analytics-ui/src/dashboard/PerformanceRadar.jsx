import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { AlertTriangle, ShieldCheck, TrendingDown, Users } from 'lucide-react';
import api from '../api/axios';

const PerformanceRadar = ({ teacherId }) => {
  const [students, setStudents] = useState([]);

  useEffect(() => {
    const fetchRadar = async () => {
      const res = await api.get(`/api/performance/teacher/${teacherId}/risk-radar`);
      setStudents(res.data);
    };
    fetchRadar();
  }, [teacherId]);

  return (
    <div className="p-8 space-y-8">
      <div className="flex items-center gap-4">
        <TrendingDown className="text-red-500 w-8 h-8" />
        <h2 className="text-3xl font-black uppercase tracking-tighter">Student <span className="text-red-500">Risk Radar</span></h2>
      </div>

      <div className="grid grid-cols-1 gap-4">
        {students.map((s, i) => (
          <motion.div 
            key={i}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className={`p-8 rounded-[2.5rem] border-2 flex items-center justify-between transition-all ${
              s.riskLevel === 'High' 
                ? 'bg-red-50/50 border-red-200 dark:bg-red-900/10 dark:border-red-900/40 shadow-lg shadow-red-500/5' 
                : 'bg-white border-slate-100 dark:bg-slate-900 dark:border-slate-800'
            }`}
          >
            <div className="flex items-center gap-6">
              <div className={`w-14 h-14 rounded-2xl flex items-center justify-center font-black ${
                s.riskLevel === 'High' ? 'bg-red-500 text-white' : 'bg-slate-100 text-slate-500'
              }`}>
                {s.studentName.charAt(0).toUpperCase()}
              </div>
              <div>
                <h3 className="text-xl font-black uppercase italic">{s.studentName}</h3>
                <p className="text-xs font-bold text-slate-400 uppercase">{s.courseName}</p>
              </div>
            </div>

            <div className="flex items-center gap-12">
              <div className="text-center">
                <p className="text-[10px] font-black text-slate-400 uppercase mb-1">Score</p>
                <p className="text-2xl font-black text-indigo-600">{s.finalScore}%</p>
              </div>
              
              <div className="text-center">
                <p className="text-[10px] font-black text-slate-400 uppercase mb-1">Grade</p>
                <p className="text-2xl font-black">{s.finalGrade}</p>
              </div>

              <div className="flex flex-col items-end">
                {s.riskLevel === 'High' ? (
                  <span className="flex items-center gap-2 px-5 py-2 bg-red-500 text-white text-[10px] font-black rounded-full animate-pulse">
                    <AlertTriangle className="w-3 h-3" /> HIGH RISK
                  </span>
                ) : (
                  <span className="flex items-center gap-2 px-5 py-2 bg-emerald-500 text-white text-[10px] font-black rounded-full">
                    <ShieldCheck className="w-3 h-3" /> STABLE
                  </span>
                )}
              </div>
            </div>
          </motion.div>
        ))}
      </div>
    </div>
  );
};

export default PerformanceRadar;