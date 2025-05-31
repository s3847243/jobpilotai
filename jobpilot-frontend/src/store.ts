import { configureStore } from "@reduxjs/toolkit";
import jobsReducer from './features/jobs/jobSlice'
import resumeReducer from './features/resume/resumeSlice';
import coverLetterReducer from './features/coverletter/coverLetterSlice';
import followUpsReducer from './features/followup/followUpSlice';
import userReducer from './features/user/userSlice';
export const store = configureStore({
  reducer: {
    jobs: jobsReducer,
    coverLetters: coverLetterReducer,
    resumes: resumeReducer,
    followUps: followUpsReducer,
    users:userReducer


  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;