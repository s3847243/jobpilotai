import React, { useEffect, useState } from 'react';
import {
  getFollowUpByFollowUpId,
  generateFollowUpEmail,
  improveFollowUpEmail,
} from '../../api/FollowUpEmailApi';
import { FollowUpEmail } from '../../types/FollowUpEmail';
import { useParams } from 'react-router-dom';
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
    <div className="flex h-[calc(100vh-64px)] bg-gray-50">
      
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
            disabled={!email || improving}
            className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-md"
          >
            {improving ? 'Improving...' : 'Send Instructions'}
          </button>
        </div>
      </div>

      {/* Right: Cover Letter Viewer */}
      <div className="w-1/2 p-6 flex flex-col gap-6">
        <div className="flex justify-between items-center">
          <h2 className="text-2xl font-semibold text-gray-800">📄 Your Follow Up Email</h2>
          <button
            disabled ={!email}
            onClick={() => {
              if (email) {
                downloadAsPdf(email.subject, email.body);
              }
            }}
            className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-md"
          >
            ⬇️ Download PDF
          </button>
        </div>

        {/* Letter Preview Box */}
        {email ? (
          <div className="bg-white p-6 rounded-xl shadow-md overflow-y-auto h-full border">
            <p className="text-gray-700 whitespace-pre-line leading-relaxed text-sm">
              {email.body}
            </p>
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center h-full text-gray-500">
            <p>No cover letter found.</p>
            <button
              onClick={handleGenerate}
              disabled={loading}
              className="bg-blue-600 text-white px-5 py-2 rounded hover:bg-blue-500 disabled:opacity-50"
            >
              {loading ? 'Generating...' : 'Generate Email'}
            </button>
          </div>
      )}
      </div>
    </div>
  );
};

export default FollowUpEmailPage;
