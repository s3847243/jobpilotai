import { useEffect, useState } from 'react';
import ResumeOverviewCard from './ResumeOverviewCard';
import ResumeFeedbackSection from './ResumeFeedbackSection';
import { useDispatch, useSelector } from 'react-redux';
import { useParams } from 'react-router-dom';
import { AppDispatch, RootState } from '../../store';
import { useNavigate } from 'react-router-dom';
import { fetchJobByIdThunk, matchJobThunk, assignResumeThunk } from '../../features/jobs/jobsThunk';

const ResumePage = () => {
  const { jobId } = useParams<{ jobId: string }>();
  const [reloadTrigger, setReloadTrigger] = useState(0);
  const navigate = useNavigate();
    const job = useSelector((state: RootState) =>
    state.jobs.jobs.find((j) => j.id === jobId)
  );
  const loading = useSelector((state: RootState) => state.jobs.loading);
  const dispatch = useDispatch<AppDispatch>();

  useEffect(() => {
    if (!jobId) return;

    const fetchAndMatchJob = async () => {
      try {
        const jobData = await dispatch(fetchJobByIdThunk(jobId)).unwrap();

        const needsMatching =
          !jobData.matchFeedback || !jobData.matchScore || !jobData.missingSkills;

        if (needsMatching) {
          await dispatch(matchJobThunk(jobId)).unwrap();
        }
      } catch (error) {
        console.error('Failed to fetch or match job:', error);
      }
    };

    fetchAndMatchJob();
  }, [dispatch, jobId, reloadTrigger]);

  if (!job) return <div>Loading...</div>;

  const handleReplace = (resumeId: string) => {
    if (!jobId) return;
    dispatch(assignResumeThunk({ jobId, resumeId }))
      .unwrap()
      .then(() => {
        console.log('Resume assigned successfully');
        setReloadTrigger((prev) => prev + 1);
      })
      .catch((error) => {
        console.error('Failed to assign resume:', error);
      });
  };

  return (
    <div className="max-w-6xl mx-auto p-6 flex flex-col gap-8">
      
      <div className="flex items-center gap-2">
        <button
          onClick={() => navigate(-1)}
          className="inline-flex items-center gap-2 text-sm text-gray-700 hover:text-gray-900 bg-gray-100 hover:bg-gray-200 px-3 py-1 rounded-md shadow-sm transition"
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
            <path fillRule="evenodd" d="M12.707 14.707a1 1 0 01-1.414 0L7 10.414a1 1 0 010-1.414L11.293 4.293a1 1 0 111.414 1.414L9.414 9l4.293 4.293a1 1 0 010 1.414z" clipRule="evenodd" />
          </svg>
          Back
        </button>
      </div>
      <ResumeOverviewCard resumeId={job.resumeId || ''} />
      <ResumeFeedbackSection
        feedback={job.matchFeedback || ""}

        onReplace={handleReplace}
        matchScore={Number(job.matchScore) || 0}
        missingSkills={job.missingSkills || []}
      />

    </div>
  );
};

export default ResumePage;