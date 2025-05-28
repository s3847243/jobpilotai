import { configureStore } from "@reduxjs/toolkit";
import jobsReducer from './features/jobs/jobSlice'
import resumeReducer from './features/resume/resumeSlice';
import coverLetterReducer from './features/coverletter/coverLetterSlice'
export const store = configureStore({
  reducer: {
    jobs: jobsReducer,
    coverLetters: coverLetterReducer,
    resumes: resumeReducer,

  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;