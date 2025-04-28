import React from 'react';

const ResumeFeedbackSection = ({ feedback, onImprove, onReplace }) => {
  return (
    <div className="bg-white p-6 rounded-xl shadow-md">
      <h2 className="text-xl font-semibold mb-4">Resume Feedback</h2>
      <p className="text-gray-700 mb-6">{feedback}</p>
      <p> Resume Match Score</p>
      <p> Resume Feedback on like quantifying numbers or add impact or something</p>
      <p>missing skills to add from the job description</p>

      <p>Make the changes above and re-upload your resume </p>

      <p>edit resume coming soon or something</p>
      <div className="flex gap-4">
        <button 
          onClick={onImprove}
          className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
        >
          🛠 Get New AI Suggestions
        </button>
        <button 
          onClick={onReplace}
          className="bg-gray-600 text-white px-4 py-2 rounded-md hover:bg-gray-700"
        >
          📄 Upload New Resume
        </button>
      </div>
    </div>
  );
};

export default ResumeFeedbackSection;
