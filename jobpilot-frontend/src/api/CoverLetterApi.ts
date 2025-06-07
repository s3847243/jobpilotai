// src/api/CoverLetterApi.ts
import { CoverLetters } from "../types/CoverLetter";
import axiosInstance from "./axiosInstance"; // your configured Axios baseURL instance

// create custom hooks for this api
export interface CoverLetterRequest {
  jobId: string;
  resumeId: string;
}

export interface CoverLetterResponse {
  id: string;
  content: string;
  jobId: string;
  createdAt: string;
  coverLetterName: string;
}

export const generateCoverLetter = async (jobId: string, resumeId: string) => {
  const res = await axiosInstance.post(
    `/cover-letters/generate`, 
    { jobId, resumeId }, 
    { withCredentials: true }
  );
  console.log(res.data);

  return res.data; 
};

export const improveCoverLetter = async (
  coverLetterId: string,
  instruction: string
): Promise<string> => {
  const res = await axiosInstance.post(
    `/cover-letters/${coverLetterId}/improve`,
    { instruction },
    { withCredentials: true }
  );
  return res.data; // improved text
};

export const getCoverLetterById = async (coverLetterId: string): Promise<CoverLetterResponse> => {
  const res = await axiosInstance.get(`/cover-letters/${coverLetterId}`, { withCredentials: true });
  console.log(res.data);
  return res.data;
};


export const fetchCoverLetters = async (): Promise<CoverLetters[]> => {
  const res = await axiosInstance.get("/cover-letters", { withCredentials: true });
  console.log(res.data);
  return res.data;
};


export const deleteCoverLetterById = async (coverLetterId: string) => {
  const response = await axiosInstance.delete(`/cover-letters/${coverLetterId}`);
  return response.data;
};