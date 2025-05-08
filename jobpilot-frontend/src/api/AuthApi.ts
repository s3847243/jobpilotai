import axiosInstance, { setAuthToken } from './axiosInstance';

// LOGIN
export const login = async (email: any, password: any) => {
    const res = await axiosInstance.post('/auth/login', { email, password },{withCredentials: true});
    return res.data; 
};
  
  // SIGN UP
export const signUp = async (userDetails: any) => {
    const res = await axiosInstance.post('/auth/register', userDetails);
    return res.data;
};
  
  // LOGOUT (client-side only)
export const logout = () => {
    setAuthToken(null);
};
  