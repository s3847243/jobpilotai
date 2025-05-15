import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { generateCoverLetter,improveCoverLetter,getCoverLetterById, getCoverLetterByJobId } from '../../api/CoverLetterApi';
import { getJobById, Job } from '../../api/JobApi';
const CoverLetterPage = () => {
  const {jobId} = useParams<{ jobId: string }>();
  const [coverLetter, setCoverLetter] = useState<string | null>(null);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const[job, setJob] = useState<Job>();
  // useEffect(() => {
  //   const loadCoverLetter = async () => {
  //     if (!jobId) return;
  //     const currentJob = await getJobById(jobId);
  //     setJob(currentJob);
  //     const { coverLetter } = await getCoverLetterByJobId(jobId);
  //     setCoverLetter(coverLetter); 
  //   };
  //   loadCoverLetter();
  // }, [jobId]);
  useEffect(() => {
    const loadCoverLetter = async () => {
      if (!jobId) return;

      try {
        const currentJob = await getJobById(jobId);
        setJob(currentJob);

        // Only fetch cover letter if job has one
        if (currentJob.coverLetter) {
          const { coverLetter } = await getCoverLetterByJobId(jobId);
          setCoverLetter(coverLetter);
        } else {
          setCoverLetter(null); // No cover letter yet
        }

      } catch (error) {
        console.error("Failed to load job or cover letter:", error);
      }
    };

    loadCoverLetter();
  }, [jobId]);
  const handleGenerate = async () => {
    if (!job || !job.resume?.id) {
      alert("No resume found for this job.");
      return;
    }

    setLoading(true);
    try {
      const data = await generateCoverLetter(job.id, job.resume.id);
      setCoverLetter(data.text);
    } catch (err) {
      console.error("Failed to generate cover letter", err);
    }
    setLoading(false);
  };
  const handleImprove = async () => {
    if (!input.trim()) return;
    setLoading(true);
    const updated = await improveCoverLetter(jobId!, input);
    setCoverLetter(updated);
    // await updateCoverLetter(jobId!, updated); // persist
    setInput('');
    setLoading(false);
  };
  return (
    <div className="flex h-[calc(100vh-64px)] bg-gray-50">
      
      {/* Left: AI Chat Assistant */}
      <div className="w-1/2 p-6 border-r border-gray-200 flex flex-col gap-4 overflow-y-auto">
        <h2 className="text-2xl font-semibold text-gray-800 mb-2">💬 Smart Assistant</h2>
        <div className="flex flex-col gap-3">
          <p className="text-sm text-gray-600 bg-white p-3 rounded-md shadow-sm">
            Hello! I’ve analyzed your cover letter. Let's improve it together.
          </p>
          <p className="text-sm text-gray-600 bg-white p-3 rounded-md shadow-sm">
            ✍️ Suggestion: Add more quantifiable impact — e.g., “improved response time by 35%”.
          </p>
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
            onClick={() => {/* trigger download */}}
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
  );
};

export default CoverLetterPage;
