import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { ClipboardCheck, FileText, Send, Clock, BrainCircuit } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios'

const AssignmentsTab = () => {
  const [assignments, setAssignments] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    const fetchAssignments = async () => {
      try {
        // 🛡️ Fetches Sujal's specific enrollments first to find relevant assignments
        const enrollRes = await api.get('/api/enrollments/my'); //
        
        // Simplified for demo: fetching all assignments for his enrolled courses
        // In a full prod app, you'd map assignments specifically to course IDs
        const res = await api.get('/api/assignments/all'); 
        setAssignments(res.data);
      } catch (err) {
        console.error("Assignment Fetch Failed", err);
      } finally { setLoading(false); }
    };
    fetchAssignments();
  }, [userId]);

  return (
    <div className="space-y-8 text-left">
      <header className="flex justify-between items-center">
        <div>
          <h2 className="text-4xl font-black text-slate-900 dark:text-white tracking-tighter uppercase italic">
            Course Assignments
          </h2>
          <p className="text-slate-400 font-bold text-xs uppercase tracking-widest mt-2">
            Descriptive NLP Evaluation Engine Active
          </p>
        </div>
      </header>

      <div className="grid grid-cols-1 gap-6">
        {loading ? (
          <div className="p-20 text-center animate-pulse text-indigo-600 font-black">SYNCING ASSIGNMENT REPOSITORY...</div>
        ) : assignments.length > 0 ? (
          assignments.map((asn) => (
            <motion.div 
              key={asn.assignmentId}
              whileHover={{ x: 10 }}
              className="bg-white dark:bg-slate-900 p-8 rounded-[2.5rem] border border-slate-100 dark:border-slate-800 shadow-sm flex items-center justify-between group"
            >
              <div className="flex items-center gap-6">
                <div className="w-16 h-16 bg-slate-50 dark:bg-slate-800 rounded-3xl flex items-center justify-center group-hover:bg-indigo-600 transition-colors">
                  <ClipboardCheck className="w-8 h-8 text-slate-400 group-hover:text-white transition-colors" />
                </div>
                <div>
                  <h3 className="text-xl font-black text-slate-800 dark:text-white italic uppercase tracking-tight">
                    {asn.description || "Practical Lab Submission"}
                  </h3>
                  <div className="flex gap-4 mt-2">
                    <span className="flex items-center gap-1.5 text-[10px] font-black text-slate-400 uppercase tracking-widest">
                       <FileText className="w-3 h-3" /> Max Marks: {asn.totalMarks}
                    </span>
                    <span className="flex items-center gap-1.5 text-[10px] font-black text-indigo-600 dark:text-indigo-400 uppercase tracking-widest">
                       <BrainCircuit className="w-3 h-3" /> AI Evaluated
                    </span>
                  </div>
                </div>
              </div>

              <button 
                onClick={() => navigate(`/attempt/assignment/${asn.assignmentId}`)}
                className="px-8 py-4 bg-slate-900 dark:bg-indigo-600 text-white rounded-2xl font-black text-xs uppercase tracking-[0.2em] hover:scale-105 transition-all shadow-xl shadow-indigo-100 dark:shadow-none"
              >
                Start Submission
              </button>
            </motion.div>
          ))
        ) : (
          <div className="p-20 bg-white dark:bg-slate-900 rounded-[3rem] border-2 border-dashed border-slate-100 dark:border-slate-800 text-center">
            <p className="text-slate-400 font-black uppercase tracking-widest italic">No pending assignments in your current curriculum.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default AssignmentsTab;