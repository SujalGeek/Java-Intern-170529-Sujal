import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../services/api';

const SignUp = () => {
  const navigate = useNavigate();
  
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    role: 'STUDENT',
    department: '',
    currentSemester: ''
  });
  
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  //  Password Strength State
  const [passwordStrength, setPasswordStrength] = useState(0); 
  // 0 = Empty, 1 = Weak, 2 = Medium, 3 = Strong, 4 = Very Strong

  // Helper: Calculate Strength
  const checkStrength = (pass) => {
    let score = 0;
    if (!pass) return 0;

    if (pass.length > 5) score += 1; // Length check
    if (pass.length > 10) score += 1; // Bonus length
    if (/[0-9]/.test(pass)) score += 1; // Has Number
    if (/[^A-Za-z0-9]/.test(pass)) score += 1; // Has Special Char (@, #, $)

    return score;
  };

  // Helper: Get Color based on score
  const getStrengthColor = () => {
    if (passwordStrength === 0) return 'bg-gray-200';
    if (passwordStrength <= 1) return 'bg-red-500';    // Weak
    if (passwordStrength === 2) return 'bg-yellow-500'; // Medium
    if (passwordStrength >= 3) return 'bg-green-500';   // Strong
    return 'bg-gray-200';
  };

  const getStrengthLabel = () => {
    if (passwordStrength === 0) return '';
    if (passwordStrength <= 1) return 'Weak';
    if (passwordStrength === 2) return 'Medium';
    if (passwordStrength >= 3) return 'Strong';
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });

    // Live update for password field
    if (name === 'password') {
        setPasswordStrength(checkStrength(value));
    }
  };

  const handleSignup = async (e) => {
    e.preventDefault();
    setError('');
    
    // Optional: Block weak passwords
    if(passwordStrength < 2) {
        setError("Please choose a stronger password.");
        return;
    }

    setLoading(true);

    if (formData.role === 'STUDENT') {
        if(!formData.department || !formData.currentSemester) {
            setError("Students must provide Department and Semester.");
            setLoading(false);
            return;
        }
    }

    try {
      await api.post('/auth/register', formData);
      alert("Registration Successful! Please Login.");
      navigate('/login');
    } catch (err) {
      console.error("Signup Error:", err);
      setError(err.response?.data?.message || 'Registration failed. Check your inputs.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-slate-100">
      <div className="w-full max-w-lg p-8 bg-white rounded-xl shadow-lg border border-slate-200">
        
        <div className="text-center mb-8">
          <h2 className="text-3xl font-extrabold text-slate-800">Create Account</h2>
          <p className="text-slate-500 mt-2">Join the Smart Student System</p>
        </div>

        {error && (
          <div className="p-4 mb-4 text-sm text-red-700 bg-red-100 rounded-lg border-l-4 border-red-500">
            {error}
          </div>
        )}

        <form onSubmit={handleSignup} className="space-y-5">
          
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-1">First Name</label>
              <input name="firstName" type="text" required onChange={handleChange}
                className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" />
            </div>
            <div>
              <label className="block text-sm font-semibold text-slate-700 mb-1">Last Name</label>
              <input name="lastName" type="text" required onChange={handleChange}
                className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" />
            </div>
          </div>

          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-1">Email Address</label>
            <input name="email" type="email" required onChange={handleChange} placeholder="student@example.com"
              className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" />
          </div>

          {/*  PASSWORD FIELD WITH METER */}
          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-1">Password</label>
            <input 
                name="password" 
                type="password" 
                required 
                onChange={handleChange} 
                placeholder="••••••••"
                className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" 
            />
            
            {/* The Strength Bar */}
            {formData.password && (
                <div className="mt-2 transition-all duration-300 ease-in-out">
                    <div className="h-1.5 w-full bg-gray-200 rounded-full overflow-hidden">
                        <div 
                            className={`h-full ${getStrengthColor()} transition-all duration-500`} 
                            style={{ width: `${(passwordStrength / 4) * 100}%` }}
                        ></div>
                    </div>
                    <p className={`text-xs mt-1 font-medium text-right ${
                        passwordStrength <= 1 ? 'text-red-500' : 
                        passwordStrength === 2 ? 'text-yellow-600' : 'text-green-600'
                    }`}>
                        {getStrengthLabel()}
                    </p>
                </div>
            )}
          </div>

          <div>
            <label className="block text-sm font-semibold text-slate-700 mb-1">I am a...</label>
            <select name="role" onChange={handleChange} value={formData.role}
              className="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none bg-white">
              <option value="STUDENT">Student</option>
              <option value="TEACHER">Teacher</option>
            </select>
          </div>

          {formData.role === 'STUDENT' && (
            <div className="p-4 bg-blue-50 rounded-lg border border-blue-100 grid grid-cols-2 gap-4">
                <div>
                    <label className="block text-sm font-semibold text-blue-800 mb-1">Department</label>
                    <select name="department" onChange={handleChange} required
                        className="w-full px-4 py-2 border border-blue-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none bg-white">
                        <option value="">Select...</option>
                        <option value="Computer Science">Computer Science</option>
                        <option value="Information Tech">Information Tech</option>
                        <option value="Electronics">Electronics</option>
                    </select>
                </div>
                <div>
                    <label className="block text-sm font-semibold text-blue-800 mb-1">Semester</label>
                    <select name="currentSemester" onChange={handleChange} required
                        className="w-full px-4 py-2 border border-blue-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none bg-white">
                        <option value="">Select...</option>
                        <option value="1">1st Sem</option>
                        <option value="2">2nd Sem</option>
                        <option value="3">3rd Sem</option>
                        <option value="4">4th Sem</option>
                        <option value="5">5th Sem</option>
                        <option value="6">6th Sem</option>
                        <option value="7">7th Sem</option>
                        <option value="8">8th Sem</option>
                    </select>
                </div>
            </div>
          )}

          <button type="submit" disabled={loading}
            className={`w-full py-3 font-bold text-white rounded-lg shadow-md transition duration-200 ${
              loading ? "bg-blue-400" : "bg-blue-600 hover:bg-blue-700"
            }`}>
            {loading ? "Creating Account..." : "Sign Up"}
          </button>

        </form>

        <p className="mt-6 text-center text-sm text-slate-600">
          Already have an account? <Link to="/login" className="text-blue-600 hover:underline">Login here</Link>
        </p>

      </div>
    </div>
  );
};

export default SignUp;