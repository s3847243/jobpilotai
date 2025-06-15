import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://13.210.143.209:8080/api',
  withCredentials: true, 
  headers: {
    'Content-Type': 'application/json',
  } 
});
let isRefreshing = false;
let failedQueue: any[] = [];

const processQueue = (error: any, tokenRefreshed: boolean) => {
  failedQueue.forEach(prom => {
    tokenRefreshed ? prom.resolve() : prom.reject(error);
  });
  failedQueue = [];
};

axiosInstance.interceptors.response.use(
  res => res,
  async error => {
    console.log("🔍 Axios interceptor triggered", error.response?.status);
    const originalRequest = error.config;
    console.log(error.response);
    // If token expired & not already retried
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then(() => axiosInstance(originalRequest));
      }

      isRefreshing = true;

      try {
        // 🔁 Request to refresh the access token (refresh token is sent automatically via HttpOnly cookie)
        await axios.post(`${import.meta.env.VITE_API_BASE_URL}/auth/refresh`, {}, { withCredentials: true });
        console.log("something happened")
        processQueue(null, true);
        return axiosInstance(originalRequest); // 🔁 Retry original
      } catch (err) {
        processQueue(err, false);
        // 🚨 Optionally redirect to login
        return Promise.reject(err);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);
export const setAuthToken = (token: any) => {
  if (token) {
    axiosInstance.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  } else {
    delete axiosInstance.defaults.headers.common['Authorization'];
  }
};

export default axiosInstance;