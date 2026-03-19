/** @type {import('tailwindcss').Config} */
export default {
  //  ESSENTIAL: Enables class-based dark mode switching
  darkMode: 'class', 
  
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  
  theme: {
    extend: {
      colors: {
        // --- Custom Brand Palette ---
        indigo: {
          50: '#eef2ff',
          100: '#e0e7ff',
          500: '#6366f1', // Main EduPulse Indigo
          600: '#4f46e5',
          700: '#4338ca',
          900: '#1e1b4b',
        },
        slate: {
          50: '#f8fafc',
          100: '#f1f5f9',
          800: '#1e293b', // Sidebar Dark
          900: '#0f172a', // Main Background Dark
        },
        // --- Kept your 'brand' key for compatibility ---
        brand: {
          50: '#383d53',
          100: '#ebf0fe',
          600: '#4f46e5', 
          700: '#4338ca',
          900: '#1e1b4b',
        }
      },
      // --- Senior UI Touch: Custom Border Radius & Animation ---
      borderRadius: {
        '4xl': '2rem',
        '5xl': '3rem',
      },
      animation: {
        'pulse-slow': 'pulse 4s cubic-bezier(0.4, 0, 0.6, 1) infinite',
      }
    },
  },
  plugins: [],
}