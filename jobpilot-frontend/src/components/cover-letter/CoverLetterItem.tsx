import { Edit3 } from 'lucide-react';
import React from 'react'

const CoverLetterItem = ({ name, date }) => {
  return (
    <div className="bg-white p-6 rounded-xl shadow-md flex flex-col gap-4 w-full max-w-sm">
      {/* Top Section */}
      <div className="flex items-start justify-between">
        <h2 className="text-lg font-semibold break-words">{name}</h2>
        <button className="text-gray-400 hover:text-gray-600">
          &#8942; {/* 3 vertical dots */}
        </button>
      </div>

      {/* Date */}
      <div>
        <span className="bg-gray-100 text-gray-600 text-xs font-semibold px-3 py-1 rounded-full">
          {date}
        </span>
      </div>

      {/* Button */}
      <button className="flex items-center justify-center gap-2 bg-gray-800 hover:bg-gray-700 text-white font-semibold py-2 px-4 rounded-full w-full">
        <Edit3 size={18} />
        Open
      </button>
    </div>
  );
};

export default CoverLetterItem
