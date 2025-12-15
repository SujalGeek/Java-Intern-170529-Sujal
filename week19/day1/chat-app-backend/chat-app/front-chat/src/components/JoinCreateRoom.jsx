import React, { useState, useEffect } from "react";
import chatIcon from "../assets/chat.png"; // Keep your icon if you want, or remove
import toast from "react-hot-toast";
import { createRoomApi, joinChatApi } from "../services/RoomService";
import useChatContext from "../context/ChatContext";
import { useNavigate } from "react-router";
import { v4 as uuidv4 } from 'uuid'; // Import UUID generator

const JoinCreateRoom = () => {
  const [roomIdInput, setRoomIdInput] = useState("");
  
  const { 
    setRoomId,
    setCurrentUser,
    currentUser,
    setConnected,
    connected 
  } = useChatContext();
  
  const navigate = useNavigate();

  // 1. Protection: If not logged in, go to Login page
  useEffect(() => {
    if (!currentUser) {
      navigate("/login");
    }
  }, [currentUser, navigate]);

  // 2. Generate Unique Room ID
  const generateRoomId = () => {
    const newId = uuidv4().slice(0, 8); // Generates a short unique ID (e.g., "a1b2c3d4")
    setRoomIdInput(newId);
    toast.success("New Room ID Generated!");
  };

  // 3. Handle Join Room
  async function joinRoom() {
    if (!roomIdInput.trim()) {
      toast.error("Please enter a Room ID");
      return;
    }

    try {
      // Optional: Check if room exists in backend before joining
      // For now, we assume if you have the ID, you can join
      const room = await joinChatApi(roomIdInput);
      
      setRoomId(room.roomId);
      setConnected(true);
      toast.success("Joined Room Successfully!");
      navigate("/chat");
      
    } catch (error) {
      if (error.response?.status === 400) {
        toast.error(error.response.data);
      } else {
        toast.error("Error joining room");
      }
    }
  }

  // 4. Handle Create Room
  async function createRoom() {
    if (!roomIdInput.trim()) {
      toast.error("Enter or Generate a Room ID first");
      return;
    }

    try {
      const response = await createRoomApi(roomIdInput);
      toast.success("Room Created Successfully!");
      
      setRoomId(response.roomId);
      setConnected(true);
      navigate("/chat");
      
    } catch (error) {
        console.log(error);
        if (error.response?.status === 400) {
          toast.error("Room already exists! Joining instead...");
          // Auto-join if it already exists
          setRoomId(roomIdInput);
          setConnected(true);
          navigate("/chat");
        } else {
          toast.error("Error creating room");
        }
    }
  }

  // 5. Handle Logout
  const handleLogout = () => {
      setCurrentUser(null);
      setConnected(false);
      navigate("/login");
  }

  if (!currentUser) return null; // Prevent flicker before redirect

  return (
    <div className="min-h-screen flex items-center justify-center dark:bg-gray-900 text-white font-sans p-4">
      <div className="w-full max-w-md p-8 dark:bg-gray-800 border dark:border-gray-700 rounded-2xl shadow-2xl flex flex-col gap-6">
        
        {/* Header Section */}
        <div className="flex flex-col items-center gap-3">
          <div className="relative">
            <img 
              src={currentUser.profilePic} 
              alt="User"
                className="w-20 h-20 rounded-full border-4 border-purple-500 shadow-lg object-cover"
            />
            <div className="absolute bottom-0 right-0 w-5 h-5 bg-green-500 border-2 border-gray-800 rounded-full"></div>
          </div>
          
          <div className="text-center">
            <h1 className="text-2xl font-bold">
              Hi, <span className="text-purple-400">{currentUser.username}</span> 👋
            </h1>
            <p className="text-gray-400 text-sm">Join a chat room or create a new one.</p>
          </div>
        </div>

        {/* Input Section */}
        <div className="flex flex-col gap-4">
          <div>
            <label htmlFor="roomId" className="block text-sm font-medium text-gray-400 mb-2">
              Room ID
            </label>
            <div className="relative">
                <input
                  name="roomId"
                  onChange={(e) => setRoomIdInput(e.target.value)}
                  value={roomIdInput}
                  type="text"
                  placeholder="Enter ID or Generate New"
                  className="w-full dark:bg-gray-700 text-center text-lg tracking-wider px-4 py-3 border dark:border-gray-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 transition"
                />
            </div>
            
            {/* Generator Link */}
            <div className="flex justify-end mt-2">
                <button 
                    onClick={generateRoomId}
                    className="text-xs text-blue-400 hover:text-blue-300 hover:underline transition flex items-center gap-1"
                >
                    ✨ Generate Unique ID
                </button>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex gap-3 mt-2">
            <button
              onClick={joinRoom}
              className="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 rounded-lg transition duration-200 shadow-lg"
            >
              Join Room
            </button>
            <button
              onClick={createRoom}
              className="flex-1 bg-orange-500 hover:bg-orange-600 text-white font-semibold py-3 rounded-lg transition duration-200 shadow-lg"
            >
              Create Room
            </button>
          </div>
        </div>

        {/* Footer / Logout */}
        <div className="border-t dark:border-gray-700 pt-4 mt-2 text-center">
            <button 
                onClick={handleLogout}
                className="text-sm text-gray-500 hover:text-red-400 transition"
            >
                Not you? Log out
            </button>
        </div>

      </div>
    </div>
  );
};

export default JoinCreateRoom;