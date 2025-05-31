// userSlice.ts

import { createSlice } from '@reduxjs/toolkit';
import { logoutUserThunk, loginUserThunk,updateUserThunk,deleteAccountThunk } from './userThunk';

interface UserState {
  id: string | null;
  name: string | null;
  email: string | null;
  loading: boolean;
  error: string | null;
}

const initialState: UserState = {
  id: null,
  name: null,
  email: null,
  loading: false,
  error: null,
};

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      // Logout
      .addCase(logoutUserThunk.fulfilled, (state) => {
        state.id = null;
        state.name = null;
        state.email = null;

      })

      // Login 
      .addCase(loginUserThunk.fulfilled, (state, action) => {
        state.id = action.payload.id;
        state.name = action.payload.name;
        state.email = action.payload.email;

      })
      .addCase(loginUserThunk.rejected, (state, action) => {
        state.error = action.payload as string;

      })
       // Update User
      .addCase(updateUserThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateUserThunk.fulfilled, (state, action) => {
        state.loading = false;
        state.name = action.payload.name ?? state.name;
        state.email = action.payload.email ?? state.email;
        state.error = null;
      })
      .addCase(updateUserThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })

      // Delete Account
      .addCase(deleteAccountThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteAccountThunk.fulfilled, (state) => {
        state.loading = false;
        state.id = null;
        state.name = null;
        state.email = null;

        state.error = null;
      })
      .addCase(deleteAccountThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      });
  },
});

export default userSlice.reducer;
