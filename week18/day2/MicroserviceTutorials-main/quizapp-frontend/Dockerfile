# ----------- 1) BUILD STAGE -----------
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files first (better caching)
COPY package.json package-lock.json ./

# Install dependencies
RUN npm install

# Copy the rest of the source code
COPY . .

# Build the project (Vite creates the 'dist' folder)
RUN npm run build


# ----------- 2) RUNTIME STAGE -----------
FROM nginx:alpine

# Copy the build output from the previous stage to Nginx's html folder
COPY --from=builder /app/dist /usr/share/nginx/html

# (Optional) Copy custom nginx config if you handle React Router issues later
# COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
