import React, { useState, useEffect } from 'react';
import api from '../api/axios'
import { PlayCircle, FileText } from 'lucide-react';

const ExamsTab = () => {
  const [exams, setExams] = useState([]);
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    const loadContent = async () => {
      try {
        // Hits Course-Service to find what Sujal is studying
        const enrollRes = await api.get('/api/enrollments/my'); 
        const courseId = enrollRes.data[0]?.courseId; 
        
        if (courseId) {
          // Hits AI-Integration-Service for the real paper
          const res = await api.get(`/api/v1/exams/midterm-paper/${courseId}`); 
          setExams([res.data]); 
        }
      } catch (err) { console.error("Data Load Failed", err); }
    };
    loadContent();
  }, [userId]);

  return (
    <div className="space-y-6 text-left">
      <h2 className="text-3xl font-black text-slate-900 dark:text-white mb-8 italic uppercase tracking-tighter">Available Assessments</h2>
      {exams.map((exam) => (
        <div key={exam.midtermId} className="bg-white dark:bg-slate-900 p-8 rounded-[2.5rem] border border-slate-100 dark:border-slate-800 shadow-sm flex items-center justify-between">
          <div className="flex items-center gap-6">
            <div className="w-14 h-14 bg-indigo-600 rounded-2xl flex items-center justify-center text-white shadow-lg">
              <FileText className="w-7 h-7" />
            </div>
            <div>
              <p className="text-lg font-black text-slate-800 dark:text-slate-200">Midterm Exam #{exam.midtermId}</p>
              <p className="text-xs text-slate-400 font-bold uppercase tracking-widest">Course: {exam.courseId} • {exam.totalQuestions} Questions</p>
            </div>
          </div>
          <button 
            onClick={() => window.location.href = `/attempt/exam/${exam.midtermId}`}
            className="flex items-center gap-3 px-8 py-4 bg-indigo-600 text-white rounded-2xl font-black text-xs uppercase tracking-[0.2em] hover:bg-indigo-700 transition-all shadow-xl shadow-indigo-200 dark:shadow-none"
          >
            Attempt Now <PlayCircle className="w-5 h-5" />
          </button>
        </div>
      ))}
    </div>
  );
};

export default ExamsTab;