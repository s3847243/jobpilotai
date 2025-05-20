import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getResumeById } from '../../api/ResumeApi';
import { Resume } from '../../types/Resume';
import { getJobsByResumeId } from '@/api/jobApi';
import { JobDTO } from '@/api/types';

const ResumeDetails: React.FC = () => {
  const { resumeId } = useParams<{ resumeId: string }>();
  const [resume, setResume] = useState<Resume | null>(null);
  const [jobs, setJobs] = useState<JobDTO[]>([]);
  const navigate = useNavigate();

  useEffect(() => {
    if (!resumeId) return;
    getResumeById(resumeId).then(setResume);
    getJobsByResumeId(resumeId).then(setJobs);
  }, [resumeId]);

  if (!resume) return <div className="p-6">Loading...</div>;

  return (
    <div className="p-6 max-w-5xl mx-auto space-y-8">
      {/* Resume Metadata */}
      <section className="bg-white shadow rounded-xl p-6 space-y-2">
        <h1 className="text-2xl font-bold">Resume Overview</h1>
        <p><strong>Name:</strong> {resume.parsedName || 'N/A'}</p>
        <p><strong>Email:</strong> {resume.parsedEmail || 'N/A'}</p>
        <p><strong>Phone:</strong> {resume.parsedPhone || 'N/A'}</p>
        <p><strong>Resume Link:</strong> <a href={resume.s3Url} className="text-blue-600 underline" target="_blank" rel="noopener noreferrer">View/Download</a></p>
      </section>

      {/* Skills */}
      <section className="bg-white shadow rounded-xl p-6">
        <h2 className="text-xl font-semibold mb-2">Skills</h2>
        <div className="flex flex-wrap gap-2">
          {resume.parsedSkills?.map(skill => (
            <span key={skill} className="bg-gray-200 text-sm px-3 py-1 rounded-full">{skill}</span>
          ))}
          {resume.parsedSkills?.length === 0 && <p className="text-gray-500">No skills found.</p>}
        </div>
      </section>

      {/* Work Experience */}
      <section className="bg-white shadow rounded-xl p-6">
        <h2 className="text-xl font-semibold mb-2">Work Experience</h2>
        <pre className="whitespace-pre-wrap text-gray-700">{resume.parsedSummary || 'Not available.'}</pre>
      </section>

      {/* Project Experience (parsed from summary for now) */}
      <section className="bg-white shadow rounded-xl p-6">
        <h2 className="text-xl font-semibold mb-2">Project Experience</h2>
        <p className="text-gray-600">Coming soon — extracted project highlights from AI parsing.</p>
      </section>

      {/* Applied Jobs Table */}
      <section className="bg-white shadow rounded-xl p-6">
        <h2 className="text-xl font-semibold mb-4">Jobs This Resume Was Used For</h2>
        {jobs.length === 0 ? (
          <p className="text-gray-600">This resume hasn’t been used yet.</p>
        ) : (
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b text-gray-700">
                <th className="py-2">Job Title</th>
                <th className="py-2">Company</th>
                <th className="py-2">Status</th>
                <th className="py-2">Actions</th>
              </tr>
            </thead>
            <tbody>
              {jobs.map(job => (
                <tr key={job.id} className="border-b hover:bg-gray-50">
                  <td className="py-2">{job.title}</td>
                  <td className="py-2">{job.company}</td>
                  <td className="py-2">{job.status}</td>
                  <td className="py-2">
                    <button
                      className="text-blue-600 underline"
                      onClick={() => navigate(`/dashboard/job/${job.id}`)}
                    >
                      View Job
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>

      {/* Call to Action */}
      <section className="bg-white shadow rounded-xl p-6 text-center">
        <h2 className="text-xl font-semibold mb-3">Want to assess this resume with another job?</h2>
        <button
          onClick={() => navigate('/dashboard/job-hub')}
          className="bg-indigo-600 hover:bg-indigo-500 text-white px-5 py-2 rounded-full font-medium"
        >
          Go to Job Hub
        </button>
      </section>
    </div>
  );
};

export default ResumeDetails;
