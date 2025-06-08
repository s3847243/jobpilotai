// userThunk.ts

import { createAsyncThunk } from '@reduxjs/toolkit';
import {logout, login,updateUserProfile,deleteAccount, getCurrentUser } from '../../api/AuthApi';


export const logoutUserThunk = createAsyncThunk('user/logout', async (_, { rejectWithValue }) => {
  try {
    await logout();
    return null; // Just clear user state
  } catch (err: any) {
    return rejectWithValue(err.message);
  }
});
export const loadUserThunk = createAsyncThunk(
  'user/loadUser',
  async (_, thunkAPI) => {
    try {
      const user = await getCurrentUser();
      console.log(user);
      return user;
    } catch (err) {
      return thunkAPI.rejectWithValue('Not authenticated');
    }
  }
);
export const loginUserThunk = createAsyncThunk(
  'user/login',
  async ({ email, password }: { email: string; password: string }, { rejectWithValue }) => {
    try {
       const response = await login(email, password);
       console.log("response.user "+response.user);
        return response.user; 
    } catch (err: any) {
      return rejectWithValue(err.message);
    }
  }
);

export const updateUserThunk = createAsyncThunk('user/update', async (data: {
  name?: string;
  location?: string;
  jobTitle?: string;
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
