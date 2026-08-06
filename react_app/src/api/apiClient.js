import axios from 'axios';
import { getToken } from '../utils/authService'; 
// 1. Create an instance configured with your base URL
const apiClient = axios.create({
  baseURL: 'https://192.168.1.81:8444', // <-- CHANGE THIS to your actual backend URL
  headers: {
    'Content-Type': 'application/json',
  },
});

// 2. THE INTERCEPTOR: This runs BEFORE every request is sent
apiClient.interceptors.request.use(
  (config) => {
    // --- Retrieve the Token ---
    // IMPORTANT: Replace this with your actual method of token retrieval!
    const token = getToken(); 

    if (token) {
      // Set the Authorization header for every request
      config.headers['Authorization'] = `Bearer ${token}`;
    } else {
      console.warn("Authentication Token not found. Request might fail.");
    }
    return config;
  },
  (error) => {
    // Handle request error (e.g., if the token retrieval failed)
    return Promise.reject(error);
  }
);

// 3. Response Interceptor (Optional but recommended for handling logout)
apiClient.interceptors.response.use(
    (response) => response, // If successful, pass the response through
    (error) => {
        // Check if the error indicates unauthorized access (usually HTTP 401)
        if (error.response && error.response.status === 401) {
            console.error("Token expired or invalid. Logging out user.");
            // --- CRITICAL SECURITY STEP ---
            localStorage.removeItem('accessToken'); // Clear the token
            // Redirect the user to the login page
            window.location.href = '/login'; 
        }
        return Promise.reject(error);
    }
);

export default apiClient;
