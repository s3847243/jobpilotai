
import { Edit3 } from 'lucide-react';
import { Link } from 'react-router-dom';

const CoverLetterItem = ({ id, title, jobId, date }: {
  id: string;
  title: string;
  jobId:string;

  date: string;
}) => {
  return (
    <div className="bg-white p-6 rounded-xl shadow-md flex flex-col gap-4 w-full max-w-sm">
      <div className="flex items-start justify-between">
        <h2 className="text-lg font-semibold break-words">{title}</h2>
        <button className="text-gray-400 hover:text-gray-600">&#8942;</button>
      </div>
      <div>
        <span className="bg-gray-100 text-gray-600 text-xs font-semibold px-2 py-1 rounded-full ">
          {new Date(date).toLocaleDateString()}
        </span>
      </div>
      <Link
        to={`/dashboard/job/${jobId}/cover-letter`}
        className="flex items-center justify-center gap-2 bg-gray-800 hover:bg-gray-700 text-white font-semibold py-2 px-4 rounded-full w-full"
      >
        <Edit3 size={18} />
        Editor
      </Link>
    </div>
  );
};

export default CoverLetterItem;

