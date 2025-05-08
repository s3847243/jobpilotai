import React, { useEffect, useState } from 'react'
import { Plus, X } from 'lucide-react';
import JobItems from './JobItems';
import JobTable from './JobTable';
import { createJobFromUrl } from '../../api/JobApi';
import { fetchJobs } from '../../api/JobApi';
export type Job = {
  id: string;
  status: string;
  company: string;
  resume: Resume;
  coverLetter: string | null;
  location: string;
  matchScore:string ;
  matchFeedback:string;
  employmentType:string;
  title:string;
  url:string;
};
export type Resume = {
  id: string;
  filename: string;
  s3Url: string;
};
const JobApp = () => {
      const [jobs , setJobs] = useState<Job[]>([]);
      const [isOpen, setIsOpen] = useState(false);
      const [jobUrl, setJobUrl] = useState('');
      const [resumeFile, setResumeFile] = useState<File | null>(null);
      useEffect(() => {
        const loadJobs = async () => {
          try {
            const jobList = await fetchJobs();
            setJobs(jobList);
          } catch (error) {
            console.error('Error fetching jobs:', error);
          }
        };

        loadJobs();
      }, []);
      const handleSubmit = async (e:any) => {
        e.preventDefault();
        if (!resumeFile) return;
      
        try {
          const newJob = await createJobFromUrl(jobUrl, resumeFile);
          console.log(newJob);
          // Optional: If the API returns a new job object, append it
          setJobs(prev => [...prev, newJob]);
      
          setJobUrl('');
          setResumeFile(null);
          setIsOpen(false);
        } catch (error) {
          console.error('Error creating job:', error);
          alert('Failed to create job. Please try again.');
        }
      };
      return (
        <section>
          <div className="flex items-center justify-between">
            <h1 className="text-3xl font-mono px-10 py-2">Job Applications</h1>
            <button
              onClick={() => setIsOpen(true)}
              className="flex items-center gap-2 bg-green-600 hover:bg-lime-500 text-white font-semibold py-2 px-4 rounded-full mr-10"
            >
              <Plus size={20} />
              New Job Application
            </button>
          </div>
    
          <hr className="my-3 border-t-4 py-3" />
          <JobTable jobs={jobs} />
    
          {/* Modal */}
          {isOpen && (
            <div className="fixed inset-0 z-50 bg-black bg-opacity-40 flex items-center justify-center">
              <div className="bg-white p-6 rounded-xl shadow-lg w-full max-w-md relative">
                <button
                  onClick={() => setIsOpen(false)}
                  className="absolute top-3 right-3 text-gray-600 hover:text-red-500"
                >
                  <X size={20} />
                </button>
                <h2 className="text-xl font-semibold mb-4">New Job Application</h2>
                <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                  <div>
                    <label className="block text-sm font-medium mb-1">Job URL</label>
                    <input
                      type="url"
                      required
                      value={jobUrl}
                      onChange={(e) => setJobUrl(e.target.value)}
                      placeholder="https://example.com/job"
                      className="w-full border border-gray-300 rounded px-3 py-2"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium mb-1">Resume (PDF)</label>
                    <input
                      type="file"
                      accept=".pdf"
                      required
                      onChange={(e) => {
                        if (e.target.files && e.target.files.length > 0) {
                          setResumeFile(e.target.files[0]);
                        }
                      }}
                      className="w-full"
                    />
                  </div>
                  <button
                    type="submit"
                    className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded"
                  >
                    Submit Application
                  </button>
                </form>
              </div>
            </div>
          )}
        </section>
      );
    };
    

export default JobApp;
