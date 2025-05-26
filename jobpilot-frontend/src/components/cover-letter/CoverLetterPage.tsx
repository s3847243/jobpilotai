import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { generateCoverLetter,improveCoverLetter,getCoverLetterById } from '../../api/CoverLetterApi';
import { getJobById, Job } from '../../api/JobApi';
import { useNavigate } from 'react-router-dom';
import { downloadAsPdf } from '../followup/DownloadAsPdf';
interface ChatMessage {
  id: number;
  text: string;
  animate: boolean;
}
const CoverLetterPage = () => {
  const {jobId} = useParams<{ jobId: string }>();
  const [coverLetter, setCoverLetter] = useState<string | null>(null);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const[job, setJob] = useState<Job>();
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [nextId, setNextId] = useState(0);
const navigate = useNavigate();

  useEffect(() => {
    const loadCoverLetter = async () => {
      if (!jobId) return;

      try {
        const currentJob = await getJobById(jobId);
        setJob(currentJob);

        if (currentJob.coverLetterId) {
          const { content } = await getCoverLetterById(currentJob.coverLetterId);
          setCoverLetter(content);
        } else {
          setCoverLetter(null); 
        }

      } catch (error) {
        console.error("Failed to load job or cover letter:", error);
      }
    };

    loadCoverLetter();
  }, [jobId]);
  const handleGenerate = async () => {
    if (!job || !job.resumeId) {
      alert("No resume found for this job.");
      return;
    }

    setLoading(true);
    try {
      const data = await generateCoverLetter(job.id, job.resumeId);
      console.log(data);

      setCoverLetter(data.coverLetterText);
    } catch (err) {
      console.error("Failed to generate cover letter", err);
    }
    setLoading(false);
  };
  const handleImprove = async () => {
    if (!input.trim()) return;
    setLoading(true);
    // Save message in local chat history
    const id = nextId;
    setNextId(prev => prev + 1);

    // Add message with animation disabled
    setMessages(prev => [...prev, { id, text: input, animate: false }]);

    // Activate animation in next tick
    setTimeout(() => {
      setMessages(prev =>
        prev.map(msg =>
          msg.id === id ? { ...msg, animate: true } : msg
        )
      );
    }, 10); // minimal delay to trigger transition
    const updated = await improveCoverLetter(jobId!, input);
    setInput('');
    setCoverLetter(updated);
    setLoading(false);
  };
  return (
      <div className="max-w-10xl mx-auto p-1">
        {/* Back Button */}
        <div className="flex items-center">
          <button
            onClick={() => navigate(-1)}
            className="inline-flex items-center gap-2 text-sm text-gray-700 hover:text-gray-900 bg-gray-100 hover:bg-gray-200 px-3 py-1 rounded-md shadow-sm transition"
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M12.707 14.707a1 1 0 01-1.414 0L7 10.414a1 1 0 010-1.414L11.293 4.293a1 1 0 111.414 1.414L9.414 9l4.293 4.293a1 1 0 010 1.414z" clipRule="evenodd" />
            </svg>
            Back
          </button>
        </div>
    <div className="flex h-[calc(100vh-90px)] bg-gray-50">
        
      {/* Left: AI Chat Assistant */}
      <div className="w-1/2 p-6 border-r border-gray-200 flex flex-col gap-4 overflow-y-auto">
        <h2 className="text-2xl font-semibold text-gray-800 mb-2">💬 Smart Assistant</h2>
        <div className="flex flex-col gap-3 overflow-y-auto">
          {messages.map((msg) => (
            <div
              key={msg.id}
              className={`
                transform transition-all duration-300 ease-out
                ${msg.animate ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-4'}
                bg-white p-3 rounded-md shadow-sm text-sm text-gray-600
              `}
            >
              🧠 You: {msg.text}
            </div>
          ))}

          {messages.length === 0 && (
            <>
              <p className="text-sm text-gray-600 bg-white p-3 rounded-md shadow-sm">
                ✍️ Suggestion: Add more quantifiable impact — e.g., “improved response time by 35%”.
              </p>
            </>
          )}
        </div>

        <div className="mt-auto flex gap-3">
          <input
            type="text"
            placeholder="Ask for help or rewrite a section..."
            value={input}
            onChange={(e) => setInput(e.target.value)}
            className="w-full border px-4 py-2 rounded-md shadow-sm"
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
          <h2 className="text-2xl font-semibold text-gray-800">📄 Your Cover Letter</h2>
          <button
            onClick={() => {
              if (coverLetter) {
                downloadAsPdf("coverLetter",coverLetter,true);
              }
            }}
            className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-md"
          >
            ⬇️ Download PDF
          </button>
        </div>

        {/* Letter Preview Box */}
        {coverLetter ? (
          <div className="bg-white p-6 rounded-xl shadow-md overflow-y-auto h-full border">
            <p className="text-gray-700 whitespace-pre-line leading-relaxed text-sm">
              {coverLetter}
            </p>
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center h-full text-gray-500">
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
