# Use the official Node.js image
FROM node:26

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the source code
COPY . .

# Build the project
RUN npm run build

# Install serve to serve the app
RUN npm install -g serve

CMD ["node", "--use-system-ca", "node_modules/vite/bin/vite.js", "--host", "--mode", "production"]
