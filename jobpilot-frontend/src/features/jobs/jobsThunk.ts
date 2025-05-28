import { createAsyncThunk } from '@reduxjs/toolkit';
import { fetchJobs, deleteJobById,createJobFromUrl, getJobById, assignResumeToJob, matchJobWithResume, updateJobStatus } from '../../api/JobApi'; // Adjust imports
import { Job } from '../../api/JobApi';

// 1️⃣ Fetch all jobs
export const fetchJobsThunk = createAsyncThunk<Job[]>(
  'jobs/fetchJobs',
  async (_, { rejectWithValue }) => {
    try {
      const response = await fetchJobs();
      return response;
    } catch (err: any) {
      return rejectWithValue(err.message || 'Failed to fetch jobs');
    }
  }
);
export const createJobThunk = createAsyncThunk<Job, { url: string; resumeId?: string }>(
  'jobs/createJob',
  async ({ url, resumeId }) => {
    return await createJobFromUrl(url, resumeId);
  }
);
// 2️⃣ Delete a job
export const deleteJobThunk = createAsyncThunk<string, string>(
  'jobs/deleteJob',
  async (jobId, { rejectWithValue }) => {
    try {
      await deleteJobById(jobId);
      return jobId;
    } catch (err: any) {
      return rejectWithValue(err.message || 'Failed to delete job');
    }
  }
);


// Fetch a job by ID
export const fetchJobByIdThunk = createAsyncThunk<Job, string>(
  'jobs/fetchJobById',
  async (jobId, thunkAPI) => {
    try {
      const data = await getJobById(jobId);
      return data;
    } catch (err: any) {
      return thunkAPI.rejectWithValue(err.response?.data || 'Failed to fetch job');
    }
  }
);

// Match a job with resume
export const matchJobThunk = createAsyncThunk<Job, string>(
  'jobs/matchJob',
  async (jobId, thunkAPI) => {
    try {
      const data = await matchJobWithResume(jobId);
      return data;
    } catch (err: any) {
      return thunkAPI.rejectWithValue(err.response?.data || 'Failed to match job');
    }
  }
);

// Assign a resume to a job
export const assignResumeThunk = createAsyncThunk<Job, { jobId: string; resumeId: string }>(
  'jobs/assignResume',
  async ({ jobId, resumeId }, thunkAPI) => {
    try {
      const data = await assignResumeToJob(jobId, resumeId);
      return data;
    } catch (err: any) {
      return thunkAPI.rejectWithValue(err.response?.data || 'Failed to assign resume');
    }
  }
);


// Update Job Status Thunk
export const updateJobStatusThunk = createAsyncThunk<
  { jobId: string; status: string },
  { jobId: string; status: string }
>(
  'jobs/updateJobStatus',
  async ({ jobId, status }, thunkAPI) => {
    try {
      await updateJobStatus(jobId, status); // Call the API
      return { jobId, status }; // Return the data you updated
    } catch (err: any) {
      return thunkAPI.rejectWithValue(err.response?.data || 'Failed to update job status');
    }
  }
);