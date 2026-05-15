import 'dotenv/config'; // This often fails if not run from root
import { neon } from '@neondatabase/serverless';
import { drizzle } from 'drizzle-orm/neon-http';
import path from 'path';
import { fileURLToPath } from 'url';

// Better way to ensure .env is loaded regardless of where you run the script
import dotenv from 'dotenv';
const __dirname = path.dirname(fileURLToPath(import.meta.url));
dotenv.config({ path: path.resolve(__dirname, '../../.env') }); 

console.log("Checking DB URL:", process.env.DATABASE_URL ? "Found ✅" : "Not Found ❌");

const sql = neon(process.env.DATABASE_URL);
export const db = drizzle(sql);