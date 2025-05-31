// userThunk.ts

import { createAsyncThunk } from '@reduxjs/toolkit';
import {logout, login,updateUserProfile,deleteAccount } from '../../api/AuthApi';


export const logoutUserThunk = createAsyncThunk('user/logout', async (_, { rejectWithValue }) => {
  try {
    await logout();
    return null; // Just clear user state
  } catch (err: any) {
    return rejectWithValue(err.message);
  }
});

export const loginUserThunk = createAsyncThunk(
  'user/login',
  async ({ email, password }: { email: string; password: string }, { rejectWithValue }) => {
    try {
      return await login(email, password);
    } catch (err: any) {
      return rejectWithValue(err.message);
    }
  }
);

export const updateUserThunk = createAsyncThunk('user/update', async (data: {
  name?: string;
  location?: string;
  jobTitle?: string;
  phone?: string;
}, { rejectWithValue }) => {
  try {
    return await updateUserProfile(data);
  } catch (err: any) {
    return rejectWithValue(err.message);
  }
});

export const deleteAccountThunk = createAsyncThunk('user/deleteAccount', async (_, { rejectWithValue }) => {
  try {
    return await deleteAccount();
  } catch (err: any) {
    return rejectWithValue(err.message);
  }
});
