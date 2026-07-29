// vite.config.js
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

import fs from 'fs'
import path from 'path'

const SSL_DIR = path.join(__dirname, 'certs'); // Create a 'certs' directory within your project

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
	host: '0.0.0.0',
    https: {
      key: fs.readFileSync(path.join(SSL_DIR, 'server_key.pem')),
      cert: fs.readFileSync(path.join(SSL_DIR, 'server_cert.pem')),
    },
  },
});


