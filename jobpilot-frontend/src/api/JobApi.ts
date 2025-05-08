import axiosInstance from './axiosInstance';
export type Resume = {
  id: string;
  filename: string;
  s3Url: string;
};
export type Job = {
  id: string;
  company: string;
  title: string;
  url: string;
  status: string;
  location: string;
  resume: Resume | null;
  coverLetter: string | null;
  matchScore: number | null;
  matchFeedback: string | null;
  missingSkills: string[] | null;
};
// LIST ALL JOBS
export const fetchJobs = async () => {
    const res = await axiosInstance.get('/job',{withCredentials: true});
    console.log(res.data);
    return res.data;
};
export const getJobById = async (jobId: string): Promise<Job> => {
  const res = await axiosInstance.get(`/job/${jobId}`,{withCredentials: true});
  return res.data;
};

// CREATE JOB (from URL + resume)
export const createJobFromUrl = async (url:string, file:File) => {
  const formData = new FormData();
  formData.append('url', url);
  formData.append('file', file);

  const res = await axiosInstance.post('/job/from-url', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
    withCredentials: true, 

  });
  return res.data;
};

// GENERATE COVER LETTER
export const generateCoverLetter = async (jobId:string) => {
  const res = await axiosInstance.post(`/job/${jobId}/generate-cover-letter`,{withCredentials: true});
  return res.data; 
};

// GET COVER LETTER
export const getCoverLetter = async (jobId:string) => {
  const res = await axiosInstance.get(`/job/${jobId}/cover-letter`);
  return res.data; // { coverLetter: "..." }
};

// Match resume to job — backend generates feedback, score, skills, etc.
export const matchJobWithResume = async (jobId: string): Promise<Job> => {
  const res = await axiosInstance.get(`job/${jobId}/match`,{withCredentials: true});
  return res.data;
};