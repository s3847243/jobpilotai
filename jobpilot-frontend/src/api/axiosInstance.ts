import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true, // ✅ this is required for sending cookies
  headers: {
    'Content-Type': 'application/json',
  } // ✅ add this here only
});

export const setAuthToken = (token: any) => {
  if (token) {
    axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  } else {
    delete axiosInstance.defaults.headers.common['Authorization'];
  }
};

export default axiosInstance;