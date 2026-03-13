// import React, { useState, useEffect } from 'react';
// import { motion, AnimatePresence } from 'framer-motion';
// import { 
//   FileSignature, CheckCircle2, Zap, LayoutList, 
//   FileText, ClipboardCheck, BookOpen, Loader2, AlertCircle , ShieldCheck
// } from 'lucide-react';
// import api from '../api/axios';

// // 🔥 Receive courseId from the global TeacherDashboard header!
// const AssessmentBuilderTab = ({ courseId }) => {
//   const [isGenerating, setIsGenerating] = useState(false);
//   const [successData, setSuccessData] = useState(null);
  
//   // Dynamic configuration state
//   const [config, setConfig] = useState({
//     type: 'midterm', // 'midterm', 'quiz', or 'assignment'
//     description: '',
//     totalMarks: 100
//   });

//   // --- 1. GLOBAL CONTEXT SYNC ---
//   // When you change the course in the dashboard header, we reset the success state
//   useEffect(() => {
//     setSuccessData(null);
//   }, [courseId]);

//   const handleGenerate = async () => {
//     if (!config.description) {
//       alert("Please provide a topic or description for the AI to process.");
//       return;
//     }

//     if (!courseId) {
//       alert("No active course detected. Please select a course from the header.");
//       return;
//     }

//     setIsGenerating(true);
//     setSuccessData(null);

//     const token = localStorage.getItem('token');
//     const reqHeaders = { 
//         headers: { 'Authorization': `Bearer ${token}` } 
//     };

//     try {
//       let res;
      
//       // 🔀 ISOLATED MICROSERVICE ROUTING Logic
//       if (config.type === 'midterm') {
//         // 🔥 Midterm Service expects 'total_marks' (snake_case)
//         const midtermPayload = {
//             courseId: parseInt(courseId),
//             description: config.description,
//             total_marks: parseInt(config.totalMarks) 
//         };
        
//         res = await api.post('/api/v1/exams/generate-dynamic', midtermPayload, reqHeaders);
//         setSuccessData({ id: res.data.midtermId, message: "Dynamic Midterm Successfully Synthesized" });

//       } else if (config.type === 'quiz') {
//         // 🔥 Quiz Service expects courseId and description
//         const quizPayload = {
//             courseId: parseInt(courseId),
//             description: config.description
//         };
        
//         res = await api.post('/api/quiz/generate', quizPayload, reqHeaders);
//         setSuccessData({ id: res.data.quizId, message: "Objective Quiz Successfully Generated" });

//       } else if (config.type === 'assignment') {
//         // 🔥 Assignment Service DTO expects 'totalMarks' (camelCase)
//         const assignmentPayload = {
//             courseId: parseInt(courseId),
//             description: config.description,
//             totalMarks: parseInt(config.totalMarks), 
//             difficulty: "medium" 
//         };
        
//         res = await api.post('/api/assignments/generate', assignmentPayload, reqHeaders);
//         setSuccessData({ id: res.data.assignmentId, message: "Subjective Assignment Successfully Generated" });
//       }

//     } catch (err) {
//       console.error("AI Generation Failed", err);
//       alert("Generation failed. Please ensure the Knowledge Base is indexed for this course.");
//     } finally {
//       setIsGenerating(false);
//     }
//   };

//   const assessmentTypes = [
//     { id: 'midterm', label: 'Midterm Exam', icon: FileText, desc: 'Multi-section paper (MCQ & Subjective)' },
//     { id: 'quiz', label: 'Quick Quiz', icon: LayoutList, desc: '100% Auto-graded Objective Questions' },
//     { id: 'assignment', label: 'Assignment', icon: ClipboardCheck, desc: 'Deep-dive subjective evaluation' }
//   ];

//   return (
//     <div className="space-y-8 text-left transition-colors duration-500 pb-20">
//       <header>
//         <div className="flex items-center gap-4 text-emerald-600 mb-2">
//           <FileSignature className="w-6 h-6" />
//           <span className="text-[10px] font-black uppercase tracking-[0.3em]">AI Generator Engine Active</span>
//         </div>
//         <h2 className="text-4xl font-black text-slate-900 dark:text-white tracking-tighter uppercase italic">
//           Assessment <span className="text-emerald-600">Builder</span>
//         </h2>
//         <p className="text-slate-400 font-bold text-xs uppercase tracking-widest mt-2">
//           Generating for <span className="text-emerald-600 bg-emerald-50 dark:bg-emerald-900/20 px-3 py-1 rounded-lg italic">Course {courseId || 'Select a Course'}</span>
//         </p>
//       </header>

//       <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
//         {/* --- MAIN CONFIGURATION PANEL --- */}
//         <div className="lg:col-span-2 bg-white dark:bg-slate-900 p-10 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm space-y-8">
          
//           {/* Assessment Type Selector */}
//           <div>
//             <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 mb-3 block opacity-60">Evaluation Format</label>
//             <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
//               {assessmentTypes.map((type) => (
//                 <button
//                   key={type.id}
//                   onClick={() => setConfig({ ...config, type: type.id })}
//                   className={`p-5 rounded-[1.5rem] border-2 text-left transition-all relative overflow-hidden group ${
//                     config.type === type.id 
//                       ? 'border-emerald-500 bg-emerald-50 dark:bg-emerald-900/20' 
//                       : 'border-slate-100 dark:border-slate-800 hover:border-emerald-200 dark:hover:border-emerald-800'
//                   }`}
//                 >
//                   <type.icon className={`w-6 h-6 mb-3 ${config.type === type.id ? 'text-emerald-600' : 'text-slate-400'}`} />
//                   <p className={`font-black uppercase tracking-tight text-sm ${config.type === type.id ? 'text-emerald-700 dark:text-emerald-400' : 'text-slate-700 dark:text-slate-300'}`}>
//                     {type.label}
//                   </p>
//                   <p className="text-[9px] text-slate-400 mt-1 font-bold uppercase tracking-wider">{type.desc}</p>
//                 </button>
//               ))}
//             </div>
//           </div>

//           {/* Configuration Data Row */}
//           <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
//             <div>
//               <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 opacity-60">Course Context</label>
//               <div className="relative mt-2">
//                 <BookOpen className="w-5 h-5 text-emerald-600 absolute left-4 top-4" />
//                 <div className="w-full pl-12 pr-5 py-4 bg-slate-50 dark:bg-slate-800 rounded-2xl text-slate-400 dark:text-slate-500 font-bold border border-transparent">
//                   ID: {courseId || 'Select in Header'}
//                 </div>
//               </div>
//             </div>

//             <AnimatePresence mode="wait">
//               {(config.type === 'midterm' || config.type === 'assignment') && (
//                 <motion.div initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: 10 }}>
//                   <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 opacity-60">Total Marks Weight</label>
//                   <input 
//                     type="number" 
//                     value={config.totalMarks}
//                     onChange={(e) => setConfig({...config, totalMarks: e.target.value})}
//                     className="w-full mt-2 p-4 bg-slate-50 dark:bg-slate-800 border-2 border-transparent rounded-2xl text-slate-700 dark:text-white font-bold focus:border-emerald-500/30 outline-none transition-all" 
//                   />
//                 </motion.div>
//               )}
//             </AnimatePresence>
//           </div>

//           {/* AI Prompt / Description */}
//           <div>
//             <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 opacity-60">AI Topic Prompt</label>
//             <textarea 
//               value={config.description}
//               onChange={(e) => setConfig({...config, description: e.target.value})}
//               className="w-full mt-2 p-6 bg-slate-50 dark:bg-slate-800 border-2 border-transparent rounded-[1.5rem] text-slate-700 dark:text-slate-200 font-medium focus:border-emerald-500/30 outline-none transition-all resize-none"
//               rows="4"
//               placeholder="e.g. Generate an exam covering advanced Polymorphism and Exception Handling in Java..."
//             />
//           </div>

//           <button 
//             onClick={handleGenerate}
//             disabled={isGenerating || !courseId}
//             className={`w-full py-6 rounded-2xl font-black uppercase tracking-[0.2em] transition-all flex items-center justify-center gap-3 shadow-xl ${
//               isGenerating 
//                 ? 'bg-slate-200 dark:bg-slate-800 text-slate-400 cursor-wait' 
//                 : !courseId ? 'bg-slate-100 dark:bg-slate-900 text-slate-400 cursor-not-allowed' : 'bg-emerald-600 text-white hover:bg-emerald-700 hover:scale-[1.01] shadow-emerald-200 dark:shadow-none'
//             }`}
//           >
//             {isGenerating ? (
//               <>
//                 <Loader2 className="w-5 h-5 animate-spin" />
//                 Synthesizing Assessment...
//               </>
//             ) : (
//               <>
//                 Initialize Generation <Zap className="w-5 h-5 fill-current" />
//               </>
//             )}
//           </button>
//         </div>

//         {/* --- STATUS & PREVIEW PANEL --- */}
//         <div className="space-y-6">
//           <AnimatePresence>
//             {successData && (
//               <motion.div 
//                 initial={{ opacity: 0, scale: 0.9 }}
//                 animate={{ opacity: 1, scale: 1 }}
//                 className="bg-emerald-600 p-8 rounded-[2.5rem] text-white shadow-2xl relative overflow-hidden"
//               >
//                 <div className="relative z-10">
//                   <CheckCircle2 className="w-12 h-12 text-emerald-300 mb-4" />
//                   <h3 className="text-2xl font-black uppercase italic tracking-tighter mb-2">Protocol Success</h3>
//                   <p className="text-emerald-100 font-medium text-xs mb-6">{successData.message}</p>
                  
//                   <div className="bg-emerald-700/50 p-4 rounded-xl flex justify-between items-center border border-emerald-500/30">
//                     <span className="text-[10px] font-black uppercase tracking-widest text-emerald-200">System ID</span>
//                     <span className="text-xl font-black tracking-tighter">{successData.id}</span>
//                   </div>
//                 </div>
//                 <Zap className="absolute -right-4 -bottom-4 w-32 h-32 text-white/10 rotate-12" />
//               </motion.div>
//             )}
//           </AnimatePresence>

//           <div className="bg-slate-900 p-8 rounded-[2.5rem] text-white border border-slate-800 shadow-xl">
//              <div className="flex items-center gap-3 mb-6">
//                <ShieldCheck className="w-5 h-5 text-emerald-500" />
//                <h4 className="font-black uppercase tracking-widest text-xs text-emerald-400">RAG Pipeline Guard</h4>
//              </div>
//              <ul className="space-y-4 text-[11px] font-bold text-slate-400 uppercase tracking-tight">
//                <li className="flex gap-3 items-start"><CheckCircle2 className="w-3.5 h-3.5 text-emerald-500 shrink-0" /> Requires pre-indexed textbook material.</li>
//                <li className="flex gap-3 items-start"><CheckCircle2 className="w-3.5 h-3.5 text-emerald-500 shrink-0" /> Questions are dynamically weighted to Bloom's levels.</li>
//                <li className="flex gap-3 items-start"><CheckCircle2 className="w-3.5 h-3.5 text-emerald-500 shrink-0" /> Immediate deployment to student classroom.</li>
//              </ul>
//           </div>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default AssessmentBuilderTab;
import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  FileSignature, CheckCircle2, Zap, LayoutList, 
  FileText, ClipboardCheck, BookOpen, Loader2, AlertCircle, ShieldCheck, AlertTriangle, Send, Trash2
} from 'lucide-react';
import api from '../api/axios';

const AssessmentBuilderTab = ({ courseId }) => {
  const [isGenerating, setIsGenerating] = useState(false);
  const [successData, setSuccessData] = useState(null);
  
  // 🔥 HITL States
  const [isPublished, setIsPublished] = useState(false);
  const [isPublishing, setIsPublishing] = useState(false);
  const [draftQuestions, setDraftQuestions] = useState([]);
  
  const [config, setConfig] = useState({
    type: 'midterm', 
    description: '',
    totalMarks: 100
  });

  useEffect(() => {
    setSuccessData(null);
    setIsPublished(false);
    setDraftQuestions([]);
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
    setIsPublished(false); 
    setDraftQuestions([]);

    const token = localStorage.getItem('token');
    const reqHeaders = { headers: { 'Authorization': `Bearer ${token}` } };

    try {
      let res;
      let generatedId;
      let fetchEndpoint = "";
      
      // 🔀 ISOLATED MICROSERVICE ROUTING
      if (config.type === 'midterm') {
        const midtermPayload = { courseId: parseInt(courseId), description: config.description, total_marks: parseInt(config.totalMarks) };
        res = await api.post('/api/v1/exams/generate-dynamic', midtermPayload, reqHeaders);
        generatedId = res.data.midtermId;
        fetchEndpoint = `/api/v1/exams/${generatedId}`;
        setSuccessData({ id: generatedId, message: "Dynamic Midterm Successfully Synthesized" });

      } else if (config.type === 'quiz') {
        const quizPayload = { courseId: parseInt(courseId), description: config.description };
        res = await api.post('/api/quiz/generate', quizPayload, reqHeaders);
        generatedId = res.data.quizId;
        fetchEndpoint = `/api/quiz/${generatedId}`;
        setSuccessData({ id: generatedId, message: "Objective Quiz Successfully Generated" });

      } else if (config.type === 'assignment') {
        const assignmentPayload = { courseId: parseInt(courseId), description: config.description, totalMarks: parseInt(config.totalMarks), difficulty: "medium" };
        res = await api.post('/api/assignments/generate', assignmentPayload, reqHeaders);
        generatedId = res.data.assignmentId;
        fetchEndpoint = `/api/assignments/${generatedId}`;
        setSuccessData({ id: generatedId, message: "Subjective Assignment Successfully Generated" });
      }

      // 🔥 Fetch the generated assessment to show a Live Preview
      try {
          const draftRes = await api.get(fetchEndpoint, reqHeaders);
          if (draftRes.data && draftRes.data.questions) {
              setDraftQuestions(draftRes.data.questions);
          }
      } catch (fetchErr) {
          console.error("Could not load draft preview", fetchErr);
      }

    } catch (err) {
      console.error("AI Generation Failed", err);
      alert("Generation failed. Please ensure the Knowledge Base is indexed for this course.");
    } finally {
      setIsGenerating(false);
    }
  };

  // 🔥 NEW: SURGICAL REJECT BUTTON LOGIC
  const handleRemoveQuestion = async (qId) => {
    if (!window.confirm("Are you sure you want to reject and remove this specific question?")) return;

    // 1. Instantly remove from UI for smooth UX
    setDraftQuestions(prev => prev.filter(item => (item.examQuestionId || item.questionId) !== qId));

    // 2. Tell Java to delete it from the DB (Fails silently if backend endpoint isn't ready, but UI still updates!)
    try {
      let deleteEndpoint = "";
      if (config.type === 'midterm') deleteEndpoint = `/api/v1/exams/questions/${qId}`;
      else if (config.type === 'quiz') deleteEndpoint = `/api/quiz/questions/${qId}`;
      else deleteEndpoint = `/api/assignments/questions/${qId}`;

      await api.delete(deleteEndpoint);
    } catch (e) {
      console.warn("Removed from UI. Backend sync failed or endpoint missing.", e);
    }
  };

  const handlePublish = async () => {
    if (!successData || !successData.id) return;
    
    setIsPublishing(true);
    try {
        const role = localStorage.getItem('role') || 2; 
        
        let publishEndpoint = "";
        if (config.type === 'assignment') publishEndpoint = `/api/assignments/${successData.id}/publish`;
        else if (config.type === 'exam' || config.type === 'midterm') publishEndpoint = `/api/v1/exams/${successData.id}/publish`;
        else publishEndpoint = `/api/quiz/${successData.id}/publish`;

        await api.put(publishEndpoint, {}, { headers: { 'X-User-Role': role } });
        
        setIsPublished(true);
        alert("✅ Success! The assessment has been approved and is now live for students.");
    } catch (err) {
        console.error(err);
        alert("❌ Failed to publish: " + (err.response?.data?.message || "Ensure you added the @PutMapping in Java!"));
    } finally {
        setIsPublishing(false);
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

      {/* 🔥 UPGRADED RESPONSIVE GRID: 50/50 Split (lg:grid-cols-2) */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 lg:gap-10">
        
        {/* --- LEFT: MAIN CONFIGURATION PANEL --- */}
        <div className="bg-white dark:bg-slate-900 p-8 lg:p-10 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm space-y-8 h-fit">
          
          <div>
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 mb-3 block opacity-60">Evaluation Format</label>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              {assessmentTypes.map((type) => (
                <button
                  key={type.id}
                  onClick={() => setConfig({ ...config, type: type.id })}
                  className={`p-4 rounded-[1.5rem] border-2 text-left transition-all relative overflow-hidden group flex flex-col justify-center items-start ${
                    config.type === type.id 
                      ? 'border-emerald-500 bg-emerald-50 dark:bg-emerald-900/20' 
                      : 'border-slate-100 dark:border-slate-800 hover:border-emerald-200 dark:hover:border-emerald-800'
                  }`}
                >
                  <type.icon className={`w-6 h-6 mb-2 ${config.type === type.id ? 'text-emerald-600' : 'text-slate-400'}`} />
                  <p className={`font-black uppercase tracking-tight text-xs lg:text-sm ${config.type === type.id ? 'text-emerald-700 dark:text-emerald-400' : 'text-slate-700 dark:text-slate-300'}`}>
                    {type.label}
                  </p>
                </button>
              ))}
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
            <div>
              <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 opacity-60">Course Context</label>
              <div className="relative mt-2">
                <BookOpen className="w-5 h-5 text-emerald-600 absolute left-4 top-4" />
                <div className="w-full pl-12 pr-5 py-4 bg-slate-50 dark:bg-slate-800 rounded-2xl text-slate-400 dark:text-slate-500 font-bold border border-transparent text-sm">
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
                    className="w-full mt-2 p-4 bg-slate-50 dark:bg-slate-800 border-2 border-transparent rounded-2xl text-slate-700 dark:text-white font-bold focus:border-emerald-500/30 outline-none transition-all text-sm" 
                  />
                </motion.div>
              )}
            </AnimatePresence>
          </div>

          <div>
            <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 opacity-60">AI Topic Prompt</label>
            <textarea 
              value={config.description}
              onChange={(e) => setConfig({...config, description: e.target.value})}
              className="w-full mt-2 p-5 bg-slate-50 dark:bg-slate-800 border-2 border-transparent rounded-[1.5rem] text-slate-700 dark:text-slate-200 font-medium focus:border-emerald-500/30 outline-none transition-all resize-none text-sm lg:text-base leading-relaxed"
              rows="5"
              placeholder="e.g. Generate an exam covering advanced Polymorphism and Exception Handling in Java..."
            />
          </div>

          <button 
            onClick={handleGenerate}
            disabled={isGenerating || !courseId}
            className={`w-full py-5 rounded-2xl font-black uppercase tracking-[0.2em] transition-all flex items-center justify-center gap-3 shadow-xl text-sm lg:text-base ${
              isGenerating 
                ? 'bg-slate-200 dark:bg-slate-800 text-slate-400 cursor-wait' 
                : !courseId ? 'bg-slate-100 dark:bg-slate-900 text-slate-400 cursor-not-allowed' : 'bg-emerald-600 text-white hover:bg-emerald-700 hover:scale-[1.01] shadow-emerald-200 dark:shadow-none'
            }`}
          >
            {isGenerating ? (
              <>
                <Loader2 className="w-6 h-6 animate-spin" />
                Synthesizing Assessment...
              </>
            ) : (
              <>
                Initialize Generation <Zap className="w-5 h-5 fill-current" />
              </>
            )}
          </button>
        </div>

        {/* --- RIGHT: STATUS & LIVE PREVIEW PANEL --- */}
        <div className="space-y-6 h-full">
          
          {/* 🟡 DRAFT MODE: Live Preview & Human Approval */}
          <AnimatePresence>
            {successData && !isPublished && (
              <motion.div 
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                className="bg-white dark:bg-slate-900 border-2 border-yellow-400 dark:border-yellow-600 border-dashed rounded-[3rem] shadow-xl overflow-hidden flex flex-col h-[750px]"
              >
                {/* Header */}
                <div className="bg-yellow-50 dark:bg-yellow-900/20 p-6 lg:p-8 border-b border-yellow-200 dark:border-yellow-800 shrink-0">
                  <div className="flex justify-between items-center">
                    <div className="flex items-center gap-3">
                      <AlertTriangle className="w-8 h-8 text-yellow-600 dark:text-yellow-400" />
                      <div>
                        <h3 className="text-xl lg:text-2xl font-black uppercase tracking-tighter text-yellow-800 dark:text-yellow-400">
                          Draft Preview
                        </h3>
                        <p className="text-xs font-bold text-yellow-700/70 dark:text-yellow-500/70 mt-1">
                          Review & Reject bad questions
                        </p>
                      </div>
                    </div>
                    <div className="bg-yellow-200/50 dark:bg-yellow-900/50 px-4 py-2 rounded-xl">
                       <span className="text-[10px] font-black uppercase tracking-widest text-yellow-700 dark:text-yellow-400 block mb-1">Draft ID</span>
                       <span className="text-lg font-black tracking-tighter text-yellow-900 dark:text-yellow-300">{successData.id}</span>
                    </div>
                  </div>
                </div>

                {/* 🔥 LIVE QUESTION PREVIEW SCROLL BOX */}
                <div className="p-6 lg:p-8 overflow-y-auto flex-1 space-y-5 bg-slate-50/50 dark:bg-slate-900/50 custom-scrollbar relative">
                  {draftQuestions.length > 0 ? (
                    draftQuestions.map((q, idx) => (
                      <div key={idx} className="bg-white dark:bg-slate-800 p-6 rounded-[2rem] border border-slate-100 dark:border-slate-700 shadow-sm relative group transition-all hover:border-red-200 dark:hover:border-red-900/50 hover:shadow-md">
                        
                        {/* 🔥 SURGICAL REJECT BUTTON */}
                        <button 
                          onClick={() => handleRemoveQuestion(q.examQuestionId || q.questionId)}
                          className="absolute top-4 right-4 p-2.5 bg-slate-50 dark:bg-slate-900 text-slate-400 hover:text-white hover:bg-red-500 rounded-xl transition-all opacity-0 group-hover:opacity-100 shadow-sm"
                          title="Reject and Remove this Question"
                        >
                          <Trash2 className="w-5 h-5" />
                        </button>

                        <div className="flex items-center gap-3 mb-4">
                          <span className="w-8 h-8 rounded-full bg-slate-100 dark:bg-slate-700 text-slate-500 flex items-center justify-center text-xs font-black">{idx + 1}</span>
                          <span className="text-[10px] font-black uppercase tracking-widest text-emerald-600 bg-emerald-50 dark:bg-emerald-900/30 px-3 py-1 rounded-md">
                            {q.marks || 1} Marks
                          </span>
                        </div>
                        
                        <p className="text-sm lg:text-base font-bold text-slate-800 dark:text-slate-200 mb-5 pr-10 leading-relaxed">
                          {q.questionText || q.question}
                        </p>
                        
                        {/* Show Options if MCQ */}
                        {q.options && q.options.length > 0 && (
                          <div className="grid grid-cols-1 gap-3 mb-5">
                            {q.options.map((opt, oIdx) => (
                              <div key={oIdx} className="text-sm font-medium text-slate-600 dark:text-slate-300 bg-slate-50 dark:bg-slate-900/50 p-4 rounded-xl border border-slate-100 dark:border-slate-800">
                                <span className="font-black mr-3 text-slate-400">{String.fromCharCode(65 + oIdx)}.</span> {opt}
                              </div>
                            ))}
                          </div>
                        )}
                        <div className="text-[11px] font-black text-emerald-700 dark:text-emerald-400 bg-emerald-50 dark:bg-emerald-900/30 inline-block px-4 py-2 rounded-lg border border-emerald-100 dark:border-emerald-800/30">
                          Target Answer: {q.correctAnswer || "Subjective Evaluation"}
                        </div>
                      </div>
                    ))
                  ) : (
                    <div className="absolute inset-0 flex flex-col items-center justify-center text-slate-400">
                      <Loader2 className="w-8 h-8 animate-spin mb-4 text-emerald-500" />
                      <span className="text-xs font-black uppercase tracking-widest">Loading Interactive Preview...</span>
                    </div>
                  )}
                </div>

                {/* Publish Button Footer */}
                <div className="p-6 lg:p-8 bg-white dark:bg-slate-900 border-t border-slate-100 dark:border-slate-800 shrink-0">
                  <button 
                      onClick={handlePublish}
                      disabled={isPublishing || draftQuestions.length === 0}
                      className="w-full py-5 bg-emerald-500 hover:bg-emerald-600 text-white rounded-2xl font-black text-sm lg:text-base uppercase tracking-[0.2em] shadow-xl transition-all flex justify-center items-center gap-3 disabled:opacity-50 hover:scale-[1.01]"
                  >
                      {isPublishing ? "Publishing..." : <><Send className="w-6 h-6" /> Approve & Publish {draftQuestions.length} Questions</>}
                  </button>
                </div>
              </motion.div>
            )}
          </AnimatePresence>

          {/* 🟢 PUBLISHED SUCCESS */}
          <AnimatePresence>
            {successData && isPublished && (
              <motion.div 
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                className="bg-emerald-600 p-10 lg:p-12 rounded-[3rem] text-white shadow-2xl relative overflow-hidden"
              >
                <div className="relative z-10">
                  <CheckCircle2 className="w-16 h-16 text-emerald-300 mb-6" />
                  <h3 className="text-3xl lg:text-4xl font-black uppercase italic tracking-tighter mb-3">Protocol Success</h3>
                  <p className="text-emerald-100 font-medium text-sm lg:text-base mb-10">
                    Assessment is now LIVE on the Student Dashboard.
                  </p>
                  
                  <div className="bg-emerald-700/50 p-6 rounded-2xl flex justify-between items-center border border-emerald-500/30">
                    <span className="text-xs font-black uppercase tracking-widest text-emerald-200">System ID</span>
                    <span className="text-2xl font-black tracking-tighter">{successData.id}</span>
                  </div>
                </div>
                <Zap className="absolute -right-4 -bottom-4 w-48 h-48 text-white/10 rotate-12" />
              </motion.div>
            )}
          </AnimatePresence>

          {/* RAG INFO BOX */}
          {(!successData || isPublished) && (
            <div className="bg-slate-900 p-8 lg:p-10 rounded-[3rem] text-white border border-slate-800 shadow-xl h-fit">
              <div className="flex items-center gap-4 mb-8">
                <ShieldCheck className="w-7 h-7 text-emerald-500" />
                <h4 className="font-black uppercase tracking-widest text-sm text-emerald-400">RAG Pipeline Guard</h4>
              </div>
              <ul className="space-y-5 text-xs font-bold text-slate-400 uppercase tracking-tight">
                <li className="flex gap-4 items-start"><CheckCircle2 className="w-4 h-4 text-emerald-500 shrink-0" /> Requires pre-indexed textbook material.</li>
                <li className="flex gap-4 items-start"><CheckCircle2 className="w-4 h-4 text-emerald-500 shrink-0" /> Questions are dynamically weighted to Bloom's levels.</li>
                <li className="flex gap-4 items-start"><AlertCircle className="w-4 h-4 text-yellow-500 shrink-0" /> Requires Human Review before deployment.</li>
              </ul>
            </div>
          )}

        </div>
      </div>

      {/* Adding a quick global style block for the custom scrollbar in the preview panel */}
      <style dangerouslySetInnerHTML={{__html: `
        .custom-scrollbar::-webkit-scrollbar {
          width: 8px;
        }
        .custom-scrollbar::-webkit-scrollbar-track {
          background: transparent;
        }
        .custom-scrollbar::-webkit-scrollbar-thumb {
          background-color: #cbd5e1;
          border-radius: 10px;
        }
        .dark .custom-scrollbar::-webkit-scrollbar-thumb {
          background-color: #334155;
        }
      `}} />
    </div>
  );
};

export default AssessmentBuilderTab;