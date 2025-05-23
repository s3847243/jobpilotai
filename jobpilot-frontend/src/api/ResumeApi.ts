import axiosInstance from './axiosInstance';
import { Resume } from '../types/Resume';
import { JobSummaryDTO } from '../types/JobSummaryDTO';
export const fetchResumes = async (): Promise<Resume[]> => {
  const res = await axiosInstance.get('/resume',{withCredentials: true});

  console.log(res.data);
  return res.data;
};

export const getResumeById = async (resumeId: string): Promise<Resume> => {
  const response = await axiosInstance.get<Resume>(`/resume/${resumeId}`);
  return response.data;
};

export const getJobsByResumeId = async (resumeId: string): Promise<JobSummaryDTO[]> => {
  const response = await axiosInstance.get(`/resume/${resumeId}/jobs`);
  return response.data;
};