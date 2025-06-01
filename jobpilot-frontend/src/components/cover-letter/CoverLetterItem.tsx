import React, { useState, useRef, useEffect } from 'react';
import { Edit3, MoreVertical, Trash2 } from 'lucide-react';
import { Link } from 'react-router-dom';
import ConfirmModal from '../modal/ConfirmModal';
import { useDispatch } from 'react-redux';
import { AppDispatch } from '../../store';
import { deleteCoverLetterThunk } from '../../features/coverletter/coverLetterThunks';
const CoverLetterItem = ({ id, title, jobId, date }: {
  id: string;
  title: string;
  jobId:string;
  date: string;
}) => {
    const [menuOpen, setMenuOpen] = useState(false);
     const menuRef = useRef<HTMLDivElement>(null);
     const [showConfirm, setShowConfirm] = useState(false);
       const [loading, setLoading] = useState(false);
       const dispatch = useDispatch<AppDispatch>();

    const handleDeleteCoverLetter = () => {
      dispatch(deleteCoverLetterThunk(id))
        .unwrap()
        .then(() => {
          console.log('Cover Letter deleted');
        })
        .catch((err) => {
          console.error('Failed to delete letter:', err);
        });
    };
    useEffect(() => {
      const handleClickOutside = (e: MouseEvent) => {
        if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
          setMenuOpen(false);
        }
      };
      document.addEventListener('mousedown', handleClickOutside);
      return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);
return (
  <div className="bg-white dark:bg-slate-800 p-6 rounded-xl shadow-md flex flex-col gap-4 w-full max-w-sm border border-gray-200 dark:border-slate-700 transition-colors duration-300">
    <div className="flex items-start justify-between">
      <h2 className="text-lg font-semibold break-words text-gray-900 dark:text-white">{title}</h2>
      <div ref={menuRef} className="relative">
        <button
          onClick={() => setMenuOpen(!menuOpen)}
          className="text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
        >
          <MoreVertical size={20} />
        </button>

        {menuOpen && (
          <div className="absolute right-0 mt-2 w-40 bg-white dark:bg-slate-700 border border-gray-200 dark:border-slate-600 rounded-md shadow-lg z-10">
            <button
              onClick={() => {
                setShowConfirm(true);
                setMenuOpen(false);
              }}
              className="w-full text-left text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20 px-4 py-2 text-sm font-medium transition"
            >
              <Trash2 size={16} className="inline mr-2" />
              Delete Letter
            </button>
          </div>
        )}
      </div>
    </div>

    <div>
      <span className="bg-gray-100 dark:bg-slate-700 text-gray-600 dark:text-gray-300 text-xs font-semibold px-2 py-1 rounded-full">
        {new Date(date).toLocaleDateString()}
      </span>
    </div>

    <Link
      to={`/dashboard/job/${jobId}/cover-letter`}
      className="flex items-center justify-center gap-2 bg-gray-800 hover:bg-gray-700 dark:bg-slate-700 dark:hover:bg-slate-600 text-white font-semibold py-2 px-4 rounded-full w-full"
    >
      <Edit3 size={18} />
      Editor
    </Link>

    {/* Confirm Modal */}
    <ConfirmModal
      isOpen={showConfirm}
      title="Delete Letter"
      message="Are you sure you want to delete this cover letter? This action cannot be undone."
      onConfirm={handleDeleteCoverLetter}
      onCancel={() => setShowConfirm(false)}
      loading={loading}
      confirmText="Delete"
      cancelText="Cancel"
    />
  </div>
);
};

export default CoverLetterItem;

