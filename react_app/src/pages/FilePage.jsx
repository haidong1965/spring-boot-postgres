import '../styles/style.css';

import { useState, useEffect } from 'react';
import apiClient from '../api/apiClient';
import { useNavigate } from 'react-router-dom';
import { getToken, removeToken } from '../utils/authService';


function FilePage() {
    const [file, setFile] = useState(null);
    const [files, setFiles] = useState(null);

    const [message, setMessage] = useState('');
    const [csrfToken, setCsrfToken] = useState('');

    const navigate = useNavigate();
    const apiClientUrl = import.meta.env.VITE_API_CLIENT_URL;

    const handleLogout = () => {
        console.log("--- Initiating Logout Process ---");
        removeToken();
        console.log("Token successfully cleared.");
        navigate('/login', { replace: true });
    };


    console.log('FilePage : start file page');

    useEffect(() => {
        const fetchFilesn = async () => {
            console.log("------------fetchFilesn----------------------");
            try {
                // Make a request to get CSRF token
                const response = await apiClient.get("/file/list", {
                    withCredentials: true
                });
                setFiles(response.data);

                console.log(response.data);
            } catch (error) {
                console.log('CSRF token fetch failed:', error);
            }
        };

        fetchFilesn();
    }, []);


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

    useEffect(() => {
        const token = getToken();
        if (token) {
            console.log(" .................  got token ...................");
        } else {
            console.log(" .................  no token ...................");
        }
        const intervalId = setInterval(async () => {
            try {
                await apiClient.get("/validate");
            } catch (err) {
                console.log("validate failed:", err);
                navigate('/login', { replace: true });
            }
            console.log("Running background token check...");
        }, 30 * 60 * 1000); // Check every 30 minutes
        return () => clearInterval(intervalId); // Cleanup the interval when component unmounts
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
        }
    };

    return (
        <div style={{ padding: '20px', fontFamily: 'sans-serif' }}>
            <p className="verdana-heading">

                <button className="verdana-top-right-button" onClick={() => { window.location.href = `${apiClientUrl}`; }}>Home</button><span style={{ paddingLeft: '20px' }}></span>
                <button onClick={handleLogout} className="verdana-top-right-button">Logout</button>
            </p>
            <h1 className="verdana-h1" >File Upload</h1>
            <form onSubmit={handleSubmit}>
                <input className="styled-file-input"
                    type="file"
                    onChange={handleFileChange}
                />
                <button className="verdana-left-button" type="submit">
                    {'Upload'}
                </button>
            </form>

            {message && <p style={{ color: message.includes('success') ? 'green' : 'red' }}>{message}</p>}

            {file && <p>Selected file: <strong>{file.name}</strong></p>}
            <br />
		
            <ul>
                {files && files.map((myfile, index) => (
                    <li key={index}>{myfile}</li>
                ))}
            </ul>
        </div>
    );
}

export default FilePage;