// api/axiosInstance.js
import axios from 'axios';
import { getToken } from '../utils/authService'; // Import our storage helper

const apiClient = axios.create({
  baseURL: 'https://192.168.1.81:8143/api', 
});

// --- THE INTERCEPTOR MAGIC HAPPENS HERE ---
clientApiClient.interceptors.request.use(
  (config) => {
    // 1. Get the token from storage
    const token = getToken();

    if (token) {
      // 2. If a token exists, attach it to the Authorization header
      config.headers.Authorization = `Bearer ${token}`;
    } else {
        console.warn("No token found. API call might fail or require re-login.");
    }
    return config;
  },
  (error) => {
    // Handle request errors here if needed
    return Promise.reject(error);
  }
);

export default apiClient;
