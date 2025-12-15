import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import useChatContext from "../context/ChatContext";
import toast from "react-hot-toast";
import axios from "axios";
import { baseURL } from "../config/AxiosHelper";

const Login = () => {
  const navigate = useNavigate();
  const { setCurrentUser } = useChatContext();
  const [credentials, setCredentials] = useState({ username: "", password: "" });

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(`${baseURL}/api/auth/login`, credentials);
      // Response.data contains: { username: "...", profilePic: "...", ... }
      setCurrentUser(response.data); 
      toast.success("Login Successful!");
      navigate("/join-room");
    } catch (error) {
      toast.error("Invalid Username or Password");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-900 p-4">
      <div className="bg-gray-800 p-8 rounded-2xl shadow-2xl w-full max-w-md border border-gray-700">
        <h2 className="text-3xl font-bold text-center text-green-500 mb-6">Welcome Back</h2>
        
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-gray-400 mb-1">Username</label>
            <input name="username" onChange={handleChange} className="w-full p-3 rounded-lg bg-gray-700 text-white focus:outline-none focus:ring-2 focus:ring-green-500 transition" required />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-400 mb-1">Password</label>
            <input name="password" type="password" onChange={handleChange} className="w-full p-3 rounded-lg bg-gray-700 text-white focus:outline-none focus:ring-2 focus:ring-green-500 transition" required />
          </div>

          <button type="submit" className="w-full bg-green-600 hover:bg-green-700 text-white font-bold py-3 rounded-lg transition duration-300 transform hover:scale-105">
            Login
          </button>
        </form>

        <p className="mt-6 text-center text-gray-400">
          New here? <Link to="/" className="text-green-400 hover:underline">Create an account</Link>
        </p>
      </div>
    </div>
  );
};
export default Login;