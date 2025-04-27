import React from 'react'
import { Plus } from 'lucide-react';
import JobItems from './JobItems';
import JobTable from './JobTable';
const JobApp = () => {
    const jobs = [
        {
          id: 1,
          status: "Interested",
          companyName: "Google",
          resume: "Resume1.pdf",
          coverLetter: "CoverLetter1.pdf",
        },
        {
          id: 2,
          status: "Applied",
          companyName: "Amazon",
          resume: "Resume2.pdf",
          coverLetter: "CoverLetter2.pdf",
        },
        {
          id: 3,
          status: "Interview",
          companyName: "Netflix",
          resume: "Resume3.pdf",
          coverLetter: "CoverLetter3.pdf",
        },
        {
          id: 4,
          status: "Offer",
          companyName: "Microsoft",
          resume: "Resume4.pdf",
          coverLetter: "CoverLetter4.pdf",
        },
        {
          id: 5,
          status: "Rejected",
          companyName: "Apple",
          resume: "Resume5.pdf",
          coverLetter: "CoverLetter5.pdf",
        },
      ];
  return (
    <section>
        <div className="flex items-center justify-between">
            <h1 className="text-3xl font-mono px-10 py-2">Job Applications</h1>
            <button className="flex items-center gap-2 bg-green-600 hover:bg-lime-500 text-white font-semibold py-2 px-4 rounded-full mr-10">
                <Plus size={20} />
                New Job Application
            </button>
        </div>
        <hr className='my-3 border-t-4 py-3'/>
        <JobTable jobs={jobs} />


    </section>
  )
}

export default JobApp;
