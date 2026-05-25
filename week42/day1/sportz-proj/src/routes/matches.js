// import { Router } from "express";
// import { createMatchSchema } from "../validation/matches.js";
// import { matches } from "../db/schema.js";
// import {db} from '../db/index.js'
// import { getMatchStatus } from "../utils/match-status.js";

// export const matchRouter = Router();

// matchRouter.get('/',(req,res)=>{
//     res.status(200).json({
//         message:'Matches List'
//     })
// });

// matchRouter.post('/', async (req,res)=>{
//     const parsed = createMatchSchema.safeParse(req.body);
//     const {
//         data :{
//             startTime,endTime,homeScore,awayScore
//         }
//     } = parsed;

//     if(!parsed.success)
//     {
//             return res.status(400).json(
//                 {
//                     error:'Invalid payload.',
//                     details: JSON.stringify(parsed.error)
//                 }
//             );
//     }


//     try {
        
//         const [event] = await db.insert(matches).values({
//             ...parsed.data,
//             startTime: new Date(startTime),
//             endTime: new Date(endTime),
//             homeScore: homeScore ?? 0,
//             awayScore: awayScore ?? 0,
//             status: getMatchStatus(startTime,endTime),
//         }).returning();

//         res.status(201).json({
//             data: event
//         });


        
//     } catch (error) {
//         res.status(500).json({
//             error:'Failed to create match.',
//             details: JSON.stringify(error)
//         });
//     }
// })

// import { Router } from "express";
// import { createMatchSchema, listMatchesQuerySchema } from "../validation/matches.js";
// import { matches } from "../db/schema.js";
// import { db } from '../db/index.js';
// import { getMatchStatus } from "../utils/match-status.js";
// import { desc } from "drizzle-orm";

// export const matchRouter = Router();

// const MAX_LIMIT = 100;
// matchRouter.get('/', async (req, res) => {
//     const parsed = listMatchesQuerySchema.safeParse(req.query);
    
//     // 1. If validation fails, return early and stop execution
//     if (!parsed.success) {
//         return res.status(400).json({
//             error: 'Invalid query',
//             details: parsed.error.issues
//         });
//     }

//     // 2. This code runs ONLY when validation is successful
//     const limit = Math.min(parsed.data?.limit ?? 50, MAX_LIMIT);

//     try {
//         const data = await db.select()
//             .from(matches)
//             .orderBy(desc(matches.createdAt))
//             .limit(limit);

//         return res.json({
//             data
//         });

//     } catch (error) {
//         console.error("Database Fetch Error:", error);
//         return res.status(500).json({
//             error: 'Failed to list matches.',
//             details: error.message
//         });
//     }
// });

// matchRouter.post('/', async (req, res) => {
//     const parsed = createMatchSchema.safeParse(req.body);

//     // 1. MUST check success FIRST before touching parsed.data
//     if (!parsed.success) {
//         return res.status(400).json({
//             error: 'Invalid payload.',
//             details: parsed.error.issues // .issues gives a much cleaner object than full stringify
//         });
//     }

//     try {
//         // 2. Safely extract variables now that validation is guaranteed
//         const { sport, homeTeam, awayTeam, startTime, endTime, homeScore, awayScore } = parsed.data;

//         // 3. Explicitly map your parameters to protect the database layer
//         const [event] = await db.insert(matches).values({
//             sport,
//             homeTeam,
//             awayTeam,
//             startTime: new Date(startTime),
//             endTime: new Date(endTime),
//             homeScore: homeScore ?? 0,
//             awayScore: awayScore ?? 0,
//             status: getMatchStatus(startTime, endTime),
//         }).returning();

//         return res.status(201).json({
//             data: event
//         });

//     } catch (error) {
//         console.error("Database Insert Error:", error); // Essential for tracking corporate/network dropouts
//         return res.status(500).json({
//             error: 'Failed to create match.',
//             details: error.message || 'Database transaction rejected.'
//         });
//     }
// });

// // 1. Add a fallback mock database helper at the top of src/routes/matches.js
// const memoryMatches = [];

// // YOUR GET ROUTE
// matchRouter.get('/', async (req, res) => {
//     const parsed = listMatchesQuerySchema.safeParse(req.query);
//     if (!parsed.success) {
//         return res.status(400).json({ error: 'Invalid query', details: parsed.error.issues });
//     }

//     const limit = Math.min(parsed.data?.limit ?? 50, MAX_LIMIT);

//     try {
//         // This will work perfectly when deployed to the cloud!
//         const data = await db.select().from(matches).orderBy(desc(matches.createdAt)).limit(limit);
//         return res.json({ data });
//     } catch (error) {
//         console.warn("⚠️ Corporate Firewall detected. Switching to local in-memory data stream.");
//         // Returns your local array so your frontend/websockets never freeze
//         return res.json({ data: memoryMatches.slice(0, limit) });
//     }
// });

// // YOUR POST ROUTE
// matchRouter.post('/', async (req, res) => {
//     const parsed = createMatchSchema.safeParse(req.body);
//     if (!parsed.success) {
//         return res.status(400).json({ error: 'Invalid payload.', details: parsed.error.issues });
//     }

//     try {
//         const { sport, homeTeam, awayTeam, startTime, endTime, homeScore, awayScore } = parsed.data;

//         // Try to save to Neon Cloud
//         const [event] = await db.insert(matches).values({
//             sport, homeTeam, awayTeam,
//             startTime: new Date(startTime),
//             endTime: new Date(endTime),
//             homeScore: homeScore ?? 0,
//             awayScore: awayScore ?? 0,
//             status: getMatchStatus(startTime, endTime),
//         }).returning();

//         return res.status(201).json({ data: event });
//     } catch (error) {
//         console.warn("⚠️ Database write blocked by firewall. Saving to local runtime snapshot.");
        
//         // Create a mock record matching Drizzle's output shape
//         const fallbackEvent = {
//             id: memoryMatches.length + 1,
//             sport: req.body.sport,
//             homeTeam: req.body.homeTeam,
//             awayTeam: req.body.awayTeam,
//             status: 'live',
//             homeScore: req.body.homeScore ?? 0,
//             awayScore: req.body.awayScore ?? 0,
//             createdAt: new Date().toISOString()
//         };
        
//         memoryMatches.unshift(fallbackEvent); // Add to beginning of our local array
//         return res.status(201).json({ data: fallbackEvent });
//     }
// });

// import { Router } from "express";
// import { createMatchSchema, listMatchesQuerySchema } from "../validation/matches.js";
// import { matches } from "../db/schema.js";
// import { db } from '../db/index.js';
// import { getMatchStatus } from "../utils/match-status.js";
// import { desc } from "drizzle-orm";

// export const matchRouter = Router();

// const MAX_LIMIT = 100;

// // 1. CLEAN GET ROUTE (No more mock memory fallback!)
// matchRouter.get('/', async (req, res) => {
//     const parsed = listMatchesQuerySchema.safeParse(req.query);
    
//     if (!parsed.success) {
//         return res.status(400).json({
//             error: 'Invalid query string parameters.',
//             details: parsed.error.issues
//         });
//     }

//     // Explicitly force the limit to be a Number using Number() to prevent driver crashes
//     const queryLimit = parsed.data?.limit ? Number(parsed.data.limit) : 50;
//     const finalLimit = Math.min(queryLimit, MAX_LIMIT);

//     try {
//         const data = await db.select()
//             .from(matches)
//             .orderBy(desc(matches.createdAt))
//             .limit(finalLimit); // Safe numeric execution

//         return res.json({ data });

//     } catch (error) {
//         console.error("Database Fetch Error:", error);
//         return res.status(500).json({
//             error: 'Failed to list matches from Neon.',
//             details: error.message
//         });
//     }
// });

// // 2. CLEAN POST ROUTE (Writing directly to cloud)
// matchRouter.post('/', async (req, res) => {
//     const parsed = createMatchSchema.safeParse(req.body);

//     if (!parsed.success) {
//         return res.status(400).json({
//             error: 'Invalid payload.',
//             details: parsed.error.issues
//         });
//     }

//     try {
//         const { sport, homeTeam, awayTeam, startTime, endTime, homeScore, awayScore } = parsed.data;

//         const [event] = await db.insert(matches).values({
//             sport,
//             homeTeam,
//             awayTeam,
//             startTime: new Date(startTime),
//             endTime: new Date(endTime),
//             homeScore: homeScore ?? 0,
//             awayScore: awayScore ?? 0,
//             status: getMatchStatus(startTime, endTime),
//         }).returning();

//         if(res.app.locals.broadcastMatchCreated)
//         {
//             res.app.locals.broadcastMatchCreated(event);
//         }



//         return res.status(201).json({ data: event });

//     } catch (error) {
//         console.error("Database Insert Error:", error);
//         return res.status(500).json({
//             error: 'Failed to create match.',
//             details: error.message
//         });
//     }
// });


import { Router } from "express";
import { createMatchSchema, listMatchesQuerySchema } from "../validation/matches.js";
import { matches } from "../db/schema.js";
import { db, isOfflineMode } from '../db/index.js';
import { getMatchStatus } from "../utils/match-status.js";
import { desc } from "drizzle-orm";

export const matchRouter = Router();
const MAX_LIMIT = 100;

const localMemoryMatches = [
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

// GET ROUTE
matchRouter.get('/', async (req, res) => {
    const parsed = listMatchesQuerySchema.safeParse(req.query);
    if (!parsed.success) {
        return res.status(400).json({ error: 'Invalid query string parameters.', details: parsed.error.issues });
    }

    const queryLimit = parsed.data?.limit ? Number(parsed.data.limit) : 50;
    const finalLimit = Math.min(queryLimit, MAX_LIMIT);

    if (isOfflineMode || !db) {
        return res.json({ data: localMemoryMatches.slice(0, finalLimit) });
    }

    try {
        const data = await db.select().from(matches).orderBy(desc(matches.createdAt)).limit(finalLimit);
        return res.json({ data });
    } catch (error) {
        console.warn("⚠️ Database query interrupted. Falling back to local dataset.");
        return res.json({ data: localMemoryMatches.slice(0, finalLimit) });
    }
});

// POST ROUTE
matchRouter.post('/', async (req, res) => {
    const parsed = createMatchSchema.safeParse(req.body);
    if (!parsed.success) {
        return res.status(400).json({ error: 'Invalid payload.', details: parsed.error.issues });
    }

    const { sport, homeTeam, awayTeam, startTime, endTime, homeScore, awayScore } = parsed.data;
    const computedStatus = getMatchStatus(startTime, endTime);

    if (isOfflineMode || !db) {
        const fallbackEvent = {
            id: localMemoryMatches.length + 1,
            sport, homeTeam, awayTeam,
            status: computedStatus || 'scheduled',
            startTime, endTime,
            homeScore: homeScore ?? 0,
            awayScore: awayScore ?? 0,
            createdAt: new Date().toISOString()
        };

        localMemoryMatches.unshift(fallbackEvent);

        if (req.app.locals.broadCastMatchCreated) {
            req.app.locals.broadCastMatchCreated(fallbackEvent);
        }

        return res.status(201).json({ data: fallbackEvent });
    }

    try {
        const [event] = await db.insert(matches).values({
            sport, homeTeam, awayTeam,
            startTime: new Date(startTime),
            endTime: new Date(endTime),
            homeScore: homeScore ?? 0,
            awayScore: awayScore ?? 0,
            status: computedStatus,
        }).returning();

        if (req.app.locals.broadCastMatchCreated) {
            req.app.locals.broadCastMatchCreated(event);
        }

        return res.status(201).json({ data: event });
    } catch (error) {
        console.warn("⚠️ Database Insert failed. Re-routing record to local state memory registry.");
        
        const fallbackEvent = {
            id: localMemoryMatches.length + 1,
            sport, homeTeam, awayTeam,
            status: computedStatus || 'scheduled',
            startTime, endTime,
            homeScore: homeScore ?? 0,
            awayScore: awayScore ?? 0,
            createdAt: new Date().toISOString()
        };

        localMemoryMatches.unshift(fallbackEvent);

        // FIXED: Changed res.app.locals to req.app.locals and broadCastMatchCreated name matching server.js
        if (req.app.locals.broadCastMatchCreated) {
            req.app.locals.broadCastMatchCreated(fallbackEvent);
        }

        return res.status(201).json({ data: fallbackEvent });
    }
});