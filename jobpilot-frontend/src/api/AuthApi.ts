import axiosInstance, { setAuthToken } from './axiosInstance';

// LOGIN
export const login = async (email: any, password: any) => {
    const res = await axiosInstance.post('/auth/login', { email, password },{withCredentials: true});
    return res.data; 
};
  
  // SIGN UP
export const signUp = async (email:string, password:string, fullName:string, jobTitle:string) => {
    const res = await axiosInstance.post('/auth/register', {email, password, fullName, jobTitle},{withCredentials: true});
    return res.data;
};
  
  // LOGOUT (client-side only)
export const logout = () => {
    setAuthToken(null);
};
  