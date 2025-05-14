import React, { useEffect, useState } from 'react';
import { Plus } from 'lucide-react';
import ResumeItem from './ResumeItem';
import { fetchResumes } from '../../api/ResumeApi';
import { Resume } from '../../types/Resume';
const ResumeApp = () => {
  const [resumes, setResumes] = useState<Resume[]>([]);

  useEffect(() => {
    const loadResumes = async () => {
      try {
        const data = await fetchResumes();
        setResumes(data);
      } catch (err) {
        console.error('Failed to fetch resumes:', err);
      }
    };
    loadResumes();
  }, []);

  return (
    <section>
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-mono px-10 py-2">My Resumes</h1>
        <button className="flex items-center gap-2 bg-green-600 hover:bg-lime-500 text-white font-semibold py-2 px-4 rounded-full mr-10">
          <Plus size={20} />
          Resume
        </button>
      </div>

      <hr className="my-3 border-t-4 py-3" />

      <div className="grid grid-cols-4 gap-6 px-5">
        {resumes.map((resume) => (
          <ResumeItem
            key={resume.id}
            id={resume.id}
            name={resume.filename}
            date={new Date(resume.uploadedAt).toLocaleDateString()}
          />
        ))}
      </div>
    </section>
  );
};

export default ResumeApp;
