import React, { useEffect, useRef, useState } from "react";
import { MdAttachFile, MdSend, MdArrowBack } from "react-icons/md";
import useChatContext from "../context/ChatContext";
import { useNavigate } from "react-router";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import toast from "react-hot-toast";
import { baseURL } from "../config/AxiosHelper";
import { getMessagess } from "../services/RoomService";
import { timeAgo } from "../config/helper";
import axios from "axios";

const ChatPage = () => {
  const { roomId, currentUser, connected, setConnected, setRoomId } = useChatContext();
  const navigate = useNavigate();

  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const chatBoxRef = useRef(null);
  const fileInputRef = useRef(null);
  const [stompClient, setStompClient] = useState(null);

  // --------------------------------------------------------
  // 1. AUTH & NAVIGATION CHECKS
  // --------------------------------------------------------
  useEffect(() => {
    if (!connected || !currentUser) navigate("/login");
  }, [connected, currentUser, navigate]);

  // --------------------------------------------------------
  // 2. LOAD OLD MESSAGES
  // --------------------------------------------------------
  useEffect(() => {
    async function loadMessages() {
      try {
        const history = await getMessagess(roomId);
        setMessages(history);
      } catch (error) {
        console.error(error);
      }
    }
    if (connected) {
      loadMessages();
    }
  }, [roomId, connected]);

  // --------------------------------------------------------
  // 3. AUTO SCROLL TO BOTTOM
  // --------------------------------------------------------
  useEffect(() => {
    if (chatBoxRef.current) {
      chatBoxRef.current.scroll({
        top: chatBoxRef.current.scrollHeight,
        behavior: "smooth",
      });
    }
  }, [messages]);

  // --------------------------------------------------------
  // 4. WEBSOCKET CONNECTION
  // --------------------------------------------------------
  useEffect(() => {
    const connectWebSocket = () => {
      const client = Stomp.over(() => new SockJS(`${baseURL}/chat`));
      client.connect({}, () => {
        setStompClient(client);
        toast.success(`Joined Room: ${roomId}`);
        client.subscribe(`/topic/room/${roomId}`, (message) => {
          const newMessage = JSON.parse(message.body);
          setMessages((prev) => [...prev, newMessage]);
        });
      });
    };

    if (connected) {
      connectWebSocket();
    }

    return () => {
      if (stompClient) stompClient.disconnect();
    };
  }, [roomId, connected]);

  // --------------------------------------------------------
  // 5. FILE UPLOAD HANDLER
  // --------------------------------------------------------
  const handleFileSelect = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append("file", file);

    try {
      toast.loading("Uploading image...");
      const response = await axios.post(`${baseURL}/api/v1/files/upload`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      const imageUrl = response.data;
      toast.dismiss();
      toast.success("Image sent!");
      sendWebSocketMessage(imageUrl, "IMAGE");
    } catch (error) {
      toast.dismiss();
      toast.error("Image upload failed");
    }
  };

  // --------------------------------------------------------
  // 6. SEND MESSAGE HELPER
  // --------------------------------------------------------
  const sendWebSocketMessage = (msgContent, msgType = "TEXT") => {
    if (stompClient && connected) {
      const message = {
        sender: currentUser.username,
        content: msgContent,
        roomId: roomId,
        type: msgType,
      };
      stompClient.send(`/app/sendMessage/${roomId}`, {}, JSON.stringify(message));
    }
  };

  const handleSendMessage = () => {
    if (input.trim()) {
      sendWebSocketMessage(input, "TEXT");
      setInput("");
    }
  };

  const handleLeave = () => {
    setConnected(false);
    setRoomId("");
    navigate("/join-room");
  };

  return (
    <div className="flex flex-col h-screen bg-gray-900 text-white">
      {/* HEADER */}
      <header className="bg-gray-800 border-b border-gray-700 py-4 px-6 flex justify-between items-center shadow-md sticky top-0 z-10">
        <div className="flex items-center gap-3">
          <button onClick={handleLeave} className="text-gray-400 hover:text-white transition">
            <MdArrowBack size={24} />
          </button>
          <div>
            <h1 className="text-lg font-bold text-white">
              Room: <span className="text-blue-400 font-mono">{roomId}</span>
            </h1>
          </div>
        </div>
        
        {/* CURRENT USER BADGE */}
        <div className="flex items-center gap-2 bg-gray-700 py-1 px-3 rounded-full border border-gray-600">
           <img 
             src={currentUser?.profilePic || "https://avatar.iran.liara.run/public/43"} 
             alt="Me" 
             className="w-8 h-8 rounded-full object-cover" 
           />
           <span className="font-semibold text-sm">{currentUser?.username}</span>
        </div>
      </header>

      {/* CHAT AREA */}
      <main
        ref={chatBoxRef}
        className="flex-1 overflow-y-auto p-4 md:p-10 space-y-4 bg-gray-900 scrollbar-thin scrollbar-thumb-gray-700"
      >
        {messages.map((message, index) => {
          const isMe = message.sender === currentUser?.username;
          
          // -------------------------------------------------
          // 🔥 FIXED IMAGE LOGIC
          // -------------------------------------------------
          let profileSrc;
          if (isMe) {
             // If it's me, use my real profile pic (or a fallback if empty)
             profileSrc = currentUser?.profilePic || "https://avatar.iran.liara.run/public/43";
          } else {
             // If it's someone else, use a reliable "Initials" generator (ui-avatars.com)
             // This is much more stable than the previous one.
             profileSrc = `https://ui-avatars.com/api/?name=${message.sender}&background=random&color=fff&size=128`;
          }
          // -------------------------------------------------

          return (
            <div key={index} className={`flex ${isMe ? "justify-end" : "justify-start"}`}>
              <div className={`flex max-w-[80%] md:max-w-[60%] gap-2 ${isMe ? "flex-row-reverse" : "flex-row"}`}>
                
                {/* Avatar Display */}
                <img
                  className="h-8 w-8 rounded-full border border-gray-600 object-cover bg-gray-700"
                  src={profileSrc}
                  alt="Avatar"
                  // Fallback in case the image completely fails to load
                  onError={(e) => { e.target.src = "https://ui-avatars.com/api/?name=?"; }}
                />

                {/* Message Bubble */}
                <div
                  className={`p-3 rounded-2xl shadow-md text-sm ${
                    isMe
                      ? "bg-purple-600 text-white rounded-tr-none"
                      : "bg-gray-800 text-gray-200 rounded-tl-none border border-gray-700"
                  }`}
                >
                  {!isMe && <p className="text-xs font-bold text-purple-400 mb-1">{message.sender}</p>}
                  
                  {message.type === "IMAGE" ? (
                    <img
                      src={message.content}
                      alt="Shared"
                      className="max-w-full h-auto rounded-lg border border-white/20 cursor-pointer hover:opacity-90 transition"
                      onClick={() => window.open(message.content, "_blank")}
                    />
                  ) : (
                    <p className="leading-relaxed break-words">{message.content}</p>
                  )}

                  <p className={`text-[10px] mt-1 text-right ${isMe ? "text-purple-200" : "text-gray-500"}`}>
                    {timeAgo(message.timeStamp)}
                  </p>
                </div>
              </div>
            </div>
          );
        })}
      </main>

      {/* INPUT AREA */}
      <div className="bg-gray-800 border-t border-gray-700 p-4">
        <div className="max-w-4xl mx-auto flex items-center gap-2 bg-gray-900 border border-gray-600 rounded-full px-2 py-1 shadow-inner">
          <input
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSendMessage()}
            type="text"
            placeholder="Type a message..."
            className="flex-1 bg-transparent text-white px-4 py-3 focus:outline-none placeholder-gray-500"
          />

          <input
            type="file"
            ref={fileInputRef}
            onChange={handleFileSelect}
            className="hidden"
            accept="image/*"
          />

          <button
            onClick={() => fileInputRef.current.click()}
            className="p-2 text-gray-400 hover:text-purple-400 transition rounded-full hover:bg-gray-800"
            title="Attach Image"
          >
            <MdAttachFile size={22} />
          </button>

          <button
            onClick={handleSendMessage}
            className="p-3 bg-purple-600 text-white rounded-full hover:bg-purple-700 transition shadow-lg flex items-center justify-center"
          >
            <MdSend size={20} />
          </button>
        </div>
      </div>
    </div>
  );
};

export default ChatPage;