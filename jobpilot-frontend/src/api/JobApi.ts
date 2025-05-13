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
export const generateCoverLetter = async (jobId: string) => {
  const res = await axiosInstance.post(
    `/job/${jobId}/generate-cover-letter`
  );

  console.log(res.data);
  return res.data;
};


// GET COVER LETTER
export const getCoverLetter = async (jobId:string) => {
  const res = await axiosInstance.get(`/job/${jobId}/cover-letter`,{withCredentials: true});
  return res.data; // { coverLetter: "..." }
};

// Update existing cover letter
export const updateCoverLetter = async (jobId: string, text: string): Promise<void> => {
  await axiosInstance.put(`/job/${jobId}/cover-letter`, text, {
    headers: { 'Content-Type': 'text/plain' }
    ,withCredentials: true
  });
};

// Improve cover letter via instruction
export const improveCoverLetter = async (jobId: string, instruction: string): Promise<string> => {
  const res = await axiosInstance.post(`/job/${jobId}/improve-cover-letter`, {instruction} ,{withCredentials: true});
  console.log(res.data);

  return res.data;
};

// Match resume to job — backend generates feedback, score, skills, etc.
export const matchJobWithResume = async (jobId: string): Promise<Job> => {
  const res = await axiosInstance.get(`job/${jobId}/match`,{withCredentials: true});
  return res.data;
};

export const replaceResumeForJob = async (jobId: string, file: File) => {
  const formData = new FormData();
  formData.append("file", file);

  const res = await axiosInstance.put(`/job/${jobId}/resume`, formData, {
    headers: {
      "Content-Type": "multipart/form-data"
    },
    withCredentials: true
  });
  console.log(res.data);
  return res.data; // updated Job object
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