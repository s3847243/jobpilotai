import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import DashboardLayout from './layout/DashboardLayout';
import CoverLetterPage from './components/cover-letter/CoverLetterPage';
import ResumePage from './components/resume/ResumePage';
import ResumeApp from './components/resume/ResumeApp';
import JobApp from './components/job-applications/JobApp';
import Home from './pages/Home';
import Login from './pages/Login'
import CoverLetter from './components/cover-letter/CoverLetter'
import FollowUpEmailPage from './components/followup/FollowUpEmailPage';
import FollowUpAll from './components/followup/FollowUpAll'
import ResumeDetails from './components/resume/ResumeDetails';
import Register from './pages/Register'
import SettingsPage from './pages/SettingsPage';
import PrivateRoute from './routes/PrivateRoute';
import { useDispatch } from 'react-redux';
import { useEffect } from 'react';
import { loadUserThunk } from './features/user/userThunk';
import { AppDispatch } from './store';
function App() {

  const dispatch = useDispatch<AppDispatch>();

  useEffect(() => {
    dispatch(loadUserThunk());
  }, [dispatch]);

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path='/login' element={<Login />} />
        <Route path='/register' element={<Register />} />
        <Route element={<PrivateRoute />}>
        <Route path="/dashboard" element={<DashboardLayout />}>
          <Route index element={<Navigate to="/dashboard/job-hub" />} />
          <Route path="job-hub" element={<JobApp />} />
          <Route path="resumes" element={<ResumeApp />} />   
          <Route path="follow-ups" element={<FollowUpAll />} />
          <Route path="job/:jobId/cover-letter" element={<CoverLetterPage />} />
          <Route path="job/:jobId/follow-up" element={<FollowUpEmailPage />} />
          <Route path="job/:jobId/resume" element={<ResumePage />} />
          <Route path="cover-letters" element={<CoverLetter />} />
          <Route path="resumes/:resumeId" element={<ResumeDetails />} /> 
          <Route path="settings" element={<SettingsPage />} />
        </Route>
        </Route>




        <Route path="*" element={<p>404 Page Not Found</p>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
