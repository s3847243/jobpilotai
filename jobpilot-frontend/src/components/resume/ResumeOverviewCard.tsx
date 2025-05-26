import React, { useEffect, useState } from 'react';
import { getResumeById } from '../../api/ResumeApi';
import { Resume } from '../../types/Resume';
import { AlertCircle, Calendar, FileText, Mail, User } from 'lucide-react';
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

  if (loading) {
    return (
      <div className="bg-white p-8 rounded-2xl shadow-lg border border-slate-200">
        <div className="animate-pulse">
          <div className="h-6 bg-slate-200 rounded w-1/3 mb-6"></div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {[...Array(4)].map((_, i) => (
              <div key={i} className="space-y-2">
                <div className="h-4 bg-slate-200 rounded w-1/4"></div>
                <div className="h-4 bg-slate-200 rounded w-3/4"></div>
              </div>
            ))}
          </div>
        </div>
      </div>
    );
  }
  if (!resume) {
    return (
      <div className="bg-white p-8 rounded-2xl shadow-lg border border-slate-200">
        <div className="text-center py-8">
          <AlertCircle className="w-12 h-12 text-red-500 mx-auto mb-4" />
          <p className="text-slate-600">Resume not found.</p>
        </div>
      </div>
    );
  }

return (
    <div className="bg-white p-8 rounded-2xl shadow-lg border border-slate-200 hover:shadow-xl transition-all duration-300">
      
      <div className="flex items-center gap-3 mb-8">
        <div className="p-3 bg-gradient-to-br from-blue-500 to-purple-600 rounded-xl">
          <FileText className="w-6 h-6 text-white" />
        </div>
        <h2 className="text-2xl font-bold text-slate-800">Resume Overview</h2>
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="space-y-2 group">
          <div className="flex items-center gap-2 text-slate-500 text-sm font-medium">
            <FileText className="w-4 h-4" />
            Resume Name
          </div>
          <p className="text-slate-800 font-semibold text-lg group-hover:text-blue-600 transition-colors">
            {resume.filename}
          </p>
        </div>
        
        <div className="space-y-2 group">
          <div className="flex items-center gap-2 text-slate-500 text-sm font-medium">
            <Calendar className="w-4 h-4" />
            Uploaded
          </div>
          <p className="text-slate-800 font-semibold text-lg group-hover:text-blue-600 transition-colors">
            {new Date(resume.uploadedAt).toLocaleDateString('en-US', {
              year: 'numeric',
              month: 'long',
              day: 'numeric'
            })}
          </p>
        </div>
        
        <div className="space-y-2 group">
          <div className="flex items-center gap-2 text-slate-500 text-sm font-medium">
            <User className="w-4 h-4" />
            Candidate Name
          </div>
          <p className="text-slate-800 font-semibold text-lg group-hover:text-blue-600 transition-colors">
            {resume.filename}
          </p>
        </div>
        
        <div className="space-y-2 group">
          <div className="flex items-center gap-2 text-slate-500 text-sm font-medium">
            <Mail className="w-4 h-4" />
            Email
          </div>
          <p className="text-slate-800 font-semibold text-lg group-hover:text-blue-600 transition-colors">
            {resume.filename}
          </p>
        </div>
      </div>
    </div>
  );
};

export default ResumeOverviewCard;