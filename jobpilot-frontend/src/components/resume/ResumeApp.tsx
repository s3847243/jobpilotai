import React, { useEffect, useState } from 'react';
import { Plus, X } from 'lucide-react';
import ResumeItem from './ResumeItem';
import { useDispatch, useSelector } from 'react-redux';
import { AppDispatch,RootState } from '../../store';
import { fetchResumesThunk, uploadResumeThunk } from '../../features/resume/resumesThunk'; // Adjust import path

const ResumeApp = () => {
  const dispatch = useDispatch<AppDispatch>();
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const { resumes, loading, error } = useSelector((state: RootState) => state.resumes);

  useEffect(() => {
    dispatch(fetchResumesThunk());
  }, [dispatch]);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      setSelectedFile(e.target.files[0]);
    }
  };
  // const handleDeleteResume = async (id: string) => {
  //   try {
  //     await deleteResumeById(id); // Assume you have this API
  //     setResumes((prev) => prev.filter((r) => r.id !== id));
  //     console.log("Resume deleted");
  //   } catch (err) {
  //     console.error("Failed to delete resume:", err);
  //   }
  // };


  // const handleUpload = async () => {
  //   if (!selectedFile) return;
  //   setUploading(true);
  //   try {
  //     await uploadResume(selectedFile);
  //     setModalOpen(false);
  //     setSelectedFile(null);
  //     // Refresh resumes list
  //     const updatedResumes = await fetchResumes();
  //     setResumes(updatedResumes);
  //   } catch (err) {
  //     console.error('Upload failed:', err);
  //   } finally {
  //     setUploading(false);
  //   }
  // };

  const handleUpload = () => {
    if (!selectedFile) return;
    setUploading(true);

    dispatch(uploadResumeThunk(selectedFile))
      .unwrap()
      .then(() => {
        console.log('Resume uploaded');
        setModalOpen(false);
        setSelectedFile(null);
      })
      .catch((err) => {
        console.error('Upload failed:', err);
      });
  };
  return (
    <section>
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-mono px-10 py-2">My Resumes</h1>
        <button
          onClick={() => setModalOpen(true)}
          className="flex items-center gap-2 bg-green-600 hover:bg-lime-500 text-white font-semibold py-2 px-4 rounded-full mr-10 transition-transform hover:scale-105"
        >
          <Plus size={20} />
          Upload Resume
        </button>
      </div>

      <hr className="my-3 border-t-4 py-3" />

      <div className="grid grid-cols-4 gap-6 px-5">
        {resumes.map((resume) => (
          <ResumeItem
            key={resume.id}
            id={resume.id}
            name={resume.filename}
            date={new Date(resume.uploadedAt).toLocaleDateString()}
          />
        ))}
      </div>

      {/* Modal */}
      {modalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-xl shadow-lg max-w-md w-full space-y-4 relative animate-fade-in">
            <button
              onClick={() => setModalOpen(false)}
              className="absolute top-2 right-2 text-gray-500 hover:text-gray-700"
            >
              <X size={20} />
            </button>

            <h2 className="text-xl font-semibold text-gray-800">Upload a Resume</h2>
            <input
              type="file"
              accept=".pdf,.doc,.docx"
              onChange={handleFileChange}
              className="border border-gray-300 rounded-md p-2 w-full"
            />
            <button
              onClick={handleUpload}
              disabled={!selectedFile || uploading}
              className={`w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded-md ${
                uploading ? 'opacity-50 cursor-not-allowed' : ''
              }`}
            >
              {uploading ? 'Uploading...' : 'Upload'}
            </button>
          </div>
        </div>
      )}
    </section>
  );
};

export default ResumeApp;

