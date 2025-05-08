import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import DashboardLayout from './layout/DashboardLayout';
import CoverLetterPage from './components/cover-letter/CoverLetterPage';
import ResumePage from './components/resume/ResumePage';
import ResumeApp from './components/resume/ResumeApp';
import JobApp from './components/job-applications/JobApp';
import Home from './pages/Home';
import Login from './pages/Login'
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path='/login' element={<Login />} />

        <Route path="/dashboard" element={<DashboardLayout />}>
          <Route index element={<Navigate to="/dashboard/job-hub" />} />
          <Route path="job-hub" element={<JobApp />} />
          <Route path="resumes" element={<ResumeApp />} />
          <Route path="cover-letters/:jobId" element={<CoverLetterPage />} />
          <Route path="resumes/:jobId" element={<ResumePage />} />
        </Route>

        <Route path="*" element={<p>404 Page Not Found</p>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
