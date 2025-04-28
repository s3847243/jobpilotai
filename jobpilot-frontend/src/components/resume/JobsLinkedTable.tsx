import React from 'react';
import { useNavigate } from 'react-router-dom';

const JobsLinkedTable = ({ jobs }) => {
  const navigate = useNavigate();

  return (
    <div className="bg-white p-6 rounded-xl shadow-md">
      <h2 className="text-xl font-semibold mb-4">Jobs Linked to This Resume</h2>
      <div className="overflow-x-auto">
        <table className="w-full text-left">
          <thead>
            <tr className="border-b">
              <th className="py-2">Job Title</th>
              <th className="py-2">Company</th>
              <th className="py-2">Match Score</th>
              <th className="py-2">Status</th>
            </tr>
          </thead>
          <tbody>
            {jobs.map((job) => (
              <tr 
                key={job.id} 
                className="border-b hover:bg-gray-50 cursor-pointer"
                onClick={() => navigate(`/dashboard/job-hub/${job.id}`)}
              >
                <td className="py-2">{job.title}</td>
                <td className="py-2">{job.company}</td>
                <td className="py-2">{job.matchScore}%</td>
                <td className="py-2">{job.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default JobsLinkedTable;
