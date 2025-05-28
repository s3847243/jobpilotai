// src/features/coverLetters/coverLetterThunks.ts
import { createAsyncThunk } from '@reduxjs/toolkit';
import {
  fetchCoverLetters,
  generateCoverLetter,
  deleteCoverLetterById,
  CoverLetterResponse,getCoverLetterById, improveCoverLetter 
} from '../../api/CoverLetterApi';

// Get cover letter by ID
export const getCoverLetterByIdThunk = createAsyncThunk<CoverLetterResponse, string, { rejectValue: string }>(
  'coverLetters/getById',
  async (coverLetterId, { rejectWithValue }) => {
    try {
      return await getCoverLetterById(coverLetterId);
    } catch (err) {
      return rejectWithValue('Failed to fetch cover letter.');
    }
  }
);

// Improve cover letter
export const improveCoverLetterThunk = createAsyncThunk<string, { coverLetterId: string; instruction: string }, { rejectValue: string }>(
  'coverLetters/improve',
  async ({ coverLetterId, instruction }, { rejectWithValue }) => {
    try {
      return await improveCoverLetter(coverLetterId, instruction);
    } catch (err) {
      return rejectWithValue('Failed to improve cover letter.');
    }
  }
);
// Fetch all cover letters
export const fetchCoverLettersThunk = createAsyncThunk<CoverLetterResponse[], void, { rejectValue: string }>(
  'coverLetters/fetchAll',
  async (_, { rejectWithValue }) => {
    try {
      return await fetchCoverLetters();
    } catch (err) {
      return rejectWithValue('Failed to fetch cover letters.');
    }
  }
);

// Generate cover letter
export const generateCoverLetterThunk = createAsyncThunk<CoverLetterResponse, { jobId: string; resumeId: string }, { rejectValue: string }>(
  'coverLetters/generate',
  async ({ jobId, resumeId }, { rejectWithValue }) => {
    try {
      return await generateCoverLetter(jobId, resumeId);
    } catch (err) {
      return rejectWithValue('Failed to generate cover letter.');
    }
  }
);

// Delete cover letter
export const deleteCoverLetterThunk = createAsyncThunk<string, string, { rejectValue: string }>(
  'coverLetters/delete',
  async (coverLetterId, { rejectWithValue }) => {
    try {
      await deleteCoverLetterById(coverLetterId);
      return coverLetterId;
    } catch (err) {
      return rejectWithValue('Failed to delete cover letter.');
    }
  }
);
