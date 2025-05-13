import React, { useEffect, useState } from 'react';
import ResumeOverviewCard from './ResumeOverviewCard';
import ResumeFeedbackSection from './ResumeFeedbackSection';
import JobsLinkedTable from './JobsLinkedTable';
import { useParams } from 'react-router-dom';
import { matchJobWithResume } from '../../api/JobApi';
// import ResumeActionButtons from './ResumeActionButtons'
import { getJobById } from '../../api/JobApi';
import { Job } from '../../api/JobApi';
import { replaceResumeForJob } from '../../api/JobApi';
const ResumePage = () => {
  const { jobId } = useParams<{ jobId: string }>();
  const [job, setJob] = useState<Job | null>(null); 
const [reloadTrigger, setReloadTrigger] = useState(0);

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

        setJob(jobData);
      } catch (error) {
        console.error('Failed to fetch or match job:', error);
      }
    };

    fetchAndMatchJob();
  }, [jobId, reloadTrigger]);
  if (!job) return <div>Loading...</div>;
  const handleImprove = () => { /* call improve API */ };
const handleReplace = async () => {
  if (!jobId) return;

  const fileInput = document.createElement("input");
  fileInput.type = "file";
  fileInput.accept = ".pdf";

  fileInput.onchange = async (e) => {
    const target = e.target as HTMLInputElement;
    if (!target.files || target.files.length === 0) return;

    const file = target.files[0];
    try {
      const updatedJob = await replaceResumeForJob(jobId, file);
      setJob(null); // trigger useEffect again to refetch & rematch
      setReloadTrigger(prev => prev + 1);
    } catch (error) {
      console.error("Failed to replace resume:", error);
    }
  };

  fileInput.click();
};  const handleGenerateCoverLetter = () => { /* open cover letter modal */ };
  const handleDelete = () => { /* confirm and delete resume */ };

        {/* <JobsLinkedTable jobs={jobs} /> */}
      {/* <ResumeActionButtons 
        onGenerateCoverLetter={handleGenerateCoverLetter}
        onDelete={handleDelete}
      /> */}
      return (
        <div className="max-w-6xl mx-auto p-6 flex flex-col gap-8">
          <ResumeOverviewCard resume={job.resume} />
          <ResumeFeedbackSection
            feedback={job.matchFeedback || ""}
            onImprove={handleImprove}
            onReplace={handleReplace}
            matchScore={Number(job.matchScore) || 0}
            missingSkills={job.missingSkills || []}
          />
        </div>
      );
};

export default ResumePage;