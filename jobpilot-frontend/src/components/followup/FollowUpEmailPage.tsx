import React, { useEffect, useState } from 'react';
import {
  getFollowUpByFollowUpId,
  generateFollowUpEmail,
  improveFollowUpEmail,
} from '../../api/FollowUpEmailApi';
import { FollowUpEmail } from '../../types/FollowUpEmail';
import { useNavigate, useParams } from 'react-router-dom';
import { getJobById } from '../../api/JobApi';
import { downloadAsPdf } from './DownloadAsPdf';
interface ChatMessage {
  id: number;
  text: string;
  animate: boolean;
}

const FollowUpEmailPage: React.FC = () => {
  const { jobId } = useParams<{ jobId: string }>();
  const [email, setEmail] = useState<FollowUpEmail | null>(null);
  const [loading, setLoading] = useState(false);
  const [improving, setImproving] = useState(false);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
    const [input, setInput] = useState('');
  const navigate = useNavigate();

    const [nextId, setNextId] = useState(0);
  useEffect(() => {
    if (jobId) fetchFollowUp();
  }, [jobId]);

const fetchFollowUp = async () => {
  try {
    // Step 1: Fetch the job to check if follow-up exists
    const job = await getJobById(jobId!);

    if (job.followUpEmailId) {
      // Step 2: Now fetch the actual follow-up email using the ID
      const data = await getFollowUpByFollowUpId(job.followUpEmailId);
      setEmail(data);
    } else {
      setEmail(null);
    }
  } catch {
    setEmail(null);
  }
};

  const handleGenerate = async () => {
    setLoading(true);
    try {
      const data = await generateFollowUpEmail(jobId!);
      setEmail(data);
    } finally {
      setLoading(false);
    }
  };

  const handleImprove = async () => {

    if (!input.trim() || !email) return;
    setImproving(true);
    try {
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
      const updated = await improveFollowUpEmail(email.id, input);
      setInput('');
      setEmail(updated);
    } finally {
      setImproving(false);
    }
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

    <div className="flex h-[calc(100vh-90px)] bg-gray-50 dark:bg-slate-900">
      {/* Left: AI Chat Assistant */}
      <div className="w-1/2 p-6 border-r border-gray-200 dark:border-gray-700 flex flex-col gap-4 overflow-y-auto">
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
            className="w-full border border-gray-300 dark:border-gray-700 bg-white dark:bg-slate-800 text-gray-900 dark:text-gray-100 px-4 py-2 rounded-md shadow-sm"
          />
          <button
            onClick={handleImprove}
            disabled={!email || improving}
            className="bg-green-600 hover:bg-green-700 text-white text-md px-4 py-2 rounded-md"
          >
            {improving ? 'Improving...' : 'Enter'}
          </button>
        </div>
      </div>

      {/* Right: Follow Up Email Viewer */}
      <div className="w-1/2 p-6 flex flex-col gap-6">
        <div className="flex justify-between items-center">
          <h2 className="text-2xl font-semibold text-gray-800 dark:text-white">📄 Your Follow Up Email</h2>
          <button
            disabled={!email}
            onClick={() => {
              if (email) {
                downloadAsPdf(email.subject, email.body, false);
              }
            }}
            className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-md"
          >
            ⬇️ Download PDF
          </button>
        </div>

        {/* Letter Preview Box */}
        {email ? (
          <div className="bg-white dark:bg-slate-800 p-6 rounded-xl shadow-md overflow-y-auto h-full border border-gray-200 dark:border-gray-700">
            <p className="text-gray-700 dark:text-gray-300 whitespace-pre-line leading-relaxed text-sm">
              {email.body}
            </p>
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center h-full text-gray-500 dark:text-gray-400">
            <p>No cover letter found.</p>
            <button
              onClick={handleGenerate}
              disabled={loading}
              className="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2 rounded hover:bg-blue-500 disabled:opacity-50"
            >
              {loading ? 'Generating...' : 'Generate Email'}
            </button>
          </div>
        )}
      </div>
    </div>
  </div>
);
};

export default FollowUpEmailPage;
