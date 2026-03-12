import React, { useState } from 'react';
import api from '../api/axios'

const GenerateExam = () => {
  const [config, setConfig] = useState({
    courseId: 101,
    description: "",
    total_marks: 100
  });

  const handleGenerate = async () => {
    try {
      // Maps to QuizController @PostMapping("/generate-dynamic")
      const res = await api.post('/api/v1/exams/generate-dynamic', config);
      alert(`Dynamic Midterm Created! ID: ${res.data.midtermId}`);
    } catch (err) { alert("Generation Error"); }
  };

  return (
    <div className="p-12 dark:bg-slate-900 min-h-screen text-left">
       <h2 className="text-4xl font-black text-white uppercase italic mb-10">Midterm Generator</h2>
       <div className="max-w-2xl space-y-6">
          <input 
            className="w-full p-5 bg-slate-800 rounded-2xl text-white outline-none focus:ring-2 focus:ring-indigo-500" 
            placeholder="Exam Description (e.g. Data Structures Final)"
            onChange={(e) => setConfig({...config, description: e.target.value})}
          />
          <button onClick={handleGenerate} className="w-full py-5 bg-indigo-600 text-white rounded-2xl font-black uppercase tracking-widest shadow-xl">
             Generate Sections A, B & C
          </button>
       </div>
    </div>
  );
};

export default GenerateExam;