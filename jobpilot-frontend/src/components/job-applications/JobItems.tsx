import React, { useState } from 'react'
import { Link } from 'react-router-dom';
import { Job } from './JobApp';
import { updateJobStatus } from '../../api/JobApi';
type Props = {
  job: Job;
};
const JobItems:React.FC<Props> = ({ job }) => {
  const coverLetterId = "asd";
  const [open, setOpen] = useState(false);
  const [selectedOption, setSelectedOption] = useState(job.status);

  const selectOption = async (option: string) => {
  try {
    await updateJobStatus(job.id, option);
    setSelectedOption(option);
  } catch (error) {
    console.error("Failed to update job status:", error);
  } finally {
    setOpen(false); // Close dropdown
  }
};
  return (
      <tr className="border-t hover:bg-gray-50">
          <td className="px-6 py-4">
          <div className="relative inline-block text-left">
  <div>
    <button
      type="button"
      onClick={() => setOpen((prev) => !prev)}
      className="inline-flex w-full justify-center gap-x-1.5 rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-gray-300 hover:bg-gray-50"
    >
      {selectedOption}
      <svg
        className="-mr-1 size-5 text-gray-400"
        viewBox="0 0 20 20"
        fill="currentColor"
        aria-hidden="true"
      >
        <path
          fillRule="evenodd"
          d="M5.22 8.22a.75.75 0 0 1 1.06 0L10 11.94l3.72-3.72a.75.75 0 1 1 1.06 1.06l-4.25 4.25a.75.75 0 0 1-1.06 0L5.22 9.28a.75.75 0 0 1 0-1.06Z"
          clipRule="evenodd"
        />
      </svg>
    </button>
  </div>

  {open && (
    <div className="absolute left-0 z-10 mt-2 w-40 origin-top-right rounded-md bg-white shadow-lg ring-1 ring-black/5 focus:outline-none">
      <div className="py-1">
        {["Saved", "Applied", "Rejected"].map((option) => (
          <div
            key={option}
            className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 cursor-pointer"
            onClick={() => selectOption(option)}
          >
            {option}
          </div>
        ))}
      </div>
    </div>
  )}
</div>
        </td>
        <td className="px-6 py-4">{job.company}</td>
        <td className="px-6 py-4">
          <Link 
            to={`/dashboard/job/${job.id}/resume`}
            className="text-indigo-600 hover:underline"
          >
            Open Resume
          </Link>
        </td>
        <td className="px-6 py-4">
          <Link 
            to={`/dashboard/job/${job.id}/cover-letter`}
            className="text-indigo-600 hover:underline"
          >
            Open Cover Letter
          </Link>
        </td>

        <td className="px-6 py-4">
          <button className="text-red-500 hover:text-red-700 font-semibold">Delete</button>
        </td>
      </tr>
    );
  };
  

  export default JobItems;
  
  