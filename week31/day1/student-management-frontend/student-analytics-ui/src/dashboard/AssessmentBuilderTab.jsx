import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  FileSignature, CheckCircle2, Zap, LayoutList, 
  FileText, ClipboardCheck, BookOpen 
} from 'lucide-react';
import api from '../api/axios';

const AssessmentBuilderTab = () => {
  const [isGenerating, setIsGenerating] = useState(false);
  const [successData, setSuccessData] = useState(null);
  
  // Default configuration state
  const [config, setConfig] = useState({
    courseId: '101',
    type: 'midterm', // 'midterm', 'quiz', or 'assignment'
    description: '',
    totalMarks: 100
  });

  const handleGenerate = async () => {
    if (!config.description) {
      alert("Please provide a topic or description for the AI to process.");
      return;
    }

    setIsGenerating(true);
    setSuccessData(null);

    try {
      let res;
      // 🔀 DYNAMIC ROUTING TO MICROSERVICES
      if (config.type === 'midterm') {
        // Hits: AiIntegrationService -> /api/v1/exams/generate-dynamic
        res = await api.post('/api/v1/exams/generate-dynamic', {
          courseId: parseInt(config.courseId),
          description: config.description,
          total_marks: parseInt(config.totalMarks)
        });
        setSuccessData({ id: res.data.midtermId, message: "Dynamic Midterm Generated" });

      } else if (config.type === 'quiz') {
        // Hits: QuizService -> /api/quiz/generate
        res = await api.post('/api/quiz/generate', {
          courseId: parseInt(config.courseId),
          description: config.description
        });
        setSuccessData({ id: res.data.quizId, message: "Objective Quiz Generated" });

      } else if (config.type === 'assignment') {
        // Hits: AssignmentService -> /api/assignments/generate (Uses RequestParams)
        res = await api.post(`/api/assignments/generate?courseId=${config.courseId}&description=${encodeURIComponent(config.description)}`);
        setSuccessData({ id: res.data.assignmentId, message: "Subjective Assignment Generated" });
      }

    } catch (err) {
      console.error("AI Generation Failed", err);
      alert("Generation failed. Please ensure the NLP Core is running and textbook is indexed.");
    } finally {
      setIsGenerating(false);
    }
  };

  // UI Helper for the Type Selector
  const assessmentTypes = [
    { id: 'midterm', label: 'Midterm Exam', icon: FileText, desc: 'Multi-section paper (MCQ & Subjective)' },
    { id: 'quiz', label: 'Quick Quiz', icon: LayoutList, desc: '100% Auto-graded Objective Questions' },
    { id: 'assignment', label: 'Assignment', icon: ClipboardCheck, desc: 'Deep-dive subjective evaluation' }
  ];

  return (
    <div className="space-y-8 text-left transition-colors duration-500">
      <header>
        <div className="flex items-center gap-4 text-emerald-600 mb-2">
          <FileSignature className="w-6 h-6" />
          <span className="text-[10px] font-black uppercase tracking-[0.3em]">AI Generator Active</span>
        </div>
        <h2 className="text-4xl font-black text-slate-900 dark:text-white tracking-tighter uppercase italic">
          Assessment <span className="text-emerald-600">Builder</span>
        </h2>
        <p className="text-slate-400 font-bold text-xs uppercase tracking-widest mt-2">
          Design cognitive evaluations instantly using the knowledge base.
        </p>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* --- MAIN CONFIGURATION PANEL --- */}
        <div className="lg:col-span-2 bg-white dark:bg-slate-900 p-10 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm space-y-8">
          
          {/* Assessment Type Selector */}
          <div>
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 mb-3 block">Evaluation Format</label>
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

          {/* Course & Marks Row */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">Target Course ID</label>
              <div className="relative group mt-2">
                <BookOpen className="w-5 h-5 text-slate-300 absolute left-4 top-4 group-focus-within:text-emerald-500 transition-colors" />
                <input 
                  type="number" 
                  value={config.courseId}
                  onChange={(e) => setConfig({...config, courseId: e.target.value})}
                  className="w-full pl-12 pr-5 py-4 bg-slate-50 dark:bg-slate-800 border-none rounded-2xl text-slate-700 dark:text-white font-bold focus:ring-2 focus:ring-emerald-500/20 outline-none transition-all" 
                />
              </div>
            </div>

            <AnimatePresence>
              {config.type === 'midterm' && (
                <motion.div initial={{ opacity: 0, scale: 0.9 }} animate={{ opacity: 1, scale: 1 }} exit={{ opacity: 0, scale: 0.9 }}>
                  <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">Total Marks</label>
                  <input 
                    type="number" 
                    value={config.totalMarks}
                    onChange={(e) => setConfig({...config, totalMarks: e.target.value})}
                    className="w-full mt-2 p-4 bg-slate-50 dark:bg-slate-800 border-none rounded-2xl text-slate-700 dark:text-white font-bold focus:ring-2 focus:ring-emerald-500/20 outline-none transition-all" 
                  />
                </motion.div>
              )}
            </AnimatePresence>
          </div>

          {/* AI Prompt / Description */}
          <div>
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">AI Prompt / Topic Description</label>
            <textarea 
              value={config.description}
              onChange={(e) => setConfig({...config, description: e.target.value})}
              className="w-full mt-2 p-6 bg-slate-50 dark:bg-slate-800 border-none rounded-[1.5rem] text-slate-700 dark:text-slate-200 font-medium focus:ring-2 focus:ring-emerald-500/20 outline-none transition-all resize-none"
              rows="4"
              placeholder="e.g. Generate an exam covering advanced Polymorphism, Interfaces, and Exception Handling in Java..."
            />
          </div>

          {/* Generate Button */}
          <button 
            onClick={handleGenerate}
            disabled={isGenerating}
            className={`w-full py-6 rounded-2xl font-black uppercase tracking-[0.2em] transition-all flex items-center justify-center gap-3 shadow-xl ${
              isGenerating 
                ? 'bg-slate-200 dark:bg-slate-800 text-slate-400 cursor-not-allowed' 
                : 'bg-emerald-600 text-white hover:bg-emerald-700 hover:scale-[1.01] shadow-emerald-200 dark:shadow-none'
            }`}
          >
            {isGenerating ? (
              <>
                <div className="w-5 h-5 border-2 border-slate-400 border-t-transparent rounded-full animate-spin" />
                Synthesizing Assessment...
              </>
            ) : (
              <>
                Compile & Generate <Zap className="w-5 h-5" />
              </>
            )}
          </button>
        </div>

        {/* --- STATUS & PREVIEW PANEL --- */}
        <div className="space-y-6">
          <AnimatePresence>
            {successData && (
              <motion.div 
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                className="bg-emerald-600 p-8 rounded-[2.5rem] text-white shadow-2xl shadow-emerald-200 dark:shadow-none relative overflow-hidden"
              >
                <CheckCircle2 className="w-12 h-12 text-emerald-300 mb-4" />
                <h3 className="text-2xl font-black uppercase italic tracking-tighter mb-2">Success!</h3>
                <p className="text-emerald-100 font-medium text-sm mb-6">{successData.message}</p>
                
                <div className="bg-emerald-700/50 p-4 rounded-xl flex justify-between items-center">
                  <span className="text-[10px] font-black uppercase tracking-widest text-emerald-200">Generated ID</span>
                  <span className="text-xl font-black">{successData.id}</span>
                </div>
              </motion.div>
            )}
          </AnimatePresence>

          <div className="bg-slate-900 p-8 rounded-[2.5rem] text-white">
             <h4 className="font-black uppercase tracking-widest text-xs mb-4 text-emerald-400">System Guidelines</h4>
             <ul className="space-y-3 text-sm font-medium text-slate-400">
               <li className="flex gap-2 items-start"><div className="w-1.5 h-1.5 rounded-full bg-emerald-500 mt-1.5 shrink-0" /> Ensure the target course has indexed textbook material via the Knowledge Base tab.</li>
               <li className="flex gap-2 items-start"><div className="w-1.5 h-1.5 rounded-full bg-emerald-500 mt-1.5 shrink-0" /> AI automatically maps questions to varied Bloom's Taxonomy levels based on the prompt.</li>
               <li className="flex gap-2 items-start"><div className="w-1.5 h-1.5 rounded-full bg-emerald-500 mt-1.5 shrink-0" /> Generated assessments are immediately visible to enrolled students.</li>
             </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AssessmentBuilderTab;