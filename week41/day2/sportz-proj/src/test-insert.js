import { db } from './db/index.js';
import { matches } from './db/schema.js';

async function seed() {
    console.log("🚀 Testing interaction with Neon...");
    try {
        await db.insert(matches).values({
            sport: "Football",
            homeTeam: "Real Madrid",
            awayTeam: "Man City",
            status: "live"
        });
        console.log("✅ Success! Check the Neon website, the data is there!");
    } catch (err) {
        console.error("❌ Connection failed:", err.message);
    }
}
seed();