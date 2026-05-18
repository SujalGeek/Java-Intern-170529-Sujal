// import arcjet from 'arcjet';
// import { detectBot, shield, slidingWindow } from "@arcjet/node";

// const arcjetKey = process.env.ARJECT_KEY;
// const arcjetMode = process.env.ARCJECT_MODE == - 'DRY_RUN' ? 'DRY_RUN' : 'LIVE';


// if (!arcjetKey) {
//     throw new Error("ARCJECT_KEY envirnonment variable is missing")
// }

// export const httpArcjet = arcjetKey ?
//     arcjet({
//         key: arcjetKey,
//         rules: [
//             shield({
//                 mode: arcjetMode
//             }),
//             detectBot({
//                 mode: arcjetMode,
//                 allow: ['CATEGORY:SEARCH_ENGINE', 'CATEGORY:PREVIEW']
//             }),
//             slidingWindow({
//                 mode: arcjetMode,
//                 interval: '10s',
//                 max: 50
//             })
//         ],
//     }) : null;


// export const wsArcjet = arcjetKey ?
//     arcjet({
//  key: arcjetKey,
//         rules: [
//             shield({
//                 mode: arcjetMode
//             }),
//             detectBot({
//                 mode: arcjetMode,
//                 allow: ['CATEGORY:SEARCH_ENGINE', 'CATEGORY:PREVIEW']
//             }),
//             slidingWindow({
//                 mode: arcjetMode,
//                 interval: '2s',
//                 max: 5
//             })
//         ],
//     }) : null;

// export function securityMiddleware(){
//     return async(req,res,next)=>{
//         if(!httpArcjet) return next();

//         try {
//             const decision = await httpArcjet.protect(req);

//             if(!decision.isDenied())
//             {
//                 if(decision.reason.isRateLimit())
//                 {
//                     return res.status(429).json({
//                         error:'Too many requests'
//                     });
//                 }

//                 return res.status(403).json({
//                     error:'Forbidden'
//                 });
//             }
//         } catch (error) {
//             console.error('Arcjet Unavailable error',error);
//             return res.status(503).json({
//                 error: 'Service Unavailable'
//             });
//         }
//         next();
//     }
// }

import arcjet from "@arcjet/node"; // Ensure correct import target
import { detectBot, shield, slidingWindow } from "@arcjet/node";

// Standardized spelling map matching your .env file keys
const arcjetKey = process.env.ARJECT_KEY; 
const arcjetMode = process.env.ARCJET_MODE === 'DRY_RUN' ? 'DRY_RUN' : 'LIVE';

if (!arcjetKey) {
    throw new Error("ARCJET_KEY environment variable is missing");
}

export const httpArcjet = arcjet({
    key: arcjetKey,
    rules: [
        shield({ mode: arcjetMode }),
        detectBot({
            mode: arcjetMode,
            allow: ['CATEGORY:SEARCH_ENGINE', 'CATEGORY:PREVIEW']
        }),
        slidingWindow({
            mode: arcjetMode,
            interval: '10s',
            max: 50
        })
    ],
});

export const wsArcjet = arcjet({
    key: arcjetKey,
    rules: [
        shield({ mode: arcjetMode }),
        detectBot({
            mode: arcjetMode,
            allow: ['CATEGORY:SEARCH_ENGINE', 'CATEGORY:PREVIEW']
        }),
        slidingWindow({
            mode: arcjetMode,
            interval: '2s',
            max: 5
        })
    ],
});

export function securityMiddleware() {
    return async (req, res, next) => {
        if (!httpArcjet) return next();

        try {
            const decision = await httpArcjet.protect(req);

            // FIXED: Removed the exclamation mark toggle trap
            if (decision.isDenied()) {
                if (decision.reason.isRateLimit()) {
                    return res.status(429).json({
                        error: 'Too many requests'
                    });
                }

                return res.status(403).json({
                    error: 'Forbidden'
                });
            }
        } catch (error) {
            console.error('Arcjet Unavailable network timeout error:', error);
            // Fail open: don't let a corporate firewall dropout break your development loop
            return next(); 
        }
        next();
    };
}