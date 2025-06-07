import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';
import { downloadAsPdf } from '../followup/DownloadAsPdf';
import { useDispatch, useSelector } from 'react-redux';
import { AppDispatch, RootState } from '../../store';
import { fetchJobByIdThunk } from '../../features/jobs/jobsThunk'
import { getCoverLetterByIdThunk, generateCoverLetterThunk, improveCoverLetterThunk } from '../../features/coverletter/coverLetterThunks';
interface ChatMessage {
  id: number;
  text: string;
  animate: boolean;
}
const CoverLetterPage = () => {
  const {jobId} = useParams<{ jobId: string }>();
  const [input, setInput] = useState('');
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [nextId, setNextId] = useState(0);
  const[loadingText, setLoadingText] = useState('');
  const navigate = useNavigate();
  const dispatch = useDispatch<AppDispatch>();
  const job = useSelector((state: RootState) =>
    state.jobs.jobs.find(j => j.id === jobId)
  );
  const coverLetter = useSelector((state: RootState) =>
    jobId ? state.coverLetters.coverLettersByJobId[jobId]?.content : null
  );
  const loading = useSelector((state: RootState) => state.coverLetters.loading);

 useEffect(() => {
    if (!jobId) return;

    dispatch(fetchJobByIdThunk(jobId))
      .unwrap()
      .then((job) => {
        if (job.coverLetterId) {
          dispatch(getCoverLetterByIdThunk(job.coverLetterId));
        }
      })
      .catch((err) => {
        console.error('Failed to load job or cover letter:', err);
      });
  }, [dispatch, jobId]);


  const handleGenerate = () => {
      if (!job || !job.resumeId) {
        alert('No resume found for this job.');
        return;
      }
      setLoadingText('Generating cover letter...');


      dispatch(generateCoverLetterThunk({ jobId: job.id, resumeId: job.resumeId }))
        .unwrap()
        .then(() => {
        console.log('Cover letter generated successfully');
        })
        .catch((err) => {
          console.error('Failed to generate cover letter:', err);
        })
        .finally(() => setLoadingText(''));
        
  };

  const handleImprove = () => {
      if (!input.trim() || !jobId) return;

      const id = nextId;
      setNextId(prev => prev + 1);

      setMessages(prev => [...prev, { id, text: input, animate: false }]);

      setTimeout(() => {
        setMessages(prev =>
          prev.map(msg =>
            msg.id === id ? { ...msg, animate: true } : msg
          )
        );
      }, 10);

      dispatch(improveCoverLetterThunk({ coverLetterId: jobId, instruction: input }))
        .unwrap()
        .then((updatedContent) => {
          console.log('Cover letter improved:', updatedContent);
        })
        .catch((err) => {
          console.error('Failed to improve cover letter:', err);
        });

      setInput('');
  };

  return (
  <div className="max-w-10xl mx-auto p-1">
    {/* Back Button */}
    <div className="flex items-center">
      <button
        onClick={() => navigate(-1)}
        className="inline-flex items-center gap-2 text-sm text-gray-700 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white bg-gray-100 dark:bg-slate-700 hover:bg-gray-200 dark:hover:bg-slate-600 px-3 py-1 rounded-md shadow-sm transition"
      >
        <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
          <path fillRule="evenodd" d="M12.707 14.707a1 1 0 01-1.414 0L7 10.414a1 1 0 010-1.414L11.293 4.293a1 1 0 111.414 1.414L9.414 9l4.293 4.293a1 1 0 010 1.414z" clipRule="evenodd" />
        </svg>
        Back
      </button>
    </div>

    <div className="flex h-[calc(100vh-90px)] bg-gray-50 dark:bg-slate-900 text-gray-800 dark:text-gray-200">

      {/* Left: AI Chat Assistant */}
      <div className="w-1/2 p-6 border-r border-gray-200 dark:border-slate-700 flex flex-col gap-4 overflow-y-auto">
        <h2 className="text-2xl font-semibold text-gray-800 dark:text-white mb-2">💬 Smart Assistant</h2>
        <div className="flex flex-col gap-3 overflow-y-auto">
          {messages.map((msg) => (
            <div
              key={msg.id}
              className={`
                transform transition-all duration-300 ease-out
                ${msg.animate ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-4'}
                bg-white dark:bg-slate-800 p-3 rounded-md shadow-sm text-sm text-gray-600 dark:text-gray-300
              `}
            >
              🧠 You: {msg.text}
            </div>
          ))}

          {messages.length === 0 && (
            <p className="text-sm text-gray-600 dark:text-gray-400 bg-white dark:bg-slate-800 p-3 rounded-md shadow-sm">
              ✍️ Suggestion: Add more quantifiable impact — e.g., “improved response time by 35%”.
            </p>
          )}
        </div>

        <div className="mt-auto flex gap-3">
          <input
            type="text"
            placeholder="Ask for help or rewrite a section..."
            value={input}
            onChange={(e) => setInput(e.target.value)}
            className="w-full border border-gray-300 dark:border-slate-600 bg-white dark:bg-slate-800 text-gray-800 dark:text-gray-200 px-4 py-2 rounded-md shadow-sm"
          />
          <button
            onClick={handleImprove}
            className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-md"
          >
            Enter
          </button>
        </div>
      </div>

      {/* Right: Cover Letter Viewer */}
      <div className="w-1/2 p-6 flex flex-col gap-6">
        <div className="flex justify-between items-center">
          <h2 className="text-2xl font-semibold text-gray-800 dark:text-white">📄 Your Cover Letter</h2>
          <button
            onClick={() => {
              if (coverLetter) {
                downloadAsPdf("coverLetter", coverLetter, true);
              }
            }}
            className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-md"
          >
            ⬇️ Download PDF
          </button>
        </div>

        {/* Letter Preview Box */}
        {loadingText ? (
          <div className="flex flex-col items-center justify-center h-full text-gray-500 dark:text-gray-400">
            <svg className="animate-spin h-5 w-5 mr-3 text-indigo-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
            </svg>
            <p className="mt-2">{loadingText}</p>
          </div>
        ) : coverLetter ? (
          <div className="bg-white dark:bg-slate-800 p-6 rounded-xl shadow-md overflow-y-auto h-full border border-gray-200 dark:border-slate-700">
            <p className="text-gray-700 dark:text-gray-300 whitespace-pre-line leading-relaxed text-sm">
              {coverLetter}
            </p>
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center h-full text-gray-500 dark:text-gray-400">
            <p>No cover letter found.</p>
            <button
              onClick={handleGenerate}
              className="mt-4 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-md"
            >
              ✍️ Generate Cover Letter
            </button>
          </div>
        )}
      </div>
    </div>
  </div>
);
};

export default CoverLetterPage;
