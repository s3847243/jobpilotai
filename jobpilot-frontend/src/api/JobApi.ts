import axiosInstance from './axiosInstance';

// LIST ALL JOBS
export const fetchJobs = async () => {
    const res = await axiosInstance.get('/jobs');
    return res.data;
  };
  
  
  // CREATE JOB (from URL + resume)
  export const createJobFromUrl = async (url:string, file:File) => {
    const formData = new FormData();
    formData.append('url', url);
    formData.append('file', file);
  
    const res = await axiosInstance.post('/jobs/from-url', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return res.data;
  };
  
  // GENERATE COVER LETTER
  export const generateCoverLetter = async (jobId:string) => {
    const res = await axiosInstance.post(`/jobs/${jobId}/generate-cover-letter`);
    return res.data; 
  };
  
  // GET COVER LETTER
  export const getCoverLetter = async (jobId:string) => {
    const res = await axiosInstance.get(`/jobs/${jobId}/cover-letter`);
    return res.data; // { coverLetter: "..." }
  };
  