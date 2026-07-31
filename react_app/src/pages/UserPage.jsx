// src/pages/UserPage.jsx

import React, { useState, useEffect } from 'react';
import { getToken, removeToken } from '../utils/authService'; 
import LoginPage from './LoginPage'; 
import axios from 'axios'; // Import axios
import { Link, useNavigate } from 'react-router-dom'; 
import apiClient from '../api/apiClient'; 
import { useParams } from 'react-router-dom';


/**
 * This component serves as the base structure for an empty page.
 * You should build out all your content, layout, and logic here.
 */
const UserPage = () => {

  const params = useParams();
  const userIdFromUrl = params.id; // This will be the 'id' part of /users/:id

  // State to hold the fetched user data (or null initially)
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const navigate = useNavigate();
  const handleLogout = () => {
    console.log("--- Initiating Logout Process ---");

    removeToken();
    console.log("Token successfully cleared.");
    navigate('/login', { replace: true });   
  };
  
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

  useEffect(() => {
    // 3. Define the async fetching function inside useEffect
    const fetchUserDetails = async () => {
      if (!userIdFromUrl) {
        setError("User ID is missing in the URL.");
        setIsLoading(false);
        return;
      }

      setIsLoading(true);

      try {
        // 4. *** CRITICAL CORRECTION HERE ***: Use template literals to inject the 'id' variable
        const response = await apiClient.get(`/users/${userIdFromUrl}`); 
        
        // Assuming your API returns the user object directly in the response data
        setUser(response.data); 
		console.log("................... " + response.data.name + " ....... " + response.data.role);

      } catch (err) {
        console.error("Failed to fetch user details:", err);
        setError("Could not load user profile. Please try again later.");
        setUser(null); // Clear any previous successful state on failure
      } finally {
        // This runs whether the request succeeded or failed
        setIsLoading(false);
      }
    };

    fetchUserDetails();

  // 5. Dependency Array: The effect MUST re-run whenever 'id' changes (e.g., navigating from /users/1 to /users/2)
  }, [userIdFromUrl]); 


  if (!isAuthenticated) {
    // If the token isn't found on load, redirect them to the login page.
    console.log("Token missing or invalid. Redirecting to Login.");
    // Example: navigate('/login'); 
    return <LoginPage />;
  }
  
   return (
    <div className="user-profile-card">
	  <button 
        onClick={handleLogout} 
        className="logout-button" // Add a class for styling purposes
        style={{ marginBottom: '25px', padding: '10px 20px', cursor: 'pointer' }}
      >
        Logout
      </button>
      <h2>User Profile Details</h2>
      <p><strong>Name:</strong> {user?.name}</p>
      <p><strong>Role:</strong> {user?.role}</p>
    </div>
  );
};

export default UserPage;