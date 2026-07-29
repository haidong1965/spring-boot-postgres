import React, { useState } from 'react';
// Assuming you are using axios or fetch for API calls
import axios from 'axios'; 
import { useNavigate } from 'react-router-dom'; // <-- 1. Import useNavigate
import { setToken } from '../utils/authService'; 

function LoginPage() {
  // 1. State to hold input values
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null); // For displaying errors
  const navigate = useNavigate(); 
  
  console.log('LoginPage : Attempting login');

  // 2. Handler for form submission
  const handleSubmit = async (e) => {
    e.preventDefault(); // IMPORTANT: Prevents the default browser page reload

    setError(null); // Clear previous errors
   

    try {
      const response = await axios.post('https://192.168.1.81:8443/api/login', 
                                 { 
                                   name: username, 
                                   password: password 
                                 }
                                );
        if(response.data.success === true) {
          console.log('Login Successful:', response);
          setToken(response.data.token);
          navigate(response.data.redirectUrl);
        } else {
            console.log('Login failed:', response);
        }
    } catch (err) {
      console.log('Login failed : ', err.response);
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message); // Display backend error message
      } else {
        setError('Could not connect to the authentication server.');
      }
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: '50px auto', padding: '20px', border: '1px solid #ccc', borderRadius: '8px' }}>
      <h2>Login</h2>
      
      {/* Display Error Message */}
      {error && <p style={{ color: 'red' }}>Error: {error}</p>}

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="username" style={{ display: 'block', marginBottom: '5px' }}>Username</label>
          <input 
            id="username" 
            type="text" 
            value={username} 
            onChange={(e) => setUsername(e.target.value)} // Update state on change
            required
            style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
          />
        </div>

        <div style={{ marginBottom: '20px' }}>
          <label htmlFor="password" style={{ display: 'block', marginBottom: '5px' }}>Password</label>
          <input 
            id="password" 
            type="password" 
            value={password} 
            onChange={(e) => setPassword(e.target.value)} // Update state on change
            required
            style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
          />
        </div>
        
        <button type="submit" style={{ width: '100%', padding: '10px', backgroundColor: '#007bff', color: 'white', border: 'none', cursor: 'pointer' }}>
          Log In
        </button>
      </form>
    </div>
  );
}

export default LoginPage;
