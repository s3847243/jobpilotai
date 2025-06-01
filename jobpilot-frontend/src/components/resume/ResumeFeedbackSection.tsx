import { useEffect, useState } from 'react';
import ResumeMatchScoreBar from './ResumeMatchScoreBar'; // assuming it's in components folder
import { fetchResumes } from '../../api/ResumeApi';
import { Star, CheckCircle, Zap, Upload, FileText } from 'lucide-react';
import { AppDispatch,RootState    } from '../../store';
import { fetchResumesThunk } from '../../features/resume/resumesThunk';
import { useDispatch, useSelector } from 'react-redux';

const ResumeFeedbackSection = ({
  feedback,
  matchScore,
  missingSkills,
  onReplace
}: {
  feedback: string;
  matchScore: number;
  missingSkills: string[];
  onReplace: (resumeId: string) => void;  

}) => {
  const [selectedResume, setSelectedResume] = useState<string>("");
  const [isAnimated, setIsAnimated] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const dispatch = useDispatch<AppDispatch>();

  const resumes = useSelector((state: RootState) => state.resumes.resumes);
  const loading = useSelector((state: RootState) => state.resumes.loading);
  const error = useSelector((state: RootState) => state.resumes.error);

  useEffect(() => {
    dispatch(fetchResumesThunk())
      .unwrap()
      .catch(console.error);
  }, [dispatch]);

  const handleAssign = () => {
      if (!selectedResume) {
        setErrorMessage("Please select a resume before proceeding.");
        return;
      }
      setErrorMessage(null); 
      onReplace(selectedResume);
  };

  return (
  <div className="bg-white dark:bg-slate-800 rounded-2xl shadow-lg dark:shadow-slate-800/50 border border-slate-200 dark:border-slate-700 overflow-hidden transition-colors">
    <div className="bg-gradient-to-r from-slate-50 to-slate-100 dark:from-slate-800 dark:to-slate-900 p-8 border-b border-slate-200 dark:border-slate-700">
      <div className="flex items-center gap-3 mb-2">
        <div className="p-2 bg-gradient-to-br from-purple-500 to-pink-600 dark:from-indigo-500 dark:to-purple-600 rounded-lg">
          <Star className="w-5 h-5 text-white" />
        </div>
        <h2 className="text-2xl font-bold text-slate-800 dark:text-white">Resume Analysis</h2>
      </div>
      <p className="text-slate-600 dark:text-slate-400">Detailed feedback and recommendations</p>
    </div>

    <div className="p-8">
      {/* Resume Match Score */}
      <ResumeMatchScoreBar score={matchScore} />

      {/* Feedback Section */}
      <div className="mb-8">
        <h3 className="text-lg font-semibold text-slate-800 dark:text-white mb-4 flex items-center gap-2">
          <CheckCircle className="w-5 h-5 text-green-600" />
          AI Feedback
        </h3>
        <div className="bg-gradient-to-br from-blue-50 to-indigo-50 dark:from-slate-700 dark:to-slate-800 p-6 rounded-xl border border-blue-200 dark:border-slate-600">
          <p className="text-slate-700 dark:text-slate-300 leading-relaxed">{feedback}</p>
        </div>
      </div>

      {/* Missing Skills Section */}
      <div className="mb-8">
        <h3 className="text-lg font-semibold text-slate-800 dark:text-white mb-4 flex items-center gap-2">
          <Zap className="w-5 h-5 text-amber-600" />
          Skills to Highlight
        </h3>

        {missingSkills.length > 0 ? (
          <div className="flex flex-wrap gap-3">
            {missingSkills.map((skill, index) => (
              <div 
                key={index} 
                className={`px-4 py-3 bg-gradient-to-r from-red-100 to-rose-100 dark:from-rose-900 dark:to-rose-800 text-red-700 dark:text-red-300 rounded-xl text-sm font-medium shadow-sm hover:shadow-md hover:scale-105 transition-all duration-200 cursor-pointer border border-red-200 dark:border-red-400 ${
                  isAnimated ? 'animate-bounce' : ''
                }`}
                style={{ animationDelay: `${index * 100}ms`, animationDuration: '1s', animationFillMode: 'both' }}
              >
                {skill}
              </div>
            ))}
          </div>
        ) : (
          <div className="bg-gradient-to-r from-green-100 to-emerald-100 dark:from-emerald-900 dark:to-green-800 text-green-800 dark:text-green-300 p-6 rounded-xl font-medium shadow-sm border border-green-200 dark:border-green-400 flex items-center gap-3">
            <CheckCircle className="w-6 h-6" />
            <span>Excellent! No major missing skills found.</span>
          </div>
        )}
      </div>

      {/* Resume Selection */}
      <div className="bg-gradient-to-br from-slate-50 to-slate-100 dark:from-slate-800 dark:to-slate-900 p-6 rounded-xl border border-slate-200 dark:border-slate-700">
        <h3 className="text-lg font-semibold text-slate-800 dark:text-white mb-4 flex items-center gap-2">
          <Upload className="w-5 h-5 text-blue-600" />
          Replace Resume
        </h3>
        
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">
              Select a different resume
            </label>
            <select
              value={selectedResume}
              onChange={(e) => setSelectedResume(e.target.value)}
              className="block w-full p-4 border border-slate-300 dark:border-slate-600 rounded-xl shadow-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all bg-white dark:bg-slate-700 text-slate-800 dark:text-slate-100"
            >
              <option value="" disabled>Choose a resume...</option>
              {resumes.map((r) => (
                <option key={r.id} value={r.id}>{r.filename}</option>
              ))}
            </select>
          </div>

          <button
            onClick={handleAssign}
            className="w-full bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 text-white px-6 py-4 rounded-xl transition-all duration-200 font-semibold shadow-lg hover:shadow-xl transform hover:scale-[1.02] flex items-center justify-center gap-2"
          >
            <FileText className="w-5 h-5" />
            Assign Selected Resume
          </button>
          {errorMessage && (
            <p className="text-red-600 dark:text-red-400 text-sm mt-2">{errorMessage}</p>
          )}
        </div>
      </div>
    </div>
  </div>
);
};

export default ResumeFeedbackSection;
