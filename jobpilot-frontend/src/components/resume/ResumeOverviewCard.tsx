import React, { useEffect, useState } from 'react';
import { getResumeById } from '../../api/ResumeApi';
import { Resume } from '../../types/Resume';
interface ResumeOverviewCardProps {
  resumeId: string;
}

const ResumeOverviewCard: React.FC<ResumeOverviewCardProps> = ({ resumeId }) => {
  const [resume, setResume] = useState<Resume >();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchResume = async () => {
      try {
        const data = await getResumeById(resumeId);
        setResume(data);
      } catch (err) {
        console.error('Failed to load resume', err);
      } finally {
        setLoading(false);
      }
    };

    fetchResume();
  }, [resumeId]);

  if (loading) return <p>Loading resume...</p>;
  if (!resume) return <p>Resume not found.</p>;

  return (
    <div className="bg-white p-6 rounded-xl shadow-md">
      <h2 className="text-xl font-semibold mb-4">Resume Overview</h2>
      <div className="grid grid-cols-2 gap-3 text-gray-700">
        <p><strong>Resume Name:</strong> {resume.filename}</p>
        <p><strong>Uploaded:</strong> {new Date(resume.uploadedAt).toLocaleDateString()}</p>
        <p><strong>Parsed Name:</strong> {resume.filename}</p>
        <p><strong>Email:</strong> {resume.filename}</p>
      </div>
    </div>
  );
};

export default ResumeOverviewCard;