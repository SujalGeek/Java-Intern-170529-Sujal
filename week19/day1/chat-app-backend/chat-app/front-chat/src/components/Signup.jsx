import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import toast from "react-hot-toast";
import axios from "axios";
import { baseURL } from "../config/AxiosHelper";
const Signup = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    file: null,
  });
  const [preview, setPreview] = useState(null);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    setFormData({ ...formData, file: file });
    // Show image preview
    if (file) {
      setPreview(URL.createObjectURL(file));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const data = new FormData();
    data.append("username", formData.username);
    data.append("email", formData.email);
    data.append("password", formData.password);
    if (formData.file) {
      data.append("file", formData.file);
    }

    try {
      await axios.post(`${baseURL}/api/auth/signup`, data, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      toast.success("Signup Successful! Please Login.");
      navigate("/login");
    } catch (error) {
      console.log(error);
      toast.error(error.response?.data || "Signup failed");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-900 p-4">
      <div className="bg-gray-800 p-8 rounded-2xl shadow-2xl w-full max-w-md border border-gray-700">
        <h2 className="text-3xl font-bold text-center text-purple-500 mb-6">Create Account</h2>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          
          {/* Image Preview Circle */}
          <div className="flex justify-center mb-4">
            <div className="relative w-24 h-24 rounded-full overflow-hidden border-4 border-purple-500 bg-gray-700">
               {preview ? (
                 <img src={preview} alt="Preview" className="w-full h-full object-cover" />
               ) : (
                 <span className="text-gray-400 text-xs flex items-center justify-center h-full">No Image</span>
               )}
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-400 mb-1">Username</label>
            <input name="username" onChange={handleChange} className="w-full p-3 rounded-lg bg-gray-700 text-white focus:outline-none focus:ring-2 focus:ring-purple-500 transition" required />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-400 mb-1">Email</label>
            <input name="email" type="email" onChange={handleChange} className="w-full p-3 rounded-lg bg-gray-700 text-white focus:outline-none focus:ring-2 focus:ring-purple-500 transition" required />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-400 mb-1">Password</label>
            <input name="password" type="password" onChange={handleChange} className="w-full p-3 rounded-lg bg-gray-700 text-white focus:outline-none focus:ring-2 focus:ring-purple-500 transition" required />
          </div>

          <div>
             <label className="block text-sm font-medium text-gray-400 mb-1">Profile Picture (Optional)</label>
             <input type="file" onChange={handleFileChange} className="w-full text-sm text-gray-400 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-purple-600 file:text-white hover:file:bg-purple-700"/>
          </div>

          <button type="submit" className="w-full bg-purple-600 hover:bg-purple-700 text-white font-bold py-3 rounded-lg transition duration-300 transform hover:scale-105">
            Sign Up
          </button>
        </form>

        <p className="mt-6 text-center text-gray-400">
          Already have an account? <Link to="/login" className="text-purple-400 hover:underline">Login here</Link>
        </p>
      </div>
    </div>
  );
};
export default Signup;