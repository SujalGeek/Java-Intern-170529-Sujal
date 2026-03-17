import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Users, BarChart3, ShieldAlert, FileText, Download, 
  Loader2, BrainCircuit, TrendingUp, ChevronRight, 
  Search, Target, AlertCircle, Mail, Hash
} from 'lucide-react';
import api from '../api/axios';

const TeacherOverviewTab = ({ courseId }) => {
  // --- STATE MANAGEMENT ---
  const [roster, setRoster] = useState([]);
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [courseStats, setCourseStats] = useState(null);
  const [recentExams, setRecentExams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState("");
  const [downloadingId, setDownloadingId] = useState(null);

  // --- 1. INDUSTRIAL DATA SYNCHRONIZATION ---
  useEffect(() => {
    if (!courseId) return;

    const fetchTeacherEnvironment = async () => {
      setLoading(true);
      try {
        // Step A: Fetch KPI Aggregate Stats
        const statsRes = await api.get(`/api/analytics/course/${courseId}`);
        setCourseStats(statsRes.data);

        // Step B: Fetch Course Midterm Registry
        const examRes = await api.get(`/api/v1/exams/course/${courseId}`);
        const examsData = Array.isArray(examRes.data) ? examRes.data : [examRes.data];
        setRecentExams(examsData);

        // Step C: Fetch Enrollment Roster
        const enrollRes = await api.get(`/api/enrollments/course/${courseId}`);
        const enrolledStudentIds = enrollRes.data.map(e => e.studentId);

        if (enrolledStudentIds.length > 0) {
          // Step D: Parallel AI Performance & Identity Resolution
          const rosterWithPerformance = await Promise.all(enrolledStudentIds.map(async (studentId) => {
            try {
              const userRes = await api.get(`/api/users/${studentId}`);
              const perfRes = await api.get(`/api/performance/${studentId}/${courseId}`, {
                headers: { 'X-User-Role': 2 } // Identify as Teacher
              });
              return { ...userRes.data, performance: perfRes.data };
            } catch (e) {
              return { userId: studentId, fullName: `Student ${studentId}`, performance: null };
            }
          }));
          
          setRoster(rosterWithPerformance);
          // Auto-select the first student if none selected
          if (rosterWithPerformance.length > 0) setSelectedStudent(rosterWithPerformance[0]);
        } else {
          setRoster([]);
          setSelectedStudent(null);
        }
      } catch (err) {
        console.error("Master Sync Failure", err);
      } finally {
        setLoading(false);
      }
    };
    
    fetchTeacherEnvironment();
  }, [courseId]);

  // --- 2. EXPORT PROTOCOL (PDF DOWNLOAD) ---
  const handleDownloadPDF = async (midtermId) => {
    setDownloadingId(midtermId);
    try {
      const res = await api.get(`/api/v1/exams/${midtermId}/pdf`, {
        responseType: 'blob', 
        headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
      });
      const blob = new Blob([res.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `Nexus_Assessment_Ref_${midtermId}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
    } catch (err) {
      console.error("Export Protocol Failure", err);
      alert("Error generating PDF Export.");
    } finally {
      setDownloadingId(null);
    }
  };

  // --- 3. FILTERING ENGINE ---
  const filteredRoster = roster.filter(s => 
    s.fullName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    s.userId?.toString().includes(searchTerm)
  );

  // --- LOADING / INITIAL STATES ---
  if (!courseId) {
    return (
      <div className="h-[60vh] flex flex-col items-center justify-center space-y-4 text-emerald-600 opacity-50">
        <Target className="w-16 h-16 animate-pulse" />
        <p className="font-black uppercase tracking-[0.5em] text-xs text-center">
          Awaiting Global Course Context...<br/>Select a sequence from the header
        </p>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="h-[70vh] flex flex-col items-center justify-center space-y-6">
        <Loader2 className="w-16 h-16 animate-spin text-emerald-600" />
        <div className="text-center">
          <p className="font-black text-emerald-600 uppercase tracking-[0.4em] text-[10px]">Synchronizing AI Analytics Hub</p>
          <p className="text-slate-400 font-bold text-[9px] uppercase mt-2 tracking-widest">Compiling Student Cognitive Patterns...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-8 pb-10">
      
      {/* --- TOP ROW: KPI AGGREGATES --- */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {[
          { label: 'Total Active Enrolled', value: courseStats?.totalStudents || 0, icon: Users, color: 'text-blue-500', bg: 'bg-blue-50 dark:bg-blue-900/20' },
          { label: 'Mean Assessment Score', value: `${courseStats?.averageScore || 0}%`, icon: BarChart3, color: 'text-emerald-500', bg: 'bg-emerald-50 dark:bg-emerald-900/20' },
          { label: 'High Risk Alert Level', value: courseStats?.highRiskCount || 0, icon: ShieldAlert, color: 'text-red-500', bg: 'bg-red-50 dark:bg-red-900/20' },
        ].map((stat, i) => (
          <motion.div 
            key={i} initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: i * 0.1 }}
            className="bg-white dark:bg-slate-900 p-8 rounded-[2.5rem] border border-slate-100 dark:border-slate-800 shadow-sm flex items-center gap-6"
          >
            <div className={`w-14 h-14 ${stat.bg} rounded-2xl flex items-center justify-center`}><stat.icon className={`w-7 h-7 ${stat.color}`} /></div>
            <div>
              <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1 leading-none">{stat.label}</p>
              <p className="text-4xl font-black text-slate-900 dark:text-white tracking-tighter italic">{stat.value}</p>
            </div>
          </motion.div>
        ))}
      </div>

      {/* --- MASTER-DETAIL WORKSPACE --- */}
      <div className="flex flex-col lg:flex-row gap-8 h-[800px]">
        
        {/* SIDEBAR: STUDENT NAVIGATION */}
        <div className="w-full lg:w-96 flex flex-col bg-white dark:bg-slate-900 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm overflow-hidden">
          <div className="p-6 border-b border-slate-50 dark:border-slate-800">
            <h4 className="text-[10px] font-black uppercase tracking-[0.3em] text-slate-400 mb-4 ml-1">Academic Roster</h4>
            <div className="relative group">
              <Search className="absolute left-4 top-3.5 w-4 h-4 text-slate-300 group-focus-within:text-emerald-500 transition-colors" />
              <input 
                type="text" 
                placeholder="Search Identity..." 
                className="w-full pl-12 pr-4 py-4 bg-slate-50 dark:bg-slate-800/50 border-none rounded-2xl text-[11px] font-bold uppercase tracking-wider focus:ring-2 focus:ring-emerald-500/20 outline-none"
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          </div>

          <div className="flex-1 overflow-y-auto p-4 space-y-2 custom-scrollbar">
            {filteredRoster.map(student => {
              const isHighRisk = student.performance?.riskLevel === 'HIGH' || student.performance?.riskLevel === 'High';
              const isActive = selectedStudent?.userId === student.userId;
              return (
                <button 
                  key={student.userId} 
                  onClick={() => setSelectedStudent(student)}
                  className={`w-full p-5 rounded-[1.8rem] flex items-center justify-between transition-all group ${isActive ? 'bg-emerald-600 text-white shadow-xl shadow-emerald-200 dark:shadow-none translate-x-2' : 'hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-400'}`}
                >
                  <div className="flex items-center gap-4">
                    <div className={`w-12 h-12 rounded-2xl flex items-center justify-center font-black text-sm ${isActive ? 'bg-white/20 text-white' : isHighRisk ? 'bg-rose-100 text-rose-600' : 'bg-slate-100 dark:bg-slate-700 text-slate-500'}`}>
                      {(student.fullName || "S").charAt(0)}
                    </div>
                    <div className="text-left">
                      <p className={`text-xs font-black uppercase tracking-tight leading-none mb-1 ${isActive ? 'text-white' : 'text-slate-800 dark:text-slate-200'}`}>{student.fullName}</p>
                      <p className={`text-[9px] font-bold uppercase tracking-widest opacity-60 ${isActive ? 'text-emerald-100' : ''}`}>ID: {student.userId}</p>
                    </div>
                  </div>
                  {isHighRisk && !isActive && <AlertCircle className="w-4 h-4 text-rose-500 animate-pulse" />}
                  {isActive && <ChevronRight className="w-4 h-4 text-white opacity-50" />}
                </button>
              );
            })}
          </div>
        </div>

        {/* DETAIL PANEL: COGNITIVE DRILLDOWN */}
        <div className="flex-1 bg-white dark:bg-slate-900 rounded-[3.5rem] border border-slate-100 dark:border-slate-800 shadow-sm overflow-hidden flex flex-col">
          <AnimatePresence mode="wait">
            {selectedStudent ? (
              <motion.div 
                key={selectedStudent.userId} 
                initial={{ opacity: 0, x: 20 }} 
                animate={{ opacity: 1, x: 0 }} 
                exit={{ opacity: 0, x: -20 }}
                className="flex-1 flex flex-col p-10 lg:p-14 overflow-y-auto custom-scrollbar"
              >
                {/* Profile Header */}
                <div className="flex flex-col md:flex-row justify-between items-start gap-8 mb-14">
                  <div className="flex items-center gap-8">
                    <div className="w-24 h-24 rounded-[2.5rem] border-4 border-emerald-500/10 p-1 bg-gradient-to-br from-emerald-50 to-white dark:from-emerald-950 dark:to-slate-900">
                      <img 
                        src={`https://ui-avatars.com/api/?name=${selectedStudent.fullName}&background=10b981&color=fff&bold=true&size=128`} 
                        className="w-full h-full object-cover rounded-[2rem]"
                        alt="Avatar"
                      />
                    </div>
                    <div className="space-y-3">
                      <h2 className="text-5xl font-black text-slate-900 dark:text-white uppercase italic tracking-tighter leading-none">
                        {selectedStudent.fullName}
                      </h2>
                      <div className="flex flex-wrap gap-4 text-[10px] font-black uppercase tracking-widest text-slate-400">
                        <span className="flex items-center gap-2 bg-slate-50 dark:bg-slate-800 px-3 py-1 rounded-lg"><Mail className="w-3 h-3 text-emerald-500"/> {selectedStudent.email}</span>
                        <span className="flex items-center gap-2 bg-slate-50 dark:bg-slate-800 px-3 py-1 rounded-lg"><Hash className="w-3 h-3 text-emerald-500"/> STU-{selectedStudent.userId}</span>
                      </div>
                    </div>
                  </div>
                  <div className={`px-8 py-4 rounded-3xl border-2 font-black uppercase tracking-[0.2em] text-xs ${selectedStudent.performance?.riskLevel === 'HIGH' || selectedStudent.performance?.riskLevel === 'High' ? 'bg-rose-50 border-rose-100 text-rose-600 animate-pulse' : 'bg-emerald-50 border-emerald-100 text-emerald-600'}`}>
                    AI Status: {selectedStudent.performance?.riskLevel || 'Processing'} Risk
                  </div>
                </div>

                {/* AI Performance Grid */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
                  {[
                    { label: 'ML Predicted Grade', value: selectedStudent.performance?.predictedGrade || 'N/A', icon: BrainCircuit, color: 'text-emerald-600' },
                    { label: 'Model Confidence', value: `${Math.round(selectedStudent.performance?.predictionConfidence * 100 || 0)}%`, icon: Target, color: 'text-indigo-600' },
                    { label: 'Weighted Efficiency', value: `${selectedStudent.performance?.finalScore || 0}%`, icon: TrendingUp, color: 'text-blue-600' },
                  ].map((item, idx) => (
                    <div key={idx} className="bg-slate-50 dark:bg-slate-800/40 p-8 rounded-[2.8rem] border border-slate-100 dark:border-slate-800 relative overflow-hidden group">
                      <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-3 leading-none relative z-10">{item.label}</p>
                      <h5 className={`text-6xl font-black italic tracking-tighter ${item.color} relative z-10`}>{item.value}</h5>
                      <item.icon className="absolute -right-2 -bottom-2 w-24 h-24 opacity-[0.03] -rotate-12 group-hover:rotate-0 transition-transform duration-700" />
                    </div>
                  ))}
                </div>

                {/* Bloom Analysis Data Stream */}
                <div className="bg-slate-950 p-10 rounded-[3.5rem] relative overflow-hidden mb-12 shadow-2xl">
                   <div className="relative z-10 flex gap-10">
                      <div className="w-16 h-16 bg-white/5 backdrop-blur-2xl rounded-2xl flex items-center justify-center border border-white/10 shrink-0">
                         <BarChart3 className="text-emerald-400 w-8 h-8" />
                      </div>
                      <div className="space-y-4">
                        <h4 className="text-xl font-black italic uppercase tracking-tight text-white">Diagnostic Feedback Stream</h4>
                        <p className="text-slate-400 font-medium leading-relaxed italic text-lg opacity-90">
                           "{selectedStudent.performance?.diagnosticFeedback || 'Academic cognitive data stream is being synthesized. Initial baseline not yet established for this context.'}"
                        </p>
                      </div>
                   </div>
                   <div className="absolute inset-0 bg-gradient-to-r from-emerald-500/5 to-transparent pointer-events-none" />
                </div>

                {/* Exam Management */}
                <div className="space-y-6">
                  <h4 className="text-lg font-black uppercase italic tracking-tighter text-slate-800 dark:text-white ml-2">Sequence Assessments</h4>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {recentExams.map(exam => (
                      <div key={exam.midtermId} className="flex items-center justify-between p-6 bg-white dark:bg-slate-900 border border-slate-100 dark:border-slate-800 rounded-[2rem] shadow-sm hover:border-emerald-500/30 transition-all group">
                         <div className="flex items-center gap-4">
                            <div className="w-10 h-10 bg-slate-50 dark:bg-slate-800 rounded-xl flex items-center justify-center"><FileText className="w-5 h-5 text-slate-400 group-hover:text-emerald-500 transition-colors"/></div>
                            <div>
                               <p className="text-xs font-black uppercase text-slate-800 dark:text-white leading-none">Midterm #{exam.midtermId}</p>
                               <p className="text-[9px] font-bold uppercase text-slate-400 mt-1">Weight: {exam.totalMarks}pts</p>
                            </div>
                         </div>
                         <button 
                           onClick={() => handleDownloadPDF(exam.midtermId)}
                           className="p-3 bg-emerald-50 dark:bg-emerald-900/20 text-emerald-600 rounded-xl hover:bg-emerald-600 hover:text-white transition-all shadow-sm"
                         >
                            {downloadingId === exam.midtermId ? <Loader2 className="w-4 h-4 animate-spin"/> : <Download className="w-4 h-4"/>}
                         </button>
                      </div>
                    ))}
                  </div>
                </div>

              </motion.div>
            ) : (
              <div className="flex-1 flex flex-col items-center justify-center opacity-20">
                <BrainCircuit className="w-32 h-32 text-slate-300 mb-6" />
                <p className="font-black uppercase tracking-[0.5em] text-xs">Select Student to Initialize Drilldown</p>
              </div>
            )}
          </AnimatePresence>
        </div>

      </div>

    </div>
  );
};

export default TeacherOverviewTab;