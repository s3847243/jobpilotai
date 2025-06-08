// src/features/coverLetters/coverLetterSlice.ts
import { createSlice } from '@reduxjs/toolkit';
import { CoverLetterResponse } from '../../api/CoverLetterApi';
import { fetchCoverLettersThunk, generateCoverLetterThunk, deleteCoverLetterThunk, getCoverLetterByIdThunk, improveCoverLetterThunk } from './coverLetterThunks';

interface CoverLetterState {
  coverLetters: CoverLetterResponse[];
  selectedCoverLetter: CoverLetterResponse | null;
  coverLettersByJobId: Record<string, CoverLetterResponse> ;
  improvedText: string | null;
  loading: boolean;
  error: string | null;
}

const initialState: CoverLetterState = {
  coverLetters: [],
  selectedCoverLetter: null,
  coverLettersByJobId: {},
  improvedText: null,
  loading: false,
  error: null,
};


const coverLetterSlice = createSlice({
  name: 'coverLetters',
  initialState,
  reducers: {      resetcoverLetterState: () => initialState},
  extraReducers: (builder) => {
    builder
      // Fetch All
      .addCase(fetchCoverLettersThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCoverLettersThunk.fulfilled, (state, action) => {
        state.loading = false;
        state.coverLetters = action.payload;
      })
      .addCase(fetchCoverLettersThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to fetch cover letters';
      })

      // Get by ID
      .addCase(getCoverLetterByIdThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getCoverLetterByIdThunk.fulfilled, (state, action) => {
        state.loading = false;
        const coverLetter = action.payload;
        if (coverLetter && coverLetter.jobId) {
          state.coverLettersByJobId[coverLetter.jobId] = coverLetter;
        }
      })
      .addCase(getCoverLetterByIdThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to get cover letter';
      })

      // Generate
      .addCase(generateCoverLetterThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(generateCoverLetterThunk.fulfilled, (state, action) => {
        state.loading = false;
        const coverLetter = action.payload;
        state.coverLetters.push(action.payload);
        if (coverLetter && coverLetter.jobId) {
          state.coverLettersByJobId[coverLetter.jobId] = coverLetter;
        }
      })
      .addCase(generateCoverLetterThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to generate cover letter';
      })

      // Improve
      .addCase(improveCoverLetterThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(improveCoverLetterThunk.fulfilled, (state, action) => {
        state.loading = false;
        if (state.selectedCoverLetter) {
          state.selectedCoverLetter.content = action.payload;
          const jobId = state.selectedCoverLetter.jobId;
          if (jobId && state.coverLettersByJobId[jobId]) {
            state.coverLettersByJobId[jobId].content = action.payload;
          }
        }
      })
      .addCase(improveCoverLetterThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to improve cover letter';
      })

      // Delete
      .addCase(deleteCoverLetterThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteCoverLetterThunk.fulfilled, (state, action) => {
        state.loading = false;
        state.coverLetters = state.coverLetters.filter(cl => cl.id !== action.payload);
      })
      .addCase(deleteCoverLetterThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to delete cover letter';
      });


  },
});
export const { resetcoverLetterState } = coverLetterSlice.actions;
export default coverLetterSlice.reducer;
