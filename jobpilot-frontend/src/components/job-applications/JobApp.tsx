import React, { useEffect, useState } from 'react'
import { Plus, X } from 'lucide-react';
import JobTable from './JobTable';
import { fetchResumes } from '../../api/ResumeApi';
import { Resume } from '../../types/Resume';
import { useDispatch, useSelector } from 'react-redux';
import { AppDispatch, RootState } from '../../store';
import { fetchJobsThunk, createJobThunk } from '../../features/jobs/jobsThunk';

const JobApp = () => {
    const [isOpen, setIsOpen] = useState(false);
    const [jobUrl, setJobUrl] = useState('');
    const [resumes, setResumes] = useState<Resume[]>([]);
    const [selectedResumeId, setSelectedResumeId] = useState<string>('');
    const dispatch = useDispatch<AppDispatch>();
    const { jobs, loading, error } = useSelector((state: RootState) => state.jobs);
    useEffect(() => {
      const loadResumes = async () => {
        const resumeList = await fetchResumes();
        setResumes(resumeList);
      };

      loadResumes();
    }, []);

    useEffect(() => {
      dispatch(fetchJobsThunk());
    }, [dispatch]);

    const handleSubmit = async (e: any) => {
      e.preventDefault();
      dispatch(createJobThunk({ url: jobUrl, resumeId: selectedResumeId }));
      setJobUrl('');
      setSelectedResumeId('');
      setIsOpen(false);
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
                  <label className="block text-sm font-medium mb-1">Select Resume</label>
                  <select
                    required
                    value={selectedResumeId}
                    onChange={(e) => setSelectedResumeId(e.target.value)}
                    className="w-full border border-gray-300 rounded px-3 py-2"
                  >
                    <option value="">-- Choose a resume --</option>
                    {resumes.map(resume => (
                      <option key={resume.id} value={resume.id}>
                        {resume.filename}
                      </option>
                    ))}
                  </select>
                </div>
                <button
                  type="submit"
                  className="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded"
                >
                  Submit Application
                </button>
                {resumes.length && (
                  <p className="text-sm text-gray-500 mt-1">
                    If you haven't uploaded your preferred resume, you can still create a job and assign a resume later.
                  </p>
                )}
              </form>
            </div>
          </div>
        )}
      </section>
    );
  };
    

export default JobApp;
