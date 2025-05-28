// features/resumes/resumesSlice.ts
import {createAsyncThunk } from '@reduxjs/toolkit';
import { fetchResumes, getResumeById, getJobsByResumeId, uploadResume, deleteResumeById } from '../../api/ResumeApi';
// Thunks
export const fetchResumesThunk = createAsyncThunk('resumes/fetchResumes', async (_, { rejectWithValue }) => {
  try {
    return await fetchResumes();
  } catch (err: any) {
    return rejectWithValue(err.message);
  }
});

export const getResumeByIdThunk = createAsyncThunk('resumes/getById', async (resumeId: string, { rejectWithValue }) => {
  try {
    return await getResumeById(resumeId);
  } catch (err: any) {
    return rejectWithValue(err.message);
  }
});

export const getJobsByResumeIdThunk = createAsyncThunk('resumes/getJobsByResumeId', async (resumeId: string, { rejectWithValue }) => {
  try {
    return await getJobsByResumeId(resumeId);
  } catch (err: any) {
    return rejectWithValue(err.message);
  }
});

export const uploadResumeThunk = createAsyncThunk('resumes/uploadResume', async (file: File, { rejectWithValue }) => {
  try {
    return await uploadResume(file);
  } catch (err: any) {
    return rejectWithValue(err.message);
  }
});

export const deleteResumeByIdThunk = createAsyncThunk('resumes/deleteResume', async (resumeId: string, { rejectWithValue }) => {
  try {
    await deleteResumeById(resumeId);
    return resumeId; // Return the deleted ID
  } catch (err: any) {
    return rejectWithValue(err.message);
  }
});