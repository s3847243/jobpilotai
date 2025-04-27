import React from "react";
import JobItems from "./JobItems"; // assuming you split the row into another file

const JobTable = ({ jobs }) => {
  return (
    <div className="overflow-x-auto rounded-xl shadow bg-white">
      <table className="w-full table-auto">
        {/* Table Head only ONCE */}
        <thead>
          <tr className="bg-gray-100 text-gray-700">
            <th className="px-6 py-3 text-left text-sm font-semibold">Status</th>
            <th className="px-6 py-3 text-left text-sm font-semibold">Company Name</th>
            <th className="px-6 py-3 text-left text-sm font-semibold">Resume</th>
            <th className="px-6 py-3 text-left text-sm font-semibold">Cover Letter</th>
            <th className="px-6 py-3 text-left text-sm font-semibold">Delete</th>
          </tr>
        </thead>

        <tbody>
          {jobs.map((job) => (
            <JobItems
              key={job.id}
              status={job.status}
              companyName={job.companyName}
              resume={job.resume}
              coverLetter={job.coverLetter}
            />
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default JobTable;
