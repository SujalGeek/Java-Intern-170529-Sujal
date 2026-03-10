import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { Send, ChevronLeft, Clock, Brain, CheckCircle2, AlertCircle } from 'lucide-react';
import api from '../api/axios'

const AttemptTask = () => {
  const { type, id } = useParams(); // 'quiz', 'exam', or 'assignment'
  const navigate = useNavigate();
  
  const [taskData, setTaskData] = useState(null);
  const [answers, setAnswers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState(null);

  const userId = localStorage.getItem('userId');

  // --- 1. DYNAMIC FETCH ENGINE (Gateway Routing Logic) ---
  useEffect(() => {
    const fetchTask = async () => {
      try {
        let endpoint = "";
        
        // 🔥 MICROSERVICE ROUTING PROTOCOL
        // If the URL is /attempt/exam/33, 'type' is 'exam'.
        // We MUST hit the AI-Integration service for Midterms.
        if (type === 'assignment') {
          endpoint = `/api/assignments/${id}`;
        } else if (type === 'exam' || type === 'midterm') {
          // Routes to AiIntegrationController (8084) via Gateway
          endpoint = `/api/v1/exams/${id}`; 
        } else {
          // Routes to QuizController (8090) via Gateway
          endpoint = `/api/quiz/${id}`;
        }

        const res = await api.get(endpoint);
        setTaskData(res.data);
        
        // Initialize answer payload structure based on your industrial DTOs
        if (res.data.questions) {
          const initialAnswers = res.data.questions.map(q => ({
            examQuestionId: q.examQuestionId || q.questionId,
            answer: "", 
            studentAnswer: "" 
          }));
          setAnswers(initialAnswers);
        }
      } catch (err) {
        console.error("CRITICAL: Microservice Task Retrieval failure", err);
      } finally {
        setLoading(false);
      }
    };
    fetchTask();
  }, [id, type]);

  // --- 2. AI EVALUATOR SUBMISSION PIPELINE ---
  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      let endpoint = "";
      let payload = {};

      if (type === 'assignment') {
        // Mapped to AssignmentController @PostMapping("/submit")
        endpoint = '/api/assignments/submit';
        payload = {
          assignmentId: parseInt(id),
          studentId: parseInt(userId),
          answers: answers.map(a => ({ 
            questionId: a.examQuestionId, 
            answer: a.answer 
          }))
        };
      } else if (type === 'exam' || type === 'midterm') {
        // Mapped to AiIntegrationController @PostMapping("/submit")
        endpoint = '/api/v1/exams/submit';
        payload = {
          midtermId: parseInt(id),
          answers: answers.map(a => ({ 
            examQuestionId: a.examQuestionId, 
            studentAnswer: a.answer 
          }))
        };
      } else {
        // Mapped to QuizController @PostMapping("/submit")
        endpoint = '/api/quiz/submit';
        payload = {
          quizId: parseInt(id),
          answers: answers.map(a => ({
            questionId: a.examQuestionId,
            selectedOption: a.answer 
          }))
        };
      }

      const res = await api.post(endpoint, payload);
      setResult(res.data); // Captures final score and AI feedback
    } catch (err) {
      console.error("AI Evaluation Sync failure", err);
      alert(err.response?.data?.message || "Submission Failed: Check Microservice Health");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return (
    <div className="h-screen flex flex-col items-center justify-center bg-white dark:bg-slate-900 transition-colors">
      <div className="animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-indigo-600 mb-4"></div>
      <p className="font-black text-indigo-600 uppercase tracking-[0.3em] text-xs">Synchronizing Task Engine...</p>
    </div>
  );

  if (result) return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-900 p-10 flex items-center justify-center transition-all">
      <motion.div 
        initial={{ scale: 0.9, opacity: 0 }} 
        animate={{ scale: 1, opacity: 1 }}
        className="max-w-2xl w-full bg-white dark:bg-slate-800 p-16 rounded-[4rem] shadow-2xl text-center border border-slate-100 dark:border-slate-700"
      >
        <div className="w-24 h-24 bg-emerald-100 dark:bg-emerald-900/30 rounded-full flex items-center justify-center mx-auto mb-10 shadow-inner">
          <CheckCircle2 className="w-12 h-12 text-emerald-600" />
        </div>
        <h2 className="text-5xl font-black text-slate-900 dark:text-white mb-3 uppercase tracking-tighter italic">AI Evaluation Sync</h2>
        <p className="text-slate-400 font-bold uppercase text-[10px] tracking-[0.4em] mb-12">Performance Node Persistence: SUCCESSFUL</p>
        
        <div className="grid grid-cols-2 gap-8 mb-12">
           <div className="p-8 bg-slate-50 dark:bg-slate-900/50 rounded-[2.5rem] border border-slate-100 dark:border-slate-700 shadow-sm">
              <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2">Total Score</p>
              <p className="text-5xl font-black text-indigo-600 tracking-tighter italic">{result.totalScore}</p>
           </div>
           <div className="p-8 bg-slate-50 dark:bg-slate-900/50 rounded-[2.5rem] border border-slate-100 dark:border-slate-700 shadow-sm">
              <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-2">AI Grade</p>
              <p className="text-5xl font-black text-indigo-600 tracking-tighter italic">{result.grade || 'A'}</p>
           </div>
        </div>

        <button 
          onClick={() => navigate('/student-dashboard')}
          className="w-full py-6 bg-slate-900 dark:bg-indigo-600 text-white rounded-[1.5rem] font-black uppercase tracking-[0.3em] hover:scale-[1.02] transition-all shadow-xl"
        >
          Return to Dashboard
        </button>
      </motion.div>
    </div>
  );

  return (
    <div className="min-h-screen bg-[#F8FAFC] dark:bg-[#0F172A] p-12 transition-colors duration-500 overflow-x-hidden">
      <div className="max-w-5xl mx-auto space-y-12 text-left">
        {/* ACTION HEADER */}
        <div className="flex items-center justify-between">
          <button onClick={() => navigate(-1)} className="p-5 bg-white dark:bg-slate-800 rounded-[1.25rem] shadow-sm text-slate-400 hover:text-indigo-600 transition-all border border-slate-100 dark:border-slate-700 group">
            <ChevronLeft className="w-6 h-6 group-hover:-translate-x-1 transition-transform" />
          </button>
          <div className="flex gap-6">
            <div className="px-6 py-4 bg-white dark:bg-slate-800 rounded-[1.5rem] shadow-sm flex items-center gap-4 border border-slate-100 dark:border-slate-700">
              <Clock className="w-5 h-5 text-indigo-600 animate-pulse" />
              <span className="text-sm font-black dark:text-white uppercase tracking-widest">Session Active</span>
            </div>
          </div>
        </div>

        <header>
          <div className="flex items-center gap-4 text-indigo-600 mb-3">
            <Brain className="w-7 h-7" />
            <span className="text-[11px] font-black uppercase tracking-[0.4em] italic">Cognitive Integration Node</span>
          </div>
          <h1 className="text-6xl font-black text-slate-900 dark:text-white tracking-tighter uppercase italic leading-none">
            Attempting <span className="text-indigo-600">{type}</span>
          </h1>
          <p className="text-slate-400 font-bold uppercase text-xs tracking-widest mt-4 opacity-70">Initializing Real-time Subjective Evaluation Engine</p>
        </header>

        {/* --- DYNAMIC QUESTIONS RENDERER --- */}
        <div className="space-y-10">
          {taskData?.questions?.map((q, index) => (
            <motion.div 
              key={index}
              initial={{ x: -30, opacity: 0 }}
              animate={{ x: 0, opacity: 1 }}
              transition={{ delay: index * 0.1, ease: "circOut" }}
              className="bg-white dark:bg-slate-800 p-12 rounded-[3.5rem] border border-slate-100 dark:border-slate-700 shadow-sm relative overflow-hidden group"
            >
              <div className="flex justify-between items-start mb-10">
                <div className="flex items-center gap-4">
                  <span className="w-12 h-12 bg-indigo-600 text-white rounded-2xl flex items-center justify-center font-black text-xl shadow-lg italic">
                    {index + 1}
                  </span>
                  <p className="text-[10px] font-black text-slate-400 uppercase tracking-[0.2em]">Question ID: {q.examQuestionId || q.questionId}</p>
                </div>
                <span className="text-[10px] font-black text-indigo-600 uppercase bg-indigo-50 dark:bg-indigo-900/30 px-4 py-2 rounded-xl border border-indigo-100 dark:border-indigo-800/50 italic">
                  Allocation: {q.marks || 10} Credits
                </span>
              </div>
              
              <p className="text-2xl font-black text-slate-800 dark:text-slate-100 mb-10 leading-[1.4] tracking-tight">
                {q.questionText || q.question}
              </p>

              {/* DYNAMIC ANSWERING INTERFACE */}
              <div className="relative">
                {/* Check if options exist for MCQ (Quiz or Midterm MCQ) */}
                {q.options ? (
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                    {q.options.map((opt, oIdx) => {
                      const optLetter = String.fromCharCode(65 + oIdx); // A, B, C, D
                      return (
                        <button
                          key={optLetter}
                          onClick={() => {
                            const newAnswers = [...answers];
                            newAnswers[index].answer = optLetter;
                            setAnswers(newAnswers);
                          }}
                          className={`p-6 rounded-[1.5rem] text-left font-black text-sm uppercase tracking-widest border-2 transition-all ${
                            answers[index]?.answer === optLetter 
                              ? 'border-indigo-600 bg-indigo-50 dark:bg-indigo-900/20 text-indigo-600' 
                              : 'border-slate-50 dark:border-slate-700 bg-slate-50/50 dark:bg-slate-900/50 text-slate-400 hover:border-indigo-200'
                          }`}
                        >
                          <span className="mr-4 opacity-40">{optLetter}.</span> {opt}
                        </button>
                      );
                    })}
                  </div>
                ) : (
                  // Subjective Area for Assignments or Short/Long Answer Midterms
                  <textarea 
                    className="w-full p-8 bg-slate-50 dark:bg-slate-900 border-2 border-slate-100 dark:border-slate-700 rounded-[2rem] focus:border-indigo-500/50 transition-all text-slate-700 dark:text-slate-200 font-bold text-lg outline-none resize-none shadow-inner"
                    rows="6"
                    placeholder="Initialize descriptive analysis... AI will evaluate based on textbook indexing."
                    value={answers[index]?.answer}
                    onChange={(e) => {
                      const newAnswers = [...answers];
                      newAnswers[index].answer = e.target.value;
                      setAnswers(newAnswers);
                    }}
                  />
                )}
              </div>
              <div className="absolute right-0 bottom-0 w-32 h-32 bg-indigo-500/5 rounded-tl-[5rem] pointer-events-none group-hover:scale-110 transition-transform duration-700" />
            </motion.div>
          ))}
        </div>

        {/* SUBMISSION HUB */}
        <div className="pt-10 pb-20">
          <button 
            onClick={handleSubmit}
            disabled={submitting || !taskData}
            className={`w-full py-10 rounded-[3rem] font-black text-sm uppercase tracking-[0.5em] shadow-2xl transition-all flex items-center justify-center gap-6 ${
              submitting 
                ? 'bg-slate-200 dark:bg-slate-800 text-slate-400 cursor-not-allowed italic' 
                : 'bg-indigo-600 text-white hover:bg-indigo-700 hover:scale-[1.01]'
            }`}
          >
            {submitting ? (
              <>
                <div className="w-6 h-6 border-2 border-slate-400 border-t-transparent rounded-full animate-spin" />
                Processing through AI Evaluator...
              </>
            ) : (
              <>
                Finalize & Submit Session <Send className="w-6 h-6 fill-current" />
              </>
            )}
          </button>
          <p className="text-center text-[10px] font-black text-slate-400 uppercase tracking-widest mt-8 opacity-50">
            Microservice Gateway Status: ACTIVE [8091]
          </p>
        </div>
      </div>
    </div>
  );
};

export default AttemptTask;