import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  BookPlus, CheckCircle, Search, AlertCircle, Sparkles, 
  Filter, ShieldCheck, PlusCircle, Settings, GraduationCap, X, Layers
} from 'lucide-react';
import api from '../api/axios';

const CourseRegistryTab = () => {
  const [availableCourses, setAvailableCourses] = useState([]);
  const [myEnrollments, setMyEnrollments] = useState([]); // PREVENT DUPLICATES
  const [loading, setLoading] = useState(true);
  const [enrollingId, setEnrollingId] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  
  // --- 🔥 1. TEACHER DEPLOYMENT STATE ---
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newCourse, setNewCourse] = useState({
    courseName: '',
    courseCode: '',
    semester: 1,
    year: 2026,
    description: '',
    credits: 3,
    maxStudents: 50
  });

  const userId = localStorage.getItem('userId');
  const userRole = localStorage.getItem('userRole'); // 2 = Teacher, 3 = Student

  // --- 2. MULTI-SERVICE DATA SYNC ---
  const fetchCatalogData = async () => {
    try {
      setLoading(true);
      // Step A: Fetch Global Course List (Admin/All View)
      const catalogRes = await api.get('/api/course/all'); 
      setAvailableCourses(catalogRes.data);

      // Step B: Role-Based Ownership Handshake
      if (userRole === '3') {
        // If Student: Fetch IDs to block re-enrollment
        const myRes = await api.get(`/api/enrollments/student/${userId}`);
        const enrolledIds = myRes.data.map(e => e.courseId);
        setMyEnrollments(enrolledIds);
      } else if (userRole === '2') {
        // If Teacher: Fetch owned courses to mark as "Managed"
        const teacherRes = await api.get('/api/course/my');
        const managedIds = teacherRes.data.map(c => c.courseId);
        setMyEnrollments(managedIds); 
      }

    } catch (err) {
      console.error("Catalog Sync Failed", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if(userId) fetchCatalogData();
  }, [userId, userRole]);

  // --- 3. TEACHER: DEPLOY NEW CURRICULUM ---
  const handleCreateCourse = async (e) => {
    e.preventDefault();
    try {
      // Hits: CourseController @PostMapping("/create")
      await api.post('/api/course/create', newCourse);
      alert("Intelligence Path Deployed Successfully!");
      setShowCreateModal(false);
      fetchCatalogData(); // Re-sync UI
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "Course deployment protocol failed.");
    }
  };

  // --- 4. STUDENT: ENROLLMENT PROTOCOL ---
  const handleEnroll = async (courseId) => {
    setEnrollingId(courseId);
    try {
      // Hits: EnrollmentController @PostMapping
      await api.post('/api/enrollments', { courseId: parseInt(courseId) });
      alert("Nexus Handshake Successful! Curriculum initialized.");
      fetchCatalogData(); // Re-sync UI
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.message || "Enrollment protocol rejected.");
    } finally {
      setEnrollingId(null);
    }
  };

  const filteredCourses = availableCourses.filter(c => 
    c.courseName.toLowerCase().includes(searchTerm.toLowerCase()) || 
    c.courseCode.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) return (
    <div className="p-20 text-center flex flex-col items-center justify-center space-y-4">
      <div className="w-12 h-12 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin" />
      <p className="font-black text-indigo-600 uppercase tracking-[0.4em] text-[10px]">Scanning Academic Ledger...</p>
    </div>
  );

  return (
    <div className="space-y-12 text-left transition-all duration-500 relative">
      
      {/* --- 🔥 5. TEACHER MODAL: DEPLOYMENT ENGINE --- */}
      <AnimatePresence>
        {showCreateModal && (
          <div className="fixed inset-0 z-[100] flex items-center justify-center p-6 bg-slate-900/60 backdrop-blur-xl">
            <motion.div 
              initial={{ opacity: 0, scale: 0.9, y: 30 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.9, y: 30 }}
              className="bg-white dark:bg-slate-900 w-full max-w-2xl rounded-[4rem] p-12 shadow-2xl border border-slate-100 dark:border-slate-800"
            >
              <div className="flex justify-between items-center mb-10">
                <div>
                  <h3 className="text-3xl font-black uppercase italic tracking-tighter dark:text-white">Deploy New Curriculum</h3>
                  <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mt-2">Faculty Intelligence Initialization</p>
                </div>
                <button onClick={() => setShowCreateModal(false)} className="p-3 bg-slate-50 dark:bg-slate-800 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-2xl transition-all group">
                  <X className="w-6 h-6 text-slate-400 group-hover:text-red-500" />
                </button>
              </div>

              <form onSubmit={handleCreateCourse} className="space-y-8">
                <div className="grid grid-cols-2 gap-8">
                  <div className="space-y-2">
                    <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-4">Curriculum Name</label>
                    <input required placeholder="e.g. Advanced AI Patterns" className="w-full p-5 bg-slate-50 dark:bg-slate-800 rounded-[1.5rem] outline-none font-bold text-sm dark:text-white border border-slate-100 dark:border-slate-700 focus:ring-4 focus:ring-emerald-500/10 transition-all" 
                      onChange={e => setNewCourse({...newCourse, courseName: e.target.value})} />
                  </div>
                  <div className="space-y-2">
                    <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-4">Unique Code</label>
                    <input required placeholder="e.g. CS-909" className="w-full p-5 bg-slate-50 dark:bg-slate-800 rounded-[1.5rem] outline-none font-bold text-sm dark:text-white border border-slate-100 dark:border-slate-700 focus:ring-4 focus:ring-emerald-500/10 transition-all"
                      onChange={e => setNewCourse({...newCourse, courseCode: e.target.value})} />
                  </div>
                </div>

                <div className="space-y-2">
                  <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-4">Course Description</label>
                  <textarea placeholder="Outline the cognitive objectives of this path..." className="w-full p-6 bg-slate-50 dark:bg-slate-800 rounded-[2rem] outline-none font-bold text-sm dark:text-white border border-slate-100 dark:border-slate-700 h-32 focus:ring-4 focus:ring-emerald-500/10 transition-all resize-none"
                    onChange={e => setNewCourse({...newCourse, description: e.target.value})} />
                </div>

                <div className="grid grid-cols-3 gap-6">
                  <input type="number" placeholder="Semester" className="p-5 bg-slate-50 dark:bg-slate-800 rounded-[1.5rem] outline-none font-bold text-sm dark:text-white border border-slate-100 dark:border-slate-700"
                    onChange={e => setNewCourse({...newCourse, semester: e.target.value})} />
                  <input type="number" placeholder="Credits" className="p-5 bg-slate-50 dark:bg-slate-800 rounded-[1.5rem] outline-none font-bold text-sm dark:text-white border border-slate-100 dark:border-slate-700"
                    onChange={e => setNewCourse({...newCourse, credits: e.target.value})} />
                  <motion.button whileHover={{ scale: 1.02 }} type="submit" className="bg-emerald-600 text-white rounded-[1.5rem] font-black uppercase tracking-widest text-[11px] shadow-xl shadow-emerald-200 dark:shadow-none transition-all">
                    Deploy Sequence
                  </motion.button>
                </div>
              </form>
            </motion.div>
          </div>
        )}
      </AnimatePresence>

      <header className="flex flex-col md:flex-row md:items-end justify-between gap-6">
        <div>
          <div className="flex items-center gap-3 text-indigo-600 mb-2">
            <Sparkles className="w-6 h-6" />
            <span className="text-[10px] font-black uppercase tracking-[0.3em]">
               {userRole === '2' ? 'Faculty Plane: Control' : 'Curriculum discovery'}
            </span>
          </div>
          <h2 className="text-5xl font-black text-slate-900 dark:text-white uppercase italic tracking-tighter leading-none">
            Course <span className="text-indigo-600">Registry</span>
          </h2>
          <p className="text-slate-400 font-bold text-xs uppercase tracking-widest mt-4">
            {userRole === '2' 
              ? "Oversee current academic sequences or initialize new cognitive paths."
              : "Initialize your cognitive learning paths via indexed curricula."}
          </p>
        </div>

        <div className="flex flex-col md:flex-row items-center gap-4">
          {/* TEACHER ONLY: ACTION TRIGGER */}
          {userRole === '2' && (
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => setShowCreateModal(true)}
              className="flex items-center gap-3 px-8 py-4 bg-emerald-600 text-white rounded-2xl font-black text-[11px] uppercase tracking-widest shadow-xl shadow-emerald-100 dark:shadow-none transition-all"
            >
              <PlusCircle className="w-4 h-4" /> Create New Course
            </motion.button>
          )}

          {/* SEARCH ENGINE */}
          <div className="relative group w-full md:w-96">
            <Search className="w-4 h-4 text-slate-400 absolute left-5 top-5 group-focus-within:text-indigo-600 transition-colors" />
            <input 
              type="text"
              placeholder="Search Global Registry..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-12 pr-6 py-4 bg-white dark:bg-slate-800 border border-slate-100 dark:border-slate-700 rounded-2xl text-sm font-black focus:ring-4 focus:ring-indigo-500/10 outline-none transition-all dark:text-white shadow-sm"
            />
          </div>
        </div>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10">
        {filteredCourses.map((course) => {
          const isOwnedOrEnrolled = myEnrollments.includes(course.courseId);

          return (
            <motion.div 
              key={course.courseId}
              whileHover={{ y: -12 }}
              className="group bg-white dark:bg-slate-900 p-10 rounded-[3.5rem] border border-slate-100 dark:border-slate-800 shadow-sm flex flex-col justify-between relative overflow-hidden transition-all"
            >
              <div>
                <div className="flex justify-between items-start mb-8">
                  <span className={`px-5 py-2 rounded-2xl text-[10px] font-black uppercase tracking-widest border ${
                    userRole === '2' && isOwnedOrEnrolled 
                      ? 'bg-emerald-50 dark:bg-emerald-900/30 text-emerald-600 border-emerald-100'
                      : 'bg-indigo-50 dark:bg-indigo-900/30 text-indigo-600 border-indigo-100'
                  }`}>
                    {course.courseCode}
                  </span>
                  {isOwnedOrEnrolled ? (
                    <ShieldCheck className={`w-7 h-7 ${userRole === '2' ? 'text-emerald-500' : 'text-emerald-500'}`} />
                  ) : (
                    <BookPlus className="w-7 h-7 text-slate-200 dark:text-slate-700 group-hover:text-indigo-300 transition-colors" />
                  )}
                </div>
                
                <h3 className="text-2xl font-black text-slate-900 dark:text-white mb-4 tracking-tight leading-tight group-hover:text-indigo-600 transition-colors">
                  {course.courseName}
                </h3>
                
                <p className="text-xs text-slate-400 font-bold mb-10 line-clamp-3 uppercase tracking-tighter leading-relaxed italic opacity-80">
                  "{course.description || "Synthesizing advanced Intelligence Analytics for descriptive student evaluations."}"
                </p>

                <div className="flex items-center gap-4 mb-8">
                   <div className="flex items-center gap-2 px-3 py-1 bg-slate-50 dark:bg-slate-800 rounded-lg border border-slate-100 dark:border-slate-700">
                      <GraduationCap className="w-3.5 h-3.5 text-slate-400" />
                      <span className="text-[9px] font-black text-slate-500 uppercase tracking-tighter">SEM-{course.semester || 'NA'}</span>
                   </div>
                   <div className="flex items-center gap-2 px-3 py-1 bg-slate-50 dark:bg-slate-800 rounded-lg border border-slate-100 dark:border-slate-700">
                      <Layers className="w-3.5 h-3.5 text-slate-400" />
                      <span className="text-[9px] font-black text-slate-300 uppercase tracking-tighter">{course.credits || 3} Units</span>
                   </div>
                </div>
              </div>

              {/* ACTION HUB: ROLE DISPATCHER */}
              {userRole === '2' ? (
                <button
                  disabled={!isOwnedOrEnrolled}
                  className={`w-full py-5 rounded-[2rem] font-black text-[11px] uppercase tracking-[0.3em] transition-all flex items-center justify-center gap-3 ${
                    isOwnedOrEnrolled
                      ? 'bg-slate-900 text-white hover:bg-emerald-600 shadow-xl shadow-slate-200 dark:shadow-none'
                      : 'bg-slate-100 dark:bg-slate-800 text-slate-400 cursor-not-allowed border border-slate-200 dark:border-slate-700'
                  }`}
                >
                  {isOwnedOrEnrolled ? (
                    <>Manage Syllabus <Settings className="w-4 h-4 animate-spin-slow" /></>
                  ) : (
                    'Unassigned Instruction'
                  )}
                </button>
              ) : (
                <button
                  onClick={() => handleEnroll(course.courseId)}
                  disabled={enrollingId === course.courseId || isOwnedOrEnrolled}
                  className={`w-full py-5 rounded-[2rem] font-black text-[11px] uppercase tracking-[0.3em] transition-all flex items-center justify-center gap-3 ${
                    isOwnedOrEnrolled
                      ? 'bg-emerald-50 dark:bg-emerald-900/20 text-emerald-600 cursor-default border border-emerald-100 dark:border-emerald-800/50'
                      : enrollingId === course.courseId
                        ? 'bg-slate-100 dark:bg-slate-800 text-slate-400 cursor-not-allowed italic'
                        : 'bg-indigo-600 text-white hover:bg-indigo-700 hover:scale-[1.02] shadow-xl shadow-indigo-100 dark:shadow-none'
                  }`}
                >
                  {isOwnedOrEnrolled ? (
                    <>Active Path <CheckCircle className="w-4 h-4" /></>
                  ) : enrollingId === course.courseId ? (
                    'Syncing...'
                  ) : (
                    <>Initialize <Sparkles className="w-4 h-4 fill-current" /></>
                  )}
                </button>
              )}
              
              <div className="absolute -right-8 -bottom-8 w-32 h-32 bg-indigo-600/5 rounded-full blur-2xl group-hover:bg-indigo-600/10 transition-all duration-700" />
            </motion.div>
          );
        })}
      </div>

      {filteredCourses.length === 0 && (
        <div className="py-20 text-center bg-white dark:bg-slate-900 rounded-[4rem] border-2 border-dashed border-slate-100 dark:border-slate-800 shadow-inner">
           <AlertCircle className="w-12 h-12 text-slate-200 dark:text-slate-700 mx-auto mb-4" />
           <p className="text-slate-400 font-black uppercase tracking-widest italic opacity-50">Zero registry segments detected in the ledger.</p>
        </div>
      )}
    </div>
  );
};

export default CourseRegistryTab;