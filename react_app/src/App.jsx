import { BrowserRouter, Routes, Route } from 'react-router-dom';

// --- IMPORT YOUR PAGES HERE ---
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';  
import HomePage from './pages/HomePage';   // Assuming you created this file in Step 1
import UserPage from './pages/UserPage'; 
import ListPage from './pages/ListPage'; 
import FilePage from './pages/FilePage'; 
import Dashboard from './pages/Dashboard'; // <-- Import the new component!

function App() {
  return (
    <BrowserRouter>
      <Routes>
         {/* Root Path: For the landing page */}
        <Route path="/" element={<HomePage />} /> 

        {/* Login Path */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        
        {/* ? NEW DASHBOARD ROUTE HERE ? */}
        <Route path="/dashboard" element={<Dashboard />} /> 
        
        <Route path="/users/:id" element={<UserPage />} />
        <Route path="/admin" element={<ListPage />} />
		
		<Route path="/file" element={<FilePage />} />

        <Route path="*" element={<h1>404 - Page Not Found</h1>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
