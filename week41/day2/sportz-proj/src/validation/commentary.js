import { z } from 'zod';

export const listcommentaryQuerySchema = z.object({
    // limit: z.coerce().int().positive().max(100).optional(),
    limit: z.coerce.number().int().positive().max(100).optional(),
});

export const createCommentarySchema = 
z.object({
    minutes: z.number().int().nonnegative().optional(),
    sequence: z.number().int().optional(),
    period: z.string().optional(),
    eventType: z.string().optional(),
    actor: z.string().optional(),
    team: z.string().optional(),
    message: z.string().min(1),
    metadata: z.record(z.string(),z.any()).optional(),
    tags: z.array(z.string()).optional(),
});