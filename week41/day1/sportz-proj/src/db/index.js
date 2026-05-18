// import 'dotenv/config'; // This often fails if not run from root
// import { neon } from '@neondatabase/serverless';
// import { drizzle } from 'drizzle-orm/neon-http';
// import path from 'path';
// import { fileURLToPath } from 'url';

// // Better way to ensure .env is loaded regardless of where you run the script
// import dotenv from 'dotenv';
// const __dirname = path.dirname(fileURLToPath(import.meta.url));
// dotenv.config({ path: path.resolve(__dirname, '../../.env') }); 

// console.log("Checking DB URL:", process.env.DATABASE_URL ? "Found ✅" : "Not Found ❌");

// const sql = neon(process.env.DATABASE_URL);
// export const db = drizzle(sql);

// import 'dotenv/config';
// import { neon } from '@neondatabase/serverless';
// import { drizzle } from 'drizzle-orm/neon-http';

// // 1. Import and activate the global network routing agent
// import { bootstrap } from 'global-agent';
// bootstrap();

// if (!process.env.DATABASE_URL) {
//     throw new Error('DATABASE URL is not defined');
// }

// // 2. Initialize the Neon client over HTTPS (Port 443)
// const sql = neon(process.env.DATABASE_URL);
// export const db = drizzle(sql);

// import 'dotenv/config';
// import { neon } from '@neondatabase/serverless';
// import { drizzle } from 'drizzle-orm/neon-http';

// // 1. Import the official Node fetch network tools
// import { setGlobalDispatcher, ProxyAgent } from 'undici';
// import { execSync } from 'child_process';

// try {
//     // 2. Automatically grab your exact Windows Gateway IP from the Linux route table
//     const windowsIp = execSync("ip route show | grep default | awk '{print $3}'").toString().trim();
    
//     if (windowsIp) {
//         console.log(`🌐 Routing Neon queries through Windows Bridge Proxy: http://${windowsIp}:8080`);
        
//         // 3. Force Node's built-in fetch to pipe traffic directly over the Windows host adapter
//         const proxyAgent = new ProxyAgent({ uri: `http://${windowsIp}:8080` }); 
//         setGlobalDispatcher(proxyAgent);
//     }
// } catch (e) {
//     console.log("⚠️ No proxy configured, continuing with native network layout.");
// }

// if (!process.env.DATABASE_URL) {
//     throw new Error('DATABASE_URL is missing from environment variables');
// }

// const sql = neon(process.env.DATABASE_URL);
// export const db = drizzle(sql);
import 'dotenv/config';
import { drizzle as drizzleServerless } from 'drizzle-orm/neon-serverless';
import ws from 'ws';

if (!process.env.DATABASE_URL) {
    throw new Error('DATABASE_URL is missing from environment variables');
}

// 1. Initialize variables globally at the top level so they are fully visible
let databaseInstance = null;
let isOfflineMode = false;

try {
    // 2. Assign the connection inside the try block
    databaseInstance = drizzleServerless({
        connection: process.env.DATABASE_URL,
        options: { ws }
    });
    console.log("⚡ Neon Serverless WebSocket Client Initialized.");
} catch (error) {
    console.warn("⚠️ Local Network Blocked: Activating isolated offline development mode.");
    isOfflineMode = true;
    databaseInstance = null;
}

// 3. Clean and direct exports
export { databaseInstance as db, isOfflineMode };