import '../styles/style.css';

const HomePage = () => {

  const apiClientUrl = import.meta.env.VITE_API_CLIENT_URL;

  return (
    <div style={{ padding: '20px', textAlign: 'center' }}>
      <h1 className="verdana-h1">Welcome to Dong's' dietary App!</h1>

      <br/>
	  <button className="verdana-home-button" 	  onClick={() => {
	            window.location.href = `${apiClientUrl}login`;
	          }}>Login</button>
	  <br/>
	  <br/>

	  <button className="verdana-home-button" onClick={() => {
	          window.location.href = `${apiClientUrl}signup`;
	        }}>Sign up
	 </button>
    </div>
  );
};

export default HomePage;
