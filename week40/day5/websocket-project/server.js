import { WebSocketServer,WebSocket } from "ws";

const wss = new WebSocketServer({ port:8090,
    host: '0.0.0.0'
 });

// 0 CONNECTING
// 1 OPEN(The only state where you can safely .send())
// 2 CLOSING
// 3 CLOSED


// Connection event
wss.on('connection',(socket,request)=>{

    const ip = request.socket.remoteAddress;

    socket.on('message',(rawData)=>{
        const message = rawData.toString();
        console.log({rawData});
        
        wss.clients.forEach((client) =>{
            if(client.readyState === WebSocket.OPEN) {client.send(`Server Broadcast: ${message}`)}
        })
    });

    socket.on('error',(err)=>{
        console.error(`Error: ${err.message}: ${ip}`);
    });

    socket.on('close',()=>{
        console.log('Client disconnected');
    })
})


console.log("WebSocket Server is live on ws://localhost:8090");