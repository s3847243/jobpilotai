import React from "react";
import JobItems from "./JobItems"; // assuming you split the row into another file
import { Job } from "./JobApp";
export type Props = {
  jobs: Job[];
};
const JobTable: React.FC<Props>  = ({ jobs }) => {
  return (
    <div className="overflow:visible rounded-xl shadow bg-white">
      <table className="w-full table-auto">
        <thead>
          <tr className="bg-gray-100 text-gray-700">
            <th className="px-6 py-3 text-left text-sm font-semibold">Status</th>
            <th className="px-6 py-3 text-left text-sm font-semibold">Company Name</th>
            <th className="px-6 py-3 text-left text-sm font-semibold">Resume</th>
            <th className="px-6 py-3 text-left text-sm font-semibold">Cover Letter</th>
            <th className="px-6 py-3 text-left text-sm font-semibold">Follow Up Email</th>
            <th className="px-6 py-3 text-left text-sm font-semibold">Delete</th>
          </tr>
        </thead>

        <tbody>
          {jobs.map((job) => (
            <JobItems key={job.id} job={job} />
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default JobTable;
