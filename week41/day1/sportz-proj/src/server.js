import express from 'express'
import http from 'http'
import { matchRouter } from './routes/matches.js';
import attachWebSocketServer from './ws/server.js';
import { securityMiddleware } from './arcjet.js';


const PORT = Number(process.env.PORT || 5000);
const HOST = process.env.HOST || '0.0.0.0';


const app = express();
const server = http.createServer(app);
const {broadCastMatchCreated}  = attachWebSocketServer(server);

app.locals.broadCastMatchCreated = broadCastMatchCreated;


app.use(express.json())

app.get('/',(req,res)=>{
    res.send("Hello from Express Server");
})

app.use(securityMiddleware())
app.use('/matches',matchRouter);

server.listen(PORT, HOST,()=>{
    const baseUrl = HOST === '0.0.0.0' ? `http://localhost:${PORT}` :`http://${HOST}:${PORT}`;
    
    console.log(`Server is running at ${baseUrl}`)
    console.log(`WebSocket is running on the ${baseUrl.replace('http','ws')}/ws`);
})

