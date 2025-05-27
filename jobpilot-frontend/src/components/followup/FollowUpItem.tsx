import { Edit3, MoreVertical, Trash2 } from 'lucide-react';
import { useState, useRef, useEffect } from 'react';
import { Link } from 'react-router-dom';
import ConfirmModal from '../modal/ConfirmModal';

const FollowUpItem = ({ id, name, jobId,date,  onDelete }: { 
    id: string; 
    name: string; 
    jobId:string;
    date: string ;
    onDelete: (id: string) => void;}) => 
  {
  
   const [menuOpen, setMenuOpen] = useState(false);
     const menuRef = useRef<HTMLDivElement>(null);
     const [showConfirm, setShowConfirm] = useState(false);
       const [loading, setLoading] = useState(false);
   
  
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
    <div className="bg-white p-6 rounded-xl shadow-md flex flex-col gap-4 w-full max-w-sm">
      <div className="flex items-start justify-between">
        <h2 className="text-lg font-semibold break-words">{name}</h2>
        <div ref={menuRef} className="relative">
          <button
            onClick={() => setMenuOpen(!menuOpen)}
            className="text-gray-400 hover:text-gray-600"
          >
            <MoreVertical size={20} />
          </button>

          {menuOpen && (
            <div className="absolute right-0 mt-2 w-40 bg-white border rounded-md shadow-lg z-10">
              <button
                 onClick={() => {
                setShowConfirm(true);
                setMenuOpen(false);
                }}
                className="w-full text-left text-red-600 hover:bg-red-50 px-4 py-2 text-sm font-medium transition"
              >
                <Trash2 size={16} className="inline mr-2" />
                Delete Email
              </button>
            </div>
          )}
        </div>
      </div>

      <div>
        <span className="bg-gray-100 text-gray-600 text-xs font-semibold px-3 py-1 rounded-full">
          {date}
        </span>
      </div>

      <Link
        to={`/dashboard/job/${jobId}/follow-up`}
        className="flex items-center justify-center gap-2 bg-gray-800 hover:bg-gray-700 text-white font-semibold py-2 px-4 rounded-full w-full"
      >
        <Edit3 size={18} />
        Editor
      </Link>
           {/* Confirm Modal */}
      <ConfirmModal
        isOpen={showConfirm}
        title="Delete Email"
        message="Are you sure you want to delete this resume? This action cannot be undone."
        onConfirm={() => onDelete(id)}
        onCancel={() => setShowConfirm(false)}
        loading={loading}
        confirmText="Delete"
        cancelText="Cancel"
      />
    </div>
  );
};

export default FollowUpItem;
