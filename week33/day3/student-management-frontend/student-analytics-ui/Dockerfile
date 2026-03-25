# --- Stage 1: Build Stage ---
FROM node:22-alpine AS build-stage

WORKDIR /app

# 1. Receive the Backend URL as a build argument
ARG VITE_API_BASE_URL
# 2. Make it available to the Vite build process
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

# Copy package files first (to leverage Docker caching)
COPY package*.json ./
RUN npm install

# Copy the rest of the source code
COPY . .

# Run the production build
RUN npm run build

# --- Stage 2: Production Stage (Nginx) ---
FROM nginx:stable-alpine

# Copy the static build files from Stage 1 to Nginx's serve directory
COPY --from=build-stage /app/dist /usr/share/nginx/html

#  CREATE CUSTOM NGINX CONFIGURATION
# Optimized for React/Vite Router and listening on port 3000
RUN printf 'server { \n\
    listen 3000; \n\
    location / { \n\
        root /usr/share/nginx/html; \n\
        index index.html index.htm; \n\
        try_files $uri $uri/ /index.html; \n\
    } \n\
}' > /etc/nginx/conf.d/default.conf

# Expose port 3000
EXPOSE 3000

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]