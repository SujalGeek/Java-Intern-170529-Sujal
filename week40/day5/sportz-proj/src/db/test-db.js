import { db } from './index.js';
import { matches } from '../schema.js';

async function test() {
    try {
        console.log("🚀 Testing Connection via HTTP (Firewall Bypass)...");
        const result = await db.select().from(matches);
        console.log("✅ Success! Database connected. Row count:", result.length);
    } catch (err) {
        console.error("❌ Connection failed:", err.message);
    }
}

test();