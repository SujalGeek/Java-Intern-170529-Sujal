import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'
import { BrowserRouter } from 'react-router-dom'
import { ChatProvider } from './context/ChatContext.jsx' // <--- 1. Import this

ReactDOM.createRoot(document.getElementById('root')).render(
  // <React.StrictMode>  <-- COMMENT THIS OUT
    <ChatProvider>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </ChatProvider>
  // </React.StrictMode>, <-- COMMENT THIS OUT
)