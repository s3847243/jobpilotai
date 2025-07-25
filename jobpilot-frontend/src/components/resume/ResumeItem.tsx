import { useState, useRef, useEffect } from 'react';
import { Edit3, Trash2, MoreVertical } from 'lucide-react';
import { Link } from 'react-router-dom';
import ConfirmModal from '../modal/ConfirmModal';
import { AppDispatch } from '../../store';
import { useDispatch } from 'react-redux';
import { deleteResumeByIdThunk } from '../../features/resume/resumesThunk';

const ResumeItem = ({
  id,
  name,
  date,

}: {
  id: string;
  name: string;
  date: string;

}) => {
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);
  const [showConfirm, setShowConfirm] = useState(false);
  const [loading, setLoading] = useState(false);
  const dispatch = useDispatch<AppDispatch>();

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setMenuOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);
  const handleDeleteResume = () => {
    dispatch(deleteResumeByIdThunk(id))
      .unwrap()
      .then(() => console.log('Resume deleted'))
      .catch((err) => console.error('Failed to delete resume:', err));
  };
  return (
  <div className="bg-white dark:bg-slate-800 p-6 rounded-xl shadow-md dark:shadow-slate-800/50 flex flex-col gap-4 w-full max-w-sm relative transition-colors">
    <div className="flex items-start justify-between">
      <h2 className="text-lg font-semibold break-words text-slate-800 dark:text-white">{name}</h2>
      <div ref={menuRef} className="relative">
        <button
          onClick={() => setMenuOpen(!menuOpen)}
          className="text-gray-400 hover:text-gray-600 dark:text-gray-300 dark:hover:text-white"
        >
          <MoreVertical size={20} />
        </button>

        {menuOpen && (
          <div className="absolute right-0 mt-2 w-40 bg-white dark:bg-slate-800 border border-gray-200 dark:border-slate-700 rounded-md shadow-lg z-10">
            <button
              onClick={() => {
                setShowConfirm(true);
                setMenuOpen(false);
              }}
              className="w-full text-left text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 px-4 py-2 text-sm font-medium transition"
            >
              <Trash2 size={16} className="inline mr-2" />
              Delete Resume
            </button>
          </div>
        )}
      </div>
    </div>

    <div>
      <span className="bg-gray-100 dark:bg-slate-700 text-gray-600 dark:text-gray-300 text-xs font-semibold px-3 py-1 rounded-full">
        {date}
      </span>
    </div>

    <Link
      to={`/dashboard/resumes/${id}`}
      className="flex items-center justify-center gap-2 bg-gray-800 dark:bg-slate-700 hover:bg-gray-700 dark:hover:bg-slate-600 text-white font-semibold py-2 px-4 rounded-full w-full transition-colors"
    >
      <Edit3 size={18} />
      Open
    </Link>

    {/* Confirm Modal */}
    <ConfirmModal
      isOpen={showConfirm}
      title="Delete Resume"
      message="Are you sure you want to delete this resume? This action cannot be undone."
      onConfirm={handleDeleteResume}
      onCancel={() => setShowConfirm(false)}
      loading={loading}
      confirmText="Delete"
      cancelText="Cancel"
    />
  </div>
);
};

export default ResumeItem;
