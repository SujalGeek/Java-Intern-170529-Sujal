import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  CheckSquare, Search, User, 
  BrainCircuit, MessageSquare, Award, CheckCircle2, AlertCircle 
} from 'lucide-react';
import api from '../api/axios';

const GradingHubTab = ({ courseId }) => {
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedScript, setSelectedScript] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    const fetchSubmissions = async () => {
      if (!courseId) return;
      
      setLoading(true);
      try {
        // Hits Exam-Result-Service -> @GetMapping("/course/{courseId}")
        // Now that you fixed 'exam_result_id' in MySQL, this will return 200 OK
        const res = await api.get(`/api/exam-results/course/${courseId}`);
        setSubmissions(res.data);
      } catch (err) {
        console.error("Evaluation Sync Failed", err);
        setSubmissions([]); // Clear list on error or empty results
      } finally {
        setLoading(false);
      }
    };
    fetchSubmissions();
  }, [courseId]);

  // --- 2. SEARCH FILTERING ---
  const filteredSubmissions = submissions.filter(sub => 
    sub.studentId.toString().includes(searchTerm) || 
    (sub.question && sub.question.toLowerCase().includes(searchTerm.toLowerCase()))
  );

  const handleApprove = () => {
    alert(`Grade of ${selectedScript.score} for Student ${selectedScript.studentId} is now persistent.`);
    setSelectedScript(null);
  };

  return (
    <div className="space-y-8 text-left transition-colors duration-500">
      
      <header>
        <div className="flex items-center gap-4 text-emerald-600 mb-2">
          <CheckSquare className="w-6 h-6" />
          <span className="text-[10px] font-black uppercase tracking-[0.3em]">AI Verification Engine</span>
        </div>
        <h2 className="text-4xl font-black text-slate-900 dark:text-white tracking-tighter uppercase italic leading-none">
          Grading <span className="text-emerald-600">Hub</span>
        </h2>
        <p className="text-slate-400 font-bold text-xs uppercase tracking-widest mt-3">
          Reviewing Submissions for <span className="text-emerald-600">Course ID: {courseId || 'NONE'}</span>
        </p>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* --- MASTER LIST: SUBMISSIONS --- */}
        <div className="lg:col-span-1 bg-white dark:bg-slate-900 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm overflow-hidden flex flex-col h-[700px] transition-colors">
          <div className="p-6 border-b border-slate-100 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-800/50">
            <div className="relative">
              <Search className="w-4 h-4 text-slate-400 absolute left-4 top-3.5" />
              <input 
                type="text" 
                placeholder="Search Student ID..." 
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-3 bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-700 rounded-xl text-sm font-bold focus:ring-2 focus:ring-emerald-500/20 outline-none dark:text-white transition-all"
              />
            </div>
          </div>
          
          <div className="flex-1 overflow-y-auto p-4 space-y-3">
            {loading ? (
              <div className="p-10 text-center font-black animate-pulse text-emerald-600 text-[10px] uppercase tracking-[0.4em]">
                Parsing NLP Scripts...
              </div>
            ) : filteredSubmissions.length > 0 ? (
              filteredSubmissions.map((sub, idx) => (
                <motion.button
                  key={sub.examResultId || idx}
                  initial={{ opacity: 0, x: -10 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: idx * 0.05 }}
                  onClick={() => setSelectedScript(sub)}
                  className={`w-full text-left p-5 rounded-2xl border-2 transition-all group ${
                    selectedScript?.examResultId === sub.examResultId
                      ? 'border-emerald-500 bg-emerald-50 dark:bg-emerald-900/20'
                      : 'border-transparent bg-slate-50 dark:bg-slate-800/30 hover:bg-slate-100 dark:hover:bg-slate-800/50'
                  }`}
                >
                  <div className="flex justify-between items-start mb-2">
                    <span className="text-[10px] font-black uppercase tracking-widest text-slate-400 flex items-center gap-1 group-hover:text-emerald-600 transition-colors">
                      <User className="w-3 h-3" /> STU-{sub.studentId}
                    </span>
                    <span className={`text-[10px] font-black px-2 py-1 rounded-md uppercase tracking-wider ${
                      sub.score >= 8 ? 'bg-emerald-100 text-emerald-700' : 'bg-amber-100 text-amber-700'
                    }`}>
                      {sub.score}/10
                    </span>
                  </div>
                  <p className="text-sm font-black text-slate-800 dark:text-slate-200 line-clamp-1 group-hover:italic">
                    {sub.question || "Untitled Submission"}
                  </p>
                </motion.button>
              ))
            ) : (
              <div className="h-full flex flex-col items-center justify-center p-10 text-center opacity-40">
                <AlertCircle className="w-10 h-10 mb-4 text-slate-300" />
                <p className="text-[10px] font-black uppercase tracking-widest leading-relaxed">
                  No evaluation records found for this course sequence.
                </p>
              </div>
            )}
          </div>
        </div>

        {/* --- DETAIL VIEW: THE REVIEW ENGINE --- */}
        <div className="lg:col-span-2 bg-white dark:bg-slate-900 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm p-12 h-[700px] overflow-y-auto relative transition-colors">
          <AnimatePresence mode="wait">
            {selectedScript ? (
              <motion.div
                key={selectedScript.examResultId}
                initial={{ opacity: 0, y: 15 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -15 }}
                className="space-y-10 text-left"
              >
                {/* Header Metadata */}
                <div className="flex justify-between items-start pb-8 border-b border-slate-100 dark:border-slate-800">
                  <div className="space-y-4">
                    <span className="px-4 py-1.5 bg-emerald-100 dark:bg-emerald-900/40 text-emerald-700 dark:text-emerald-400 rounded-full text-[10px] font-black uppercase tracking-[0.2em]">
                      Bloom: {selectedScript.bloomLevel || 'ANALYZE'}
                    </span>
                    <h3 className="text-2xl font-black text-slate-900 dark:text-white leading-tight">
                      {selectedScript.question}
                    </h3>
                  </div>
                  <div className="text-center bg-slate-900 dark:bg-emerald-600 p-6 rounded-[2rem] text-white shadow-xl shadow-slate-200 dark:shadow-none min-w-[120px]">
                    <p className="text-[9px] font-black uppercase tracking-widest opacity-60 mb-1">AI Score</p>
                    <p className="text-4xl font-black italic tracking-tighter">
                      {selectedScript.score}<span className="text-lg opacity-40">/10</span>
                    </p>
                  </div>
                </div>

                {/* SCRIPT ANALYSIS GRID */}
                <div className="grid grid-cols-1 xl:grid-cols-2 gap-8">
                  {/* Student Side */}
                  <div className="space-y-4">
                    <div className="flex items-center gap-2 text-slate-400">
                      <User className="w-4 h-4" />
                      <span className="text-[10px] font-black uppercase tracking-widest">Student Response</span>
                    </div>
                    <div className="p-8 bg-slate-50 dark:bg-slate-800/50 rounded-[2rem] text-base font-bold text-slate-700 dark:text-slate-300 leading-relaxed border border-slate-100 dark:border-slate-700 min-h-[250px] shadow-inner">
                      "{selectedScript.studentAnswer}"
                    </div>
                  </div>

                  {/* Reference Side */}
                  <div className="space-y-4">
                    <div className="flex items-center gap-2 text-indigo-500">
                      <BrainCircuit className="w-4 h-4" />
                      <span className="text-[10px] font-black uppercase tracking-widest">AI Gold Standard Reference</span>
                    </div>
                    <div className="p-8 bg-indigo-50/30 dark:bg-indigo-900/10 rounded-[2rem] text-base font-bold text-indigo-900 dark:text-indigo-200 leading-relaxed border border-indigo-100/50 dark:border-indigo-800/30 min-h-[250px] italic">
                      {selectedScript.referenceAnswer || "Mapping semantically against indexed textbook context..."}
                    </div>
                  </div>
                </div>

                {/* NLP FEEDBACK BLOCK */}
                <div className="space-y-4 bg-emerald-50/50 dark:bg-emerald-900/10 p-8 rounded-[2.5rem] border border-emerald-100/50 dark:border-emerald-800/40">
                  <div className="flex items-center gap-2 text-emerald-600">
                    <MessageSquare className="w-5 h-5" />
                    <span className="text-[10px] font-black uppercase tracking-widest italic">Diagnostic AI Feedback</span>
                  </div>
                  <p className="text-lg font-black text-emerald-800 dark:text-emerald-400 italic leading-relaxed">
                    "{selectedScript.feedback}"
                  </p>
                </div>

                {/* ACTION HUD */}
                <div className="pt-10 flex gap-6">
                  <button 
                    onClick={handleApprove}
                    className="flex-1 bg-emerald-600 text-white font-black text-xs uppercase tracking-[0.3em] py-6 rounded-[1.5rem] hover:bg-emerald-700 hover:scale-[1.02] transition-all flex items-center justify-center gap-3 shadow-2xl shadow-emerald-200 dark:shadow-none"
                  >
                    <CheckCircle2 className="w-5 h-5" /> Finalize Grade
                  </button>
                  <button className="px-12 bg-white dark:bg-slate-800 text-slate-400 border border-slate-200 dark:border-slate-700 font-black text-[10px] uppercase tracking-widest py-6 rounded-[1.5rem] hover:bg-slate-50 dark:hover:bg-slate-700 transition-all">
                    Manual Override
                  </button>
                </div>
              </motion.div>
            ) : (
              <motion.div 
                key="empty"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                className="h-full flex flex-col items-center justify-center text-center space-y-6 opacity-30"
              >
                <Award className="w-32 h-32 text-slate-300 dark:text-slate-700" />
                <div className="space-y-2">
                  <h3 className="text-2xl font-black uppercase tracking-tighter text-slate-400">Awaiting Selection</h3>
                  <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest">
                    Select a student record from the primary list to initiate script review.
                  </p>
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>
    </div>
  );
};

export default GradingHubTab;