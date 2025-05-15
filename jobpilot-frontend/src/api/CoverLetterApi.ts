// src/api/CoverLetterApi.ts
import axiosInstance from "./axiosInstance"; // your configured Axios baseURL instance

// create custom hooks for this api
export interface CoverLetterRequest {
  jobId: string;
  resumeId: string;
}

export interface CoverLetterResponse {
  id: string;
  text: string;
  jobId: string;
  createdAt: string;
}

export const generateCoverLetter = async (jobId: string, resumeId: string) => {
  const res = await axiosInstance.post(
    `/cover-letters/generate`, 
    { jobId, resumeId }, // ✅ CoverLetterRequest DTO
    { withCredentials: true }
  );
  return res.data; // returns { id, text, ... }
};

export const improveCoverLetter = async (
  coverLetterId: string,
  instruction: string
): Promise<string> => {
  const res = await axiosInstance.post(
    `/cover-letters/improve/${coverLetterId}`,
    { instruction },
    { withCredentials: true }
  );
  return res.data; // improved text
};

export const getCoverLetterById = async (coverLetterId: string): Promise<CoverLetterResponse> => {
  const res = await axiosInstance.get(`/cover-letters/${coverLetterId}`, { withCredentials: true });
  return res.data;
};


export const getAllCoverLetters = async (): Promise<CoverLetterResponse[]> => {
  const res = await axiosInstance.get("/cover-letters", { withCredentials: true });
  return res.data;
};

// Get Cover Letter by Job ID
export const getCoverLetterByJobId = async (jobId: string) => {
  const res = await axiosInstance.get(`/cover-letters/job/${jobId}`, { withCredentials: true });
  return res.data; // { id, text, createdAt, job: { id, ... }, ... }
};
