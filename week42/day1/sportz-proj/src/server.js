import AgentAPI from "apminsight"
AgentAPI.config()


import express from 'express'
import http from 'http'
import { matchRouter } from './routes/matches.js';
import attachWebSocketServer from './ws/server.js';
import { securityMiddleware } from './arcjet.js';
import { commentaryRouter } from './routes/commentary.js';


const PORT = Number(process.env.PORT || 5000);
const HOST = process.env.HOST || '0.0.0.0';


const app = express();
const server = http.createServer(app);
const {broadCastMatchCreated,broadcastCommentary}  = attachWebSocketServer(server);

app.locals.broadCastMatchCreated = broadCastMatchCreated;
app.locals.broadcastCommentary = broadcastCommentary;


app.use(express.json())
app.use((req, res, next) => {
    res.setHeader("Access-Control-Allow-Origin", "*"); 
    res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
    res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, x-arcjet-client-ip");
    
    // Gracefully handle preflight browser pre-check requests safely
    if (req.method === "OPTIONS") {
        return res.sendStatus(200);
    }
    next();
});

app.get('/',(req,res)=>{
    res.send("Hello from Express Server");
})

// app.use(securityMiddleware())
app.use('/matches',matchRouter);
app.use('/matches/:id/commentary',commentaryRouter)

server.listen(PORT, HOST,()=>{
    const baseUrl = HOST === '0.0.0.0' ? `http://localhost:${PORT}` :`http://${HOST}:${PORT}`;
    
    console.log(`Server is running at ${baseUrl}`)
    console.log(`WebSocket is running on the ${baseUrl.replace('http','ws')}/ws`);
})

