// vite.config.js
import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

import fs from 'fs'
import path from 'path'

const SSL_DIR = path.join(__dirname, 'certs'); // Create a 'certs' directory within your project

const apiClientPort = process.env.VITE_API_URL_CLIENT_PORT

export default defineConfig( ({ mode }) => {

  const env = loadEnv(mode, process.cwd(), '');
  
  return {
    plugins: [react()],
    server: {
      port: parseInt(env.VITE_API_URL_CLIENT_PORT, 10),
      host: '0.0.0.0',
      https: {
        key: fs.readFileSync(path.join(SSL_DIR, 'server_key.pem')),
        cert: fs.readFileSync(path.join(SSL_DIR, 'server_cert.pem')),
      },
    },
  };
});


