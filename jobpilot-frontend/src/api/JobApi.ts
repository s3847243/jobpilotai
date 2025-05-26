import axiosInstance from './axiosInstance';
import { Resume } from '../types/Resume';
import { FollowUpEmail } from '../types/FollowUpEmail';
export type Job = {
  id: string;
  company: string;
  title: string;
  url: string;
  status: string;
  location: string;
  resumeId: string | null;
  coverLetterId: string | null;
  followUpEmailId: string | null;
  matchScore: number | null;
  matchFeedback: string | null;
  missingSkills: string[] | null;
};
// LIST ALL JOBS
export const fetchJobs = async () => {
    const res = await axiosInstance.get('/job',{withCredentials: true});
    console.log(res.data);
      console.log('Type:', typeof res.data); // This should say "object"

    return res.data;
};
export const getJobById = async (jobId: string): Promise<Job> => {
  const res = await axiosInstance.get(`/job/${jobId}`,{withCredentials: true});
  return res.data;
};

// CREATE JOB (from URL + resume)
export const createJobFromUrl = async (url: string, resumeId?: string) => {
  const queryParams = new URLSearchParams();
  queryParams.append('url', url);
  if (resumeId) queryParams.append('resumeId', resumeId);

  const res = await axiosInstance.post(`/job/from-url?${queryParams.toString()}`, null, {
    withCredentials: true,
  });
  return res.data;
};

// // GENERATE COVER LETTER
// export const generateCoverLetter = async (jobId: string) => {
//   const res = await axiosInstance.post(
//     `/job/${jobId}/generate-cover-letter`
//   );

//   console.log(res.data);
//   return res.data;
// };


// // GET COVER LETTER
// export const getCoverLetter = async (jobId:string) => {
//   const res = await axiosInstance.get(`/job/${jobId}/cover-letter`,{withCredentials: true});
//   return res.data; // { coverLetter: "..." }
// };

// // Update existing cover letter
// export const updateCoverLetter = async (jobId: string, text: string): Promise<void> => {
//   await axiosInstance.put(`/job/${jobId}/cover-letter`, text, {
//     headers: { 'Content-Type': 'text/plain' }
//     ,withCredentials: true
//   });
// };

// // Improve cover letter via instruction
// export const improveCoverLetter = async (jobId: string, instruction: string): Promise<string> => {
//   const res = await axiosInstance.post(`/job/${jobId}/improve-cover-letter`, {instruction} ,{withCredentials: true});
//   console.log(res.data);

//   return res.data;
// };

// Match resume to job — backend generates feedback, score, skills, etc.
export const matchJobWithResume = async (jobId: string): Promise<Job> => {
  const res = await axiosInstance.get(`job/${jobId}/match`,{withCredentials: true});
  return res.data;
};

export const assignResumeToJob = async (jobId: string, resumeId: string) => {
  const res = await axiosInstance.put(`/job/${jobId}/assign-resume/${resumeId}`, {
    withCredentials: true
  });
  console.log(res.data);
  return res.data;
};

export const updateJobStatus = async (jobId: string, status: string) => {
  const res = await axiosInstance.patch(
    `/job/${jobId}/status`,
    { status },
    {
      withCredentials: true,
      headers: {
        "Content-Type": "application/json",
      },
    }
  );

  return res.data; 
};


