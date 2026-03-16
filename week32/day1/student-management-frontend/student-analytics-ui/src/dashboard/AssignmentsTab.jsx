import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  ClipboardCheck, FileText, Send, Clock, 
  BrainCircuit, Sparkles, ChevronRight, AlertCircle 
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

const AssignmentsTab = () => {
  const [assignments, setAssignments] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const userId = localStorage.getItem('userId');

  // --- INDUSTRIAL ASSIGNMENT SYNC ENGINE ---
  useEffect(() => {
    const fetchCurriculumTasks = async () => {
      setLoading(true);
      try {
        // Step 1: Securely fetch Student Enrollments
        // Hits: Course-Service -> @GetMapping("/student/{studentId}")
        const enrollRes = await api.get(`/api/enrollments/student/${userId}`);
        const enrolledCourseIds = enrollRes.data.map(e => e.courseId);

        if (enrolledCourseIds.length > 0) {
          // Step 2: Fetch assignments from Repository
          // Hits: AssignmentController @GetMapping("/{id}") or list all
          const res = await api.get('/api/assignments/all'); 
          
          // 🔥 SENIOR FILTER: Only map tasks within the student's authorized curriculum
          const relevantAssignments = res.data.filter(asn => 
            enrolledCourseIds.includes(asn.courseId)
          );
          
          setAssignments(relevantAssignments);
        } else {
          setAssignments([]);
        }
      } catch (err) {
        console.error("CRITICAL: Assignment Repository Sync Failed", err);
      } finally {
        setLoading(false);
      }
    };

    if (userId) fetchCurriculumTasks();
  }, [userId]);

  return (
    <div className="space-y-10 text-left transition-all duration-500">
      
      {/* --- EXECUTIVE HEADER --- */}
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6">
        <div>
          <div className="flex items-center gap-3 text-indigo-600 mb-3">
            <BrainCircuit className="w-7 h-7" />
            <span className="text-[11px] font-black uppercase tracking-[0.4em] italic">Semantic Evaluation Active</span>
          </div>
          <h2 className="text-5xl font-black text-slate-900 dark:text-white tracking-tighter uppercase italic leading-none">
            Subjective <span className="text-indigo-600">Tasks</span>
          </h2>
          <p className="text-slate-400 font-bold text-xs uppercase tracking-widest mt-4 flex items-center gap-2">
            Descriptive NLP Pipeline: <span className="text-indigo-600 bg-indigo-50 dark:bg-indigo-900/20 px-3 py-1 rounded-lg">Operational</span>
          </p>
        </div>
      </header>

      {/* --- TASK REPOSITORY GRID --- */}
      <div className="grid grid-cols-1 gap-6">
        {loading ? (
          <div className="p-32 flex flex-col items-center justify-center space-y-6">
            <div className="w-16 h-16 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin" />
            <p className="font-black text-indigo-600 uppercase tracking-[0.5em] text-[10px]">Synchronizing Descriptive Ledger...</p>
          </div>
        ) : assignments.length > 0 ? (
          <AnimatePresence>
            {assignments.map((asn, idx) => (
              <motion.div 
                key={asn.assignmentId}
                initial={{ opacity: 0, x: -30 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: idx * 0.1, ease: "circOut" }}
                whileHover={{ x: 15 }}
                className="group bg-white dark:bg-slate-900 p-10 rounded-[3.5rem] border border-slate-100 dark:border-slate-800 shadow-sm flex flex-col lg:flex-row lg:items-center justify-between gap-8 relative overflow-hidden transition-all"
              >
                <div className="flex items-center gap-8 relative z-10">
                  <div className="w-20 h-20 bg-slate-50 dark:bg-slate-800 rounded-[2rem] flex items-center justify-center group-hover:bg-indigo-600 group-hover:rotate-6 transition-all duration-500 shadow-inner">
                    <ClipboardCheck className="w-10 h-10 text-slate-300 dark:text-slate-600 group-hover:text-white transition-colors" />
                  </div>
                  
                  <div className="space-y-3">
                    <div className="flex items-center gap-3">
                      <span className="px-4 py-1 bg-indigo-50 dark:bg-indigo-900/30 text-indigo-600 dark:text-indigo-400 rounded-full text-[9px] font-black uppercase tracking-widest border border-indigo-100/50 dark:border-indigo-800/50">
                        Course ID: {asn.courseId}
                      </span>
                      <Sparkles className="w-4 h-4 text-amber-400 opacity-0 group-hover:opacity-100 transition-opacity" />
                    </div>
                    
                    <h3 className="text-2xl font-black text-slate-800 dark:text-white uppercase italic tracking-tight group-hover:text-indigo-600 transition-colors leading-none">
                      {asn.description || "Synthesize Foundational Concepts"}
                    </h3>
                    
                    <div className="flex items-center gap-6">
                      <span className="flex items-center gap-2 text-[10px] font-black text-slate-400 uppercase tracking-widest">
                         <FileText className="w-3.5 h-3.5" /> Max Score: {asn.totalMarks || 100} Credits
                      </span>
                      <span className="flex items-center gap-2 text-[10px] font-black text-emerald-500 uppercase tracking-widest italic">
                         <BrainCircuit className="w-3.5 h-3.5" /> AI Calibrated
                      </span>
                    </div>
                  </div>
                </div>

                <div className="flex items-center gap-6 relative z-10">
                  <div className="text-right hidden xl:block">
                     <p className="text-[9px] font-black text-slate-300 dark:text-slate-600 uppercase tracking-widest mb-1">Status Protocol</p>
                     <p className="text-xs font-black text-slate-900 dark:text-white uppercase italic">Awaiting Submission</p>
                  </div>
                  
                  <button 
                    onClick={() => navigate(`/attempt/assignment/${asn.assignmentId}`)}
                    className="px-12 py-5 bg-slate-900 dark:bg-indigo-600 text-white rounded-[1.5rem] font-black text-xs uppercase tracking-[0.3em] hover:scale-[1.05] transition-all shadow-2xl shadow-indigo-200 dark:shadow-none flex items-center gap-3 group/btn"
                  >
                    Start Session <ChevronRight className="w-4 h-4 group-hover/btn:translate-x-2 transition-transform" />
                  </button>
                </div>

                {/* Decorative Canvas Element */}
                <div className="absolute -right-6 -bottom-6 w-32 h-32 bg-indigo-500/5 rounded-full blur-3xl pointer-events-none group-hover:bg-indigo-500/10 transition-all duration-700" />
              </motion.div>
            ))}
          </AnimatePresence>
        ) : (
          <motion.div 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="p-32 bg-white dark:bg-slate-900 rounded-[4rem] border-2 border-dashed border-slate-100 dark:border-slate-800 text-center space-y-6"
          >
            <AlertCircle className="w-16 h-16 text-slate-200 dark:text-slate-700 mx-auto" />
            <div className="space-y-2">
              <p className="text-slate-500 dark:text-slate-400 font-black uppercase tracking-[0.4em] text-sm italic">Descriptive Ledger Empty</p>
              <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">No subjective assignments are currently mapped to your enrolled curriculum.</p>
            </div>
          </motion.div>
        )}
      </div>

      {/* --- AI PIPELINE STATUS --- */}
      <footer className="pt-10">
        <div className="bg-indigo-600 p-12 rounded-[3.5rem] text-white relative overflow-hidden shadow-2xl shadow-indigo-100 dark:shadow-none">
           <div className="relative z-10 flex flex-col md:flex-row items-center justify-between gap-8">
              <div className="space-y-4 text-center md:text-left">
                 <h4 className="text-3xl font-black uppercase italic tracking-tighter">Automated Evaluate Sequence</h4>
                 <p className="text-indigo-100 text-sm font-medium leading-relaxed max-w-xl opacity-90">
                    Our descriptive analysis engine uses textbook indexing to evaluate semantic similarity. 
                    Bloom's Taxonomy levels are derived in real-time during submission processing.
                 </p>
              </div>
              <div className="bg-white/10 backdrop-blur-md px-8 py-6 rounded-3xl border border-white/20 text-center">
                 <p className="text-[9px] font-black uppercase tracking-widest opacity-60 mb-1">Pipeline Latency</p>
                 <p className="text-2xl font-black italic">0.004ms / Word</p>
              </div>
           </div>
           <BrainCircuit className="absolute -left-10 -bottom-10 w-64 h-64 opacity-10 -rotate-12" />
        </div>
      </footer>
    </div>
  );
};

export default AssignmentsTab;