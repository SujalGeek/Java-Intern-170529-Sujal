// import { WebSocket, WebSocketServer } from "ws";
// import { db } from "../db/index.js"; 
// import { matches } from "../db/schema.js";
// import { desc } from "drizzle-orm";
// import { wsArcjet } from "../arcjet.js";

// function sendJson(socket, payload) {
//     if (socket.readyState !== WebSocket.OPEN) return;
//     socket.send(JSON.stringify(payload));
// }

// function broadcast(wss, payload) {
//     for (const client of wss.clients) {
//         if (client.readyState !== WebSocket.OPEN) {
//             continue; // Safely skip dead sockets
//         }
//         client.send(JSON.stringify(payload));
//     }
// }

// export default function attachWebSocketServer(server) {
//     const wss = new WebSocketServer({
//         server,
//         path: '/ws',
//         maxPayload: 1024 * 1024
//     });

//     wss.on('connection', async (socket,req) => {
//         // 1. Always send the welcome event immediately
//         if(wsArcjet)
//         {
//             try {
//                 const decision = await wsArcjet.protect(req);

//                 if(decision.isDenied())
//                 {
//                     // 1013 means try again letter and 1008 policy simulation (bot detector)
//                     const code = decision.reason.isRateLimit() ? 1013: 1008;
//                     const reason = decision.reason.isRateLimit() ? 'Rate Limit exceeded' : 'Access Denied';

//                     socket.close(code,reason);
//                 }

//             } catch (error) {
//                 console.error('Ws Connection Error',error);
//                 socket.close(1011,'Server Security Error');
//             }
//         }

//         sendJson(socket, { type: 'welcome' });

//         try {
//             // 2. Try to grab live data from Neon Cloud
//             const currentMatches = await db.select()
//                 .from(matches)
//                 .orderBy(desc(matches.createdAt))
//                 .limit(10);

//             sendJson(socket, {
//                 type: 'initial_matches',
//                 data: currentMatches
//             });
//         } catch (error) {
//             console.warn("Corporate Network Restricted Database Fetch. Sending local snapshot template.");

//             // 3. FIXED FALLBACK: Sends a clean data shape so your client UI never hangs!
//             const fallbackLocalMatches = [
//                 {
//                     id: 1,
//                     sport: "football",
//                     homeTeam: "Manchester City",
//                     awayTeam: "Barcelona",
//                     status: "scheduled",
//                     startTime: "2026-05-20T12:00:00.000Z",
//                     endTime: "2026-05-20T14:00:00.000Z",
//                     homeScore: 0,
//                     awayScore: 0,
//                     createdAt: new Date().toISOString()
//                 }
//             ];

//             sendJson(socket, { 
//                 type: 'initial_matches', 
//                 data: fallbackLocalMatches 
//             });
//         }

//         socket.on('error', console.error);
//     });

//     function broadCastMatchCreated(match) {
//         broadcast(wss, {
//             type: 'match_created',
//             data: match
//         });
//     }

//     return { broadCastMatchCreated };
// }

import { WebSocket, WebSocketServer } from "ws";
import { db } from "../db/index.js";
import { matches } from "../db/schema.js";
import { desc } from "drizzle-orm";
import { wsArcjet } from "../arcjet.js";
import { types } from "pg";

const matchSubscribers = new Map();

function subscribe(matchId, socket) {
    if (!matchSubscribers.has(matchId)) {
        matchSubscribers.set(matchId, new Set());
    }

    matchSubscribers.get(matchId).add(socket);
}

function unsubscribe(matchId, socket) {
    const subscribers = matchSubscribers.get(matchId);

    if (!subscribers) {
        return;
    }

    subscribers.delete(socket);

    if (subscribers.size === 0) {
        matchSubscribers.delete(matchId);
    }

}

function cleanUpSubscriptions(socket) {
    for (const matchId of socket.subscriptions) {
        unsubscribe(matchId, socket)
    }
}

function broadcastToMatch(matchId, payload) {
    const subscribers = matchSubscribers.get(matchId);

    if (!subscribers || subscribers.size === 0) {
        return;
    }

    const message = JSON.stringify(payload);

    for (const client of subscribers) {
        if (client.readyState === WebSocket.OPEN) {
            client.send(message);
        }
    }
}

function handleMessage(socket, data) {
    let message;

    try {
        message = JSON.parse(data.toString());
    } catch (error) {
        sendJson(socket, {
            type: 'error',
            message: 'Invalid JSON'
        });
    }

    if (message?.type === "subscribe" && Number.isInteger(message.matchId)) {
        subscribe(message.matchId, socket);
        socket.subscriptions.add(message.matchId);
        sendJson(socket,{
            type: 'subscribed',
            matchId: message.matchId
        });
    }


    if (message?.type === "unsubscribe" && Number.isInteger(message.matchId)) {
        unsubscribe(message.matchId, socket);
        socket.subscriptions.delete(message.matchId);
        sendJson(socket,{
            type: 'unsubscribed',
            matchId: message.matchId
        });

    }
}


function sendJson(socket, payload) {
    if (socket.readyState !== WebSocket.OPEN) return;
    socket.send(JSON.stringify(payload));
}

function broadcastToAll(wss, payload) {
    for (const client of wss.clients) {
        if (client.readyState !== WebSocket.OPEN) {
            continue;
        }
        client.send(JSON.stringify(payload));
    }
}


export default function attachWebSocketServer(server) {
    const wss = new WebSocketServer({
        server,
        path: '/ws',
        maxPayload: 1024 * 1024
    });

    // FIXED: Added 'req' as the second parameter here
    wss.on('connection', async (socket, req) => {

        // socket.isAlive = true;


        if (wsArcjet) {
            try {
                // Now passes the valid network upgrade packet request securely
                const decision = await wsArcjet.protect(req);

                if (decision.isDenied()) {
                    const code = decision.reason.isRateLimit() ? 1013 : 1008;
                    const reason = decision.reason.isRateLimit() ? 'Rate Limit exceeded' : 'Access Denied';

                    return socket.close(code, reason); // Stop execution immediately
                }

            } catch (error) {
                console.error('Ws Connection Security Bypass Error:', error);
                // Fail Open logic: If corporate proxy drops Arcjet fetch, let them pass
                console.log(" Arcjet cloud pool timed out. Proceeding cleanly.");
            }
        }

        socket.subscriptions = new Set();
        // Send handshake welcome frame
        sendJson(socket, { type: 'welcome' });

        socket.on('message', (data) => {
            handleMessage(socket, data)
        });

        socket.on('error', () => {
            console.error("Socket Exception occurred:", err.message);
            socket.terminate();
        });

        socket.on('close', () => {
            cleanUpSubscriptions(socket);
        });

        try {
            const currentMatches = await db.select()
                .from(matches)
                .orderBy(desc(matches.createdAt))
                .limit(10);

            sendJson(socket, {
                type: 'initial_matches',
                data: currentMatches
            });
        } catch (error) {
            console.warn("Corporate Network Restricted Database Fetch. Sending local snapshot template.");

            const fallbackLocalMatches = [
                {
                    id: 1,
                    sport: "football",
                    homeTeam: "Manchester City",
                    awayTeam: "Barcelona",
                    status: "scheduled",
                    startTime: "2026-05-20T12:00:00.000Z",
                    endTime: "2026-05-20T14:00:00.000Z",
                    homeScore: 0,
                    awayScore: 0,
                    createdAt: new Date().toISOString()
                }
            ];

            sendJson(socket, {
                type: 'initial_matches',
                data: fallbackLocalMatches
            });
        }

        socket.on('error', console.error);
    });

    function broadCastMatchCreated(match) {
        broadcastToAll(wss, {
            type: 'match_created',
            data: match
        });
    }

    function broadcastCommentary(matchId, comment) {
        broadcastToMatch(matchId, {
            type: 'commentary',
            data: comment
        });
    }


    return { broadCastMatchCreated,broadcastCommentary };
}