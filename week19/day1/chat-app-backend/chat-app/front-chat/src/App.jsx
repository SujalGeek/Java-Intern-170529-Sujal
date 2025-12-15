import { useState } from "react";
import "./App.css";
import { Route, Routes } from "react-router";
import { Toaster } from "react-hot-toast";
import ChatPage from "./components/ChatPage";
import Signup from "./components/Signup";
import Login from "./components/Login";
import JoinCreateRoom from "./components/JoinCreateRoom";

function App() {
  return (
    <div className="min-h-screen dark:bg-gray-900 text-white font-sans">
      <Toaster position="top-center" />
      <Routes>
        <Route path="/" element={<Signup />} />
        <Route path="/login" element={<Login />} />
        <Route path="/join-room" element={<JoinCreateRoom />} />
        <Route path="/chat" element={<ChatPage />} />
      </Routes>
    </div>
  );
}

export default App;