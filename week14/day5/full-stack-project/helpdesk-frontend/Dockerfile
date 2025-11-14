# Step 1: Build the app using the official node image
FROM node:16 as build

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy package.json and package-lock.json
COPY package*.json ./

# Step 4: Install dependencies
RUN npm install

# Step 5: Copy the rest of the application
COPY . .

# Step 6: Build the app
RUN npm run build

# Step 7: Use NGINX to serve the built app
FROM nginx:alpine

# Step 8: Copy the build folder from the previous stage to NGINX
COPY --from=build /app/build /usr/share/nginx/html

# Step 9: Expose the port that NGINX will run on
EXPOSE 80

# Step 10: Start NGINX server
CMD ["nginx", "-g", "daemon off;"]
