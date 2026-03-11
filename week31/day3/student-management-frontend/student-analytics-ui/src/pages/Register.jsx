import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { UserCircle, GraduationCap, ShieldCheck, UserPlus, ArrowRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom'; // 🔥 Added for navigation
import api from '../api/axios';

const Register = () => {
  const navigate = useNavigate(); // Hook initialization
  const [role, setRole] = useState(3); 
  const [passwordStrength, setPasswordStrength] = useState({ score: 0, label: 'Empty' });
  const [formData, setFormData] = useState({
    username: '', password: '', email: '', fullName: '',
    studentNumber: '', major: '', year: '', semester: '', maxCoursesPerSemester: 3,
    employeeId: '', department: '', officeLocation: '', phone: ''
  });


  const checkStrength = (pass) => {
    let score = 0;
    if (pass.length > 6) score++;
    if (/[A-Z]/.test(pass)) score++;
    if (/[0-9]/.test(pass)) score++;
    if (/[^A-Za-z0-9]/.test(pass)) score++;

    const labels = ['Weak', 'Weak', 'Fair', 'Good', 'Strong'];
    setPasswordStrength({ score, label: labels[score] });
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    if (e.target.name === 'password') checkStrength(e.target.value);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (passwordStrength.score < 2) {
      alert("Please choose a stronger password.");
      return;
    }
    
    // 🔥 Keeping your current Payload Sanitation logic exactly as provided
    let payload = {
      username: formData.username,
      password: formData.password,
      email: formData.email,
      fullName: formData.fullName,
      role: role
    };

    if (role === 3) {
      Object.assign(payload, {
        studentNumber: formData.studentNumber,
        major: formData.major,
        year: parseInt(formData.year),
        semester: parseInt(formData.semester),
        maxCoursesPerSemester: parseInt(formData.maxCoursesPerSemester)
      });
    } else if (role === 2) {
      Object.assign(payload, {
        employeeId: formData.employeeId,
        department: formData.department,
        officeLocation: formData.officeLocation,
        phone: formData.phone
      });
    }

    try {
      // Adjusted endpoint to match your recent API Gateway structure
      await api.post('/api/auth/register', payload);
      
      // SUCCESS LOGIC: Refresh data and navigate
      alert('Registration Successful!');
      
      // Reset form fields
      setFormData({
        username: '', password: '', email: '', fullName: '',
        studentNumber: '', major: '', year: '', semester: '', maxCoursesPerSemester: 3,
        employeeId: '', department: '', officeLocation: '', phone: ''
      });

      // Give user a moment to see the alert before moving to login
      setTimeout(() => {
        navigate('/login');
      }, 1000);

    } catch (err) {
      alert('Error: ' + (err.response?.data?.message || 'Registration failed'));
    }
  };

  return (
    <div className="min-h-screen bg-[#F0F2F5] flex items-center justify-center p-6 font-sans">
      <motion.div 
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        className="max-w-5xl w-full bg-white rounded-[2.5rem] shadow-[0_20px_60px_rgba(0,0,0,0.08)] flex overflow-hidden border border-white"
      >
        {/* Side Branding */}
        <div className="hidden lg:flex w-1/3 bg-[#0F172A] p-12 flex-col justify-between text-white relative">
          <div className="absolute top-0 left-0 w-full h-full opacity-10 pointer-events-none">
            <div className="absolute top-10 left-10 w-40 h-40 bg-blue-500 rounded-full blur-[80px]"></div>
          </div>
          
          <div className="relative z-10">
            <div className="w-12 h-12 bg-indigo-600 rounded-2xl flex items-center justify-center mb-8 shadow-xl shadow-indigo-500/20">
              <UserPlus className="w-6 h-6 text-white" />
            </div>
            <h1 className="text-3xl font-black leading-tight">Join the <br/>Intelligence.</h1>
            <p className="text-slate-400 mt-4 text-sm font-medium">Initialize your profile to access AI-driven academic insights and performance tracking.</p>
          </div>

          <div className="relative z-10">
            <div className="flex items-center space-x-2 text-[10px] font-bold uppercase tracking-[0.2em] text-slate-500">
              <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
              <span>Protocol v4.0 Active</span>
            </div>
          </div>
        </div>

        {/* Registration Form */}
        <div className="flex-1 p-10 lg:p-14 overflow-y-auto max-h-[90vh] bg-[#FFFFFF]">
          <header className="mb-10">
            <h2 className="text-3xl font-black text-slate-900 tracking-tight">Create Account</h2>
            <p className="text-slate-400 text-sm mt-2 font-semibold">Select your role and provide your professional details.</p>
          </header>

          <form onSubmit={handleSubmit} className="space-y-8">
            {/* Role Toggle */}
            <div className="bg-slate-100 p-1.5 rounded-2xl flex relative">
              {[
                { label: 'Student', val: 3, icon: GraduationCap },
                { label: 'Teacher', val: 2, icon: UserCircle },
                { label: 'Admin', val: 1, icon: ShieldCheck }
              ].map((r) => (
                <button
                  key={r.val}
                  type="button"
                  onClick={() => setRole(r.val)}
                  className={`flex-1 flex items-center justify-center gap-2 py-3 rounded-xl text-xs font-bold transition-all relative z-10 ${
                    role === r.val ? 'text-indigo-600' : 'text-slate-400 hover:text-slate-600'
                  }`}
                >
                  <r.icon className="w-4 h-4" />
                  {r.label}
                </button>
              ))}
              <motion.div 
                layoutId="active-pill"
                animate={{ x: (role === 3 ? 0 : role === 2 ? '100%' : '200%') }}
                className="absolute top-1.5 left-1.5 bottom-1.5 w-[calc(33.33%-4px)] bg-white rounded-xl shadow-md -z-0"
              />
            </div>

            {/* Core Fields */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-2">
                <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Username</label>
                <input name="username" value={formData.username} required onChange={handleChange} className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:ring-4 focus:ring-indigo-500/10 focus:border-indigo-500 outline-none transition-all font-medium text-slate-700" placeholder="e.g. sujal_01" />
              </div>
              <div className="space-y-2">
                <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Full Name</label>
                <input name="fullName" value={formData.fullName} required onChange={handleChange} className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:ring-4 focus:ring-indigo-500/10 focus:border-indigo-500 outline-none transition-all font-medium text-slate-700" placeholder="Sujal Morwani" />
              </div>
              <div className="space-y-2">
                <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Email</label>
                <input name="email" value={formData.email} type="email" required onChange={handleChange} className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:ring-4 focus:ring-indigo-500/10 focus:border-indigo-500 outline-none transition-all font-medium text-slate-700" placeholder="sujal@gmail.com" />
              </div>
              <div className="space-y-2">
                <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Password</label>
                <input 
                name="password" 
                type="password" 
                required 
                onChange={handleChange} 
                className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:border-indigo-500 outline-none transition-all font-medium" 
                placeholder="••••••••" 
    />
          <div className="flex items-center gap-1 mt-2 px-1">
            {[1, 2, 3, 4].map((step) => (
              <div 
                key={step}
                className={`h-1.5 flex-1 rounded-full transition-colors duration-500 ${
                  passwordStrength.score >= step 
                    ? (passwordStrength.score <= 2 ? 'bg-red-400' : passwordStrength.score === 3 ? 'bg-yellow-400' : 'bg-green-500')
                    : 'bg-slate-200'
                }`}
              />
            ))}
            <span className="text-[10px] font-bold text-slate-400 uppercase ml-2">{passwordStrength.label}</span>
            </div>
              </div>
            </div>

            {/* Dynamic Sections */}
            <AnimatePresence mode="wait">
              {role === 3 && (
                <motion.div 
                  key="student-fields"
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: -10 }}
                  className="grid grid-cols-1 md:grid-cols-2 gap-6 pt-4 border-t border-slate-100"
                >
                  <div className="space-y-2">
                    <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Student ID</label>
                    <input name="studentNumber" value={formData.studentNumber} required onChange={handleChange} className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:border-indigo-500 outline-none transition-all font-medium" placeholder="STU2026001" />
                  </div>
                  <div className="space-y-2">
                    <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Major</label>
                    <input name="major" value={formData.major} required onChange={handleChange} className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white focus:border-indigo-500 outline-none transition-all font-medium" placeholder="CSE" />
                  </div>
                  <div className="grid grid-cols-3 gap-4 col-span-2">
                    <div className="space-y-2">
                      <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Year</label>
                      <input name="year" value={formData.year} type="number" required onChange={handleChange} className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white outline-none" placeholder="2" />
                    </div>
                    <div className="space-y-2">
                      <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Semester</label>
                      <input name="semester" value={formData.semester} type="number" required onChange={handleChange} className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white outline-none" placeholder="4" />
                    </div>
                    <div className="space-y-2">
                      <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Max Courses</label>
                      <input name="maxCoursesPerSemester" value={formData.maxCoursesPerSemester} type="number" required onChange={handleChange} className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white outline-none" placeholder="3" />
                    </div>
                  </div>
                </motion.div>
              )}

              {role === 2 && (
                <motion.div 
                  key="teacher-fields"
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: -10 }}
                  className="grid grid-cols-1 md:grid-cols-2 gap-6 pt-4 border-t border-slate-100"
                >
                  <div className="space-y-2">
                    <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Employee ID</label>
                    <input name="employeeId" value={formData.employeeId} required onChange={handleChange} className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white outline-none transition-all" placeholder="EMP2026001" />
                  </div>
                  <div className="space-y-2">
                    <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Department</label>
                    <input name="department" value={formData.department} required onChange={handleChange} className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white outline-none" placeholder="Computer Science" />
                  </div>
                  <div className="space-y-2">
                    <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Office Location</label>
                    <input name="officeLocation" value={formData.officeLocation} required onChange={handleChange} className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white outline-none" placeholder="Block A - 204" />
                  </div>
                  <div className="space-y-2">
                    <label className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Phone</label>
                    <input name="phone" value={formData.phone} required onChange={handleChange} className="w-full px-5 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:bg-white outline-none" placeholder="9876543210" />
                  </div>
                </motion.div>
              )}
            </AnimatePresence>

            <motion.button 
              whileHover={{ scale: 1.01 }}
              whileTap={{ scale: 0.99 }}
              type="submit" 
              className="w-full bg-[#0F172A] text-white font-bold py-4 rounded-2xl shadow-xl hover:bg-indigo-600 transition-all flex items-center justify-center gap-2 group"
            >
              Initialize Profile
              <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
            </motion.button>

            <p className="text-center text-sm text-slate-500 font-medium">
                Already have an account? {' '}
                <button 
                  type="button" 
                  onClick={() => navigate('/login')}
                  className="text-indigo-600 font-bold hover:underline"
                >
                  Sign in here
                </button>
              </p>
          </form>
        </div>
      </motion.div>
    </div>
  );
};

export default Register;