// src/pages/ListPage.jsx

import { useState, useEffect, useCallback } from 'react';
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

    // State to manage any errors during fetching
    const [error, setError] = useState(null);

    const [csrfToken, setCsrfToken] = useState('');

    const navigate = useNavigate();

    const fetchUsers = useCallback(async () => {
        try {
            const response = await apiClient.get('/users/list');
            setUsers(response.data);
        } catch (fetchError) {
            console.error("Fetching error:", fetchError);
            setError('Failed to load users. Please check the server is running.');
        }

    }, []);


    const handleLogout = () => {
        console.log("--- Initiating Logout Process ---");

        setToken();
        console.log("Token successfully cleared.");
        navigate('/login', { replace: true });
    };


    const handleDeleteUser = async (userId) => {
        console.log(" +++++++ Deleting id:", userId);
        const response = await apiClient.delete("/delete/" + userId,
            {
                headers: {
                    'X-XSRF-TOKEN': csrfToken,
                },
                withCredentials: true, // Important for cookies
            });
        console.log("--------------------" + response.status);
        if (response.status == 204) {
            fetchUsers();
        }
    };

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
        fetchUsers();   // TODO: fix it later maybe use Query instead of useEffet
    }, [fetchUsers]);

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
            <h1>Welcome to ADMIN Page</h1>



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
                                        <strong>{user.name}</strong> ({user.email}) - Role: {user.role} <button className="trash-btn" onClick={() => handleDeleteUser(user.id)}>🗑️</button>
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            !error && <p>No users found in the system.</p>
                        )}
                    </ul>
                )}
            </div>

        </div>

    );
};

export default ListPage;