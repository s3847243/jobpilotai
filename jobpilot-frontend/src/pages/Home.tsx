import React from 'react'
import { Link } from 'react-router-dom';

const Home = () => {
  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      
      {/* Top Nav */}
      <header className="flex justify-between items-center p-6 bg-white shadow-md">
        <h1 className="text-2xl font-bold text-gray-800">
          JobPilot AI 🚀
        </h1>
        
        <Link to="/dashboard/job-hub">
          <button className="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-4 py-2 rounded-md">
            Go to Dashboard
          </button>
        </Link>
      </header>

      {/* Main Content */}
      <main className="flex flex-col items-center justify-center flex-grow p-10 text-center">
        <h2 className="text-4xl font-bold mb-4 text-gray-800">
          Your AI Assistant for Smarter Job Applications
        </h2>
        <p className="text-lg text-gray-600 max-w-2xl mb-8">
          Upload your resumes, match them to jobs, generate personalized cover letters, 
          track your applications, and get AI-powered improvement tips to land your dream role faster.
        </p>

        <Link to="/dashboard/job-hub">
          <button className="bg-green-600 hover:bg-green-700 text-white font-semibold px-6 py-3 rounded-xl text-lg">
            Start Applying Today 🚀
          </button>
        </Link>
      </main>

      {/* Footer */}
      <footer className="p-6 text-center text-gray-500 text-sm">
        © {new Date().getFullYear()} JobPilot AI. All rights reserved.
      </footer>

    </div>
  );
};

export default Home;

