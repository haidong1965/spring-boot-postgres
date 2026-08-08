
const HomePage = () => {

  const apiClientUrl = import.meta.env.VITE_API_CLIENT_URL;

  return (
    <div style={{ padding: '20px', textAlign: 'center' }}>
      <h1>Welcome to the App!</h1>
      <a href={apiClientUrl + 'login'} target="_self">
         Login
      </a>
      <br/>
      <a href={apiClientUrl + 'signup'} target="_self">
         Sign up 
      </a>
    </div>
  );
};

export default HomePage;
