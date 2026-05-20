import 'dotenv/config';
import { defineConfig } from 'drizzle-kit';

export default defineConfig({
    schema: "./src/db/schema.js",
    out: "./drizzle",
    dialect: "postgresql",
    dbCredentials: {
        url: process.env.DATABASE_URL,
    },
    // Adding this can help drizzle-kit understand the environment
    verbose: true,
    strict: true,
});