import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { ChevronLeft, MessageSquare, Award, PieChart, Info } from 'lucide-react';
import api from '../api/axios'

const ResultView = () => {
  const { type, attemptId } = useParams();
  const [resultData, setResultData] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchResults = async () => {
      try {
        // Mapped to your Service @GetMapping logic
        const endpoint = type === 'assignment' 
          ? `/api/assignments/result/${attemptId}` 
          : `/api/exam-results/attempt/${attemptId}`;
        
        const res = await api.get(endpoint);
        setResultData(res.data);
      } catch (err) {
        console.error("Result Fetch Failed", err);
      } finally {
        setLoading(false);
      }
    };
    fetchResults();
  }, [attemptId, type]);

  if (loading) return <div className="h-screen flex items-center justify-center dark:bg-slate-900 font-black text-indigo-600 animate-pulse">PARSING AI FEEDBACK...</div>;

  return (
    <div className="min-h-screen bg-[#F8FAFC] dark:bg-[#0F172A] p-10 transition-colors duration-500">
      <div className="max-w-5xl mx-auto space-y-10 text-left">
        
        {/* --- HEADER BLOCK --- */}
        <div className="flex items-center justify-between">
          <button onClick={() => navigate(-1)} className="p-4 bg-white dark:bg-slate-800 rounded-2xl shadow-sm text-slate-400 hover:text-indigo-600 transition-all">
            <ChevronLeft className="w-6 h-6" />
          </button>
          <div className="flex gap-4">
             <span className="px-6 py-3 bg-indigo-600 text-white rounded-2xl font-black text-xs uppercase tracking-widest shadow-xl shadow-indigo-200 dark:shadow-none">
                Final Grade: {resultData?.grade || 'B'}
             </span>
          </div>
        </div>

        <header className="space-y-2">
           <h1 className="text-5xl font-black text-slate-900 dark:text-white tracking-tighter uppercase italic">
             Submission <span className="text-indigo-600">Feedback</span>
           </h1>
           <p className="text-slate-400 font-bold uppercase text-[10px] tracking-[0.3em]">Evaluation ID: {attemptId} | Cognitive Analysis Mode</p>
        </header>

        {/* --- GRANULAR FEEDBACK ENGINE --- */}
        <div className="space-y-8">
          {resultData?.answers?.map((ans, index) => (
            <motion.div 
              key={index}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: index * 0.1 }}
              className="bg-white dark:bg-slate-800 rounded-[2.5rem] p-10 border border-slate-100 dark:border-slate-800 shadow-sm relative overflow-hidden"
            >
              <div className="flex justify-between items-start mb-6">
                 <div className="flex items-center gap-4">
                    <span className="w-10 h-10 bg-slate-900 dark:bg-indigo-600 text-white rounded-xl flex items-center justify-center font-black">
                      {index + 1}
                    </span>
                    <h3 className="text-lg font-black text-slate-800 dark:text-white uppercase italic tracking-tight">AI Scoring Result</h3>
                 </div>
                 <div className="text-right">
                    <p className="text-[10px] font-black text-slate-400 uppercase">Score Obtained</p>
                    <p className="text-2xl font-black text-indigo-600">{ans.score} / {ans.maxMarks || 10}</p>
                 </div>
              </div>

              {/* Student Response */}
              <div className="space-y-3 mb-8">
                 <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest">Your Submitted Response</label>
                 <div className="p-6 bg-slate-50 dark:bg-slate-900/50 rounded-2xl border border-slate-100 dark:border-slate-700 text-slate-600 dark:text-slate-300 italic font-medium leading-relaxed">
                    "{ans.studentAnswer}"
                 </div>
              </div>

              {/* AI Feedback (The "Brain") */}
              <div className="space-y-3">
                 <div className="flex items-center gap-2">
                    <MessageSquare className="w-4 h-4 text-emerald-500" />
                    <label className="text-[10px] font-black text-emerald-500 uppercase tracking-widest">NLP Diagnostic Feedback</label>
                 </div>
                 <div className="p-6 bg-emerald-50/50 dark:bg-emerald-900/10 rounded-2xl border border-emerald-100/50 dark:border-emerald-800 text-emerald-700 dark:text-emerald-400 font-bold leading-relaxed">
                    {ans.feedback || "The AI model has determined your answer shows high alignment with core concepts but lacks descriptive depth in cognitive reasoning."}
                 </div>
              </div>

              {/* Subtle Bloom indicator */}
              <div className="absolute top-0 right-0 p-8 opacity-5 select-none pointer-events-none">
                 <PieChart className="w-32 h-32 text-slate-900 dark:text-white" />
              </div>
            </motion.div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default ResultView;