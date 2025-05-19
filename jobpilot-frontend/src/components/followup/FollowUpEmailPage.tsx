import React, { useEffect, useState } from 'react';
import {
  getFollowUpByFollowUpId,
  generateFollowUpEmail,
  improveFollowUpEmail,
} from '../../api/FollowUpEmailApi';
import { FollowUpEmail } from '../../types/FollowUpEmail';
import { useParams } from 'react-router-dom';
import { getJobById } from '../../api/JobApi';
const FollowUpEmailPage: React.FC = () => {
  const { jobId } = useParams<{ jobId: string }>();
  const [email, setEmail] = useState<FollowUpEmail | null>(null);
  const [instructions, setInstructions] = useState('');
  const [loading, setLoading] = useState(false);
  const [improving, setImproving] = useState(false);

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
    if (!instructions.trim() || !email) return;
    setImproving(true);
    try {
      const updated = await improveFollowUpEmail(email.id, instructions);
      setEmail(updated);
      setInstructions('');
    } finally {
      setImproving(false);
    }
  };

  return (
    <div className="flex h-screen">
      {/* Left: Follow-up email content */}
      <div className="w-1/2 border-r p-6 overflow-y-auto">
        {email ? (
          <>
            <h1 className="text-2xl font-bold text-gray-800 mb-4">{email.subject}</h1>
            <pre className="bg-gray-100 text-gray-700 whitespace-pre-wrap p-4 rounded-md">
              {email.body}
            </pre>
            <p className="mt-4 text-sm text-gray-500">
              Last updated: {new Date(email.createdAt).toLocaleString()}
            </p>
          </>
        ) : (
          <div className="flex flex-col items-center justify-center h-full text-center">
            <p className="text-lg mb-4 text-gray-600">No follow-up email generated yet.</p>
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

      {/* Right: Chatbox for improvements */}
      <div className="w-1/2 p-6 flex flex-col">
        <h2 className="text-xl font-semibold text-gray-800 mb-4">Suggest Improvements</h2>
        <textarea
          rows={10}
          value={instructions}
          onChange={(e) => setInstructions(e.target.value)}
          placeholder="e.g. Make it more concise, add a closing thank-you line..."
          className="flex-grow resize-none border border-gray-300 rounded p-3 text-gray-800 mb-4"
        />
        <button
          onClick={handleImprove}
          disabled={!email || improving}
          className="self-end bg-green-600 text-white px-5 py-2 rounded hover:bg-green-500 disabled:opacity-50"
        >
          {improving ? 'Improving...' : 'Send Instructions'}
        </button>
      </div>
    </div>
  );
};

export default FollowUpEmailPage;
