import React from 'react'

const JobItems = ({ status, companyName, resume, coverLetter }) => {
    return (
      <tr className="border-t hover:bg-gray-50">
        <td className="px-6 py-4">{status}</td>
        <td className="px-6 py-4">{companyName}</td>
        <td className="px-6 py-4">
          <button className="text-indigo-600 hover:underline">Open Resume</button>
        </td>
        <td className="px-6 py-4">
          <button className="text-indigo-600 hover:underline">Open Cover Letter</button>
        </td>
        <td className="px-6 py-4">
          <button className="text-red-500 hover:text-red-700 font-semibold">Delete</button>
        </td>
      </tr>
    );
  };
  
  export default JobItems;
  
  