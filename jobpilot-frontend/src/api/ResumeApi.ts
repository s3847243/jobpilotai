import axiosInstance from './axiosInstance';
import { Resume } from '../types/Resume';

export const fetchResumes = async (): Promise<Resume[]> => {
  const res = await axiosInstance.get('/resume',{withCredentials: true});

  console.log(res.data);
  return res.data;
};
