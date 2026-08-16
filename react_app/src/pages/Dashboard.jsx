import '../styles/style.css';

function Dashboard() {

    const apiClientUrl = import.meta.env.VITE_API_CLIENT_URL;

    return (
        <div>
            <h1 className="verdana-h1">Please check your email to confirm the new user registration.</h1>


            <br />
            <button className="verdana-home-button" onClick={() => {
                window.location.href = `${apiClientUrl}login`;
            }}>Login</button>
            <br />
            <br />
        </div>
    );
}

export default Dashboard;
