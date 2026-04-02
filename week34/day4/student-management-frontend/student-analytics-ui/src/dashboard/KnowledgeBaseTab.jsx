import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Upload, Database, Zap, BookOpen, CheckCircle2, AlertCircle, Loader2 } from 'lucide-react';
import api from '../api/axios';

const KnowledgeBaseTab = ({ courseId }) => {
  const [file, setFile] = useState(null);
  const [isIndexing, setIsIndexing] = useState(false);
  const [status, setStatus] = useState(null); // 'uploading', 'success', 'error'

  // --- 1. SYNC WITH GLOBAL STATE ---
  // When the courseId changes in the header, we reset the file selection for safety
  useEffect(() => {
    setFile(null);
    setStatus(null);
  }, [courseId]);

  const handleIndexBook = async () => {
    if (!file) {
      alert("Please select a PDF textbook to index.");
      return;
    }

    if (!courseId) {
      alert("No active course detected. Please select a course from the dashboard header.");
      return;
    }
    
    setIsIndexing(true);
    setStatus('uploading');

    const formData = new FormData();
    formData.append("file", file);

    const token = localStorage.getItem('token');
    
    //  SENIOR MAPPING: Sending courseId to your Spring Boot IndexController
    // This ensures the vector embeddings are tagged with the correct course ID
    formData.append("courseId", courseId);

    try {
      // Hits: Ai-Integration-Service -> @PostMapping("/api/v1/admin/index-book")
      // Using multipart/form-data for byte-stream processing
     // Inside handleIndexBook
      const res = await api.post(`/api/v1/admin/${courseId}/index`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          'Authorization': `Bearer ${token}` 
        }
      });
      
      setStatus('success');
      // alert("Vectorization Successful!");
      setFile(null); // Clear file after successful vectorization
    } catch (err) {
      console.error("Vectorization Engine Failure:", err);
      setStatus('error');
    } finally { 
      setIsIndexing(false); 
      // Reset status after 5 seconds to clear the UI
      setTimeout(() => setStatus(null), 5000);
    }
  };

  return (
    <div className="space-y-10 text-left transition-colors duration-500">
      <header>
        <div className="flex items-center gap-4 text-emerald-600 mb-3">
          <Database className="w-7 h-7" />
          <span className="text-[11px] font-black uppercase tracking-[0.4em] italic">Knowledge RAG Pipeline</span>
        </div>
        <h2 className="text-4xl font-black text-slate-900 dark:text-white tracking-tighter uppercase italic">
          Knowledge <span className="text-emerald-600">Indexing</span>
        </h2>
        <p className="text-slate-400 font-bold text-xs uppercase tracking-widest mt-3 flex items-center gap-2">
          Calibrating semantic understanding for <span className="text-emerald-600 bg-emerald-50 dark:bg-emerald-900/20 px-3 py-1 rounded-lg">Course ID: {courseId || 'PENDING'}</span>
        </p>
      </header>

      <div className="grid grid-cols-1 lg:grid-cols-5 gap-10">
        
        {/* --- UPLOAD ZONE --- */}
        <div className="lg:col-span-3 bg-white dark:bg-slate-900 p-12 rounded-[3.5rem] border border-slate-100 dark:border-slate-800 shadow-sm relative overflow-hidden transition-all">
          <div className="space-y-10 relative z-10">
            
            <div 
              className={`border-4 border-dashed rounded-[3rem] p-16 text-center transition-all group relative overflow-hidden ${
                file 
                  ? 'border-emerald-500 bg-emerald-50/50 dark:bg-emerald-900/10' 
                  : 'border-slate-100 dark:border-slate-800 hover:border-emerald-500/30 cursor-pointer'
              }`}
            >
              <input 
                type="file" 
                id="file-upload" 
                accept=".pdf"
                className="hidden" 
                onChange={(e) => setFile(e.target.files[0])} 
                disabled={isIndexing}
              />
              <label htmlFor="file-upload" className={`${isIndexing ? 'cursor-not-allowed' : 'cursor-pointer'} space-y-6 block`}>
                <AnimatePresence mode="wait">
                  {file ? (
                    <motion.div 
                      key="file-active"
                      initial={{ scale: 0.8, opacity: 0 }}
                      animate={{ scale: 1, opacity: 1 }}
                      className="flex flex-col items-center"
                    >
                      <BookOpen className="w-20 h-20 text-emerald-500 mb-4" />
                      <p className="font-black text-emerald-600 text-sm uppercase tracking-widest truncate max-w-xs">
                        {file.name}
                      </p>
                      <p className="text-[10px] text-emerald-400 font-bold mt-2 uppercase">Ready for Vectorization</p>
                    </motion.div>
                  ) : (
                    <motion.div key="file-empty" className="space-y-4">
                      <div className="w-20 h-20 bg-slate-50 dark:bg-slate-800 rounded-3xl flex items-center justify-center mx-auto group-hover:scale-110 group-hover:bg-emerald-50 transition-all">
                        <Upload className="w-10 h-10 text-slate-300 dark:text-slate-600 group-hover:text-emerald-500" />
                      </div>
                      <div>
                        <p className="text-slate-500 dark:text-slate-400 font-black uppercase text-xs tracking-[0.2em]">Drop PDF Textbook</p>
                        <p className="text-[9px] font-bold text-slate-400 mt-2 uppercase opacity-60">Optimized for descriptive NLP papers</p>
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>
              </label>
            </div>

            <button 
              onClick={handleIndexBook}
              disabled={isIndexing || !file}
              className={`w-full py-7 rounded-[2rem] font-black uppercase tracking-[0.4em] transition-all flex items-center justify-center gap-4 shadow-2xl ${
                isIndexing 
                  ? 'bg-slate-100 dark:bg-slate-800 text-slate-400 cursor-not-allowed' 
                  : !file 
                    ? 'bg-slate-50 dark:bg-slate-800 text-slate-300 cursor-not-allowed border border-slate-100 dark:border-slate-700'
                    : 'bg-emerald-600 text-white hover:bg-emerald-700 hover:scale-[1.01] shadow-emerald-200 dark:shadow-none'
              }`}
            >
              {isIndexing ? (
                <>
                  <Loader2 className="w-6 h-6 animate-spin" />
                  Processing Embeddings...
                </>
              ) : status === 'success' ? (
                <>
                  <CheckCircle2 className="w-6 h-6" /> Indexing Complete
                </>
              ) : (
                <>
                  Synchronize Knowledge <Zap className="w-5 h-5 fill-current" />
                </>
              )}
            </button>
          </div>
          
          {/* Subtle Decorative Icon */}
          <Database className="absolute -right-10 -bottom-10 w-64 h-64 text-slate-50 dark:text-slate-800/20 -rotate-12 pointer-events-none" />
        </div>

        {/* --- PIPELINE STATUS CARD --- */}
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-slate-900 p-10 rounded-[3rem] text-white shadow-2xl relative overflow-hidden">
             <div className="relative z-10">
               <h4 className="text-xl font-black uppercase italic tracking-tighter mb-6 text-emerald-400">RAG Guidelines</h4>
               <ul className="space-y-5">
                 {[
                   "Upload official PDFs to calibrate evaluators.",
                   "AI splits content into semantic chunks.",
                   "Embeddings are stored in the Vector DB.",
                   "Bloom's mapping is automatically derived."
                 ].map((text, i) => (
                   <li key={i} className="flex gap-4 items-start group">
                     <div className="w-6 h-6 rounded-lg bg-emerald-500/20 flex items-center justify-center shrink-0 group-hover:bg-emerald-500 transition-colors">
                        <CheckCircle2 className="w-3.5 h-3.5 text-emerald-500 group-hover:text-white" />
                     </div>
                     <span className="text-xs font-bold text-slate-400 group-hover:text-white transition-colors leading-relaxed uppercase tracking-tighter">
                       {text}
                     </span>
                   </li>
                 ))}
               </ul>
             </div>
          </div>

          <AnimatePresence>
            {status === 'error' && (
              <motion.div 
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, scale: 0.9 }}
                className="bg-red-50 dark:bg-red-900/10 border border-red-100 dark:border-red-900/30 p-8 rounded-[2.5rem] flex items-center gap-6"
              >
                <AlertCircle className="w-10 h-10 text-red-500 shrink-0" />
                <div>
                  <p className="text-[10px] font-black text-red-500 uppercase tracking-widest">Pipeline Interrupted</p>
                  <p className="text-sm font-bold text-red-900 dark:text-red-200 mt-1">Check NLP Service connection (Port 5001)</p>
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>

      </div>
    </div>
  );
};

export default KnowledgeBaseTab;