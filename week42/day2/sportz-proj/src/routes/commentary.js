import { Router } from "express";
import { eq, desc } from "drizzle-orm";
import { matchIdParamSchema } from "../validation/matches.js";
import { createCommentarySchema, listcommentaryQuerySchema } from "../validation/commentary.js";

// FIXED: Importing from the correct database index file containing your WebSocket pool and offline mode configs
import { db, isOfflineMode } from "../db/index.js"; 
import { commentary } from "../db/schema.js";

const MAX_LIMIT = 100;
export const commentaryRouter = Router({ mergeParams: true });

// Local in-memory store fallback to keep your presentation active if the network proxy cuts out
const localMemoryCommentaries = [];

// 1. GET ROUTE
commentaryRouter.get('/', async (req, res) => {
    const paramsResult = matchIdParamSchema.safeParse(req.params);

    if (!paramsResult.success) {
        return res.status(400).json({ error: 'Invalid match ID.', details: paramsResult.error.issues });
    }

    const queryResult = listcommentaryQuerySchema.safeParse(req.query);
    if (!queryResult.success) {
        return res.status(400).json({ error: 'Invalid query parameters.', details: queryResult.error.issues });
    }

    const { id: matchId } = paramsResult.data;
    const { limit = 10 } = queryResult.data;
    const safeLimit = Math.min(limit, MAX_LIMIT);

    // Network Resiliency Fallback
    if (isOfflineMode || !db) {
        const filtered = localMemoryCommentaries
            .filter(c => c.matchId === matchId)
            .slice(0, safeLimit);
        return res.status(200).json({ data: filtered });
    }

    try {
        const results = await db
            .select()
            .from(commentary)
            .where(eq(commentary.matchId, matchId))
            .orderBy(desc(commentary.createdAt))
            .limit(safeLimit);

        return res.status(200).json({ data: results });
    } catch (error) {
        console.error('Failed to fetch commentary from Neon. Falling back to memory:', error);
        const filtered = localMemoryCommentaries
            .filter(c => c.matchId === matchId)
            .slice(0, safeLimit);
        return res.status(200).json({ data: filtered });
    }
});

// 2. POST ROUTE
commentaryRouter.post('/', async (req, res) => {
    const paramsResult = matchIdParamSchema.safeParse(req.params);

    if (!paramsResult.success) {
        return res.status(400).json({ error: 'Invalid match ID.', details: paramsResult.error.issues });
    }

    const bodyResult = createCommentarySchema.safeParse(req.body);

    if (!bodyResult.success) {

        console.error("❌ Seeding Validation Failed! Blocked Schema Issues:", bodyResult.error.issues);
        console.error("Payload received was:", req.body);

        return res.status(400).json({ error: 'Invalid commentary payload.', details: bodyResult.error.issues });
    }

    const { minutes, ...rest } = bodyResult.data;
    const currentMatchId = paramsResult.data.id;

    // Network Resiliency Fallback
    if (isOfflineMode || !db) {
        const fallbackResult = {
            id: localMemoryCommentaries.length + 1,
            matchId: currentMatchId,
            minute: minutes ?? null, // Maps cleanly to schema structure
            ...rest,
            createdAt: new Date().toISOString()
        };

        localMemoryCommentaries.unshift(fallbackResult);

        if (req.app.locals.broadCastMatchCreated) { // Safety hook matching your ws server handler broadcast target
            req.app.locals.broadCastMatchCreated(fallbackResult);
        }

        if(req.app.locals.broadcastCommentary)
        {
            req.app.locals.broadcastCommentary(fallbackResult);
            res.app.locals.broadcastCommentary(currentMatchId,bodyResult);
        }

        return res.status(201).json({ data: fallbackResult });
    }

    try {
        const [result] = await db.insert(commentary).values({
            matchId: currentMatchId,
            minute: minutes, //  FIXED: Mapped correctly to your singular table schema property 'minute'
            ...rest
        }).returning();

        // FIXED: Changed res.app.locals to req.app.locals
        if (req.app.locals.broadcastCommentary) {
            req.app.locals.broadcastCommentary(result.matchId, result);
        }

        return res.status(201).json({ data: result });
    } catch (error) {
        console.error('Failed to create commentary on Neon. Re-routing to offline registry:', error);
        
        const fallbackResult = {
            id: localMemoryCommentaries.length + 1,
            matchId: currentMatchId,
            minute: minutes ?? null,
            ...rest,
            createdAt: new Date().toISOString()
        };

        localMemoryCommentaries.unshift(fallbackResult);

        if (req.app.locals.broadCastMatchCreated) {
            req.app.locals.broadCastMatchCreated(fallbackResult);
        }

        return res.status(201).json({ data: fallbackResult });
    }
});