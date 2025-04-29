import React from "react";
const ResumeMatchScoreBar = ({ score }: { score: number }) => {
    let bgColor = "bg-red-500";
  
    if (score >= 55 && score < 75) {
      bgColor = "bg-yellow-400";
    } else if (score >= 75) {
      bgColor = "bg-green-500";
    }
  
    return (
      <div className="w-full">
        <p className="mb-2 font-medium text-gray-700">Resume Match Score</p>
        <div className="w-full bg-gray-200 rounded-full h-5 overflow-hidden">
          <div
            className={`h-full ${bgColor} transition-all duration-300`}
            style={{ width: `${score}%` }}
          ></div>
        </div>
        <p className="mt-1 text-l text-gray-600">{score}%</p>
      </div>
    );
  };
  
  export default ResumeMatchScoreBar;
  