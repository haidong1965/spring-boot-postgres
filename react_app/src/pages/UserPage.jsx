// src/pages/UserPage.jsx

import { useState, useEffect } from 'react';
import { getToken, removeToken } from '../utils/authService'; 
import { useNavigate } from 'react-router-dom'; 
import { useParams } from 'react-router-dom';
import LoginPage from './LoginPage'; 
import apiClient from '../api/apiClient'; 


/**
 * This component serves as the base structure for an empty page.
 * You should build out all your content, layout, and logic here.
 */
const UserPage = () => {

  const params = useParams();
  const userIdFromUrl = params.id; // This will be the 'id' part of /users/:id

  // State to hold the fetched user data (or null initially)
  const [user, setUser] = useState(null);

  const navigate = useNavigate();
  const handleLogout = () => {
    console.log("--- Initiating Logout Process ---");

    removeToken();
    console.log("Token successfully cleared.");
    navigate('/login', { replace: true });   
  };
  

  useEffect(() => {
    const token = getToken();
	console.log(" .................   Running getToken() ...................");
    if (token) {
	  console.log(" .................  got token ...................");
    } else {
		console.log(" .................  no token ...................");
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
        console.log("User ID is missing in the URL.");
        return;
      }

      try {
        // 4. *** CRITICAL CORRECTION HERE ***: Use template literals to inject the 'id' variable
        const response = await apiClient.get(`/users/${userIdFromUrl}`); 
        
        // Assuming your API returns the user object directly in the response data
        setUser(response.data); 
		console.log("................... " + response.data.name + " ....... " + response.data.role);

      } catch (err) {
        console.error("Failed to fetch user details:", err);
        console.log("Could not load user profile. Please try again later.");
        setUser(null); // Clear any previous successful state on failure
      } finally {
        // This runs whether the request succeeded or failed
      }
    };

    fetchUserDetails();

  // 5. Dependency Array: The effect MUST re-run whenever 'id' changes (e.g., navigating from /users/1 to /users/2)
  }, [userIdFromUrl]); 


  if (!user) {
    // If the token isn't found on load, redirect them to the login page.
    console.log("invalid login. Redirecting to Login.");
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