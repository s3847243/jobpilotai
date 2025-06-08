import { Job } from "../../api/JobApi";
import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { fetchJobsThunk, deleteJobThunk,createJobThunk ,fetchJobByIdThunk, matchJobThunk, assignResumeThunk,updateJobStatusThunk} from './jobsThunk';
interface JobsState {
  jobs: Job[];
  loading: boolean;
  error: string | null;
}

const initialState: JobsState = {
  jobs: [],
  loading: false,
  error: null,
};

const jobsSlice = createSlice({
  name: 'jobs',
  initialState,
  reducers: {      resetJobState: () => initialState
},
  extraReducers: (builder) => {
    builder
      // Fetch Jobs
      .addCase(fetchJobsThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchJobsThunk.fulfilled, (state, action) => {
        state.jobs = action.payload;
        state.loading = false;
      })
      .addCase(fetchJobsThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })

      // Delete Job
      .addCase(deleteJobThunk.fulfilled, (state, action) => {
        state.jobs = state.jobs.filter((job) => job.id !== action.payload);
      })
      .addCase(deleteJobThunk.rejected, (state, action) => {
        state.error = action.payload as string;
      })
            // Create Job
      .addCase(createJobThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createJobThunk.fulfilled, (state, action) => {
        state.jobs.push(action.payload); // Add the new job to the list
        state.loading = false;
      })
      .addCase(createJobThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Error creating job';
      })  

       // Fetch job by ID 
      .addCase(fetchJobByIdThunk.fulfilled, (state, action) => {
        const index = state.jobs.findIndex(job => job.id === action.payload.id);
        if (index !== -1) {
          state.jobs[index] = action.payload;
        } else {
          state.jobs.push(action.payload);
        }
      })

      // Match job
      .addCase(matchJobThunk.fulfilled, (state, action) => {
        const index = state.jobs.findIndex(job => job.id === action.payload.id);
        if (index !== -1) {
          state.jobs[index] = action.payload;
        }
      })

      // Assign resume
      .addCase(assignResumeThunk.fulfilled, (state, action) => {
        const index = state.jobs.findIndex(job => job.id === action.payload.id);
        if (index !== -1) {
          state.jobs[index] = action.payload;
        }
      })
      // update job status
      .addCase(updateJobStatusThunk.fulfilled, (state, action) => {
        const index = state.jobs.findIndex((job) => job.id === action.payload.jobId);
        if (index !== -1) {
          state.jobs[index].status = action.payload.status;
        }
      })
      .addCase(updateJobStatusThunk.rejected, (state, action) => {
        state.error = action.payload as string;
      });




  },
});
export const { resetJobState } = jobsSlice.actions;

export default jobsSlice.reducer;