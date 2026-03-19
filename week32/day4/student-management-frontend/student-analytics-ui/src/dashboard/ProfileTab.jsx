import React, { useState } from 'react';
import api from '../api/axios'
import { Save, User, Mail, GraduationCap } from 'lucide-react';

const ProfileTab = ({ studentInfo }) => {
  const [formData, setFormData] = useState({ fullName: studentInfo?.fullName });

  const handleUpdate = async () => {
    // Hits User-Service @PutMapping("/{id}")
    await api.put(`/api/users/${studentInfo.userId}`, formData);
    alert("Profile commit success!");
  };

  return (
    <div className="bg-white dark:bg-slate-900 p-12 rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm text-left max-w-4xl">
      <h3 className="text-2xl font-black text-slate-900 dark:text-white mb-10 italic uppercase">Student Profile Settings</h3>
      <div className="grid grid-cols-1 gap-8">
        <div className="space-y-3">
          <label className="text-[10px] font-black text-indigo-600 uppercase tracking-widest ml-1">Academic Name</label>
          <input 
             value={formData.fullName} 
             onChange={(e) => setFormData({fullName: e.target.value})}
             className="w-full p-5 bg-slate-50 dark:bg-slate-800 border-none rounded-2xl text-slate-700 dark:text-slate-200 font-bold outline-none focus:ring-2 focus:ring-indigo-500/20"
          />
        </div>
        <button onClick={handleUpdate} className="w-full py-5 bg-slate-900 dark:bg-indigo-600 text-white rounded-2xl font-black uppercase tracking-widest hover:opacity-90 transition-all shadow-xl">
           Update Record
        </button>
      </div>
    </div>
  );
};

export default ProfileTab;