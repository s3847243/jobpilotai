import { useEffect, useState } from 'react';
import ResumeOverviewCard from './ResumeOverviewCard';
import ResumeFeedbackSection from './ResumeFeedbackSection';

import { useParams } from 'react-router-dom';
import { matchJobWithResume } from '../../api/JobApi';
import { getJobById } from '../../api/JobApi';
import { Job } from '../../api/JobApi';
import { assignResumeToJob } from '../../api/JobApi';

import { useNavigate } from 'react-router-dom';


const ResumePage = () => {
  const { jobId } = useParams<{ jobId: string }>();
  const [job, setJob] = useState<Job | null>(null); 
const [reloadTrigger, setReloadTrigger] = useState(0);
const navigate = useNavigate();
useEffect(() => {
    const fetchAndMatchJob = async () => {
      if (!jobId) return;

      try {
        // Step 1: Get the job
        let jobData = await getJobById(jobId);

        // Step 2: If match data is missing, call match API
        const needsMatching =
          !jobData.matchFeedback || !jobData.matchScore || !jobData.missingSkills;

        if (needsMatching) {
          jobData = await matchJobWithResume(jobId);
        }
        console.log(jobData);
        setJob(jobData);
      } catch (error) {
        console.error('Failed to fetch or match job:', error);
      }
    };

    fetchAndMatchJob();
  }, [jobId, reloadTrigger]);
  if (!job) return <div>Loading...</div>;

const handleReplace = async (resumeId: string) => {
  if (!jobId) return;
  console.log(resumeId);
  try {
    const updatedJob = await assignResumeToJob(jobId, resumeId);
    setJob(updatedJob);
    setReloadTrigger((prev) => prev + 1);
  } catch (error) {
    console.error("Failed to assign resume:", error);
  }
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