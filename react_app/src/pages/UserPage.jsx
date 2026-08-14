// src/pages/UserPage.jsx
import '../styles/style.css';
import { useState, useEffect } from 'react';
import { getToken, removeToken } from '../utils/authService';
import { useNavigate } from 'react-router-dom';
import { useParams } from 'react-router-dom';
import { format } from 'date-fns';
import Select from 'react-select';
import LoginPage from './LoginPage';
import apiClient from '../api/apiClient';


/**
 * This component serves as the base structure for an empty page.
 * You should build out all your content, layout, and logic here.
 */
const UserPage = () => {
    const params = useParams();
    const userIdFromUrl = params.id;
    const [user, setUser] = useState(null);
    const [inputValue, setInputValue] = useState("");
    const [unit, setUnit] = useState("");
    const [selectedValue, setSelectedValue] = useState('');
    const [userFood, setUserFood] = useState('');
    const [foods, setfoods] = useState(null);
    const [csrfToken, setCsrfToken] = useState('');
   // const [userfoods, setUserFoods] = useState(null);

    //const [selectedValue, setSelectedValue] = useState("");

    const currentDate = new Date();
    const formattedDate = format(currentDate, 'MMMM dd, yyyy');
    const [options, setOptions] = useState([]);
    const [loading, setLoading] = useState(true);

    const navigate = useNavigate();
    const handleLogout = () => {
        console.log("--- Initiating Logout Process ---");
        removeToken();
        console.log("Token successfully cleared.");
        navigate('/login', { replace: true });
    };


    const handleAddEntry = async (e) => {
        e.preventDefault();
        
        const payload = {
            userName: user.name,
            foodName: userFood.name,
            volumn: Number(inputValue),
            unit: Number(userFood.type),
        };
		
        const response = await apiClient.post("/user_food/add",
			payload, 
			{
			    headers: {
			        'Content-Type': 'application/json',
			        'X-XSRF-TOKEN': csrfToken,
			    },
			    withCredentials: true, // Important for cookies
            });
		console.log("--------------------" + response.data);
		
    };

    const handleSelectChange = (selectedOption) => {
        setSelectedValue(selectedOption.value);
        const selectedFood = foods.find(
            food => food.name === selectedOption.value);
        if (selectedFood.type == 0) {
            setUnit('gram');
        } else if (selectedFood.type == 1) {
            setUnit('pcs');
        } else if (selectedFood.type == 2) {
            setUnit('dish');
        } else {
            setUnit('');
        }
        setUserFood(selectedFood);
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

    // Check for Expiry/Stale Token 
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
        }, 2 * 60 * 1000); // Check every 10 minutes

        return () => clearInterval(intervalId); // Cleanup the interval when component unmounts
    }, []);

    useEffect(() => {
        const fetchUserDetails = async () => {
            if (!userIdFromUrl) {
                console.log("User ID is missing in the URL.");
                return;
            }
            try {
                const response = await apiClient.get(`/users/${userIdFromUrl}`);
                setUser(response.data);
				console.log("++++++++++++++++++++++++++++ user : " + response.data);
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

    useEffect(() => {
        const fetchOptions = async () => {
            try {
                const response = await apiClient.get("/food/list");
                setfoods(response.data);
                //response.data.forEach((food) => {
                //  console.log(food.name, food.energy, food.protein, food.fiber, food.calcium, food.type);
                //});
                const formattedOptions = response.data.map((item) => ({
                    value: item.name,
                    label: item.name
                }));
                setOptions(formattedOptions); // Store options in state
            } catch (error) {
                console.error('Failed to fetch options:', error);
            } finally {
                setLoading(false); // Set loading to false once done
            }
        };

        fetchOptions(); // Call the function
    }, []); // Empty dependency array means it runs once on mount

    if (loading) {
        return <div>Loading options...</div>; // Handle loading state
    }

    if (!user) {
        // If the token isn't found on load, redirect them to the login page.
        console.log("invalid login. Redirecting to Login.");
        // Example: navigate('/login'); 
        return <LoginPage />;
    }

    return (
        <div className="user-profile-card">
            <div style={{ display: 'flex', alignItems: 'left', textAlign: 'left' }}>
                <p className="verdana-heading"> {user?.name} </p> <span style={{ paddingLeft: '200px' }}></span>
                <p className="verdana-heading">{formattedDate}</p><span style={{ paddingLeft: '200px' }}></span>
                <button onClick={handleLogout} className="verdana-button">Logout</button>
            </div>
            <div style={{ display: 'flex', alignItems: 'left', textAlign: 'left' }}>
                <label className="verdana-text">Name:</label><span style={{ paddingLeft: '100px' }}></span>
                <div style={{ display: 'inline-block', width: '200px' }}>
                    <Select className="select-verdana"
                        value={options.find(option => option.value === selectedValue) || null}
                        onChange={handleSelectChange}
                        id="Select Food"
                        options={options}
                    />
                </div>
            </div>
            <div style={{ display: 'flex', alignItems: 'left', textAlign: 'left' }}>
                <label className="verdana-text">Amount:</label><span style={{ paddingLeft: '89px' }}></span>
                <div style={{ display: 'inline-block', width: '158px' }}>
                    <input className="input-verdana"
                        type="text"
                        value={inputValue}
                        maxLength={10}
                        onChange={(e) => setInputValue(e.target.value)}
                    />
                </div><span style={{ paddingLeft: '89px' }}></span>
                <label className="verdana-text">{unit}</label>
            </div>
            <div style={{ display: 'flex', alignItems: 'left', textAlign: 'left' }}>
                <button onClick={handleAddEntry} className="verdana-button">Add Entry</button>
            </div>
            <div style={{ display: 'flex', alignItems: 'left', textAlign: 'left' }}>
                <table className="food-table">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Energy</th>
                            <th>Protein</th>
                            <th>Fiber</th>
                            <th>Calcium</th>
                            <th>Unit</th>
                        </tr>
                    </thead>
                    {/*
				  <tbody>
				    {userfoods.map((userfood) => (
				      <tr key={userfood.name}>
				        <td>{userfood.name}</td>
				        <td>{userfood.energy}</td>
				        <td>{userfood.type}</td>
				      </tr>
				    ))}
				  </tbody>
				  */}
                </table>
            </div>

        </div>
    );
};

export default UserPage;