import { createSlice } from '@reduxjs/toolkit';
import { Resume } from '../../types/Resume';
import { JobSummaryDTO } from '../../types/JobSummaryDTO';
import { deleteResumeByIdThunk, fetchResumesThunk, getJobsByResumeIdThunk, getResumeByIdThunk, uploadResumeThunk } from './resumesThunk';
interface ResumeState {
  resumes: Resume[];
  selectedResume: Resume | null;
  resumeJobs: JobSummaryDTO[]; // Jobs associated with a resume
  loading: boolean;
  error: string | null;
}

const initialState: ResumeState = {
  resumes: [],
  selectedResume: null,
  resumeJobs: [],
  loading: false,
  error: null,
};

// Slice
const resumesSlice = createSlice({
  name: 'resumes',
  initialState,
  reducers: {      resetResumeState: () => initialState,
},
  extraReducers: (builder) => {
    builder
      .addCase(fetchResumesThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchResumesThunk.fulfilled, (state, action) => {
        state.loading = false;
        state.resumes = action.payload;
      })
      .addCase(fetchResumesThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      // Get resume by ID
      .addCase(getResumeByIdThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getResumeByIdThunk.fulfilled, (state, action) => {
        state.loading = false;
        state.selectedResume = action.payload;
      })
      .addCase(getResumeByIdThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      // Get jobs by resume ID
      .addCase(getJobsByResumeIdThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getJobsByResumeIdThunk.fulfilled, (state, action) => {
        state.loading = false;
        state.resumeJobs = action.payload;
      })
      .addCase(getJobsByResumeIdThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      // Upload resume
      .addCase(uploadResumeThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(uploadResumeThunk.fulfilled, (state, action) => {
        state.loading = false;
        state.resumes.push(action.payload);
      })
      .addCase(uploadResumeThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })
      .addCase(deleteResumeByIdThunk.fulfilled, (state, action) => {
        state.resumes = state.resumes.filter((r) => r.id !== action.payload);
      })
      .addCase(deleteResumeByIdThunk.rejected, (state, action) => {
        state.error = action.payload as string;
      });

  },
});
export const { resetResumeState } = resumesSlice.actions;

export default resumesSlice.reducer;