import { Router } from "express";
import { createMatchSchema } from "../validation/matches.js";
import { matches } from "../db/schema.js";
import {db} from '../db/index.js'
import { getMatchStatus } from "../utils/match-status.js";

export const matchRouter = Router();

matchRouter.get('/',(req,res)=>{
    res.status(200).json({
        message:'Matches List'
    })
});

matchRouter.post('/', async (req,res)=>{
    const parsed = createMatchSchema.safeParse(req.body);
    const {
        data :{
            startTime,endTime,homeScore,awayScore
        }
    } = parsed;

    if(!parsed.success)
    {
            return res.status(400).json(
                {
                    error:'Invalid payload.',
                    details: JSON.stringify(parsed.error)
                }
            );
    }


    try {
        
        const [event] = await db.insert(matches).values({
            ...parsed.data,
            startTime: new Date(startTime),
            endTime: new Date(endTime),
            homeScore: homeScore ?? 0,
            awayScore: awayScore ?? 0,
            status: getMatchStatus(startTime,endTime),
        }).returning();

        res.status(201).json({
            data: event
        });


        
    } catch (error) {
        res.status(500).json({
            error:'Failed to create match.',
            details: JSON.stringify(error)
        });
    }
})
