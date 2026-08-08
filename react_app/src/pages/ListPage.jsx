// src/pages/ListPage.jsx

import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom'; 
import { setToken } from '../utils/authService'; 
import apiClient from '../api/apiClient'; 

/**
 * This component serves as the base structure for an empty page.
 * You should build out all your content, layout, and logic here.
 */
const ListPage = () => {

  // State to hold the list of users
  const [users, setUsers] = useState([]);
  // State to manage loading status
  const [isLoading, setIsLoading] = useState(true);
  // State to manage any errors during fetching
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const handleLogout = () => {
    console.log("--- Initiating Logout Process ---");

    setToken();
    console.log("Token successfully cleared.");
    navigate('/login', { replace: true });   
  };
  
  useEffect(() => {
    const fetchUsers = async () => {
      setIsLoading(true); // Set loading state to true before the request
      setError(null);    // Clear previous errors
      console.log('++++++++++++++++++++++++++++++++++++++');
      try {
          // *** THE DATA FETCHING CALL GOES HERE ***
        const response = await apiClient.get('/users/list');  
        setUsers(response.data);  
      } catch (fetchError) {
        console.error("Fetching error:", fetchError);
        setError('Failed to load users. Please check the server is running.');
      } finally {
        // This runs regardless of success or failure
        setIsLoading(false);
      }

    };

    fetchUsers();
  }, []); // The empty dependency array [] ensures this runs ONLY ONCE after mounting

  // --- Rendering Logic ---

  if (isLoading) {
    return <div className="list-page">Loading users...</div>;
  }

  if (error) {
    return <div className="list-page error"><p>{error}</p></div>;
  }

  
  
  
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
      <h1>Welcome to This Page!</h1>
      <p>
        This is your new, empty page component. All content specific to this view
        should be placed inside this component's JSX structure.
      </p>

      
      <div className="list-page">
      <h1>User List</h1>
      {users.length === 0 ? (
        <p>No users found in the system.</p>
      ) : (
        <ul>
          {Array.isArray(users) && users.length > 0 ? (
        <ul>
          {users.map((user) => (
            <li key={user.id || user._id}>
              <strong>{user.name}</strong> ({user.email}) - Role: {user.role}
            </li>
          ))}
        </ul>
      ) : (
        !isLoading && !error && <p>No users found in the system.</p>
      )}
        </ul>
      )}
    </div>

    </div>
	
  );
};

export default ListPage;