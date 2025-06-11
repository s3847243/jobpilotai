import axiosInstance from './axiosInstance';
import { Resume } from '../types/Resume';
import { JobSummaryDTO } from '../types/JobSummaryDTO';
export const fetchResumes = async (): Promise<Resume[]> => {
  const res = await axiosInstance.get('/resume',{withCredentials: true});
  return res.data;
};

export const getResumeById = async (resumeId: string): Promise<Resume> => {
  const response = await axiosInstance.get<Resume>(`/resume/${resumeId}`);
  return response.data;
};

export const getJobsByResumeId = async (resumeId: string): Promise<JobSummaryDTO[]> => {
  const response = await axiosInstance.get(`/job/jobs/${resumeId}`);
  return response.data;
};


export const uploadResume = async (file: File) => {
  const formData = new FormData();
  formData.append('file', file);

  const response = await axiosInstance.post('/resume/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });

  return response.data;
};

export const deleteResumeById = async (resumeId: string) => {
  const response = await axiosInstance.delete(`/resume/${resumeId}`);
  return response.data;
};