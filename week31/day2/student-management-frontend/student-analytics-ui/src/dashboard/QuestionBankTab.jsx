import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Database, Edit3, Trash2, Save, X, Loader2, CheckCircle2, ArrowLeft, BookOpen, CheckSquare, AlignLeft, LayoutList } from 'lucide-react';
import api from '../api/axios';

const QuestionBankTab = ({ courseId }) => {
  const [questions, setQuestions] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  
  // UI States
  const [isEditingMode, setIsEditingMode] = useState(false);
  const [editForm, setEditForm] = useState({});
  
  // 🔥 NEW: Filter State for the Tabs
  const [filter, setFilter] = useState('ALL'); // 'ALL', 'MCQ', 'SUBJECTIVE'

  const fetchQuestions = async () => {
    if (!courseId) return;
    setIsLoading(true);
    try {
      const res = await api.get(`/api/v1/exams/questions/course/${courseId}`);
      setQuestions(Array.isArray(res.data) ? res.data : [res.data]);
    } catch (err) {
      console.error("Failed to load question bank", err);
      setQuestions([]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchQuestions();
  }, [courseId]);

  // --- ACTIONS ---
  const handleDelete = async (questionId) => {
    if (!window.confirm("Are you sure you want to delete this AI-generated question?")) return;
    try {
      await api.delete(`/api/v1/exams/questions/${questionId}`);
      setQuestions(questions.filter(q => q.examQuestionId !== questionId));
    } catch (err) {
      alert("Failed to delete question.");
    }
  };

  // Helper to accurately determine if a question is an MCQ
  const checkIsMCQ = (q) => {
    if (q.optionsJson && q.optionsJson !== "null" && q.optionsJson !== "[]") return true;
    if (q.options && q.options.length > 0) return true;
    return false;
  };

  const startEditing = (q) => {
    const isM = checkIsMCQ(q);
    let parsedOptions = ['', '', '', '']; 
    
    if (isM) {
      try {
        let rawOpts = q.optionsJson || q.options;
        if (typeof rawOpts === 'string') {
          rawOpts = JSON.parse(rawOpts);
          if (typeof rawOpts === 'string') rawOpts = JSON.parse(rawOpts);
        }
        if (Array.isArray(rawOpts) && rawOpts.length > 0) {
          parsedOptions = [
            rawOpts[0] || '',
            rawOpts[1] || '',
            rawOpts[2] || '',
            rawOpts[3] || ''
          ];
        }
      } catch (e) {
        console.error("Parse error", e);
      }
    }

    // Attach isMCQ to the edit form so the UI knows which inputs to show
    setEditForm({ ...q, parsedOptions, isMCQ: isM });
    setIsEditingMode(true);
  };

  const cancelEditing = () => {
    setIsEditingMode(false);
    setEditForm({});
  };

  const saveEdit = async () => {
    try {
      const payload = { ...editForm };
      
      if (payload.isMCQ && payload.parsedOptions) {
        payload.optionsJson = JSON.stringify(payload.parsedOptions);
      }

      // Remove temporary UI state before sending to Spring Boot
      delete payload.parsedOptions;
      delete payload.isMCQ;

      await api.put(`/api/v1/exams/questions/${editForm.examQuestionId}`, payload);
      
      setQuestions(questions.map(q => q.examQuestionId === editForm.examQuestionId ? { ...payload, optionsJson: payload.optionsJson } : q));
      setIsEditingMode(false);
      setEditForm({});
    } catch (err) {
      alert("Failed to update question.");
    }
  };

  if (!courseId) {
    return <div className="p-20 text-center font-black animate-pulse text-emerald-600 uppercase tracking-widest">Waiting for Course Selection...</div>;
  }

  // Derived counts for the tabs
  const mcqCount = questions.filter(q => checkIsMCQ(q)).length;
  const subjCount = questions.length - mcqCount;

  // Filter the list based on the active tab
  const displayQuestions = questions.filter(q => {
    const isM = checkIsMCQ(q);
    if (filter === 'MCQ') return isM;
    if (filter === 'SUBJECTIVE') return !isM;
    return true; // 'ALL'
  });

  return (
    <div className="space-y-8 text-left transition-colors duration-500">
      
      {/* Dynamic Header */}
      <header>
        <div className="flex items-center gap-4 text-emerald-600 mb-2">
          {isEditingMode ? <Edit3 className="w-6 h-6" /> : <Database className="w-6 h-6" />}
          <span className="text-[10px] font-black uppercase tracking-[0.3em]">
            {isEditingMode ? 'Editing Mode Active' : 'Human-in-the-Loop Override'}
          </span>
        </div>
        <h2 className="text-4xl font-black text-slate-900 dark:text-white tracking-tighter uppercase italic">
          {isEditingMode ? (
            <>Edit <span className="text-emerald-600">Question</span></>
          ) : (
            <>Question <span className="text-emerald-600">Repository</span></>
          )}
        </h2>
        <p className="text-slate-400 font-bold text-xs uppercase tracking-widest mt-2">
          {isEditingMode ? 'Modify the AI draft to perfection.' : 'Review, filter, and approve AI-Generated drafts before building exams.'}
        </p>
      </header>

      {isLoading ? (
        <div className="flex justify-center py-20">
          <Loader2 className="w-10 h-10 animate-spin text-emerald-600" />
        </div>
      ) : (
        <div className="relative">
          <AnimatePresence mode="wait">
            
            {/* ========================================== */}
            {/* VIEW 1: THE DEDICATED EDIT SCREEN */}
            {/* ========================================== */}
            {isEditingMode ? (
              <motion.div 
                key="edit-screen"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                transition={{ duration: 0.2 }}
                className="bg-white dark:bg-slate-900 p-8 rounded-[2.5rem] border border-emerald-100 dark:border-emerald-900/30 shadow-xl shadow-emerald-900/5"
              >
                <button 
                  onClick={cancelEditing} 
                  className="mb-8 flex items-center gap-2 text-xs font-black uppercase tracking-widest text-slate-400 hover:text-emerald-600 transition-colors"
                >
                  <ArrowLeft className="w-4 h-4" /> Back to Repository
                </button>

                <div className="space-y-6">
                  {/* Question Text */}
                  <div>
                    <label className="text-xs font-black text-slate-500 uppercase tracking-widest ml-2 mb-2 flex items-center gap-2">
                      <BookOpen className="w-4 h-4" /> Question Text
                    </label>
                    <textarea 
                      value={editForm.question || ''}
                      onChange={(e) => setEditForm({...editForm, question: e.target.value})}
                      className="w-full p-5 bg-slate-50 dark:bg-slate-800 border-2 border-transparent focus:border-emerald-500 rounded-2xl text-sm font-bold text-slate-800 dark:text-slate-100 outline-none transition-all shadow-inner"
                      rows="4"
                    />
                  </div>

                  {/* 🔥 DYNAMIC RENDER: MCQ vs SUBJECTIVE */}
                  {editForm.isMCQ ? (
                    /* ---------------- MCQ INPUTS ---------------- */
                    <div className="bg-slate-50 dark:bg-slate-800/50 p-6 rounded-3xl border border-slate-100 dark:border-slate-700">
                      <label className="text-xs font-black text-slate-500 uppercase tracking-widest mb-4 flex items-center gap-2">
                        <CheckSquare className="w-4 h-4" /> Multiple Choice Options
                      </label>
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        {editForm.parsedOptions.map((opt, i) => (
                          <div key={i} className="flex flex-col">
                            <span className="text-[10px] font-black text-emerald-600 uppercase tracking-widest mb-1 ml-1">
                              Option {String.fromCharCode(65 + i)}
                            </span>
                            <input 
                              type="text"
                              value={opt || ''}
                              onChange={(e) => {
                                const newOpts = [...editForm.parsedOptions];
                                newOpts[i] = e.target.value;
                                setEditForm({...editForm, parsedOptions: newOpts});
                              }}
                              className="w-full p-4 bg-white dark:bg-slate-900 border-2 border-transparent focus:border-emerald-500 rounded-xl text-xs font-bold text-slate-700 dark:text-slate-200 outline-none transition-all shadow-sm"
                            />
                          </div>
                        ))}
                      </div>
                    </div>
                  ) : (
                    /* ---------------- SUBJECTIVE INPUTS ---------------- */
                    <div className="bg-slate-50 dark:bg-slate-800/50 p-6 rounded-3xl border border-slate-100 dark:border-slate-700">
                      <label className="text-xs font-black text-slate-500 uppercase tracking-widest mb-4 flex items-center gap-2">
                        <AlignLeft className="w-4 h-4" /> AI Reference Answer (Rubric)
                      </label>
                      <textarea 
                        value={editForm.referenceAnswer || ''}
                        onChange={(e) => setEditForm({...editForm, referenceAnswer: e.target.value})}
                        placeholder="Enter the ideal answer used by the NLP engine to grade students..."
                        className="w-full p-5 bg-white dark:bg-slate-900 border-2 border-transparent focus:border-emerald-500 rounded-2xl text-sm font-bold text-slate-700 dark:text-slate-200 outline-none transition-all shadow-sm"
                        rows="5"
                      />
                    </div>
                  )}

                  {/* Settings Grid */}
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6 bg-slate-50 dark:bg-slate-800/50 p-6 rounded-3xl border border-slate-100 dark:border-slate-700">
                    
                    {/* Only show Correct Answer Dropdown if it's an MCQ */}
                    {editForm.isMCQ ? (
                      <div>
                        <label className="text-[10px] font-black text-slate-500 uppercase tracking-widest ml-1 mb-2 block">Correct Answer Key</label>
                        <select 
                          value={editForm.correctAnswer || 'A'}
                          onChange={(e) => setEditForm({...editForm, correctAnswer: e.target.value})}
                          className="w-full p-4 bg-white dark:bg-slate-900 border-none rounded-xl text-xs font-black text-emerald-600 outline-none focus:ring-2 focus:ring-emerald-500/20 cursor-pointer shadow-sm"
                        >
                          <option value="A">Option A</option>
                          <option value="B">Option B</option>
                          <option value="C">Option C</option>
                          <option value="D">Option D</option>
                        </select>
                      </div>
                    ) : (
                      <div>
                        <label className="text-[10px] font-black text-slate-500 uppercase tracking-widest ml-1 mb-2 block">Question Type</label>
                        <div className="w-full p-4 bg-slate-100 dark:bg-slate-900/50 border-none rounded-xl text-xs font-black text-slate-500 cursor-not-allowed shadow-sm">
                          SUBJECTIVE / DESCRIPTIVE
                        </div>
                      </div>
                    )}

                    <div>
                      <label className="text-[10px] font-black text-slate-500 uppercase tracking-widest ml-1 mb-2 block">Difficulty Level</label>
                      <select 
                        value={editForm.difficulty || 'MEDIUM'}
                        onChange={(e) => setEditForm({...editForm, difficulty: e.target.value})}
                        className="w-full p-4 bg-white dark:bg-slate-900 border-none rounded-xl text-xs font-black text-amber-600 outline-none focus:ring-2 focus:ring-emerald-500/20 cursor-pointer shadow-sm"
                      >
                        <option value="EASY">EASY</option>
                        <option value="MEDIUM">MEDIUM</option>
                        <option value="HARD">HARD</option>
                      </select>
                    </div>
                  </div>

                  {/* Action Buttons */}
                  <div className="flex justify-end gap-4 pt-6 border-t border-slate-100 dark:border-slate-800 mt-6">
                    <button onClick={cancelEditing} className="px-8 py-4 text-xs font-black uppercase tracking-widest text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-2xl transition-colors flex items-center gap-2">
                      <X className="w-5 h-5" /> Cancel
                    </button>
                    <button onClick={saveEdit} className="px-10 py-4 bg-emerald-600 hover:bg-emerald-700 text-white rounded-2xl text-xs font-black uppercase tracking-widest transition-all shadow-xl shadow-emerald-200 dark:shadow-none flex items-center gap-3">
                      <Save className="w-5 h-5" /> Save Changes
                    </button>
                  </div>
                </div>
              </motion.div>
            ) : (

            /* ========================================== */
            /* VIEW 2: THE MAIN LIST VIEW WITH TABS       */
            /* ========================================== */
              <motion.div 
                key="list-screen"
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: 20 }}
                transition={{ duration: 0.2 }}
              >
                
                {/* 🔥 NEW: THE FILTER TABS */}
                <div className="flex flex-wrap items-center gap-2 mb-8 bg-slate-50 dark:bg-slate-800/50 p-2 rounded-2xl w-fit border border-slate-100 dark:border-slate-700/50">
                  <button 
                    onClick={() => setFilter('ALL')} 
                    className={`px-6 py-3 rounded-xl text-xs font-black uppercase tracking-widest flex items-center gap-2 transition-all ${filter === 'ALL' ? 'bg-white dark:bg-slate-900 text-emerald-600 shadow-sm border border-slate-100 dark:border-slate-700' : 'text-slate-400 hover:text-slate-600 dark:hover:text-slate-300'}`}
                  >
                    <LayoutList className="w-4 h-4"/> All ({questions.length})
                  </button>
                  <button 
                    onClick={() => setFilter('MCQ')} 
                    className={`px-6 py-3 rounded-xl text-xs font-black uppercase tracking-widest flex items-center gap-2 transition-all ${filter === 'MCQ' ? 'bg-white dark:bg-slate-900 text-emerald-600 shadow-sm border border-slate-100 dark:border-slate-700' : 'text-slate-400 hover:text-slate-600 dark:hover:text-slate-300'}`}
                  >
                    <CheckSquare className="w-4 h-4"/> MCQs ({mcqCount})
                  </button>
                  <button 
                    onClick={() => setFilter('SUBJECTIVE')} 
                    className={`px-6 py-3 rounded-xl text-xs font-black uppercase tracking-widest flex items-center gap-2 transition-all ${filter === 'SUBJECTIVE' ? 'bg-white dark:bg-slate-900 text-emerald-600 shadow-sm border border-slate-100 dark:border-slate-700' : 'text-slate-400 hover:text-slate-600 dark:hover:text-slate-300'}`}
                  >
                    <AlignLeft className="w-4 h-4"/> Subjective ({subjCount})
                  </button>
                </div>

                {/* THE FILTERED LIST */}
                <div className="space-y-4">
                  {displayQuestions.length > 0 ? displayQuestions.map((q) => {
                    const isM = checkIsMCQ(q);
                    
                    return (
                      <div 
                        key={q.examQuestionId}
                        className="bg-white dark:bg-slate-900 p-6 rounded-[2rem] border border-slate-100 dark:border-slate-800 shadow-sm hover:border-emerald-200 dark:hover:border-emerald-800 transition-colors"
                      >
                        <div className="flex justify-between items-start gap-6">
                          <div className="flex-1">
                            
                            {/* Badges */}
                            <div className="flex items-center gap-3 mb-3">
                              <span className={`flex items-center gap-1 px-2.5 py-1 rounded-md text-[9px] font-black uppercase tracking-widest ${
                                isM ? 'bg-indigo-100 text-indigo-600 dark:bg-indigo-900/30' : 'bg-fuchsia-100 text-fuchsia-600 dark:bg-fuchsia-900/30'
                              }`}>
                                {isM ? <CheckSquare className="w-3 h-3"/> : <AlignLeft className="w-3 h-3"/>}
                                {isM ? 'MCQ' : 'SUBJECTIVE'}
                              </span>

                              <span className={`px-2.5 py-1 rounded-md text-[9px] font-black uppercase tracking-widest ${
                                q.difficulty === 'HARD' ? 'bg-red-100 text-red-600 dark:bg-red-900/30' : 
                                q.difficulty === 'MEDIUM' ? 'bg-amber-100 text-amber-600 dark:bg-amber-900/30' : 
                                'bg-emerald-100 text-emerald-600 dark:bg-emerald-900/30'
                              }`}>
                                {q.difficulty}
                              </span>
                              
                              <span className="px-2.5 py-1 rounded-md bg-blue-100 dark:bg-blue-900/30 text-blue-600 text-[9px] font-black uppercase tracking-widest">
                                {q.bloomLevel}
                              </span>
                            </div>
                            
                            <p className="text-sm font-bold text-slate-800 dark:text-slate-200 leading-relaxed mb-4">
                              {q.question}
                            </p>

                            {/* Smart Display: Show Correct Answer for MCQ, Reference Snippet for Subjective */}
                            <div className="inline-flex items-center gap-2 text-xs font-bold text-slate-500 dark:text-slate-400 bg-slate-50 dark:bg-slate-800/50 px-3 py-1.5 rounded-lg border border-slate-100 dark:border-slate-700/50">
                              <CheckCircle2 className={`w-4 h-4 ${isM ? 'text-emerald-500' : 'text-fuchsia-500'}`} /> 
                              {isM 
                                ? <span className="text-emerald-600 dark:text-emerald-400">Answer Key: {q.correctAnswer}</span>
                                : <span className="text-fuchsia-600 dark:text-fuchsia-400 line-clamp-1">Ref: {q.referenceAnswer || "Pending"}</span>
                              }
                            </div>

                          </div>
                          
                          {/* ACTION BUTTONS */}
                          <div className="flex flex-col gap-2 shrink-0">
                            <button 
                              onClick={() => startEditing(q)} 
                              className="p-4 text-slate-400 hover:text-blue-600 hover:bg-blue-50 dark:hover:bg-blue-900/30 rounded-2xl transition-all"
                              title="Edit this Question"
                            >
                              <Edit3 className="w-5 h-5" />
                            </button>
                            <button 
                              onClick={() => handleDelete(q.examQuestionId)} 
                              className="p-4 text-slate-400 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-900/30 rounded-2xl transition-all"
                              title="Delete this Question"
                            >
                              <Trash2 className="w-5 h-5" />
                            </button>
                          </div>
                        </div>
                      </div>
                    );
                  }) : (
                    <div className="text-center p-12 bg-slate-50 dark:bg-slate-800/50 rounded-[3rem] border border-dashed border-slate-200 dark:border-slate-700">
                      <Database className="w-12 h-12 text-slate-300 mx-auto mb-4" />
                      <p className="font-black text-slate-400 uppercase tracking-widest text-sm">No {filter !== 'ALL' ? filter : ''} Questions Found</p>
                    </div>
                  )}
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      )}
    </div>
  );
};

export default QuestionBankTab;