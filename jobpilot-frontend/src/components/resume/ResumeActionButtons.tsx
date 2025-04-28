import React from 'react';

const ResumeActionButtons = ({ onGenerateCoverLetter, onDelete }) => {
  return (
    <div className="flex gap-4">
      <button 
        onClick={onGenerateCoverLetter}
        className="bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700"
      >
        ➕ Generate Cover Letter
      </button>
      <button 
        onClick={onDelete}
        className="bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700"
      >
        🗑 Delete Resume
      </button>
    </div>
  );
};

export default ResumeActionButtons;
