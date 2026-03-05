import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Upload, Book, Zap, Database, CheckCircle, FilePlus } from 'lucide-react';
// import api from '../api/axios';
import api from '../api/axios'

const AdminPortal = () => {
  const [file, setFile] = useState(null);
  const [indexing, setIndexing] = useState(false);
  const [courseId, setCourseId] = useState("");

  // --- 1. THE INDEXING ENGINE (IndexController: /api/v1/admin/index-book) ---
  const handleIndexBook = async () => {
    if (!file || !courseId) return alert("Select file and Course ID");
    setIndexing(true);
    
    const formData = new FormData();
    formData.append("file", file);

    try {
      // Maps to your @PostMapping("/index-book") in IndexController
      await api.post(`/api/v1/admin/index-book`, formData, {
        headers: { "Content-Type": "multipart/form-data" }
      });
      alert("AI Knowledge Base Updated Successfully!");
    } catch (err) {
      console.error("Indexing failed", err);
    } finally { setIndexing(false); }
  };

  return (
    <div className="p-12 bg-[#F8FAFC] dark:bg-[#0F172A] min-h-screen text-left transition-colors duration-500">
      <div className="max-w-6xl mx-auto space-y-12">
        <header>
           <h1 className="text-5xl font-black text-slate-900 dark:text-white uppercase italic tracking-tighter">
             Faculty <span className="text-indigo-600">Control Plane</span>
           </h1>
           <p className="text-slate-400 font-bold uppercase text-[10px] tracking-[0.4em] mt-2">AI-Driven Curriculum Management</p>
        </header>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-10">
          {/* --- UPLOAD & INDEX CARD --- */}
          <div className="bg-white dark:bg-slate-900 p-10 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm">
            <div className="flex items-center gap-4 mb-8 text-indigo-600">
               <Database className="w-8 h-8" />
               <h3 className="text-2xl font-black uppercase italic tracking-tight">Knowledge Indexing</h3>
            </div>
            
            <div className="space-y-6">
              <div>
                <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1">Target Course ID</label>
                <input 
                  type="number" 
                  value={courseId}
                  onChange={(e) => setCourseId(e.target.value)}
                  className="w-full mt-2 p-4 bg-slate-50 dark:bg-slate-800 border-none rounded-2xl text-slate-700 dark:text-white font-bold" 
                  placeholder="e.g., 101"
                />
              </div>

              <div className="border-4 border-dashed border-slate-50 dark:border-slate-800 rounded-[2.5rem] p-12 text-center hover:border-indigo-500/20 transition-all group">
                <input 
                  type="file" 
                  id="file-upload" 
                  className="hidden" 
                  onChange={(e) => setFile(e.target.files[0])} 
                />
                <label htmlFor="file-upload" className="cursor-pointer space-y-4 block">
                  <Upload className="w-12 h-12 text-slate-300 mx-auto group-hover:text-indigo-600 transition-colors" />
                  <p className="text-slate-500 font-bold uppercase text-xs tracking-widest">
                    {file ? file.name : "Drop PDF Textbook here"}
                  </p>
                </label>
              </div>

              <button 
                onClick={handleIndexBook}
                disabled={indexing}
                className="w-full py-5 bg-indigo-600 text-white rounded-2xl font-black uppercase tracking-widest hover:bg-indigo-700 shadow-xl shadow-indigo-200 dark:shadow-none transition-all flex items-center justify-center gap-3"
              >
                {indexing ? "AI INDEXING IN PROGRESS..." : "SYCHRONIZE WITH AI ENGINE"} <Zap className="w-4 h-4" />
              </button>
            </div>
          </div>

          {/* --- GENERATOR TOOLS --- */}
          <div className="space-y-6">
             <div className="bg-slate-900 p-10 rounded-[3rem] text-white group cursor-pointer" onClick={() => window.location.href='/admin/generate-exam'}>
                <FilePlus className="w-10 h-10 text-indigo-400 mb-6" />
                <h4 className="text-3xl font-black uppercase italic tracking-tighter mb-2">Build AI Midterm</h4>
                <p className="text-slate-400 font-medium text-sm">Generate multi-section papers (MCQ, Short, Long) based on indexed concepts.</p>
             </div>
             
             <div className="bg-indigo-600 p-10 rounded-[3rem] text-white shadow-2xl shadow-indigo-200 dark:shadow-none">
                <CheckCircle className="w-10 h-10 text-white/50 mb-6" />
                <h4 className="text-3xl font-black uppercase italic tracking-tighter mb-2">Evaluate Scripts</h4>
                <p className="text-indigo-100 font-medium text-sm">Review manual subjective submissions and trigger AI re-evaluation.</p>
             </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminPortal;