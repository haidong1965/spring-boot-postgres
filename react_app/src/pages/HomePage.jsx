import React from 'react';

function HomePage() {
  // TEMPORARY FIX: Put something visible here!
  return (
    <div style={{ padding: '20px', textAlign: 'center' }}>
      <h1>Welcome to the App!</h1>
      <a href="https://192.168.1.81:5174/login" target="_self">
         Login
      </a>
      <br/>
      <a href="https://192.168.1.81:5174/signup" target="_self">
         Sign up 
      </a>
    </div>
  );
}

export default HomePage;
