import { createAsyncThunk } from '@reduxjs/toolkit';
import {
  getFollowUpByFollowUpId,
  generateFollowUpEmail,
  improveFollowUpEmail,
  getAllFollowUpsForUser,
  deleteFollowUpEmailById,
} from '../../api/FollowUpEmailApi'; // adjust path as per your structure
import { FollowUpEmail } from '../../types/FollowUpEmail'; // assuming you have a type for FollowUpEmail

export const fetchAllFollowUpsThunk = createAsyncThunk<FollowUpEmail[]>(
  'followUp/fetchAll',
  async (_, { rejectWithValue }) => {
    try {
      return await getAllFollowUpsForUser();
    } catch (err: any) {
      return rejectWithValue(err.message);
    }
  }
);

export const getFollowUpByIdThunk = createAsyncThunk<FollowUpEmail, string,{ rejectValue: string }>(
  'followUp/getById',
  async (followUpId, { rejectWithValue }) => {
    try {
      return await getFollowUpByFollowUpId(followUpId);
    } catch (err: any) {
      return rejectWithValue(err.message);
    }
  }
);

export const generateFollowUpThunk = createAsyncThunk<FollowUpEmail, string,{ rejectValue: string }>(
  'followUp/generate',
  async (jobId, { rejectWithValue }) => {
    try {
      return await generateFollowUpEmail(jobId);
    } catch (err: any) {
      return rejectWithValue(err.message);
    }
  }
);

export const improveFollowUpThunk = createAsyncThunk<
  FollowUpEmail,
  { followUpId: string; instructions: string }
>(
  'followUp/improve',
  async ({ followUpId, instructions }, { rejectWithValue }) => {
    try {
      return await improveFollowUpEmail(followUpId, instructions);
    } catch (err: any) {
      return rejectWithValue(err.message);
    }
  }
);

export const deleteFollowUpThunk = createAsyncThunk<string, string>(
  'followUp/delete',
  async (followUpId, { rejectWithValue }) => {
    try {
      await deleteFollowUpEmailById(followUpId);
      return followUpId;
    } catch (err: any) {
      return rejectWithValue(err.message);
    }
  }
);

