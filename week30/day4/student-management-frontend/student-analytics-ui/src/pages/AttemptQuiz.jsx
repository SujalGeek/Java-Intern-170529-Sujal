import React, { useState } from 'react';
import api from '../api/axios';

const AttemptQuiz = ({ quizId }) => {
  const [answers, setAnswers] = useState({});

  const handleSubmit = async () => {
    try {
      // Logic mapped to your QuizController @PostMapping("/submit")
      const res = await api.post('/api/quiz/submit', {
        quizId: quizId,
        answers: answers
      });
      alert(`Quiz Submitted! Score: ${res.data.score}%`);
    } catch (err) {
      console.error("Submission failed", err);
    }
  };

  return (
    <div className="p-10 dark:bg-slate-900 min-h-screen text-left">
      <h2 className="text-3xl font-black text-slate-900 dark:text-white mb-8">Active Quiz: #{quizId}</h2>
      {/* Question rendering logic here */}
      <button 
        onClick={handleSubmit}
        className="mt-10 px-8 py-4 bg-indigo-600 text-white rounded-2xl font-bold uppercase tracking-widest shadow-xl"
      >
        Submit Answers
      </button>
    </div>
  );
};

export default AttemptQuiz;