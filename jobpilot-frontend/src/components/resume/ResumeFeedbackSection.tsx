import React from 'react';
import ResumeMatchScoreBar from './ResumeMatchScoreBar'; // assuming it's in components folder

const ResumeFeedbackSection = ({
  feedback,
  matchScore,
  missingSkills,
  onImprove,
  onReplace
}: {
  feedback: string;
  matchScore: number;
  missingSkills: string[];
  onImprove: () => void;
  onReplace: () => void;
}) => {
  return (
    <div className="bg-white p-6 rounded-xl shadow-md">
      <h2 className="text-xl font-semibold mb-4">Resume Feedback</h2>
       {/* Resume Match Score */}
       <ResumeMatchScoreBar score={matchScore} />
      <p className="text-gray-700 mb-6">{feedback}</p>

      <p> Resume Feedback on like quantifying numbers or add impact or something</p>

      <div className="mt-6 mb-4">
        <h3 className="text-lg font-semibold text-gray-800 mb-3">
          🚀 Missing Skills You Should Highlight
        </h3>

        {missingSkills.length > 0 ? (
          <div className="flex flex-wrap gap-3">
            {missingSkills.map((skill, index) => (
              <div 
                key={index} 
                className="px-4 py-2 bg-red-100 text-red-700 rounded-full text-sm font-medium shadow-sm hover:bg-red-200 transition-all cursor-pointer"
              >
                {skill}
              </div>
            ))}
          </div>
        ) : (
          <div className="bg-green-100 text-green-700 p-4 rounded-md font-medium shadow-sm">
            Great job! No major missing skills found.
          </div>
        )}
        
      </div>


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
