// src/pages/EmptyPage.jsx

import React from 'react';
import { Link, useNavigate } from 'react-router-dom'; 
import { setToken } from '../utils/authService'; 

/**
 * This component serves as the base structure for an empty page.
 * You should build out all your content, layout, and logic here.
 */
const EmptyPage = () => {

  const navigate = useNavigate();
  const handleLogout = () => {
    console.log("--- Initiating Logout Process ---");

    setToken();
    console.log("Token successfully cleared.");
    navigate('/login', { replace: true });   
  };
  
   return (
    <div className="page-container">
      {/* --- LOGOUT BUTTON ADDITION --- */}
      <button 
        onClick={handleLogout} 
        className="logout-button" // Add a class for styling purposes
        style={{ marginBottom: '25px', padding: '10px 20px', cursor: 'pointer' }}
      >
        Logout
      </button>

      {/* Main Content */}
      <h1>Welcome to This Page!</h1>

      <p>
        This is your new, empty page component. All content specific to this view
        should be placed inside this component's JSX structure.
      </p>

      {/* Example of internal navigation using React Router Link */}
      <div className="navigation-links">
        <h3>Go Somewhere Else:</h3>
        <Link to="/users/2" style={{ marginRight: '15px', display: 'inline-block' }}>
          View User 2 Profile (Example)
        </Link>
        {/* Add more internal links here */}
      </div>

    </div>
  );
};

export default EmptyPage;