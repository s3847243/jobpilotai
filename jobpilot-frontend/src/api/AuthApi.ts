import axiosInstance, { setAuthToken } from './axiosInstance';

// LOGIN
export const login = async (email: any, password: any) => {
    const res = await axiosInstance.post('/auth/login', { email, password },{withCredentials: true});
    console.log(res.data);
    return res.data; 
};
  
  // SIGN UP
export const signUp = async (email:string, password:string, fullName:string, jobTitle:string) => {
    const res = await axiosInstance.post('/auth/register', {email, password, fullName, jobTitle},{withCredentials: true});
    return res.data;
};
  
  // LOGOUT (client-side only)
export const logout = async () => {
  const res = await axiosInstance.post('/auth/logout', {}, { withCredentials: true });
  return res.data;
};

export const updateUserProfile = async (data: {
  name?: string;
  location?: string;
  jobTitle?: string;
  phone?: string;
}) => {
  const response = await axiosInstance.patch('/user/profile', data, { withCredentials: true });
  return response.data;
};

export const deleteAccount = async () => {
  const response = await axiosInstance.delete('/user/delete', { withCredentials: true });
  return response.data;
};

export const getCurrentUser = async ()=> {
  const response = await axiosInstance.get('/user/me', {
    withCredentials: true,
  });
  return response.data;
};