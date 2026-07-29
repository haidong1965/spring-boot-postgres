// utils/authService.js

const TOKEN_KEY = 'authToken'; // Use a specific, consistent key

export const setToken = (token) => {
  if (token) {
    console.log('...........  setToken  .............');
    localStorage.setItem(TOKEN_KEY, token);
  } else {
    localStorage.removeItem(TOKEN_KEY);
  }
};

export const getToken = () => {
  console.log('...........  getToken  .............');
  return localStorage.getItem(TOKEN_KEY);
};

export const removeToken = () => {
  localStorage.removeItem(TOKEN_KEY);
};
