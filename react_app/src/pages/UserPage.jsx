// src/pages/UserPage.jsx
import '../styles/style.css';
import { useState, useEffect, useCallback, useMemo } from 'react';
import { getToken, removeToken } from '../utils/authService';
import { useNavigate } from 'react-router-dom';
import { useParams } from 'react-router-dom';
import { format } from 'date-fns';
import Select from 'react-select';
import LoginPage from './LoginPage';
import apiClient from '../api/apiClient';
import axios from 'axios';

const UserPage = () => {
    const apiClientUrl = import.meta.env.VITE_API_CLIENT_URL;
    const params = useParams();
    const userIdFromUrl = params.id;
    const [user, setUser] = useState(null);
    const [inputValue, setInputValue] = useState("");
    const [unit, setUnit] = useState("");
    const [selectedValue, setSelectedValue] = useState('');
    const [userFood, setUserFood] = useState('');
    const [foods, setfoods] = useState(null);
    const [csrfToken, setCsrfToken] = useState('');
    const [foodList, setFoodList] = useState([]);
    const [formattedDate, setformattedDate] = useState([format(new Date(), 'yyyy-MM-dd')]);
    const [currentDate, setCurrentDate] = useState([new Date()]);

    const [options, setOptions] = useState([]);

    const navigate = useNavigate();

    const handleLogout = async () => {
		const token = getToken();
        console.log("--- Initiating Logout Process ---" + user.name + " token " + token);
		
		try {
			 const response = await axios.post("https://2.106.164.55:8445/api/auth/logout",  {username: user.name},
				            {
			                 headers: {
			                     'Content-Type': 'application/json',
			                     'Authorization': `Bearer ${token}`,
			                     'X-XSRF-TOKEN': csrfToken
			                 },
			                 withCredentials: true
			             });
			 console.log(response);
			 console.log("--- Post Logout Done ---");
		   } catch (err) {
		     console.error('Logout request failed:', err);
		   }
		
        removeToken();
        console.log("Token successfully cleared.");
        navigate('/login', { replace: true });
    };


    const totals = useMemo(() => {
        const targetDate = String(formattedDate).trim();
        return foodList.reduce(
            (acc, item) => {
                const itemDate = String(item.date).trim();

                if (itemDate === targetDate) {
                    acc.totalItem += 1;
                    acc.totalEnergy += Number(item.energy || 0);
                    acc.totalProtein += Number(item.protein || 0);
                    acc.totalFiber += Number(item.fiber || 0);
                    acc.totalCalcium += Number(item.calcium || 0);
                }

                return acc;
            },
            {
                totalItem: 0,
                totalEnergy: 0,
                totalProtein: 0,
                totalFiber: 0,
                totalCalcium: 0
            }
        );
    }, [foodList, formattedDate]);

    const fetchFoodList = useCallback(async () => {
        try {
            const response = await apiClient.get("/user_food/user/" + userIdFromUrl, {
                withCredentials: true
            });

            // If server sends token in response body
            if (response.data) {
                setFoodList(response.data);
            }
            console.log('foodListn:', response.data);
        } catch (error) {
            console.log('Food list fetch failed:', error);
        }
    }, []);

    useEffect(() => {
        //setCurrentDate(new Date());
        //setformattedDate(format(currentDate, 'yyyy-MM-dd'));   // TODO: fix it later maybe use Query instead of useEffet
        console.log('+++++++++++++++++++++ currentDate:', formattedDate);
        fetchFoodList();   // TODO: fix it later maybe use Query instead of useEffet
    }, [fetchFoodList]);

    const handleAddEntry = async (e) => {
        e.preventDefault();
        const payload = {
            userName: user.name,
            foodName: userFood.name,
            volumn: Number(inputValue),
            unit: Number(userFood.type),
            date: String(formattedDate).trim()
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
        fetchFoodList();
    };

    const handleDateBack = async () => {
        const yesterday = new Date(currentDate);
        yesterday.setDate(yesterday.getDate() - 1);
        console.log(" +++++++ Date : " + format(yesterday, 'yyyy-MM-dd'));
        setCurrentDate(yesterday);
        setformattedDate(format(yesterday, 'yyyy-MM-dd'));
    };

    const handleDateNext = async () => {

        const today = new Date(currentDate);
        today.setDate(today.getDate() + 1);
        console.log(" +++++++ Date : " + format(today, 'yyyy-MM-dd'));
        setCurrentDate(today);
        setformattedDate(format(today, 'yyyy-MM-dd'));
    };


    const handleDeleteEntry = async (userFoodId) => {
        console.log(" +++++++ Deleting id:", userFoodId);
        const response = await apiClient.delete("/user_food/" + userFoodId,
            {
                headers: {
                    'X-XSRF-TOKEN': csrfToken,
                },
                withCredentials: true, // Important for cookies
            });
        console.log("--------------------" + response.status);
        if (response.status == 204) {
            fetchFoodList();
        }
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
        }, 30 * 60 * 1000); // Check every 30 minutes

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
            }
        };

        fetchOptions(); // Call the function
    }, []); // Empty dependency array means it runs once on mount


    if (!user) {
        // If the token isn't found on load, redirect them to the login page.
        console.log("invalid login. Redirecting to Login.");
        // Example: navigate('/login'); 
        return <LoginPage />;
    }

    return (
        <div>
            <div class="right-container">
                <p></p>
            </div>
            <div>
                <p className="verdana-heading">
                    <button onClick={handleDateBack} className="verdana-top-button">&laquo;</button><span style={{ paddingLeft: '20px' }}></span>
                    {formattedDate}<span style={{ paddingLeft: '20px' }}></span>
                    <button onClick={handleDateNext} className="verdana-top-button"> &raquo;</button><span style={{ paddingLeft: '65px' }}></span>
                    <button className="verdana-top-right-button" onClick={() => { window.location.href = `${apiClientUrl}file`; }}>File</button><span style={{ paddingLeft: '20px' }}></span>
                    <button onClick={handleLogout} className="verdana-top-right-button">Logout</button>
                </p>
            </div>
            <div style={{ display: 'flex', alignItems: 'left', textAlign: 'left' }}>
                <label className="verdana-lading-label">Item:</label>
                <div style={{ display: 'inline-block', width: '50px' }}>
                    <Select className="select-verdana"
                        value={options.find(option => option.value === selectedValue) || null}
                        onChange={handleSelectChange}
                        id="Choose"
                        options={options}
                    />
                </div>
            </div>
            <div style={{ display: 'flex', alignItems: 'left', textAlign: 'left' }}>
                <label className="verdana-lading-label">Amount:</label>
                <div style={{ display: 'inline-block', width: '158px' }}>
                    <input className="input-verdana"
                        type="text"
                        value={inputValue}
                        maxLength={10}
                        onChange={(e) => setInputValue(e.target.value)}
                    />
                </div><span style={{ paddingLeft: '50px' }}></span>
                <label className="verdana-text">{unit}</label>
            </div>
            <div className="verdana-div">
                <span style={{ paddingLeft: '80px' }}></span>
                <button onClick={handleAddEntry} className="verdana-button">Add</button>
            </div>
            <br />
            <div>
                <table className="food-table">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Energy</th>
                            <th>Protein</th>
                            <th>Fiber</th>
                            <th>Calcium</th>
                            <th></th>
                        </tr>
                    </thead>
                    {
                        <tbody>
                            {foodList.filter(userfood => userfood.date === String(formattedDate).trim())
                                .map(
                                    (userfood) => (
                                        <tr key={userfood.name}>
                                            <td>{userfood.name}</td>
                                            <td>{userfood.energy}</td>
                                            <td>{userfood.protein}</td>
                                            <td>{userfood.fiber}</td>
                                            <td>{userfood.calcium}</td>
                                            <td><button className="trash-btn" onClick={() => handleDeleteEntry(userfood.id)}>🗑️</button></td>

                                        </tr>
                                    ))}
                        </tbody>
                    }
                </table>
            </div>
            <br />
            <div>
                <table className="food-summary-table">
                    <thead>
                        <tr>
                            <th>Total</th>
                            <th>Energy</th>
                            <th>Protein</th>
                            <th>Fiber</th>
                            <th>Calcium</th>
                            <th>.....</th>
                        </tr>
                    </thead>
                    {
                        <tbody>
                            <tr>
                                <td>{totals.totalItem}</td>
                                <td>{totals.totalEnergy}</td>
                                <td>{totals.totalProtein.toFixed(1)}</td>
                                <td>{totals.totalFiber.toFixed(1)}</td>
                                <td>{totals.totalCalcium.toFixed(1)}</td>
                                <td />
                            </tr>
                        </tbody>
                    }
                </table>
            </div>

        </div>
    );
};

export default UserPage;