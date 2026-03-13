import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Loader2, CheckCircle2, AlertCircle } from 'lucide-react';
import api from '../api/axios';

const AttemptQuiz = ({ quizId: propQuizId }) => {
  // Grab ID from URL (e.g., /attempt/quiz/10)
  const { id } = useParams();
  const activeQuizId = propQuizId || id;
  const navigate = useNavigate();

  const [quiz, setQuiz] = useState(null);
  
  // 🔥 CRITICAL: State initialized as a pure Object {}
  const [answers, setAnswers] = useState({}); 
  
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  const studentId = localStorage.getItem('userId');

  // 1. Fetch the Quiz Questions from the Backend
  useEffect(() => {
    const fetchQuiz = async () => {
      if (!activeQuizId) return;
      setIsLoading(true);
      try {
        const res = await api.get(`/api/quiz/${activeQuizId}`); 
        setQuiz(res.data);
      } catch (err) {
        console.error("Failed to load quiz", err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchQuiz();
  }, [activeQuizId]);

  // 2. Handle Answer Selection
  const handleSelectOption = (questionId, selectedLetter) => {
    setAnswers(prev => ({
      ...prev,
      [questionId]: selectedLetter // Forces Question ID to be the Key, Letter to be the Value
    }));
  };

  // 3. Submit to the Backend
  const handleSubmit = async () => {
    if (Object.keys(answers).length < quiz?.questions.length) {
      if (!window.confirm("You haven't answered all questions! Are you sure you want to submit?")) {
        return;
      }
    }

    setIsSubmitting(true);
    
    // 🔥 FORCING THE OBJECT STRUCTURE: This guarantees Spring Boot gets a Map<Long, String>
    const payload = {
      quizId: parseInt(activeQuizId, 10),
      answers: { ...answers } 
    };

    console.log("🚀 SENDING STRICT JSON PAYLOAD TO SPRING BOOT:", JSON.stringify(payload, null, 2));

    try {
      const res = await api.post('/api/quiz/submit', payload, {
        headers: {
          'X-User-Id': studentId,
          'X-User-Role': 3 // Student Role
        }
      });
      
      alert(`Quiz Submitted Successfully! \nGrade: ${res.data.grade} \nScore: ${res.data.percentage}%`);
      navigate('/dashboard'); 
      
    } catch (err) {
      console.error("Submission failed", err);
      alert(err.response?.data?.message || err.response?.data || "Submission failed. You may have already attempted this quiz.");
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50 dark:bg-slate-900">
        <Loader2 className="w-12 h-12 animate-spin text-indigo-600" />
      </div>
    );
  }

  if (!quiz || !quiz.questions) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50 dark:bg-slate-900 flex-col gap-4">
        <AlertCircle className="w-16 h-16 text-red-500" />
        <h2 className="text-2xl font-black text-slate-800 dark:text-white">Quiz Not Found</h2>
      </div>
    );
  }

  return (
    <div className="p-10 bg-slate-50 dark:bg-slate-900 min-h-screen text-left transition-colors duration-500">
      <div className="max-w-4xl mx-auto">
        
        {/* Header */}
        <header className="mb-10 flex items-center justify-between">
          <div>
            <h2 className="text-4xl font-black text-slate-900 dark:text-white uppercase tracking-tighter italic">
              Active Quiz <span className="text-indigo-600">#{activeQuizId}</span>
            </h2>
            <p className="text-slate-500 font-bold uppercase tracking-widest text-sm mt-2">
              Course {quiz.courseId} • {quiz.totalMarks} Marks
            </p>
          </div>
          <div className="bg-indigo-100 dark:bg-indigo-900/30 text-indigo-700 dark:text-indigo-400 px-6 py-3 rounded-2xl font-black text-lg">
            {Object.keys(answers).length} / {quiz.questions.length} Answered
          </div>
        </header>

        {/* Question Rendering Logic */}
        <div className="space-y-8">
          {quiz.questions.map((q, index) => (
            <div key={q.questionId} className="bg-white dark:bg-slate-800 p-8 rounded-[2rem] border border-slate-200 dark:border-slate-700 shadow-sm">
              <div className="flex items-start justify-between gap-4 mb-6">
                <h3 className="text-lg font-bold text-slate-800 dark:text-slate-100 leading-relaxed">
                  <span className="text-indigo-600 font-black mr-2">{index + 1}.</span> 
                  {q.questionText}
                </h3>
                <span className="shrink-0 bg-slate-100 dark:bg-slate-700 text-slate-500 dark:text-slate-300 text-xs font-black px-3 py-1 rounded-lg">
                  {q.marks} Mark
                </span>
              </div>

              {/* Options Grid */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {q.options && q.options.map((opt, optIndex) => {
                  
                  // Extract letter and text accurately
                  const displayLetter = opt.includes(')') ? opt.split(')')[0].trim() : String.fromCharCode(65 + optIndex);
                  const displayText = opt.includes(')') ? opt.split(')')[1].trim() : opt;
                  
                  const isSelected = answers[q.questionId] === displayLetter;

                  return (
                    <button
                      key={optIndex}
                      onClick={() => handleSelectOption(q.questionId, displayLetter)}
                      className={`flex items-center gap-4 p-4 rounded-2xl border-2 transition-all text-left group ${
                        isSelected 
                          ? 'border-indigo-600 bg-indigo-50 dark:bg-indigo-900/20 dark:border-indigo-500' 
                          : 'border-slate-100 dark:border-slate-700 hover:border-indigo-300 dark:hover:border-indigo-700 bg-transparent'
                      }`}
                    >
                      <span className={`w-10 h-10 flex items-center justify-center rounded-full font-black text-sm shrink-0 transition-colors ${
                        isSelected 
                          ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-200 dark:shadow-none' 
                          : 'bg-slate-100 dark:bg-slate-700 text-slate-500 dark:text-slate-400 group-hover:bg-indigo-100 group-hover:text-indigo-600'
                      }`}>
                        {displayLetter}
                      </span>
                      <span className={`font-semibold text-sm ${isSelected ? 'text-indigo-900 dark:text-indigo-100' : 'text-slate-700 dark:text-slate-300'}`}>
                        {displayText}
                      </span>
                    </button>
                  );
                })}
              </div>
            </div>
          ))}
        </div>

        {/* Submit Button */}
        <div className="mt-12 flex justify-end">
          <button 
            onClick={handleSubmit}
            disabled={isSubmitting}
            className="flex items-center gap-3 px-10 py-5 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white rounded-2xl font-black text-sm uppercase tracking-[0.2em] shadow-xl shadow-indigo-200 dark:shadow-none transition-all"
          >
            {isSubmitting ? (
              <><Loader2 className="w-5 h-5 animate-spin" /> Submitting...</>
            ) : (
              <><CheckCircle2 className="w-6 h-6" /> Submit Quiz</>
            )}
          </button>
        </div>

      </div>
    </div>
  );
};

export default AttemptQuiz;