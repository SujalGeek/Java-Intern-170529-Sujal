import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { Send, ChevronLeft, Clock, Brain, CheckCircle2, AlertCircle } from 'lucide-react';
import api from '../api/axios'

const AttemptTask = () => {
  const { type, id } = useParams(); // 'quiz', 'midterm', or 'assignment'
  const navigate = useNavigate();
  
  const [taskData, setTaskData] = useState(null);
  const [answers, setAnswers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState(null);

  const userId = localStorage.getItem('userId');

  // --- 1. DYNAMIC FETCH ENGINE ---
  useEffect(() => {
    const fetchTask = async () => {
      try {
        let endpoint = "";
        // Mapping to your specific Controllers
        if (type === 'assignment') endpoint = `/api/assignments/${id}`;
        else if (type === 'midterm') endpoint = `/api/v1/exams/${id}`;
        else endpoint = `/api/quiz/${id}`;

        const res = await api.get(endpoint);
        setTaskData(res.data);
        
        // Initialize answer payload structure based on your DTOs
        const initialAnswers = res.data.questions.map(q => ({
          questionId: q.questionId || q.examQuestionId,
          answer: "", // For assignments/subjective
          studentAnswer: "" // For midterms/MCQs
        }));
        setAnswers(initialAnswers);
      } catch (err) {
        console.error("Task Retrieval Failed", err);
      } finally {
        setLoading(false);
      }
    };
    fetchTask();
  }, [id, type]);

  // --- 2. THE AI SUBMISSION HANDLER ---
  const handleSubmit = async () => {
    setSubmitting(true);
    try {
      let endpoint = "";
      let payload = {};

      if (type === 'assignment') {
        // Mapped to AssignmentController @PostMapping("/submit")
        endpoint = '/api/assignments/submit';
        payload = {
          assignmentId: id,
          answers: answers.map(a => ({ questionId: a.questionId, answer: a.answer }))
        };
      } else {
        // Mapped to AiIntegrationController @PostMapping("/submit")
        endpoint = '/api/v1/exams/submit';
        payload = {
          midtermId: id,
          answers: answers.map(a => ({ examQuestionId: a.questionId, studentAnswer: a.answer }))
        };
      }

      const res = await api.post(endpoint, payload);
      setResult(res.data); // Captures score, percentage, and grade
    } catch (err) {
      alert(err.response?.data?.message || "AI Evaluation Service Unreachable");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return (
    <div className="h-screen flex items-center justify-center bg-white dark:bg-slate-900">
      <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-600"></div>
    </div>
  );

  if (result) return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-900 p-10 flex items-center justify-center">
      <motion.div 
        initial={{ scale: 0.9, opacity: 0 }} 
        animate={{ scale: 1, opacity: 1 }}
        className="max-w-2xl w-full bg-white dark:bg-slate-800 p-12 rounded-[3rem] shadow-2xl text-center border border-slate-100 dark:border-slate-700"
      >
        <div className="w-20 h-20 bg-green-100 dark:bg-green-900/30 rounded-full flex items-center justify-center mx-auto mb-8">
          <CheckCircle2 className="w-10 h-10 text-green-600" />
        </div>
        <h2 className="text-4xl font-black text-slate-900 dark:text-white mb-2 uppercase tracking-tighter italic">AI Evaluation Complete</h2>
        <p className="text-slate-400 font-bold uppercase text-[10px] tracking-widest mb-10">Real-time Performance Sync Successful</p>
        
        <div className="grid grid-cols-2 gap-6 mb-10">
           <div className="p-6 bg-slate-50 dark:bg-slate-900 rounded-3xl">
              <p className="text-[10px] font-black text-slate-400 uppercase mb-1">Final Score</p>
              <p className="text-4xl font-black text-indigo-600">{result.totalScore}</p>
           </div>
           <div className="p-6 bg-slate-50 dark:bg-slate-900 rounded-3xl">
              <p className="text-[10px] font-black text-slate-400 uppercase mb-1">AI Grade</p>
              <p className="text-4xl font-black text-indigo-600">{result.grade}</p>
           </div>
        </div>

        <button 
          onClick={() => navigate('/student-dashboard')}
          className="w-full py-5 bg-slate-900 dark:bg-indigo-600 text-white rounded-2xl font-black uppercase tracking-widest hover:opacity-90 transition-all"
        >
          Return to Dashboard
        </button>
      </motion.div>
    </div>
  );

  return (
    <div className="min-h-screen bg-[#F8FAFC] dark:bg-[#0F172A] p-10 transition-colors duration-500">
      <div className="max-w-4xl mx-auto space-y-10 text-left">
        {/* HEADER */}
        <div className="flex items-center justify-between">
          <button onClick={() => navigate(-1)} className="p-4 bg-white dark:bg-slate-800 rounded-2xl shadow-sm text-slate-400 hover:text-indigo-600 transition-all">
            <ChevronLeft className="w-6 h-6" />
          </button>
          <div className="flex gap-4">
            <div className="px-5 py-3 bg-white dark:bg-slate-800 rounded-2xl shadow-sm flex items-center gap-3">
              <Clock className="w-4 h-4 text-indigo-600" />
              <span className="text-sm font-black dark:text-white">45:00 REMAINING</span>
            </div>
          </div>
        </div>

        <header>
          <div className="flex items-center gap-4 text-indigo-600 mb-2">
            <Brain className="w-6 h-6" />
            <span className="text-[10px] font-black uppercase tracking-[0.3em]">AI Integration Active</span>
          </div>
          <h1 className="text-5xl font-black text-slate-900 dark:text-white tracking-tighter uppercase italic">
            Attempting <span className="text-indigo-600">{type}</span>
          </h1>
        </header>

        {/* QUESTIONS ENGINE */}
        <div className="space-y-8">
          {taskData?.questions.map((q, index) => (
            <motion.div 
              key={index}
              initial={{ x: -20, opacity: 0 }}
              animate={{ x: 0, opacity: 1 }}
              transition={{ delay: index * 0.1 }}
              className="bg-white dark:bg-slate-800 p-10 rounded-[2.5rem] border border-slate-100 dark:border-slate-700 shadow-sm"
            >
              <div className="flex justify-between items-start mb-6">
                <span className="w-10 h-10 bg-indigo-600 text-white rounded-xl flex items-center justify-center font-black">
                  {index + 1}
                </span>
                <span className="text-[10px] font-black text-slate-300 uppercase bg-slate-50 dark:bg-slate-900 px-3 py-1 rounded-lg border dark:border-slate-700">
                  Worth {q.marks} Marks
                </span>
              </div>
              
              <p className="text-xl font-bold text-slate-800 dark:text-slate-200 mb-8 leading-relaxed">
                {q.questionText || q.question}
              </p>

              {/* SUBJECTIVE AREA */}
              <div className="relative">
                <textarea 
                  className="w-full p-6 bg-slate-50 dark:bg-slate-900 border-2 border-slate-100 dark:border-slate-700 rounded-[1.5rem] focus:border-indigo-500/50 transition-all text-slate-700 dark:text-slate-200 font-medium outline-none resize-none"
                  rows="5"
                  placeholder="Analyze the question and provide your descriptive answer..."
                  value={answers[index]?.answer}
                  onChange={(e) => {
                    const newAnswers = [...answers];
                    newAnswers[index].answer = e.target.value;
                    setAnswers(newAnswers);
                  }}
                />
              </div>
            </motion.div>
          ))}
        </div>

        {/* SUBMIT BUTTON */}
        <button 
          onClick={handleSubmit}
          disabled={submitting}
          className={`w-full py-8 rounded-[2rem] font-black text-sm uppercase tracking-[0.4em] shadow-2xl transition-all flex items-center justify-center gap-4 ${
            submitting ? 'bg-slate-200 text-slate-400 cursor-not-allowed' : 'bg-indigo-600 text-white hover:bg-indigo-700 hover:scale-[1.01]'
          }`}
        >
          {submitting ? 'Processing through AI Core...' : 'Finalize & Submit'} 
          <Send className="w-5 h-5" />
        </button>
      </div>
    </div>
  );
};

export default AttemptTask;