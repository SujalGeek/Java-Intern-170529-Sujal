import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  ShieldAlert, Users, BookOpen, Server, UserCheck, Search, PlusCircle, 
  AlertCircle, CheckCircle, Edit3, Trash2, UserCog, X, LayoutList, 
  GraduationCap, Activity, Cpu, LayoutDashboard, UserPlus, BookCopy, LogOut, UsersRound, Loader2
} from 'lucide-react';
import api from '../api/axios';

const AdminPortal = () => {
  const navigate = useNavigate();

  const [activeTab, setActiveTab] = useState('DASHBOARD'); 
  const [users, setUsers] = useState([]);
  const [courses, setCourses] = useState([]);
  const [aiHealth, setAiHealth] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  const [userFilter, setUserFilter] = useState('ALL');
  const [editingUser, setEditingUser] = useState(null);
  const [editForm, setEditForm] = useState({ 
    fullName: "", email: "", role: 3, password: "",
    employeeId: "", department: "", officeLocation: "", phone: "",
    major: "", year: "", semester: "", maxCoursesPerSemester: ""
  });
  const [isUpdatingUser, setIsUpdatingUser] = useState(false);
  
  const [isCreatingUser, setIsCreatingUser] = useState(false);
  const [newUserForm, setNewUserForm] = useState({
    username: "", password: "", email: "", fullName: "", role: 3, 
    employeeId: "", department: "", officeLocation: "", phone: "",
    studentNumber: "", major: "", year: "", semester: "", maxCoursesPerSemester: ""
  });

  const [editingCourse, setEditingCourse] = useState(null);
  const [courseForm, setCourseForm] = useState({
    courseCode: "", courseName: "", description: "", credits: "", maxStudents: "", semester: "", year: new Date().getFullYear(), teacherId: ""
  });
  const [creatingCourse, setCreatingCourse] = useState(false);
  
  // 🔥 ROSTER MODAL STATE
  const [viewingRoster, setViewingRoster] = useState(null); 
  const [rosterStudents, setRosterStudents] = useState([]);
  const [isLoadingRoster, setIsLoadingRoster] = useState(false);

  const adminId = localStorage.getItem('userId');
  const adminRole = 1;

  useEffect(() => {
    const fetchAdminData = async () => {
      setIsLoading(true);
      try {
        const [usersRes, coursesRes, healthRes] = await Promise.all([
          api.get('/api/users/admin/all-users'),
          api.get('/api/course/all'), 
          api.get('/api/predict/health').catch(() => ({ data: { status: 'DOWN' } }))
        ]);
        
        setUsers(usersRes.data);
        setCourses(coursesRes.data);
        setAiHealth(healthRes.data);
      } catch (err) {
        console.error("Failed to fetch Admin Data.", err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchAdminData();
  }, []);

  const handleLogout = async () => {
    if (window.confirm("Are you sure you want to terminate the secure admin session?")) {
      try {
        const token = localStorage.getItem('refreshToken') || "null";
        await api.post('/api/users/logout', { refreshToken: token });
      } catch (err) {
        console.log("Backend logout failed or not needed, clearing local storage anyway.");
      }
      localStorage.clear();
      navigate('/login');
    }
  };

  const handleCreateUser = async (e) => {
    e.preventDefault();
    setIsCreatingUser(true);
    
    if (newUserForm.role === 3 && !newUserForm.studentNumber) {
        alert("Student Number is strictly required for Students!");
        setIsCreatingUser(false);
        return;
    }

    try {
      await api.post('/api/users', newUserForm, { headers: { 'X-User-Role': adminRole } });
      alert(`User ${newUserForm.username} provisioned successfully!`);
      const updatedUsers = await api.get('/api/users/admin/all-users');
      setUsers(updatedUsers.data);
      
      setNewUserForm({ 
        username: "", password: "", email: "", fullName: "", role: 3, 
        employeeId: "", department: "", officeLocation: "", phone: "",
        studentNumber: "", major: "", year: "", semester: "", maxCoursesPerSemester: ""
      });
    } catch (err) {
      alert(err.response?.data?.message || "Failed to create user. Check constraints.");
    } finally {
      setIsCreatingUser(false);
    }
  };

  const handleEditClick = async (user) => {
    const rawRole = user.role || user.roleId || 3;
    let numericRole = 3;
    if (rawRole === 1 || String(rawRole).includes('ADMIN')) numericRole = 1;
    if (rawRole === 2 || String(rawRole).includes('TEACHER')) numericRole = 2;

    setEditingUser(user);
    setEditForm({
      fullName: user.fullName || user.full_name || user.username || "",
      email: user.email || "",
      role: numericRole,
      password: "", employeeId: "", department: "", officeLocation: "", phone: "", major: "", year: "", semester: "", maxCoursesPerSemester: ""
    });

    try {
        const res = await api.get(`/api/users/${user.userId || user.id}`, {
            headers: { 'X-User-Role': adminRole, 'X-User-Id': adminId }
        });
        const profile = res.data;
        setEditForm(prev => ({
            ...prev,
            major: profile.major || prev.major,
            semester: profile.semester || prev.semester,
            year: profile.year || prev.year
        }));
    } catch (e) { console.log("Deep profile fetch not available yet"); }
  };

  const handleUpdateUser = async (e) => {
    e.preventDefault();
    setIsUpdatingUser(true);
    const targetId = editingUser.userId || editingUser.id;
    try {
      await api.put(`/api/users/${targetId}`, editForm, { 
        headers: { 'X-User-Role': adminRole, 'X-User-Id': adminId } 
      });
      alert("User updated successfully!");
      const updatedUsers = await api.get('/api/users/admin/all-users');
      setUsers(updatedUsers.data);
      setEditingUser(null);
    } catch (err) {
      alert(err.response?.data?.message || "Failed to update user.");
    } finally {
      setIsUpdatingUser(false);
    }
  };

  const handleDeleteUser = async (userId) => {
    if (!window.confirm(`PERMANENT DELETE: Are you sure you want to completely remove User #${userId}?`)) return;
    try {
      await api.delete(`/api/users/${userId}`, { headers: { 'X-User-Role': adminRole } });
      setUsers(users.filter(u => (u.userId || u.id) !== userId));
    } catch (err) {
      alert(err.response?.data?.message || "Failed to delete user.");
    }
  };

  const handleEditCourseClick = (course) => {
    setEditingCourse(course);
    setCourseForm({
      courseCode: course.courseCode || course.code || "",
      courseName: course.courseName || course.name || "",
      description: course.description || "",
      credits: course.credits || "",
      maxStudents: course.maxStudents || "",
      semester: course.semester || "",
      year: course.year || new Date().getFullYear(),
      teacherId: course.teacherId || ""
    });
  };

  const handleCourseSubmit = async (e) => {
    e.preventDefault();
    setCreatingCourse(true);
    try {
      const payload = {
         ...courseForm,
         credits: courseForm.credits || 3,
         maxStudents: courseForm.maxStudents || 60,
         semester: courseForm.semester || 1,
      };

      if (editingCourse) {
        const courseId = editingCourse.courseId || editingCourse.id;
        await api.put(`/api/course/${courseId}`, payload, { headers: { 'X-User-Role': adminRole }});
        
        if (courseForm.teacherId) {
           await api.put(`/api/course/${courseId}/assign/${courseForm.teacherId}`);
        }
        alert("Course updated successfully!");
        setEditingCourse(null);
      } else {
        const res = await api.post('/api/course/create', payload, { headers: { 'X-User-Role': adminRole, 'X-User-Id': adminId }});
        
        if (courseForm.teacherId) {
           const courseId = res.data.courseId || res.data.id;
           await api.put(`/api/course/${courseId}/assign/${courseForm.teacherId}`);
        }
        alert(`Course deployed successfully!`);
      }

      const updatedCourses = await api.get('/api/course/all');
      setCourses(updatedCourses.data);
      setCourseForm({ courseCode: "", courseName: "", description: "", credits: "", maxStudents: "", semester: "", year: new Date().getFullYear(), teacherId: "" });
    } catch (error) {
      alert(error.response?.data?.message || "Failed to process course action.");
    } finally {
      setCreatingCourse(false);
    }
  };

  const handleDropCourse = async (courseId) => {
    if (!window.confirm("CRITICAL WARNING: Drop this course? All associated data might be lost.")) return;
    try {
      await api.delete(`/api/course/${courseId}`, { headers: { 'X-User-Role': adminRole } });
      alert(`Course dropped successfully.`);
      setCourses(courses.filter(c => (c.courseId || c.id) !== courseId));
    } catch (err) {
      alert(err.response?.data?.message || "Failed to drop course. Ensure no students are enrolled.");
    }
  };

  // 🔥 THE NEW ROSTER SYNC FUNCTION 🔥
  const handleViewRoster = async (course) => {
    setViewingRoster(course);
    setRosterStudents([]); // Clear old state
    setIsLoadingRoster(true);

    try {
      const courseId = course.courseId || course.id;
      // Call the enrollment service
      const res = await api.get(`/api/enrollments/course/${courseId}`, {
         headers: { 'X-User-Role': adminRole }
      });
      
      // The API returns an array of enrollments. We extract the student IDs.
      const enrolledStudentIds = res.data.map(e => e.studentId || e.userId);
      
      // Match the IDs with the users we already have in global state!
      const studentsInCourse = users.filter(u => enrolledStudentIds.includes(u.userId || u.id));
      
      setRosterStudents(studentsInCourse);
    } catch (error) {
      console.error("No enrollments found or endpoint missing", error);
    } finally {
      setIsLoadingRoster(false);
    }
  };

  const allTeachers = users.filter(u => String(u.role || u.roleId) === '2' || String(u.role).includes('TEACHER'));
  const studentCount = users.filter(u => String(u.role || u.roleId) === '3' || String(u.role).includes('STUDENT')).length;
  const adminCount = users.length - studentCount - allTeachers.length;
  
  const filteredUsers = users.filter(u => {
    if (userFilter === 'ALL') return true;
    const r = String(u.role || u.roleId);
    if (userFilter === 'TEACHER') return r === '2' || String(u.role).includes('TEACHER');
    if (userFilter === 'STUDENT') return r === '3' || String(u.role).includes('STUDENT');
    if (userFilter === 'ADMIN') return r === '1' || String(u.role).includes('ADMIN');
    return true;
  });

  return (
    <div className="flex min-h-screen bg-[#F8FAFC] dark:bg-[#0F172A] transition-colors duration-500">
      
      <aside className="w-72 bg-white dark:bg-slate-900 border-r border-slate-100 dark:border-slate-800 p-6 flex flex-col shrink-0 z-10 hidden lg:flex">
        <div className="flex items-center gap-3 mb-12 px-2 mt-4">
          <ShieldAlert className="w-10 h-10 text-rose-600" />
          <div>
            <h2 className="text-xl font-black text-slate-900 dark:text-white tracking-tighter uppercase italic leading-none">Global</h2>
            <h2 className="text-xl font-black text-rose-600 tracking-tighter uppercase italic leading-none">Command</h2>
          </div>
        </div>

        <nav className="flex-1 space-y-2">
          <button onClick={() => setActiveTab('DASHBOARD')} className={`w-full flex items-center gap-4 px-4 py-4 rounded-2xl font-black uppercase tracking-widest text-[10px] transition-all ${activeTab === 'DASHBOARD' ? 'bg-indigo-50 text-indigo-600 dark:bg-indigo-900/30 dark:text-indigo-400' : 'text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 hover:text-slate-600'}`}><LayoutDashboard className="w-5 h-5" /> Overview</button>
          <button onClick={() => setActiveTab('USERS')} className={`w-full flex items-center gap-4 px-4 py-4 rounded-2xl font-black uppercase tracking-widest text-[10px] transition-all ${activeTab === 'USERS' ? 'bg-indigo-50 text-indigo-600 dark:bg-indigo-900/30 dark:text-indigo-400' : 'text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 hover:text-slate-600'}`}><UserCheck className="w-5 h-5" /> Identity Access</button>
          <button onClick={() => setActiveTab('COURSES')} className={`w-full flex items-center gap-4 px-4 py-4 rounded-2xl font-black uppercase tracking-widest text-[10px] transition-all ${activeTab === 'COURSES' ? 'bg-indigo-50 text-indigo-600 dark:bg-indigo-900/30 dark:text-indigo-400' : 'text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 hover:text-slate-600'}`}><BookCopy className="w-5 h-5" /> Course Infra</button>
        </nav>

        <button onClick={handleLogout} className="mt-4 mb-4 w-full flex items-center gap-4 px-4 py-4 rounded-2xl font-black uppercase tracking-widest text-[10px] text-rose-400 hover:bg-rose-50 hover:text-rose-600 dark:hover:bg-rose-900/30 dark:hover:text-rose-500 transition-all">
          <LogOut className="w-5 h-5" /> Terminate Session
        </button>

        <div className="mt-auto p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-800">
          <p className="text-[9px] font-black uppercase tracking-widest text-slate-400 mb-3">System Status</p>
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-slate-700 dark:text-slate-300 flex items-center gap-2"><Cpu className="w-4 h-4 text-indigo-500" /> AI Engine</span>
            {aiHealth?.status === 'UP' ? (
              <span className="flex items-center gap-1.5 text-[10px] font-black text-emerald-500 bg-emerald-50 dark:bg-emerald-900/20 px-2 py-1 rounded-md"><span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span> ONLINE</span>
            ) : (
              <span className="flex items-center gap-1.5 text-[10px] font-black text-rose-500 bg-rose-50 dark:bg-rose-900/20 px-2 py-1 rounded-md"><span className="w-1.5 h-1.5 rounded-full bg-rose-500"></span> OFFLINE</span>
            )}
          </div>
        </div>
      </aside>

      <main className="flex-1 p-8 lg:p-12 overflow-y-auto custom-scrollbar relative">
        <AnimatePresence mode="wait">
          
          {/* DASHBOARD TAB */}
          {activeTab === 'DASHBOARD' && (
            <motion.div key="dashboard" initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -10 }} className="space-y-8 max-w-7xl mx-auto">
              <header>
                 <h1 className="text-4xl font-black text-slate-900 dark:text-white uppercase italic tracking-tighter">System <span className="text-indigo-600">Metrics</span></h1>
                 <p className="text-slate-400 font-bold uppercase text-[10px] tracking-[0.4em] mt-2">Level 1 Authorization View</p>
              </header>

              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                <div className="bg-white dark:bg-slate-900 p-8 rounded-[2rem] border border-slate-100 dark:border-slate-800 shadow-sm">
                  <div className="w-12 h-12 bg-indigo-50 dark:bg-indigo-900/30 text-indigo-600 rounded-2xl flex items-center justify-center mb-6"><Users className="w-6 h-6" /></div>
                  <p className="text-4xl font-black text-slate-800 dark:text-white mb-1">{studentCount}</p>
                  <p className="text-[10px] font-black uppercase tracking-widest text-slate-400">Total Students</p>
                </div>
                <div className="bg-white dark:bg-slate-900 p-8 rounded-[2rem] border border-slate-100 dark:border-slate-800 shadow-sm">
                  <div className="w-12 h-12 bg-fuchsia-50 dark:bg-fuchsia-900/30 text-fuchsia-600 rounded-2xl flex items-center justify-center mb-6"><UserCheck className="w-6 h-6" /></div>
                  <p className="text-4xl font-black text-slate-800 dark:text-white mb-1">{allTeachers.length}</p>
                  <p className="text-[10px] font-black uppercase tracking-widest text-slate-400">Total Faculty</p>
                </div>
                <div className="bg-white dark:bg-slate-900 p-8 rounded-[2rem] border border-slate-100 dark:border-slate-800 shadow-sm">
                  <div className="w-12 h-12 bg-emerald-50 dark:bg-emerald-900/30 text-emerald-600 rounded-2xl flex items-center justify-center mb-6"><BookOpen className="w-6 h-6" /></div>
                  <p className="text-4xl font-black text-slate-800 dark:text-white mb-1">{courses.length}</p>
                  <p className="text-[10px] font-black uppercase tracking-widest text-slate-400">Active Courses</p>
                </div>
                <div className="bg-white dark:bg-slate-900 p-8 rounded-[2rem] border border-slate-100 dark:border-slate-800 shadow-sm">
                  <div className="w-12 h-12 bg-rose-50 dark:bg-rose-900/30 text-rose-600 rounded-2xl flex items-center justify-center mb-6"><Activity className="w-6 h-6" /></div>
                  <p className="text-2xl font-black text-slate-800 dark:text-white mb-1 mt-2">{aiHealth?.status || 'N/A'}</p>
                  <p className="text-[10px] font-black uppercase tracking-widest text-slate-400">Prediction Engine</p>
                </div>
              </div>

              <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                <div className="lg:col-span-2 bg-white dark:bg-slate-900 rounded-[2.5rem] border border-slate-100 dark:border-slate-800 p-8 shadow-sm">
                  <div className="flex justify-between items-center mb-8">
                    <h3 className="text-lg font-black uppercase tracking-widest text-slate-800 dark:text-white flex items-center gap-3"><Server className="w-5 h-5 text-indigo-500" /> Recent Deployments</h3>
                    <button onClick={() => setActiveTab('COURSES')} className="text-xs font-bold text-indigo-600 dark:text-indigo-400 hover:underline">View All &rarr;</button>
                  </div>
                  <div className="space-y-4">
                    {courses.slice().reverse().slice(0, 4).map(c => (
                      <div key={c.courseId || c.id} className="flex items-center justify-between p-5 rounded-2xl bg-slate-50 dark:bg-slate-800/50 border border-slate-100 dark:border-slate-700/50 hover:border-indigo-200 dark:hover:border-indigo-800/50 transition-colors">
                         <div className="flex items-center gap-5">
                             <div className="w-12 h-12 rounded-xl bg-indigo-100 dark:bg-indigo-900/40 text-indigo-600 flex items-center justify-center font-black text-sm">{c.courseCode ? c.courseCode.substring(0,2) : 'CS'}</div>
                             <div>
                                 <p className="font-bold text-slate-800 dark:text-white text-base">{c.courseName || c.name}</p>
                                 <p className="text-[10px] font-black uppercase tracking-widest text-slate-400 mt-1">{c.courseCode} • {c.credits || 3} Credits</p>
                             </div>
                         </div>
                         <span className="px-3 py-1.5 bg-emerald-100 text-emerald-600 dark:bg-emerald-900/30 rounded-lg text-[10px] font-black tracking-widest flex items-center gap-1.5"><span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span> Active</span>
                      </div>
                    ))}
                  </div>
                </div>

                <div className="space-y-8">
                  <div className="bg-white dark:bg-slate-900 rounded-[2.5rem] border border-slate-100 dark:border-slate-800 p-8 shadow-sm">
                     <h3 className="text-sm font-black uppercase tracking-widest text-slate-800 dark:text-white mb-6 flex items-center gap-2"><Users className="w-4 h-4 text-fuchsia-500" /> Identity Dist.</h3>
                     <div className="space-y-6">
                        <div>
                            <div className="flex justify-between text-xs font-bold mb-2"><span className="text-slate-500 dark:text-slate-400">Students</span><span className="text-indigo-500">{studentCount}</span></div>
                            <div className="w-full bg-slate-100 dark:bg-slate-800 rounded-full h-2.5 overflow-hidden"><motion.div initial={{width:0}} animate={{width: `${users.length ? (studentCount/users.length)*100 : 0}%`}} transition={{duration: 1}} className="bg-indigo-500 h-full"></motion.div></div>
                        </div>
                        <div>
                            <div className="flex justify-between text-xs font-bold mb-2"><span className="text-slate-500 dark:text-slate-400">Faculty</span><span className="text-fuchsia-500">{allTeachers.length}</span></div>
                            <div className="w-full bg-slate-100 dark:bg-slate-800 rounded-full h-2.5 overflow-hidden"><motion.div initial={{width:0}} animate={{width: `${users.length ? (allTeachers.length/users.length)*100 : 0}%`}} transition={{duration: 1, delay: 0.2}} className="bg-fuchsia-500 h-full"></motion.div></div>
                        </div>
                     </div>
                  </div>

                  <div className="bg-indigo-600 rounded-[2.5rem] p-8 text-white shadow-xl shadow-indigo-900/20 dark:shadow-none relative overflow-hidden">
                      <div className="absolute top-0 right-0 p-8 opacity-10 pointer-events-none"><Server className="w-40 h-40" /></div>
                      <h3 className="text-sm font-black uppercase tracking-widest text-indigo-200 mb-6 flex items-center gap-2 relative z-10"><Cpu className="w-4 h-4" /> Node Topology</h3>
                      <div className="space-y-5 relative z-10">
                          <div className="flex justify-between items-end border-b border-indigo-500/50 pb-3"><span className="text-[10px] font-black uppercase tracking-widest text-indigo-200">API Gateway</span><span className="text-xs font-bold bg-indigo-500/50 px-2 py-1 rounded">PORT 8091</span></div>
                          <div className="flex justify-between items-end border-b border-indigo-500/50 pb-3"><span className="text-[10px] font-black uppercase tracking-widest text-indigo-200">Auth / User</span><span className="text-xs font-bold bg-indigo-500/50 px-2 py-1 rounded">PORT 8080</span></div>
                          <div className="flex justify-between items-end"><span className="text-[10px] font-black uppercase tracking-widest text-indigo-200">Course / Exams</span><span className="text-xs font-bold bg-indigo-500/50 px-2 py-1 rounded">PORT 8082</span></div>
                      </div>
                  </div>
                </div>
              </div>
            </motion.div>
          )}

          {/* USERS TAB */}
          {activeTab === 'USERS' && (
            <motion.div key="users" initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -10 }} className="space-y-8 max-w-7xl mx-auto">
              <header className="flex justify-between items-end">
                 <div><h1 className="text-4xl font-black text-slate-900 dark:text-white uppercase italic tracking-tighter">Identity <span className="text-indigo-600">Access</span></h1></div>
              </header>

              <div className="grid grid-cols-1 xl:grid-cols-3 gap-8">
                <div className="xl:col-span-2 space-y-4">
                  <div className="flex flex-wrap items-center justify-between bg-white dark:bg-slate-900 p-4 rounded-3xl border border-slate-100 dark:border-slate-800 shadow-sm">
                    <div className="flex gap-2">
                      <button onClick={() => setUserFilter('ALL')} className={`px-5 py-2.5 rounded-xl text-xs font-black uppercase tracking-widest transition-all ${userFilter === 'ALL' ? 'bg-indigo-50 text-indigo-600 dark:bg-indigo-900/30' : 'text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800'}`}>All</button>
                      <button onClick={() => setUserFilter('STUDENT')} className={`px-5 py-2.5 rounded-xl text-xs font-black uppercase tracking-widest transition-all ${userFilter === 'STUDENT' ? 'bg-indigo-50 text-indigo-600 dark:bg-indigo-900/30' : 'text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800'}`}>Students</button>
                      <button onClick={() => setUserFilter('TEACHER')} className={`px-5 py-2.5 rounded-xl text-xs font-black uppercase tracking-widest transition-all ${userFilter === 'TEACHER' ? 'bg-fuchsia-50 text-fuchsia-600 dark:bg-fuchsia-900/30' : 'text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800'}`}>Teachers</button>
                    </div>
                  </div>

                  <div className="bg-white dark:bg-slate-900 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm overflow-hidden">
                    <div className="overflow-x-auto max-h-[600px] custom-scrollbar">
                      <table className="w-full text-sm text-left">
                        <thead className="bg-slate-50 dark:bg-slate-800/80 text-[10px] uppercase font-black tracking-widest text-slate-500 sticky top-0 z-10">
                          <tr>
                            <th className="px-8 py-4">ID</th>
                            <th className="px-8 py-4">Name / Email</th>
                            <th className="px-8 py-4">Role</th>
                            <th className="px-8 py-4 text-right">Actions</th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100 dark:divide-slate-800/50">
                          {filteredUsers.map(u => {
                            const id = u.userId || u.id;
                            const r = String(u.role || u.roleId);
                            const roleName = r === '1' ? 'ADMIN' : r === '2' ? 'TEACHER' : 'STUDENT';
                            return (
                              <tr key={id} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/20 transition-colors">
                                <td className="px-8 py-5 font-black text-slate-400">#{id}</td>
                                <td className="px-8 py-5">
                                  <p className="font-bold text-slate-800 dark:text-white">{u.fullName || u.username}</p>
                                  <p className="text-xs text-slate-500">{u.email}</p>
                                </td>
                                <td className="px-8 py-5">
                                  <span className={`px-3 py-1 rounded-lg text-[10px] font-black tracking-widest ${roleName === 'TEACHER' ? 'bg-fuchsia-100 text-fuchsia-600 dark:bg-fuchsia-900/30' : roleName === 'ADMIN' ? 'bg-rose-100 text-rose-600 dark:bg-rose-900/30' : 'bg-slate-100 text-slate-600 dark:bg-slate-800'}`}>{roleName}</span>
                                </td>
                                <td className="px-8 py-5 text-right flex justify-end gap-2">
                                  <button onClick={() => handleEditClick(u)} className="p-2 text-slate-400 hover:text-indigo-600 bg-slate-50 dark:bg-slate-800 rounded-lg"><Edit3 className="w-4 h-4" /></button>
                                  <button onClick={() => handleDeleteUser(id)} className="p-2 text-slate-400 hover:text-rose-600 bg-slate-50 dark:bg-slate-800 rounded-lg"><Trash2 className="w-4 h-4" /></button>
                                </td>
                              </tr>
                            );
                          })}
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>

                <div className="bg-slate-900 rounded-[3rem] p-10 text-white shadow-2xl h-fit sticky top-12">
                  {editingUser ? (
                    <form onSubmit={handleUpdateUser} className="space-y-4">
                      <div className="flex justify-between items-center mb-6">
                        <h3 className="text-xl font-black uppercase italic tracking-tight"><UserCog className="w-5 h-5 inline mr-2 text-indigo-400"/> Update User</h3>
                        <button type="button" onClick={() => setEditingUser(null)}><X className="w-5 h-5 text-slate-400 hover:text-white"/></button>
                      </div>
                      <input type="text" placeholder="Full Name" value={editForm.fullName} onChange={(e) => setEditForm({...editForm, fullName: e.target.value})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none placeholder:text-slate-500" required />
                      <input type="email" placeholder="Email" value={editForm.email} onChange={(e) => setEditForm({...editForm, email: e.target.value})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none placeholder:text-slate-500" required />
                      
                      {editForm.role === 2 && (
                        <div className="grid grid-cols-2 gap-3">
                           <input type="text" placeholder="Emp ID" value={editForm.employeeId} onChange={e => setEditForm({...editForm, employeeId: e.target.value})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none" />
                           <input type="text" placeholder="Dept" value={editForm.department} onChange={e => setEditForm({...editForm, department: e.target.value})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none" />
                        </div>
                      )}

                      {editForm.role === 3 && (
                        <div className="grid grid-cols-2 gap-3">
                           <input type="text" placeholder="Major" value={editForm.major} onChange={e => setEditForm({...editForm, major: e.target.value})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none" />
                           <input type="number" placeholder="Year" value={editForm.year} onChange={e => setEditForm({...editForm, year: e.target.value ? parseInt(e.target.value) : ""})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none" />
                           <input type="number" placeholder="Semester" value={editForm.semester} onChange={e => setEditForm({...editForm, semester: e.target.value ? parseInt(e.target.value) : ""})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none" />
                           <input type="number" placeholder="Max Courses" value={editForm.maxCoursesPerSemester} onChange={e => setEditForm({...editForm, maxCoursesPerSemester: e.target.value ? parseInt(e.target.value) : ""})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none" />
                        </div>
                      )}

                      <button type="submit" disabled={isUpdatingUser} className="w-full py-4 mt-4 bg-emerald-600 hover:bg-emerald-500 rounded-xl font-black uppercase tracking-widest text-xs">Save Changes</button>
                    </form>
                  ) : (
                    <form onSubmit={handleCreateUser} className="space-y-4">
                      <h3 className="text-xl font-black uppercase italic tracking-tight mb-6"><UserPlus className="w-5 h-5 inline mr-2 text-indigo-400"/> Provision Identity</h3>
                      
                      <div className="grid grid-cols-2 gap-3">
                        <input type="text" placeholder="Username" value={newUserForm.username} onChange={e => setNewUserForm({...newUserForm, username: e.target.value})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none placeholder:text-slate-500" required />
                        <input type="password" placeholder="Password" value={newUserForm.password} onChange={e => setNewUserForm({...newUserForm, password: e.target.value})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none placeholder:text-slate-500" required />
                      </div>
                      
                      <input type="text" placeholder="Full Name" value={newUserForm.fullName} onChange={e => setNewUserForm({...newUserForm, fullName: e.target.value})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none placeholder:text-slate-500" required />
                      <input type="email" placeholder="Email" value={newUserForm.email} onChange={e => setNewUserForm({...newUserForm, email: e.target.value})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none placeholder:text-slate-500" required />
                      
                      <div className="bg-slate-800/50 p-4 rounded-xl border border-slate-700">
                        <label className="text-[10px] font-black text-slate-400 uppercase tracking-widest ml-1 mb-2 block">Select Role</label>
                        <select value={newUserForm.role} onChange={e => setNewUserForm({...newUserForm, role: parseInt(e.target.value)})} className="w-full p-3 bg-slate-800 rounded-lg text-sm font-bold border-none text-white cursor-pointer focus:ring-2 focus:ring-indigo-500">
                          <option value={3}>STUDENT</option>
                          <option value={2}>TEACHER</option>
                          <option value={1}>ADMIN</option>
                        </select>
                      </div>

                      {newUserForm.role === 2 && (
                        <motion.div initial={{opacity:0, height:0}} animate={{opacity:1, height:'auto'}} className="grid grid-cols-2 gap-3">
                          <input type="text" placeholder="Employee ID *" value={newUserForm.employeeId} onChange={e => setNewUserForm({...newUserForm, employeeId: e.target.value})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none placeholder:text-rose-400" required />
                          <input type="text" placeholder="Department" value={newUserForm.department} onChange={e => setNewUserForm({...newUserForm, department: e.target.value})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none placeholder:text-slate-500" />
                        </motion.div>
                      )}

                      {newUserForm.role === 3 && (
                        <motion.div initial={{opacity:0, height:0}} animate={{opacity:1, height:'auto'}} className="grid grid-cols-2 gap-3">
                          <input type="text" placeholder="Student Number *" value={newUserForm.studentNumber} onChange={e => setNewUserForm({...newUserForm, studentNumber: e.target.value})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none placeholder:text-rose-400" required />
                          <input type="text" placeholder="Major" value={newUserForm.major} onChange={e => setNewUserForm({...newUserForm, major: e.target.value})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none placeholder:text-slate-500" />
                          <input type="number" placeholder="Year" value={newUserForm.year} onChange={e => setNewUserForm({...newUserForm, year: e.target.value ? parseInt(e.target.value) : ""})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none placeholder:text-slate-500" />
                          <input type="number" placeholder="Semester" value={newUserForm.semester} onChange={e => setNewUserForm({...newUserForm, semester: e.target.value ? parseInt(e.target.value) : ""})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none placeholder:text-slate-500" />
                        </motion.div>
                      )}

                      <button type="submit" disabled={isCreatingUser} className="w-full py-4 mt-4 bg-indigo-600 hover:bg-indigo-500 rounded-xl font-black uppercase tracking-widest text-xs transition-colors shadow-lg shadow-indigo-900/50">
                        {isCreatingUser ? "Provisioning..." : "Create Identity"}
                      </button>
                    </form>
                  )}
                </div>
              </div>
            </motion.div>
          )}

          {/* 🟢 TAB: COURSE INFRASTRUCTURE */}
          {activeTab === 'COURSES' && (
            <motion.div key="courses" initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -10 }} className="space-y-8 max-w-7xl mx-auto">
              <header>
                 <h1 className="text-4xl font-black text-slate-900 dark:text-white uppercase italic tracking-tighter">Course <span className="text-indigo-600">Infrastructure</span></h1>
              </header>

              <div className="grid grid-cols-1 xl:grid-cols-3 gap-8">
                
                <div className="xl:col-span-2 bg-white dark:bg-slate-900 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm p-8">
                  <h3 className="text-xl font-black uppercase tracking-tight text-slate-800 dark:text-white mb-6">Deployed Configurations</h3>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {courses.map(c => (
                      <div key={c.courseId || c.id} className="p-6 rounded-3xl border border-slate-100 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/30">
                        <div className="flex justify-between items-start mb-2">
                          <span className="px-2.5 py-1 bg-indigo-100 text-indigo-600 dark:bg-indigo-900/30 dark:text-indigo-400 rounded-md text-[10px] font-black uppercase tracking-widest">{c.courseCode || c.code}</span>
                          <div className="flex gap-2">
                             <button onClick={() => handleViewRoster(c)} className="text-slate-400 hover:text-indigo-500 transition-colors" title="View Roster"><UsersRound className="w-4 h-4" /></button>
                             <button onClick={() => handleEditCourseClick(c)} className="text-slate-400 hover:text-emerald-500 transition-colors" title="Edit Course"><Edit3 className="w-4 h-4" /></button>
                             <button onClick={() => handleDropCourse(c.courseId || c.id)} className="text-slate-400 hover:text-rose-500 transition-colors" title="Drop Course"><Trash2 className="w-4 h-4" /></button>
                          </div>
                        </div>
                        <h4 className="font-bold text-slate-800 dark:text-white text-lg leading-tight mb-2">{c.courseName || c.name}</h4>
                        <div className="flex items-center gap-4 text-xs font-bold text-slate-500">
                          <span>{c.credits || 3} Credits</span> • <span>Max: {c.maxStudents || 60}</span>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>

                <div className="bg-slate-900 rounded-[3rem] p-10 text-white shadow-2xl h-fit sticky top-12">
                  <div className="flex justify-between items-center mb-6">
                    <h3 className="text-xl font-black uppercase italic tracking-tight">
                      {editingCourse ? <><Edit3 className="w-5 h-5 inline mr-2 text-emerald-400"/> Update Course</> : <><PlusCircle className="w-5 h-5 inline mr-2 text-indigo-400"/> Init Course</>}
                    </h3>
                    {editingCourse && (
                      <button type="button" onClick={() => { setEditingCourse(null); setCourseForm({ courseCode: "", courseName: "", description: "", credits: "", maxStudents: "", semester: "", year: new Date().getFullYear(), teacherId: "" }); }}><X className="w-5 h-5 text-slate-400 hover:text-white"/></button>
                    )}
                  </div>

                  <form onSubmit={handleCourseSubmit} className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                      <input type="text" placeholder="Code (CS-101)" value={courseForm.courseCode} onChange={e => setCourseForm({...courseForm, courseCode: e.target.value})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none placeholder:text-slate-500" required />
                      <input type="number" placeholder="Credits" value={courseForm.credits} onChange={e => setCourseForm({...courseForm, credits: e.target.value ? parseInt(e.target.value) : ""})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none placeholder:text-slate-500" required />
                    </div>
                    <input type="text" placeholder="Course Name" value={courseForm.courseName} onChange={e => setCourseForm({...courseForm, courseName: e.target.value})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none placeholder:text-slate-500" required />
                    <textarea placeholder="Course Description (Syllabus)" value={courseForm.description} onChange={e => setCourseForm({...courseForm, description: e.target.value})} rows="2" className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none placeholder:text-slate-500" />

                    <div className="grid grid-cols-2 gap-4">
                      <input type="number" placeholder="Max Capacity" value={courseForm.maxStudents} onChange={e => setCourseForm({...courseForm, maxStudents: e.target.value ? parseInt(e.target.value) : ""})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none placeholder:text-slate-500" />
                      <input type="number" placeholder="Semester" value={courseForm.semester} onChange={e => setCourseForm({...courseForm, semester: e.target.value ? parseInt(e.target.value) : ""})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none placeholder:text-slate-500" />
                    </div>

                    <select value={courseForm.teacherId} onChange={e => setCourseForm({...courseForm, teacherId: e.target.value})} className="w-full p-4 bg-slate-800 rounded-xl text-sm font-bold border-none text-white cursor-pointer">
                      <option value="">-- Assign Faculty --</option>
                      {allTeachers.map(t => (
                        <option key={t.userId || t.id} value={t.userId || t.id}>{t.fullName || t.username}</option>
                      ))}
                    </select>

                    <button type="submit" disabled={creatingCourse} className={`w-full py-4 mt-4 rounded-xl font-black uppercase tracking-widest text-xs transition-colors shadow-lg ${editingCourse ? 'bg-emerald-600 hover:bg-emerald-500 shadow-emerald-900/50' : 'bg-indigo-600 hover:bg-indigo-500 shadow-indigo-900/50'}`}>
                      {editingCourse ? 'Save Changes' : 'Deploy Infrastructure'}
                    </button>
                  </form>
                </div>
              </div>
            </motion.div>
          )}

        </AnimatePresence>
      </main>

      {/* 🔥 THE ENROLLMENT ROSTER MODAL OVERLAY */}
      <AnimatePresence>
        {viewingRoster && (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/80 backdrop-blur-sm">
            <motion.div initial={{ scale: 0.95 }} animate={{ scale: 1 }} exit={{ scale: 0.95 }} className="bg-white dark:bg-slate-900 rounded-[3rem] w-full max-w-3xl overflow-hidden shadow-2xl border border-slate-100 dark:border-slate-800">
              
              {/* Header */}
              <div className="p-8 border-b border-slate-100 dark:border-slate-800 flex justify-between items-center bg-slate-50 dark:bg-slate-800/50">
                <div>
                  <h2 className="text-2xl font-black text-slate-800 dark:text-white uppercase italic tracking-tight">{viewingRoster.courseCode} Roster</h2>
                  <p className="text-xs font-bold text-slate-500">{viewingRoster.courseName}</p>
                </div>
                <button onClick={() => setViewingRoster(null)} className="p-2 bg-slate-200 dark:bg-slate-700 text-slate-600 dark:text-slate-300 rounded-full hover:bg-rose-100 hover:text-rose-600 transition-colors">
                  <X className="w-5 h-5" />
                </button>
              </div>

              {/* Dynamic Roster List */}
              <div className="p-8 max-h-[500px] overflow-y-auto custom-scrollbar">
                {isLoadingRoster ? (
                  <div className="text-center py-12">
                     <Loader2 className="w-12 h-12 animate-spin text-indigo-500 mx-auto mb-4" />
                     <p className="text-slate-400 font-bold">Syncing Student Data...</p>
                  </div>
                ) : rosterStudents.length > 0 ? (
                  <div className="space-y-3">
                    {rosterStudents.map(student => (
                      <div key={student.userId || student.id} className="flex items-center justify-between p-4 bg-slate-50 dark:bg-slate-800/50 rounded-2xl border border-slate-100 dark:border-slate-700/50 hover:border-indigo-100 dark:hover:border-indigo-900/30 transition-colors">
                        <div className="flex items-center gap-4">
                          <div className="w-10 h-10 bg-indigo-100 dark:bg-indigo-900/40 text-indigo-600 rounded-xl flex items-center justify-center font-black">
                             {(student.fullName || student.username || "U").charAt(0).toUpperCase()}
                          </div>
                          <div>
                            <p className="text-sm font-bold text-slate-800 dark:text-white">{student.fullName || student.username}</p>
                            <p className="text-[10px] text-slate-500 font-black tracking-widest">{student.email}</p>
                          </div>
                        </div>
                        <span className="px-3 py-1.5 bg-emerald-100 text-emerald-600 dark:bg-emerald-900/30 rounded-lg text-[10px] font-black tracking-widest flex items-center gap-1.5">
                           <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span> Enrolled
                        </span>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="text-center py-12">
                     <UsersRound className="w-16 h-16 text-slate-200 dark:text-slate-800 mx-auto mb-4" />
                     <h3 className="text-lg font-bold text-slate-400">No Students Enrolled</h3>
                     <p className="text-xs text-slate-500 mt-2">There are currently no students registered for this course.</p>
                  </div>
                )}
              </div>

            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

    </div>
  );
};

export default AdminPortal;