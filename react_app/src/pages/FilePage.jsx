import '../styles/style.css';

import { useState , useEffect} from 'react';
import apiClient from '../api/apiClient'; 


function FilePage() {
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [message, setMessage] = useState('');
  const [csrfToken, setCsrfToken] = useState('');

  console.log('FilePage : start file page');
    
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
   
  
  
  // 1. Handle file selection
  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
    }
  };

  // 2. Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!file) {
      setMessage('Please select a file first.');
      return;
    }

    setUploading(true);
    setMessage('');

    // 3. Create FormData and append the file
    const formData = new FormData();
    // 'myFile' is the key name your backend will look for
    formData.append('myFile', file); 

    try {
      // 4. Send the request
	  const response = await apiClient.post("/file/upload", formData, {
				headers: {
					 'Content-Type': 'multipart/form-data', 
	                 'X-XSRF-TOKEN': csrfToken // Send CSRF token
	             },
	             withCredentials: true 
	         });
	       
      

      if (response.data) {
        setMessage(response.data);
      } else {
        setMessage('Upload failed.');
      }
    } catch (error) {
      console.error('Error uploading file:', error);
      setMessage('An error occurred during upload.');
    } finally {
      setUploading(false);
    }
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'sans-serif' }}>
      <h2  >Upload a File</h2>
      <form onSubmit={handleSubmit}>
        <input 
          type="file" 
          onChange={handleFileChange} 
          disabled={uploading}
        />
        <button type="submit" disabled={uploading || !file}>
          {uploading ? 'Uploading...' : 'Upload'}
        </button>
      </form>
      
      {message && <p style={{ color: message.includes('success') ? 'green' : 'red' }}>{message}</p>}
      
      {file && <p>Selected file: <strong>{file.name}</strong></p>}
    </div>
  );
}

export default FilePage;