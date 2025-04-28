import React from 'react';

const ResumeOverviewCard = ({ resume }) => {
  return (
    <div className="bg-white p-6 rounded-xl shadow-md">
      <h2 className="text-xl font-semibold mb-4">Resume Overview</h2>
      <div className="flex gap-1 justify-between">
        <p><strong>Resume Name:</strong> {resume.fileName}</p>
        <p><strong>Uploaded:</strong> {new Date(resume.uploadedAt).toLocaleDateString()}</p>
        <p><strong>Parsed Name:</strong> {resume.parsedName}</p>
        <p><strong>Email:</strong> {resume.parsedEmail}</p>
      </div>
    </div>
  );
};

export default ResumeOverviewCard;
