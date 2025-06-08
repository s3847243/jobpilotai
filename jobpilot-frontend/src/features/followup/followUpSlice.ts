// src/features/followUp/followUpSlice.ts
import { createSlice } from '@reduxjs/toolkit';
import { fetchAllFollowUpsThunk, getFollowUpByIdThunk, generateFollowUpThunk, improveFollowUpThunk, deleteFollowUpThunk } from './followUpThunk';
import { FollowUpEmail } from '../../types/FollowUpEmail';

interface FollowUpState {
  followUps: FollowUpEmail[];
  currentFollowUp: FollowUpEmail | null;
  followUpsByJobId: Record<string, FollowUpEmail> ;
  loading: boolean;
  error: string | null;
}

const initialState: FollowUpState = {
  followUps: [],
  currentFollowUp: null,
  followUpsByJobId:{},
  loading: false,
  error: null,
};

const followUpSlice = createSlice({
  name: 'followUp',
  initialState,
  reducers: {      resetfollowUpState: () => initialState},
  extraReducers: (builder) => {
    builder
      // Fetch All
      .addCase(fetchAllFollowUpsThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAllFollowUpsThunk.fulfilled, (state, action) => {
        state.followUps = action.payload;
        state.loading = false;
      })
      .addCase(fetchAllFollowUpsThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })

      // Get by ID
      .addCase(getFollowUpByIdThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getFollowUpByIdThunk.fulfilled, (state, action) => {
        state.loading = false;
        const followUp = action.payload;
        if (followUp && followUp.jobId) {
          state.followUpsByJobId[followUp.jobId] = followUp;
        }
      })
      .addCase(getFollowUpByIdThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to get cover letter';
      })

      // Generate
      .addCase(generateFollowUpThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(generateFollowUpThunk.fulfilled, (state, action) => {
        state.loading = false;
        const followUp = action.payload;
        state.followUps.push(action.payload);
        if (followUp && followUp.jobId) {
          state.followUpsByJobId[followUp.jobId] = followUp;
        }
      })
      .addCase(generateFollowUpThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || 'Failed to generate cover letter';
      })

      // Improve
      .addCase(improveFollowUpThunk.fulfilled, (state, action) => {
        state.currentFollowUp = action.payload;
      })

      // Delete
      .addCase(deleteFollowUpThunk.fulfilled, (state, action) => {
        state.followUps = state.followUps.filter((f) => f.id !== action.payload);
      });


  },
});
export const { resetfollowUpState } = followUpSlice.actions;

export default followUpSlice.reducer;
