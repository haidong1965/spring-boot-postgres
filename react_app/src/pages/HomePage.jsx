import '../styles/style.css';
import screendump from '../screendump.jpg';

const HomePage = () => {

  const apiClientUrl = import.meta.env.VITE_API_CLIENT_URL;

  return (
    <div style={{ padding: '20px', textAlign: 'center' }}>
		  <span style={{ paddingLeft: '365px' }}></span><button className="verdana-home-button" 	  onClick={() => {
		            window.location.href = `${apiClientUrl}login`;
		          }}>Login</button>
				  <span style={{ paddingLeft: '25px' }}></span>
		  <button className="verdana-home-button" onClick={() => {
		          window.location.href = `${apiClientUrl}signup`;
		        }}>Sign up
		 </button>
      <h1 className="verdana-h1">Welcome to Dong's' dietary App!</h1>
      <p className="verdana-bold-text">
	  This is a demo application based on Java Spring Boot RESTApi endpoint + React client + Postgresql database. The whole project can be deployed through Docker containers. User need to signup with valid email address.
	  </p><br/>
	  <p  className="verdana-bold-text">This app's function is to track the daily energy, protein, dietary fiber, and calcium intake from your diet.
	  </p><br/>
	  <img src={screendump} alt="Screen dump" />
    </div>
  );
};

export default HomePage;
