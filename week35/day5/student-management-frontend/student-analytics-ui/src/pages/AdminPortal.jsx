import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  ShieldAlert, Users, BookOpen, Server, UserCheck, Search, PlusCircle, 
  AlertCircle, CheckCircle, Edit3, Trash2, UserCog, X, LayoutList, 
  GraduationCap, Activity, Cpu, LayoutDashboard, UserPlus, BookCopy, LogOut, UsersRound, Loader2, Menu, Bell, Sun, Moon, ChevronRight, Info, Globe
} from 'lucide-react';
import api from '../api/axios';

const AdminPortal = () => {
  const navigate = useNavigate();

  const [activeTab, setActiveTab] = useState('DASHBOARD'); 
  const [users, setUsers] = useState([]);
  const [courses, setCourses] = useState([]);
  const [aiHealth, setAiHealth] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isDark, setIsDark] = useState(localStorage.getItem('theme') === 'dark');

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
  
  const [viewingRoster, setViewingRoster] = useState(null); 
  const [rosterStudents, setRosterStudents] = useState([]);
  const [isLoadingRoster, setIsLoadingRoster] = useState(false);

  const adminId = localStorage.getItem('userId');
  const adminRole = 1;

  // --- THEME ENGINE ---
  useEffect(() => {
    const root = window.document.documentElement;
    if (isDark) { root.classList.add('dark'); localStorage.setItem('theme', 'dark'); }
    else { root.classList.remove('dark'); localStorage.setItem('theme', 'light'); }
  }, [isDark]);

  // --- FETCH INITIAL DATA ---
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
      } catch (err) { console.error("Admin Sync Failed", err); }
      finally { setIsLoading(false); }
    };
    fetchAdminData();
  }, []);

  const handleLogout = async () => {
    if (window.confirm("Terminate secure admin session?")) {
      try {
        const token = localStorage.getItem('refreshToken') || "null";
        await api.post('/api/users/logout', { refreshToken: token });
      } catch (err) { console.log("Logout cleanup."); }
      localStorage.clear();
      navigate('/login');
    }
  };

  // --- USER IDENTITY LOGIC ---
  const handleCreateUser = async (e) => {
    e.preventDefault();
    setIsCreatingUser(true);
    if (newUserForm.role === 3 && !newUserForm.studentNumber) {
        alert("Student Number is required!");
        setIsCreatingUser(false);
        return;
    }
    try {
      await api.post('/api/users', newUserForm, { headers: { 'X-User-Role': adminRole } });
      alert(`User provisioned!`);
      const updatedUsers = await api.get('/api/users/admin/all-users');
      setUsers(updatedUsers.data);
      setNewUserForm({ username: "", password: "", email: "", fullName: "", role: 3, employeeId: "", department: "", officeLocation: "", phone: "", studentNumber: "", major: "", year: "", semester: "", maxCoursesPerSemester: "" });
    } catch (err) { alert("Creation failed."); }
    finally { setIsCreatingUser(false); }
  };

  const handleEditClick = async (user) => {
    const rawRole = user.role || user.roleId || 3;
    let numericRole = (rawRole === 1 || String(rawRole).includes('ADMIN')) ? 1 : (rawRole === 2 || String(rawRole).includes('TEACHER')) ? 2 : 3;
    setEditingUser(user);
    setEditForm({ 
        fullName: user.fullName || user.username || "", email: user.email || "", role: numericRole, password: "", 
        employeeId: user.employeeId || "", department: user.department || "", officeLocation: user.officeLocation || "", 
        phone: user.phone || "", major: user.major || "", year: user.year || "", semester: user.semester || "", maxCoursesPerSemester: user.maxCoursesPerSemester || "" 
    });
    try {
        const res = await api.get(`/api/users/${user.userId || user.id}`, { headers: { 'X-User-Role': adminRole, 'X-User-Id': adminId } });
        setEditForm(prev => ({ ...prev, ...res.data }));
    } catch (e) { console.log("Profile deep sync failed."); }
  };

  const handleUpdateUser = async (e) => {
    e.preventDefault();
    setIsUpdatingUser(true);
    try {
      await api.put(`/api/users/${editingUser.userId || editingUser.id}`, editForm, { headers: { 'X-User-Role': adminRole, 'X-User-Id': adminId } });
      alert("User updated.");
      const updatedUsers = await api.get('/api/users/admin/all-users');
      setUsers(updatedUsers.data);
      setEditingUser(null);
    } catch (err) { alert("Update failed."); }
    finally { setIsUpdatingUser(false); }
  };

  const handleDeleteUser = async (userId) => {
    if (!window.confirm("Delete Identity?")) return;
    try {
      await api.delete(`/api/users/${userId}`, { headers: { 'X-User-Role': adminRole } });
      setUsers(users.filter(u => (u.userId || u.id) !== userId));
    } catch (err) { alert("Deletion failed."); }
  };

  // --- COURSE INFRASTRUCTURE LOGIC ---
  const handleEditCourseClick = (course) => {
    setEditingCourse(course);
    setCourseForm({ courseCode: course.courseCode || "", courseName: course.courseName || "", description: course.description || "", credits: course.credits || "", maxStudents: course.maxStudents || "", semester: course.semester || "", year: course.year || new Date().getFullYear(), teacherId: course.teacherId || "" });
  };

  const handleCourseSubmit = async (e) => {
    e.preventDefault();
    setCreatingCourse(true);
    try {
      const payload = { ...courseForm, credits: courseForm.credits || 3, maxStudents: courseForm.maxStudents || 60, semester: courseForm.semester || 1 };
      if (editingCourse) {
        await api.put(`/api/course/${editingCourse.courseId || editingCourse.id}`, payload, { headers: { 'X-User-Role': adminRole }});
        if (courseForm.teacherId) await api.put(`/api/course/${editingCourse.courseId || editingCourse.id}/assign/${courseForm.teacherId}`);
        alert("Course updated.");
        setEditingCourse(null);
      } else {
        const res = await api.post('/api/course/create', payload, { headers: { 'X-User-Role': adminRole, 'X-User-Id': adminId }});
        if (courseForm.teacherId) await api.put(`/api/course/${res.data.courseId || res.data.id}/assign/${courseForm.teacherId}`);
        alert(`Course deployed.`);
      }
      const updatedCourses = await api.get('/api/course/all');
      setCourses(updatedCourses.data);
      setCourseForm({ courseCode: "", courseName: "", description: "", credits: "", maxStudents: "", semester: "", year: new Date().getFullYear(), teacherId: "" });
    } catch (error) { alert("Action failed."); }
    finally { setCreatingCourse(false); }
  };

  const handleDropCourse = async (courseId) => {
    if (!window.confirm("Drop this course?")) return;
    try {
      await api.delete(`/api/course/${courseId}`, { headers: { 'X-User-Role': adminRole } });
      setCourses(courses.filter(c => (c.courseId || c.id) !== courseId));
    } catch (err) { alert("Ensure no students are enrolled."); }
  };

  const handleViewRoster = async (course) => {
    setViewingRoster(course);
    setRosterStudents([]); 
    setIsLoadingRoster(true);
    try {
      const res = await api.get(`/api/enrollments/course/${course.courseId || course.id}`, { headers: { 'X-User-Role': adminRole } });
      const enrolledStudentIds = res.data.map(e => e.studentId || e.userId);
      setRosterStudents(users.filter(u => enrolledStudentIds.includes(u.userId || u.id)));
    } catch (error) { console.error("Roster failed."); }
    finally { setIsLoadingRoster(false); }
  };

  // 🔥 FILTER LOGIC PROPERLY RESTORED 🔥
  const allTeachers = users.filter(u => String(u.role || u.roleId) === '2' || String(u.role).includes('TEACHER'));
  const studentCount = users.filter(u => String(u.role || u.roleId) === '3' || String(u.role).includes('STUDENT')).length;
  const filteredUsers = users.filter(u => {
    if (userFilter === 'ALL') return true;
    const r = String(u.role || u.roleId);
    if (userFilter === 'TEACHER') return r === '2' || String(u.role).includes('TEACHER');
    if (userFilter === 'STUDENT') return r === '3' || String(u.role).includes('STUDENT');
    if (userFilter === 'ADMIN') return r === '1' || String(u.role).includes('ADMIN');
    return true;
  });

  return (
    <div className="flex h-screen bg-[#F8FAFC] dark:bg-[#050a18] font-sans antialiased text-slate-900 dark:text-slate-100 transition-colors duration-300 overflow-hidden relative">
      
      {/* 📱 MOBILE OVERLAY */}
      <AnimatePresence>
        {isMobileMenuOpen && (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} onClick={() => setIsMobileMenuOpen(false)} className="fixed inset-0 bg-black/60 backdrop-blur-sm z-40 lg:hidden" />
        )}
      </AnimatePresence>

      {/* --- SIDEBAR --- */}
      <aside className={`fixed lg:static inset-y-0 left-0 w-72 bg-white dark:bg-slate-900 border-r border-slate-100 dark:border-slate-800 flex flex-col z-50 transition-transform duration-300 ease-in-out ${isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}`}>
        <div className="p-8 flex-1 overflow-y-auto custom-scrollbar">
          <div className="flex items-center justify-between mb-12">
            <div className="flex items-center gap-3">
              <ShieldAlert className="w-10 h-10 text-rose-600" />
              <div>
                <h2 className="text-xl font-black text-slate-900 dark:text-white uppercase italic leading-none tracking-tighter">Global</h2>
                <h2 className="text-xl font-black text-rose-600 uppercase italic leading-none tracking-tighter">Command</h2>
              </div>
            </div>
            <button onClick={() => setIsMobileMenuOpen(false)} className="lg:hidden text-slate-400 hover:text-rose-600"><X className="w-6 h-6" /></button>
          </div>
          <nav className="space-y-2">
            <button onClick={() => { setActiveTab('DASHBOARD'); setIsMobileMenuOpen(false); }} className={`w-full flex items-center gap-4 px-4 py-4 rounded-2xl font-black uppercase tracking-widest text-[10px] transition-all ${activeTab === 'DASHBOARD' ? 'bg-indigo-50 text-indigo-600 dark:bg-indigo-900/30' : 'text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800'}`}><LayoutDashboard className="w-5 h-5" /> Overview</button>
            <button onClick={() => { setActiveTab('USERS'); setIsMobileMenuOpen(false); }} className={`w-full flex items-center gap-4 px-4 py-4 rounded-2xl font-black uppercase tracking-widest text-[10px] transition-all ${activeTab === 'USERS' ? 'bg-indigo-50 text-indigo-600 dark:bg-indigo-900/30' : 'text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800'}`}><UserCheck className="w-5 h-5" /> Identity Access</button>
            <button onClick={() => { setActiveTab('COURSES'); setIsMobileMenuOpen(false); }} className={`w-full flex items-center gap-4 px-4 py-4 rounded-2xl font-black uppercase tracking-widest text-[10px] transition-all ${activeTab === 'COURSES' ? 'bg-indigo-50 text-indigo-600 dark:bg-indigo-900/30' : 'text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800'}`}><BookCopy className="w-5 h-5" /> Course Infra</button>
          </nav>
        </div>
        <div className="p-8 border-t border-slate-100 dark:border-slate-800 bg-slate-50/30 dark:bg-slate-900/50">
          <button onClick={handleLogout} className="w-full flex items-center gap-4 px-4 py-4 rounded-2xl font-black uppercase tracking-widest text-[10px] text-rose-400 hover:bg-rose-50 dark:hover:bg-rose-900/30 transition-all"><LogOut className="w-5 h-5" /> Terminate Session</button>
          <div className="mt-6 p-4 rounded-2xl bg-white dark:bg-slate-800 border border-slate-100 dark:border-slate-700 shadow-sm">
            <div className="flex items-center justify-between">
              <span className="text-xs font-bold text-slate-700 dark:text-slate-300 flex items-center gap-2"><Cpu className="w-4 h-4 text-indigo-500" /> AI CORE</span>
              <span className={`flex items-center gap-1.5 text-[9px] font-black tracking-tighter uppercase ${aiHealth?.status === 'UP' ? 'text-emerald-500' : 'text-rose-500'}`}>{aiHealth?.status || 'OFFLINE'}</span>
            </div>
          </div>
        </div>
      </aside>

      <div className="flex-1 flex flex-col min-w-0">
        <header className="h-24 bg-white dark:bg-slate-900 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between px-6 lg:px-12 shrink-0 transition-colors">
          <div className="flex items-center gap-4">
            <button onClick={() => setIsMobileMenuOpen(true)} className="lg:hidden p-2 bg-slate-100 dark:bg-slate-800 rounded-xl"><Menu className="w-6 h-6" /></button>
            <div className="hidden sm:flex items-center gap-4 text-slate-400 dark:text-slate-500 text-[11px] font-black uppercase tracking-[0.3em]"><span>Root Level 1</span> <ChevronRight className="w-3 h-3 text-rose-400" /> <span className="text-rose-600 bg-rose-50 dark:bg-rose-900/20 px-3 py-1 rounded-lg">{activeTab}</span></div>
          </div>
          <div className="flex items-center gap-4 lg:gap-8">
            <div className="flex items-center bg-slate-50 dark:bg-slate-800 p-1.5 rounded-[1.25rem] border border-slate-100 dark:border-slate-700 shadow-inner">
               <button onClick={() => setIsDark(false)} className={`p-2.5 rounded-xl transition-all ${!isDark ? 'bg-white text-rose-600 shadow-md' : 'text-slate-500'}`}><Sun className="w-4 h-4" /></button>
               <button onClick={() => setIsDark(true)} className={`p-2.5 rounded-xl transition-all ${isDark ? 'bg-slate-700 text-rose-400 shadow-md' : 'text-slate-500'}`}><Moon className="w-4 h-4" /></button>
            </div>
            <div className="flex items-center gap-3 lg:gap-5 border-l dark:border-slate-800 pl-4 lg:pl-8 border-slate-200">
              <div className="text-right hidden lg:block"><p className="text-sm font-black text-slate-900 dark:text-white uppercase leading-none">System Admin</p><p className="text-[10px] font-bold text-rose-500 mt-1 uppercase tracking-widest animate-pulse">Auth: Level-1</p></div>
              <div className="w-12 h-12 rounded-2xl bg-slate-900 flex items-center justify-center text-white font-black text-xs border-2 border-rose-500/20 shadow-lg">AD</div>
            </div>
          </div>
        </header>

        <main className="flex-1 overflow-y-auto p-6 lg:p-12 bg-[#F8FAFC] dark:bg-[#050a18] transition-colors duration-500 custom-scrollbar">
          <AnimatePresence mode="wait">
            <motion.div key={activeTab} initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0, y: -20 }} className="max-w-7xl mx-auto">
              
              {/* --- DASHBOARD TAB --- */}
              {activeTab === 'DASHBOARD' && (
                <div className="space-y-8">
                  <h1 className="text-4xl lg:text-5xl font-black text-slate-900 dark:text-white uppercase italic tracking-tighter">System <span className="text-rose-600">Metrics</span></h1>
                  <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                    {[
                      { label: 'Students', val: studentCount, icon: Users, bg: 'bg-indigo-50 dark:bg-indigo-900/30', text: 'text-indigo-600' },
                      { label: 'Faculty', val: allTeachers.length, icon: UserCheck, bg: 'bg-fuchsia-50 dark:bg-fuchsia-900/30', text: 'text-fuchsia-600' },
                      { label: 'Courses', val: courses.length, icon: BookOpen, bg: 'bg-emerald-50 dark:bg-emerald-900/30', text: 'text-emerald-600' },
                      { label: 'Engine', val: aiHealth?.status || 'N/A', icon: Activity, bg: 'bg-rose-50 dark:bg-rose-900/30', text: 'text-rose-600' }
                    ].map((card, idx) => (
                      <div key={idx} className="bg-white dark:bg-slate-900 p-8 rounded-[2.5rem] border border-slate-100 dark:border-slate-800 shadow-sm transition-all hover:border-rose-300">
                        <div className={`w-12 h-12 ${card.bg} ${card.text} rounded-2xl flex items-center justify-center mb-6`}><card.icon className="w-6 h-6" /></div>
                        <p className="text-4xl font-black text-slate-800 dark:text-white mb-1">{card.val}</p>
                        <p className="text-[10px] font-black uppercase tracking-widest text-slate-400">{card.label}</p>
                      </div>
                    ))}
                  </div>
                  <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    <div className="lg:col-span-2 bg-white dark:bg-slate-900 rounded-[3rem] border border-slate-100 dark:border-slate-800 p-6 lg:p-10 shadow-sm">
                      <div className="flex justify-between items-center mb-8 px-2"><h3 className="text-xl font-black uppercase tracking-widest text-slate-800 dark:text-white flex items-center gap-3"><Server className="w-6 h-6 text-rose-600" /> Recent Deployments</h3><button onClick={() => setActiveTab('COURSES')} className="text-[10px] font-black uppercase text-rose-600 hover:underline tracking-widest">Global Log &rarr;</button></div>
                      <div className="space-y-4">
                        {courses.slice().reverse().slice(0, 4).map(c => (
                          <div key={c.courseId || c.id} className="flex flex-col sm:flex-row items-start sm:items-center justify-between p-6 rounded-[2rem] bg-slate-50 dark:bg-[#0b1224] border border-slate-100 dark:border-slate-800 transition-all hover:border-rose-200 gap-4">
                             <div className="flex items-center gap-5"><div className="w-14 h-14 rounded-2xl bg-slate-900 dark:bg-slate-800 text-white flex items-center justify-center font-black text-sm italic shadow-xl">IT</div><div><p className="font-black text-slate-800 dark:text-white text-base uppercase tracking-tight">{c.courseName || c.name}</p><p className="text-[9px] font-black uppercase tracking-widest text-slate-400 mt-1">{c.courseCode} • {c.credits || 3} Credits</p></div></div>
                             <span className="px-4 py-2 bg-emerald-50 dark:bg-emerald-900/20 text-emerald-600 rounded-xl text-[9px] font-black tracking-[0.2em] flex items-center gap-2 uppercase"><span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span> Node Active</span>
                          </div>
                        ))}
                      </div>
                    </div>
                    <div className="space-y-8">
                       <div className="bg-white dark:bg-slate-900 rounded-[3rem] border border-slate-100 dark:border-slate-800 p-8 shadow-sm">
                          <h3 className="text-xs font-black uppercase tracking-widest text-slate-800 dark:text-white mb-8 flex items-center gap-2 italic"><Users className="w-4 h-4 text-rose-500" /> Distribution Log</h3>
                          <div className="space-y-8">
                            <div><div className="flex justify-between text-[10px] font-black uppercase mb-3"><span className="text-slate-400">Students</span><span className="text-indigo-500">{studentCount}</span></div><div className="w-full bg-slate-100 dark:bg-slate-800 rounded-full h-3 overflow-hidden"><motion.div initial={{width:0}} animate={{width: `${users.length ? (studentCount/users.length)*100 : 0}%`}} transition={{duration: 1.5}} className="bg-indigo-500 h-full"></motion.div></div></div>
                            <div><div className="flex justify-between text-[10px] font-black uppercase mb-3"><span className="text-slate-400">Faculty</span><span className="text-rose-500">{allTeachers.length}</span></div><div className="w-full bg-slate-100 dark:bg-slate-800 rounded-full h-3 overflow-hidden"><motion.div initial={{width:0}} animate={{width: `${users.length ? (allTeachers.length/users.length)*100 : 0}%`}} transition={{duration: 1.5, delay: 0.2}} className="bg-rose-500 h-full"></motion.div></div></div>
                          </div>
                       </div>
                       <div className="bg-gradient-to-br from-rose-600 to-rose-900 rounded-[3rem] p-10 text-white shadow-2xl relative overflow-hidden group"><div className="absolute -right-10 -bottom-10 opacity-10 group-hover:rotate-12 transition-transform duration-700"><Server className="w-60 h-60" /></div><h3 className="text-xs font-black uppercase tracking-[0.3em] text-rose-200 mb-6 flex items-center gap-2"><Cpu className="w-4 h-4" /> Node Mapping</h3><div className="space-y-6 relative z-10"><div className="flex justify-between items-center border-b border-rose-500/50 pb-4"><span className="text-[10px] font-black uppercase tracking-widest">GATEWAY</span><span className="text-[10px] font-black bg-white/20 px-3 py-1.5 rounded-lg border border-white/10">8091</span></div><div className="flex justify-between items-center border-b border-rose-500/50 pb-4"><span className="text-[10px] font-black uppercase tracking-widest">IDENTITY</span><span className="text-[10px] font-black bg-white/20 px-3 py-1.5 rounded-lg border border-white/10">8080</span></div><div className="flex justify-between items-center"><span className="text-[10px] font-black uppercase tracking-widest">COURSE</span><span className="text-[10px] font-black bg-white/20 px-3 py-1.5 rounded-lg border border-white/10">8082</span></div></div></div>
                    </div>
                  </div>
                </div>
              )}

              {/* --- IDENTITY ACCESS TAB --- */}
              {activeTab === 'USERS' && (
                <div className="space-y-8">
                  <h1 className="text-4xl lg:text-5xl font-black text-slate-900 dark:text-white uppercase italic tracking-tighter">Identity <span className="text-rose-600">Access</span></h1>
                  <div className="grid grid-cols-1 xl:grid-cols-3 gap-8">
                    <div className="xl:col-span-2 space-y-6">
                      <div className="flex gap-2 overflow-x-auto pb-2 custom-scrollbar">
                        {['ALL', 'STUDENT', 'TEACHER', 'ADMIN'].map(f => (
                          <button key={f} onClick={() => setUserFilter(f)} className={`px-5 py-3 rounded-2xl text-[10px] font-black uppercase tracking-widest transition-all whitespace-nowrap ${userFilter === f ? 'bg-rose-50 text-rose-600 dark:bg-rose-900/30' : 'text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800'}`}>{f}</button>
                        ))}
                      </div>
                      <div className="bg-white dark:bg-slate-900 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm overflow-hidden">
                        <div className="overflow-x-auto max-h-[700px] custom-scrollbar">
                          <table className="w-full text-sm text-left">
                            <thead className="bg-slate-50 dark:bg-slate-800/80 text-[10px] uppercase font-black tracking-widest text-slate-500 sticky top-0 z-10">
                              <tr><th className="hidden sm:table-cell px-8 py-6">ID</th><th className="px-8 py-6">Entity Signature</th><th className="px-8 py-6">Tier</th><th className="px-8 py-6 text-right">Overrides</th></tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100 dark:divide-slate-800/50">
                              {filteredUsers.map(u => (
                                <tr key={u.userId || u.id} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/20 group transition-colors">
                                  <td className="hidden sm:table-cell px-8 py-6 font-black text-slate-400 italic text-xs">#INST-0{u.userId || u.id}</td>
                                  <td className="px-8 py-6"><p className="font-black text-slate-800 dark:text-white uppercase text-sm">{u.fullName || u.username}</p><p className="text-[10px] text-slate-500 font-bold">{u.email}</p></td>
                                  <td className="px-8 py-6"><span className={`px-3 py-1.5 rounded-lg text-[9px] font-black tracking-widest border ${String(u.role).includes('TEACHER') || u.roleId === 2 ? 'bg-fuchsia-50 text-fuchsia-600 border-fuchsia-100 dark:bg-fuchsia-900/20' : String(u.role).includes('ADMIN') || u.roleId === 1 ? 'bg-rose-50 text-rose-600 border-rose-100 dark:bg-rose-900/20' : 'bg-slate-50 text-slate-500 dark:bg-slate-800 dark:border-slate-700'}`}>{u.role || 'STUDENT'}</span></td>
                                  <td className="px-8 py-6 text-right"><div className="flex justify-end gap-2 opacity-100 lg:opacity-0 lg:group-hover:opacity-100 transition-opacity"><button onClick={() => handleEditClick(u)} className="p-2.5 text-indigo-600 bg-indigo-50 dark:bg-indigo-900/40 rounded-xl"><Edit3 className="w-4 h-4" /></button><button onClick={() => handleDeleteUser(u.userId || u.id)} className="p-2.5 text-rose-600 bg-rose-50 dark:bg-rose-900/40 rounded-xl"><Trash2 className="w-4 h-4" /></button></div></td>
                                </tr>
                              ))}
                            </tbody>
                          </table>
                        </div>
                      </div>
                    </div>

                    {/* 🔥 FORM: ALL ORIGINAL FIELDS RESTORED 🔥 */}
                    <div className="bg-slate-900 rounded-[3.5rem] p-8 lg:p-12 text-white shadow-2xl h-fit border border-white/5 sticky top-12 relative overflow-hidden">
                      <form onSubmit={editingUser ? handleUpdateUser : handleCreateUser} className="space-y-5 relative z-10">
                        <div className="flex justify-between items-center mb-8"><h3 className="text-2xl font-black uppercase italic tracking-tighter text-rose-500">{editingUser ? "Sync Identity" : "New Provision"}</h3>{editingUser && <button type="button" onClick={() => setEditingUser(null)}><X className="w-6 h-6 text-slate-400 hover:text-white" /></button>}</div>
                        <div className="grid grid-cols-2 gap-4">
                          <input type="text" placeholder="Username" value={editingUser ? editingUser.username : newUserForm.username} onChange={e => !editingUser && setNewUserForm({...newUserForm, username: e.target.value})} className="w-full p-4 bg-white/5 rounded-2xl text-sm font-bold border border-white/10 outline-none" required={!editingUser} disabled={editingUser} />
                          <input type="password" placeholder="Pass-Key" value={editingUser ? "" : newUserForm.password} onChange={e => !editingUser && setNewUserForm({...newUserForm, password: e.target.value})} className="w-full p-4 bg-white/5 rounded-2xl text-sm font-bold border border-white/10 outline-none" required={!editingUser} />
                        </div>
                        <input type="text" placeholder="Full Identity Name" value={editingUser ? editForm.fullName : newUserForm.fullName} onChange={e => editingUser ? setEditForm({...editForm, fullName: e.target.value}) : setNewUserForm({...newUserForm, fullName: e.target.value})} className="w-full p-4 bg-white/5 rounded-2xl text-sm font-bold border border-white/10 outline-none" required />
                        <input type="email" placeholder="Email Address" value={editingUser ? editForm.email : newUserForm.email} onChange={e => editingUser ? setEditForm({...editForm, email: e.target.value}) : setNewUserForm({...newUserForm, email: e.target.value})} className="w-full p-4 bg-white/5 rounded-2xl text-sm font-bold border border-white/10 outline-none" required />
                        
                        <div className="p-4 bg-white/5 rounded-2xl border border-white/10"><label className="text-[9px] font-black text-slate-500 uppercase block mb-2 tracking-widest">Access Tier</label>
                          <select value={editingUser ? editForm.role : newUserForm.role} onChange={e => { const val = parseInt(e.target.value); editingUser ? setEditForm({...editForm, role: val}) : setNewUserForm({...newUserForm, role: val})}} className="w-full bg-transparent text-sm font-bold outline-none cursor-pointer"><option value={3} className="bg-slate-900 text-white font-bold">STUDENT TIER</option><option value={2} className="bg-slate-900 text-white font-bold">TEACHER TIER</option><option value={1} className="bg-slate-900 text-white font-bold">ADMIN TIER</option></select>
                        </div>

                        {/* TEACHER EXTRA FIELDS */}
                        {((editingUser ? editForm.role : newUserForm.role) === 2) && (
                          <div className="space-y-4">
                            <div className="grid grid-cols-2 gap-4">
                               <input type="text" placeholder="Emp ID" value={editingUser ? editForm.employeeId : newUserForm.employeeId} onChange={e => editingUser ? setEditForm({...editForm, employeeId: e.target.value}) : setNewUserForm({...newUserForm, employeeId: e.target.value})} className="p-4 bg-white/5 rounded-2xl text-sm font-bold border border-white/10 outline-none" required />
                               <input type="text" placeholder="Dept" value={editingUser ? editForm.department : newUserForm.department} onChange={e => editingUser ? setEditForm({...editForm, department: e.target.value}) : setNewUserForm({...newUserForm, department: e.target.value})} className="p-4 bg-white/5 rounded-2xl text-sm font-bold border border-white/10 outline-none" />
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                               <input type="text" placeholder="Office Location" value={editingUser ? editForm.officeLocation : newUserForm.officeLocation} onChange={e => editingUser ? setEditForm({...editForm, officeLocation: e.target.value}) : setNewUserForm({...newUserForm, officeLocation: e.target.value})} className="p-4 bg-white/5 rounded-2xl text-sm font-bold border border-white/10 outline-none" />
                               <input type="text" placeholder="Phone" value={editingUser ? editForm.phone : newUserForm.phone} onChange={e => editingUser ? setEditForm({...editForm, phone: e.target.value}) : setNewUserForm({...newUserForm, phone: e.target.value})} className="p-4 bg-white/5 rounded-2xl text-sm font-bold border border-white/10 outline-none" />
                            </div>
                          </div>
                        )}

                        {/* STUDENT EXTRA FIELDS */}
                        {((editingUser ? editForm.role : newUserForm.role) === 3) && (
                          <div className="space-y-4">
                            <div className="grid grid-cols-2 gap-4">
                               <input type="text" placeholder="Student Number" value={editingUser ? "" : newUserForm.studentNumber} onChange={e => !editingUser && setNewUserForm({...newUserForm, studentNumber: e.target.value})} className="p-4 bg-white/5 rounded-2xl text-sm font-bold border border-white/10 outline-none" required={!editingUser} disabled={editingUser} />
                               <input type="text" placeholder="Major" value={editingUser ? editForm.major : newUserForm.major} onChange={e => editingUser ? setEditForm({...editForm, major: e.target.value}) : setNewUserForm({...newUserForm, major: e.target.value})} className="p-4 bg-white/5 rounded-2xl text-sm font-bold border border-white/10 outline-none" />
                            </div>
                            <div className="grid grid-cols-3 gap-2">
                               <input type="number" placeholder="Year" value={editingUser ? editForm.year : newUserForm.year} onChange={e => { const val = parseInt(e.target.value); editingUser ? setEditForm({...editForm, year: val}) : setNewUserForm({...newUserForm, year: val})}} className="p-4 bg-white/5 rounded-2xl text-[10px] font-bold border border-white/10 outline-none" />
                               <input type="number" placeholder="Sem" value={editingUser ? editForm.semester : newUserForm.semester} onChange={e => { const val = parseInt(e.target.value); editingUser ? setEditForm({...editForm, semester: val}) : setNewUserForm({...newUserForm, semester: val})}} className="p-4 bg-white/5 rounded-2xl text-[10px] font-bold border border-white/10 outline-none" />
                               <input type="number" placeholder="Max" value={editingUser ? editForm.maxCoursesPerSemester : newUserForm.maxCoursesPerSemester} onChange={e => { const val = parseInt(e.target.value); editingUser ? setEditForm({...editForm, maxCoursesPerSemester: val}) : setNewUserForm({...newUserForm, maxCoursesPerSemester: val})}} className="p-4 bg-white/5 rounded-2xl text-[10px] font-bold border border-white/10 outline-none" />
                            </div>
                          </div>
                        )}
                        <button type="submit" disabled={isUpdatingUser || isCreatingUser} className={`w-full py-5 mt-6 rounded-2xl font-black uppercase tracking-widest text-xs shadow-xl transition-all active:scale-95 ${editingUser ? 'bg-rose-600 hover:bg-rose-500 shadow-rose-900/40' : 'bg-indigo-600 hover:bg-indigo-500 shadow-indigo-900/40'}`}>
                          {editingUser ? 'Sync Identity' : 'Provision Entity'}
                        </button>
                        {editingUser && <button type="button" onClick={() => setEditingUser(null)} className="w-full text-slate-500 hover:text-white transition-colors font-bold uppercase text-[9px] mt-2">Abort Sync</button>}
                      </form>
                    </div>
                  </div>
                </div>
              )}

              {/* --- COURSE INFRASTRUCTURE TAB --- */}
              {activeTab === 'COURSES' && (
                <div className="space-y-8">
                  <h1 className="text-4xl lg:text-5xl font-black text-slate-900 dark:text-white uppercase italic tracking-tighter">Node <span className="text-rose-600">Infrastructure</span></h1>
                  <div className="grid grid-cols-1 xl:grid-cols-3 gap-8">
                    <div className="xl:col-span-2 bg-white dark:bg-slate-900/50 rounded-[3rem] border border-slate-200 dark:border-slate-800 shadow-sm p-6 lg:p-10 backdrop-blur-xl">
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        {courses.map(c => (
                          <div key={c.courseId || c.id} className="p-8 rounded-[2.5rem] border border-slate-100 dark:border-slate-800 bg-white dark:bg-[#0b1224] transition-all relative overflow-hidden group">
                            <div className="flex justify-between items-start mb-6"><span className="px-3 py-1.5 bg-slate-900 dark:bg-rose-600 text-white rounded-xl text-[9px] font-black uppercase tracking-widest shadow-lg">{c.courseCode || c.code}</span><div className="flex gap-2"><button onClick={() => handleViewRoster(c)} className="p-2.5 text-slate-400 hover:text-indigo-500 transition-all"><UsersRound className="w-5 h-5" /></button><button onClick={() => handleEditCourseClick(c)} className="p-2.5 text-slate-400 hover:text-emerald-500 transition-all"><Edit3 className="w-5 h-5" /></button><button onClick={() => handleDropCourse(c.courseId || c.id)} className="p-2.5 text-slate-400 hover:text-rose-600 transition-all"><Trash2 className="w-5 h-5" /></button></div></div>
                            <h4 className="font-black text-slate-800 dark:text-white text-xl uppercase italic tracking-tighter mb-6">{c.courseName || c.name}</h4>
                            <div className="flex items-center justify-between mt-4 pt-6 border-t dark:border-slate-800"><div className="flex items-center gap-4 text-[9px] font-black uppercase text-slate-400 tracking-widest"><span>{c.maxStudents || 60} Nodes</span><span className="w-1 h-1 bg-slate-300 dark:bg-slate-700 rounded-full"></span><span>Tier {c.semester || 1}</span></div><div className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse shadow-[0_0_10px_rgba(16,185,129,0.5)]"></div></div>
                          </div>
                        ))}
                      </div>
                    </div>
                    <div className="bg-slate-900 rounded-[3.5rem] p-8 lg:p-10 text-white shadow-2xl h-fit border border-white/5 overflow-hidden sticky top-12">
                      <form onSubmit={handleCourseSubmit} className="space-y-5 relative z-10">
                        <h3 className="text-2xl font-black uppercase italic tracking-tighter text-rose-500 mb-8">{editingCourse ? "Re-Config" : "Deployment"}</h3>
                        <div className="grid grid-cols-2 gap-4"><input type="text" placeholder="Protocol" value={courseForm.courseCode} onChange={e => setCourseForm({...courseForm, courseCode: e.target.value})} className="w-full p-4 bg-white/5 rounded-2xl text-sm font-bold border border-white/10 outline-none" required /><input type="number" placeholder="Weight" value={courseForm.credits} onChange={e => setCourseForm({...courseForm, credits: parseInt(e.target.value)})} className="w-full p-4 bg-white/5 rounded-2xl text-sm font-bold border border-white/10 outline-none" required /></div>
                        <input type="text" placeholder="Unit Name" value={courseForm.courseName} onChange={e => setCourseForm({...courseForm, courseName: e.target.value})} className="w-full p-4 bg-white/5 rounded-2xl text-sm font-bold border border-white/10 outline-none" required />
                        <textarea placeholder="Metadata..." value={courseForm.description} onChange={e => setCourseForm({...courseForm, description: e.target.value})} rows="3" className="w-full p-4 bg-white/5 rounded-2xl text-sm font-bold border border-white/10" />
                        <select value={courseForm.teacherId} onChange={e => setCourseForm({...courseForm, teacherId: e.target.value})} className="w-full p-4 bg-white/5 rounded-2xl text-sm font-bold border border-white/10 outline-none cursor-pointer"><option value="" className="bg-slate-900 text-slate-500">Faculty Lead</option>{allTeachers.map(t => (<option key={t.userId || t.id} value={t.userId || t.id} className="bg-slate-900">{t.fullName || t.username}</option>))}</select>
                        <button type="submit" disabled={creatingCourse} className={`w-full py-5 mt-6 rounded-2xl font-black uppercase tracking-widest text-xs shadow-xl transition-all active:scale-95 ${editingCourse ? 'bg-emerald-600 hover:bg-emerald-500 shadow-emerald-900/40' : 'bg-rose-600 hover:bg-rose-500 shadow-rose-900/40'}`}>{creatingCourse ? 'Processing...' : editingCourse ? 'Update Module' : 'Initialize Deploy'}</button>
                        {editingCourse && <button type="button" onClick={() => { setEditingCourse(null); setCourseForm({ courseCode: "", courseName: "", description: "", credits: "", maxStudents: "", semester: "", year: new Date().getFullYear(), teacherId: "" }); }} className="w-full text-slate-500 hover:text-white transition-colors font-bold uppercase text-[9px] mt-2">Cancel Edit</button>}
                      </form>
                    </div>
                  </div>
                </div>
              )}
            </motion.div>
          </AnimatePresence>
        </main>
      </div>

      {/* --- ROSTER MODAL --- */}
      <AnimatePresence>
        {viewingRoster && (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }} className="fixed inset-0 z-[60] flex items-center justify-center p-4 bg-slate-900/90 backdrop-blur-md">
            <motion.div initial={{ scale: 0.9, y: 20 }} animate={{ scale: 1, y: 0 }} className="bg-white dark:bg-slate-900 rounded-[3rem] w-full max-w-2xl overflow-hidden shadow-2xl border border-slate-100 dark:border-slate-800">
              <div className="p-8 border-b border-slate-100 dark:border-slate-800 flex justify-between items-center bg-slate-50 dark:bg-[#0b1224]">
                <div><h2 className="text-2xl font-black text-slate-800 dark:text-white uppercase italic tracking-tighter leading-none">{viewingRoster.courseCode} Node Map</h2></div>
                <button onClick={() => setViewingRoster(null)} className="p-3 bg-white dark:bg-slate-800 rounded-full shadow-sm hover:rotate-90 transition-transform"><X className="w-5 h-5" /></button>
              </div>
              <div className="p-8 max-h-[500px] overflow-y-auto custom-scrollbar">
                {isLoadingRoster ? ( <div className="text-center py-12"><Loader2 className="w-12 h-12 animate-spin text-rose-500 mx-auto mb-4" /></div> ) : rosterStudents.length > 0 ? (
                  <div className="space-y-3">
                    {rosterStudents.map(student => (
                      <div key={student.userId || student.id} className="flex items-center justify-between p-5 bg-slate-50 dark:bg-slate-800/40 rounded-2xl border border-slate-100 dark:border-slate-800 group transition-colors">
                        <div className="flex items-center gap-5">
                          <div className="w-12 h-12 bg-white dark:bg-slate-900 rounded-xl flex items-center justify-center font-black text-rose-600 shadow-sm border border-slate-100 dark:border-slate-800 italic">{(student.fullName || "U").charAt(0)}</div>
                          <div><p className="text-sm font-black text-slate-800 dark:text-white uppercase tracking-tight">{student.fullName || student.username}</p><p className="text-[10px] text-slate-400 font-bold uppercase">{student.email}</p></div>
                        </div>
                        <span className="px-3 py-1.5 bg-emerald-50 text-emerald-600 dark:bg-emerald-900/20 rounded-lg text-[9px] font-black uppercase">Verified</span>
                      </div>
                    ))}
                  </div>
                ) : ( <div className="text-center py-12"><UsersRound className="w-16 h-16 text-slate-200 dark:text-slate-800 mx-auto mb-4" /><p className="text-sm font-black text-slate-400 uppercase tracking-widest">Empty Roster</p></div> )}
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default AdminPortal;