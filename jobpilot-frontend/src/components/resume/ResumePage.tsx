import React from 'react';
import ResumeOverviewCard from './ResumeOverviewCard';
import ResumeFeedbackSection from './ResumeFeedbackSection';
import JobsLinkedTable from './JobsLinkedTable';
import ResumeActionButtons from './ResumeActionButtons'

const ResumePage = () => {
  const resume = {feedback:"something"}; // Fetch from API
  const jobs = [];   // Fetch from API
  
  const handleImprove = () => { /* call improve API */ };
  const handleReplace = () => { /* upload new resume */ };
  const handleGenerateCoverLetter = () => { /* open cover letter modal */ };
  const handleDelete = () => { /* confirm and delete resume */ };

  return (
    <div className="max-w-6xl mx-auto p-6 flex flex-col gap-8">
      
      <ResumeOverviewCard resume={resume} />
      <ResumeFeedbackSection 
        feedback={resume.feedback}
        onImprove={handleImprove}
        onReplace={handleReplace}
        matchScore={50}
        missingSkills={["AWS", "Dockers"]}
      />
      <JobsLinkedTable jobs={jobs} />
      <ResumeActionButtons 
        onGenerateCoverLetter={handleGenerateCoverLetter}
        onDelete={handleDelete}
      />
    </div>
  );
};

export default ResumePage;