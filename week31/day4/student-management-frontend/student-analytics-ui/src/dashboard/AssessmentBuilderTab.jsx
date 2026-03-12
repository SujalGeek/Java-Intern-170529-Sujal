import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  FileSignature, CheckCircle2, Zap, LayoutList, 
  FileText, ClipboardCheck, BookOpen, Loader2, AlertCircle , ShieldCheck
} from 'lucide-react';
import api from '../api/axios';

// 🔥 Receive courseId from the global TeacherDashboard header!
const AssessmentBuilderTab = ({ courseId }) => {
  const [isGenerating, setIsGenerating] = useState(false);
  const [successData, setSuccessData] = useState(null);
  
  // Dynamic configuration state
  const [config, setConfig] = useState({
    type: 'midterm', // 'midterm', 'quiz', or 'assignment'
    description: '',
    totalMarks: 100
  });

  // --- 1. GLOBAL CONTEXT SYNC ---
  // When you change the course in the dashboard header, we reset the success state
  useEffect(() => {
    setSuccessData(null);
  }, [courseId]);

  const handleGenerate = async () => {
    if (!config.description) {
      alert("Please provide a topic or description for the AI to process.");
      return;
    }

    if (!courseId) {
      alert("No active course detected. Please select a course from the header.");
      return;
    }

    setIsGenerating(true);
    setSuccessData(null);

    const token = localStorage.getItem('token');
    const reqHeaders = { 
        headers: { 'Authorization': `Bearer ${token}` } 
    };

    try {
      let res;
      
      // 🔀 ISOLATED MICROSERVICE ROUTING Logic
      if (config.type === 'midterm') {
        // 🔥 Midterm Service expects 'total_marks' (snake_case)
        const midtermPayload = {
            courseId: parseInt(courseId),
            description: config.description,
            total_marks: parseInt(config.totalMarks) 
        };
        
        res = await api.post('/api/v1/exams/generate-dynamic', midtermPayload, reqHeaders);
        setSuccessData({ id: res.data.midtermId, message: "Dynamic Midterm Successfully Synthesized" });

      } else if (config.type === 'quiz') {
        // 🔥 Quiz Service expects courseId and description
        const quizPayload = {
            courseId: parseInt(courseId),
            description: config.description
        };
        
        res = await api.post('/api/quiz/generate', quizPayload, reqHeaders);
        setSuccessData({ id: res.data.quizId, message: "Objective Quiz Successfully Generated" });

      } else if (config.type === 'assignment') {
        // 🔥 Assignment Service DTO expects 'totalMarks' (camelCase)
        const assignmentPayload = {
            courseId: parseInt(courseId),
            description: config.description,
            totalMarks: parseInt(config.totalMarks), 
            difficulty: "medium" 
        };
        
        res = await api.post('/api/assignments/generate', assignmentPayload, reqHeaders);
        setSuccessData({ id: res.data.assignmentId, message: "Subjective Assignment Successfully Generated" });
      }

    } catch (err) {
      console.error("AI Generation Failed", err);
      alert("Generation failed. Please ensure the Knowledge Base is indexed for this course.");
    } finally {
      setIsGenerating(false);
    }
  };

  const assessmentTypes = [
    { id: 'midterm', label: 'Midterm Exam', icon: FileText, desc: 'Multi-section paper (MCQ & Subjective)' },
    { id: 'quiz', label: 'Quick Quiz', icon: LayoutList, desc: '100% Auto-graded Objective Questions' },
    { id: 'assignment', label: 'Assignment', icon: ClipboardCheck, desc: 'Deep-dive subjective evaluation' }
  ];

  return (
    <div className="space-y-8 text-left transition-colors duration-500 pb-20">
      <header>
        <div className="flex items-center gap-4 text-emerald-600 mb-2">
          <FileSignature className="w-6 h-6" />
          <span className="text-[10px] font-black uppercase tracking-[0.3em]">AI Generator Engine Active</span>
        </div>
        <h2 className="text-4xl font-black text-slate-900 dark:text-white tracking-tighter uppercase italic">
          Assessment <span className="text-emerald-600">Builder</span>
        </h2>
        <p className="text-slate-400 font-bold text-xs uppercase tracking-widest mt-2">
          Generating for <span className="text-emerald-600 bg-emerald-50 dark:bg-emerald-900/20 px-3 py-1 rounded-lg italic">Course {courseId || 'Select a Course'}</span>
        </p>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* --- MAIN CONFIGURATION PANEL --- */}
        <div className="lg:col-span-2 bg-white dark:bg-slate-900 p-10 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm space-y-8">
          
          {/* Assessment Type Selector */}
          <div>
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 mb-3 block opacity-60">Evaluation Format</label>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              {assessmentTypes.map((type) => (
                <button
                  key={type.id}
                  onClick={() => setConfig({ ...config, type: type.id })}
                  className={`p-5 rounded-[1.5rem] border-2 text-left transition-all relative overflow-hidden group ${
                    config.type === type.id 
                      ? 'border-emerald-500 bg-emerald-50 dark:bg-emerald-900/20' 
                      : 'border-slate-100 dark:border-slate-800 hover:border-emerald-200 dark:hover:border-emerald-800'
                  }`}
                >
                  <type.icon className={`w-6 h-6 mb-3 ${config.type === type.id ? 'text-emerald-600' : 'text-slate-400'}`} />
                  <p className={`font-black uppercase tracking-tight text-sm ${config.type === type.id ? 'text-emerald-700 dark:text-emerald-400' : 'text-slate-700 dark:text-slate-300'}`}>
                    {type.label}
                  </p>
                  <p className="text-[9px] text-slate-400 mt-1 font-bold uppercase tracking-wider">{type.desc}</p>
                </button>
              ))}
            </div>
          </div>

          {/* Configuration Data Row */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 opacity-60">Course Context</label>
              <div className="relative mt-2">
                <BookOpen className="w-5 h-5 text-emerald-600 absolute left-4 top-4" />
                <div className="w-full pl-12 pr-5 py-4 bg-slate-50 dark:bg-slate-800 rounded-2xl text-slate-400 dark:text-slate-500 font-bold border border-transparent">
                  ID: {courseId || 'Select in Header'}
                </div>
              </div>
            </div>

            <AnimatePresence mode="wait">
              {(config.type === 'midterm' || config.type === 'assignment') && (
                <motion.div initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: 10 }}>
                  <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 opacity-60">Total Marks Weight</label>
                  <input 
                    type="number" 
                    value={config.totalMarks}
                    onChange={(e) => setConfig({...config, totalMarks: e.target.value})}
                    className="w-full mt-2 p-4 bg-slate-50 dark:bg-slate-800 border-2 border-transparent rounded-2xl text-slate-700 dark:text-white font-bold focus:border-emerald-500/30 outline-none transition-all" 
                  />
                </motion.div>
              )}
            </AnimatePresence>
          </div>

          {/* AI Prompt / Description */}
          <div>
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 opacity-60">AI Topic Prompt</label>
            <textarea 
              value={config.description}
              onChange={(e) => setConfig({...config, description: e.target.value})}
              className="w-full mt-2 p-6 bg-slate-50 dark:bg-slate-800 border-2 border-transparent rounded-[1.5rem] text-slate-700 dark:text-slate-200 font-medium focus:border-emerald-500/30 outline-none transition-all resize-none"
              rows="4"
              placeholder="e.g. Generate an exam covering advanced Polymorphism and Exception Handling in Java..."
            />
          </div>

          <button 
            onClick={handleGenerate}
            disabled={isGenerating || !courseId}
            className={`w-full py-6 rounded-2xl font-black uppercase tracking-[0.2em] transition-all flex items-center justify-center gap-3 shadow-xl ${
              isGenerating 
                ? 'bg-slate-200 dark:bg-slate-800 text-slate-400 cursor-wait' 
                : !courseId ? 'bg-slate-100 dark:bg-slate-900 text-slate-400 cursor-not-allowed' : 'bg-emerald-600 text-white hover:bg-emerald-700 hover:scale-[1.01] shadow-emerald-200 dark:shadow-none'
            }`}
          >
            {isGenerating ? (
              <>
                <Loader2 className="w-5 h-5 animate-spin" />
                Synthesizing Assessment...
              </>
            ) : (
              <>
                Initialize Generation <Zap className="w-5 h-5 fill-current" />
              </>
            )}
          </button>
        </div>

        {/* --- STATUS & PREVIEW PANEL --- */}
        <div className="space-y-6">
          <AnimatePresence>
            {successData && (
              <motion.div 
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                className="bg-emerald-600 p-8 rounded-[2.5rem] text-white shadow-2xl relative overflow-hidden"
              >
                <div className="relative z-10">
                  <CheckCircle2 className="w-12 h-12 text-emerald-300 mb-4" />
                  <h3 className="text-2xl font-black uppercase italic tracking-tighter mb-2">Protocol Success</h3>
                  <p className="text-emerald-100 font-medium text-xs mb-6">{successData.message}</p>
                  
                  <div className="bg-emerald-700/50 p-4 rounded-xl flex justify-between items-center border border-emerald-500/30">
                    <span className="text-[10px] font-black uppercase tracking-widest text-emerald-200">System ID</span>
                    <span className="text-xl font-black tracking-tighter">{successData.id}</span>
                  </div>
                </div>
                <Zap className="absolute -right-4 -bottom-4 w-32 h-32 text-white/10 rotate-12" />
              </motion.div>
            )}
          </AnimatePresence>

          <div className="bg-slate-900 p-8 rounded-[2.5rem] text-white border border-slate-800 shadow-xl">
             <div className="flex items-center gap-3 mb-6">
               <ShieldCheck className="w-5 h-5 text-emerald-500" />
               <h4 className="font-black uppercase tracking-widest text-xs text-emerald-400">RAG Pipeline Guard</h4>
             </div>
             <ul className="space-y-4 text-[11px] font-bold text-slate-400 uppercase tracking-tight">
               <li className="flex gap-3 items-start"><CheckCircle2 className="w-3.5 h-3.5 text-emerald-500 shrink-0" /> Requires pre-indexed textbook material.</li>
               <li className="flex gap-3 items-start"><CheckCircle2 className="w-3.5 h-3.5 text-emerald-500 shrink-0" /> Questions are dynamically weighted to Bloom's levels.</li>
               <li className="flex gap-3 items-start"><CheckCircle2 className="w-3.5 h-3.5 text-emerald-500 shrink-0" /> Immediate deployment to student classroom.</li>
             </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AssessmentBuilderTab;