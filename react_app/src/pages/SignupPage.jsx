import React, { useState, useEffect } from 'react';
// Assuming you are using axios or fetch for API calls
import axios from 'axios'; 
import { useNavigate } from 'react-router-dom'; // <-- 1. Import useNavigate

function SignupPage() {
  // 1. State to hold input values
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [csrfToken, setCsrfToken] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState(null); // For displaying errors
  const navigate = useNavigate(); 
  
  console.log('SignupPage : create new user and password');
  
  const csrf = import.meta.env.VITE_API_CSRF;
  const signup = import.meta.env.VITE_API_SIGNUP;
  
  const isValidusername = (username) => {
    if (!username || typeof username !== 'string') {
        return false; // Handle null, undefined, or non-string inputs
    }
    
    // A robust but manageable regex pattern:
    const usernamePattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    
    return usernamePattern.test(username);
};
  
  const isPasswordMatch = password && confirmPassword && (password === confirmPassword);
  
  const userData = {
      username: username, // Keys match the expected JSON structure on the backend
      password: password
    };
    
    // Get CSRF token when component mounts
    useEffect(() => {
        const fetchCsrfToken = async () => {
            try {
                // Make a request to get CSRF token
                const response = await axios.get(csrf, {
                    withCredentials: true
                });
                
                // If server sends token in response body
                if (response.data.token) {
                    setCsrfToken(response.data.token);
                }
                console.log('setCsrfToken:', response.data.token);
            } catch (error) {
                console.log('CSRF token fetch failed:', error);
            }
        };

        fetchCsrfToken();
    }, []);

  // 2. Handler for form submission
  const handleSubmit = async (e) => {
    event.preventDefault(); // Prevents the form from doing a default page reload
    if(isValidusername(username)) {  
      if (isPasswordMatch && password && isValidusername(username)) {
        console.log("Form is valid! Submitting with:", { 
          password: password,
          confirm_password: confirmPassword
        });

        setError(null); // Clear previous errors

        try {
          const response = await axios.post(signup, userData,
                           {
                              headers: {
                                'Content-Type': 'application/json',
                                'X-XSRF-TOKEN': csrfToken // Send CSRF token
                              },
                              withCredentials: true // Important for cookies
                           }
                         );
                        
        
          //setMessage(`Success: ${response.data}`); 
          window.location.replace("/dashboard");
        } catch (err) {
          console.log('Signup failed : ', err.response);
          if (err.response && err.response.data && err.response.data.message) {
            setError(err.response.data.message); // Display backend error message
          } else {
            setError('Could not connect to the authentication server.');
          }
        }
      } else {
        alert("Passwords do not match or fields are empty.");
      } 
    } else {
         alert("Invalid email address.");
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: '50px auto', padding: '20px', border: '1px solid #ccc', borderRadius: '8px' }}>
      <h2>Register User</h2>
      
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
        <div style={{ marginBottom: '20px' }}>
          <label htmlFor="confirmPassword" style={{ display: 'block', marginBottom: '5px' }}>Confirm Password</label>
          <input 
            id="confirmPassword" 
            type="password" 
            value={confirmPassword} 
            onChange={(e) => setConfirmPassword(e.target.value)} // Update state on change
            required
            style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
          />
        </div>
        
        <button type="submit" style={{ width: '100%', padding: '10px', backgroundColor: '#007bff', color: 'white', border: 'none', cursor: 'pointer' }}>
          Sign up
        </button>
      </form>
    </div>
  );
}

export default SignupPage;
