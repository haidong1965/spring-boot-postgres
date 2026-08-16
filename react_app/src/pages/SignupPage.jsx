import '../styles/style.css';

import  { useState, useEffect } from 'react';
import apiClient from '../api/apiClient'; 


function SignupPage() {
  // 1. State to hold input values
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [csrfToken, setCsrfToken] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState(null); // For displaying errors
  
  console.log('SignupPage : create new user and password');
  
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
                const response = await apiClient.get('/csrf', 
                    {
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
  const handleSubmit = async () => {
    event.preventDefault(); // Prevents the form from doing a default page reload
    if(isValidusername(username)) {  
      if (isPasswordMatch && password && isValidusername(username)) {
        console.log("Form is valid! Submitting with:", { 
          password: password,
          confirm_password: confirmPassword
        });

        setError(null); // Clear previous errors

        try {
          const response = await apiClient.post("/signup", 
                           userData,
                           {
                              headers: {
                                'Content-Type': 'application/json',
                                'X-XSRF-TOKEN': csrfToken // Send CSRF token
                              },
                              withCredentials: true // Important for cookies
                           }
                         );
                        
        
          console.log(response.data); 
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
    <div style={{ maxWidth: '600px', margin: '50px auto', padding: '20px', border: '1px solid #ccc', borderRadius: '8px' }}>
      <h1 className="verdana-h1">Register User</h1>
      
      {/* Display Error Message */}
      {error && <p style={{ color: 'red' }}>Error: {error}</p>}

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="username" className="verdana-lading-label">Username</label>
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
          <label htmlFor="password" className="verdana-lading-label">Password</label>
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
          <label htmlFor="confirmPassword" className="verdana-long-lading-label">Confirm Password</label>
          <input 
            id="confirmPassword" 
            type="password" 
            value={confirmPassword} 
            onChange={(e) => setConfirmPassword(e.target.value)} // Update state on change
            required
            style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
          />
        </div>
        <br/>
        <button type="submit" className="verdana-home-button" >
          Sign up
        </button>
		<br/><br/>
      </form>
    </div>
  );
}

export default SignupPage;
