import React from 'react';

const CoverLetterPage = () => {
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
          <p className="text-sm text-gray-600 bg-white p-3 rounded-md shadow-sm">
            💡 Tip: Match keywords like “microservices”, “Docker” — they appear in the job ad.
          </p>
          <p className="text-sm text-gray-600 bg-white p-3 rounded-md shadow-sm">
            💡 Tip: Match keywords like “microservices”, “Docker” — they appear in the job ad.
          </p>
          <p className="text-sm text-gray-600 bg-white p-3 rounded-md shadow-sm">
            💡 Tip: Match keywords like “microservices”, “Docker” — they appear in the job ad.
          </p>
        </div>

        <div className="mt-auto flex gap-3">
          <input
            type="text"
            placeholder="Ask for help or rewrite a section..."
            className="w-full border px-4 py-2 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
          <button
              className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-md shadow-md transition duration-200"
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
        <div className="bg-white p-6 rounded-xl shadow-md overflow-y-auto h-full border">
          <p className="text-gray-700 whitespace-pre-line leading-relaxed text-sm">
            Dear Hiring Manager,{"\n\n"}
            I am writing to express my interest in the Backend Engineer role at Netflix...
            {/* Add more cover letter text here */}
          </p>
        </div>
      </div>
    </div>
  );
};

export default CoverLetterPage;
