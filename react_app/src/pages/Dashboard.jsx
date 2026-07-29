import React, { useState, useEffect } from 'react';
import { getToken, removeToken } from '../utils/authService'; 
import LoginPage from './LoginPage'; // <--- THIS IS THE CRITICAL ADDITION! 
import axios from 'axios'; // Import axios

// Assume useAuthContext provides the global state management hook

function Dashboard() {
  // 1. INITIAL CHECK: Check storage when the component mounts
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    const token = getToken();
	console.log(" .................   Running getToken() ...................");
    if (token) {
      setIsAuthenticated(true);
	  console.log(" .................   setIsAuthenticated(true) ...................");
      // OPTIONAL: If you use a global state manager (e.g., Redux), dispatch an action here
      // dispatch({ type: 'AUTH_SET', payload: token }); 

    } else {
      setIsAuthenticated(false);
    }
  }, []); // Empty dependency array means this runs ONLY ONCE after the first render

  // --- OPTIONAL: Check for Expiry/Stale Token (Advanced) ---
  useEffect(() => {
    const intervalId = setInterval(() => {
        // In a real app, you would hit an /api/validate endpoint here.
        // If that API returns 401 Unauthorized, THEN you know the token is stale.
        console.log("Running background token check...");
        // Example: apiService.validateToken().catch(() => { /* Token expired */ });
    }, 5 * 60 * 1000); // Check every 5 minutes

    return () => clearInterval(intervalId); // Cleanup the interval when component unmounts
  }, []);


  if (!isAuthenticated) {
    // If the token isn't found on load, redirect them to the login page.
    console.log("Token missing or invalid. Redirecting to Login.");
    // Example: navigate('/login'); 
    return <LoginPage />;
  }

  // If we reach here, the user is authenticated (at least by token presence)
  return (
    <div>
      <h1>Welcome Back!</h1>
      <p>You are logged in. Your token is valid.</p>
      {/* ... Rest of your dashboard content */}
    </div>
  );
}

export default Dashboard;
