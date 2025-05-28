// src/features/coverLetters/coverLetterSlice.ts
import { createSlice } from '@reduxjs/toolkit';
import { CoverLetterResponse } from '../../api/CoverLetterApi';
import { fetchCoverLettersThunk, generateCoverLetterThunk, deleteCoverLetterThunk, getCoverLetterByIdThunk, improveCoverLetterThunk } from './coverLetterThunks';

interface CoverLetterState {
  coverLetters: CoverLetterResponse[];
  selectedCoverLetter: CoverLetterResponse | null;
  improvedText: string | null;
  loading: boolean;
  error: string | null;
}

const initialState: CoverLetterState = {
  coverLetters: [],
  selectedCoverLetter: null,
  improvedText: null,
  loading: false,
  error: null,
};


const coverLetterSlice = createSlice({
  name: 'coverLetters',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      // Fetch all cover letters
      .addCase(fetchCoverLettersThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCoverLettersThunk.fulfilled, (state, action) => {
        state.coverLetters = action.payload;
        state.loading = false;
      })
      .addCase(fetchCoverLettersThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })

      // Generate new cover letter
      .addCase(generateCoverLetterThunk.fulfilled, (state, action) => {
        state.coverLetters.push(action.payload);
      })

      // Delete cover letter
      .addCase(deleteCoverLetterThunk.fulfilled, (state, action) => {
        state.coverLetters = state.coverLetters.filter((cl) => cl.id !== action.payload);
      })
       // Get cover letter by ID
      .addCase(getCoverLetterByIdThunk.fulfilled, (state, action) => {
        state.selectedCoverLetter = action.payload;
      })
      .addCase(getCoverLetterByIdThunk.rejected, (state, action) => {
        state.error = action.payload as string;
      })

      // Improve cover letter
      .addCase(improveCoverLetterThunk.fulfilled, (state, action) => {
        state.improvedText = action.payload;
      })
      .addCase(improveCoverLetterThunk.rejected, (state, action) => {
        state.error = action.payload as string;
      });
  },
});

export default coverLetterSlice.reducer;
