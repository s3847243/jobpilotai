// userSlice.ts

import { createSlice } from '@reduxjs/toolkit';
import { logoutUserThunk, loginUserThunk,updateUserThunk,deleteAccountThunk, loadUserThunk } from './userThunk';

interface UserState {
  id: string | null;
  fullName: string | null;
  email: string | null;
  jobTitle: string | null;
  location:string |null;
  loading: boolean;
  isAuthenticated: boolean;
  error: string | null;
}

const initialState: UserState = {
  id: null,
  fullName: null,
  email: null,
  jobTitle: null,
  location:null,
  loading: false,
  isAuthenticated:false,
  error: null,
};

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {      resetUserState: () => initialState// ✅ reset action
},
  extraReducers: (builder) => {
    builder
      .addCase(loadUserThunk.fulfilled, (state, action) => {
        state.id = action.payload.id;
        state.fullName = action.payload.fullName;
        state.email = action.payload.email;
        state.jobTitle = action.payload.jobTitle;
        state.location = action.payload.location;
        state.isAuthenticated = true;
      })
      .addCase(loadUserThunk.rejected, (state) => {
        state.isAuthenticated = false;
      })
           // Login 
      .addCase(loginUserThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(loginUserThunk.fulfilled, (state, action) => {
        state.loading = false;
        state.id = action.payload.id;
        state.fullName = action.payload.fullName;
        state.email = action.payload.email;
        state.jobTitle = action.payload.jobTitle;
        state.location = action.payload.location;
        state.isAuthenticated = true; 
        state.error = null;
      })
      .addCase(loginUserThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })

      // Logout
      .addCase(logoutUserThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(logoutUserThunk.fulfilled, (state) => {
        state.loading = false;
        state.id = null;
        state.fullName = null;
        state.email = null;
        state.error = null;
      })
      .addCase(logoutUserThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      })

      // Update User
      .addCase(updateUserThunk.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateUserThunk.fulfilled, (state, action) => {
        state.loading = false;
        state.fullName = action.payload.fullName ?? state.fullName;
        state.email = action.payload.email ?? state.email;
        state.jobTitle = action.payload.jobTitle ?? state.jobTitle;
        state.location = action.payload.location ?? state.location;
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
        state.fullName = null;
        state.email = null;
        state.error = null;
      })
      .addCase(deleteAccountThunk.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      });



  },
});
export const { resetUserState } = userSlice.actions;

export default userSlice.reducer;
