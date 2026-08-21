import '../styles/style.css';

import { useState , useEffect} from 'react';
import apiClient from '../api/apiClient'; 
import { useNavigate } from 'react-router-dom'; // <-- 1. Import useNavigate
import { setToken } from '../utils/authService'; 


function LoginPage() {
	
  const apiClientUrl = import.meta.env.VITE_API_CLIENT_URL;
	
  // 1. State to hold input values
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [csrfToken, setCsrfToken] = useState('');
  const [error, setError] = useState(null); // For displaying errors
  const navigate = useNavigate(); 
  
  console.log('LoginPage : Attempting login');
  
    // Get CSRF token when component mounts
    useEffect(() => {
        const fetchCsrfToken = async () => {
            try {
                // Make a request to get CSRF token
                const response = await apiClient.get("/csrf", {
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
    e.preventDefault(); // IMPORTANT: Prevents the default browser page reload

    setError(null); // Clear previous errors

    try {
        
        console.log('csrfToken : ' + csrfToken);
        const response = await apiClient.post("/login", {
            name: username,
            password: password
        }, {
            headers: {
                'Content-Type': 'application/json',
                'X-XSRF-TOKEN': csrfToken // Send CSRF token
            },
            withCredentials: true // Important for cookies
        });
      
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
	
    <div style={{ maxWidth: '400px', margin: '50px auto', padding: '20px', borderRadius: '8px' }}>
	    <div>
		    <button className="verdana-top-right-button" onClick={() => { window.location.href = `${apiClientUrl}`;}}>Home</button>
		</div>
		  <br/>
      {/* Display Error Message */}
      {error && <p style={{ color: 'red' }}>Error: {error}</p>}
	  <br/>
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
          <label htmlFor="password"  className="verdana-lading-label">Password</label>
          <input 
            id="password" 
            type="password" 
            value={password} 
            onChange={(e) => setPassword(e.target.value)} // Update state on change
            required
            style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
          />
        </div>
		<br/>
        <button className="verdana-home-button"  type="submit">
          Log In
        </button>
		<br/><br/>
      </form>
    </div>
  );
}

export default LoginPage;
