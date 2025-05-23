

import React, { useEffect, useState } from 'react';
import { Plus } from 'lucide-react';
import CoverLetterItem from './CoverLetterItem';
import { fetchCoverLetters } from '../../api/CoverLetterApi';
import { CoverLetters } from '../../types/CoverLetter';

const CoverLetter = () => {
  const [coverLetters, setCoverLetters] = useState<CoverLetters[]>([]);

  useEffect(() => {
    const load = async () => {
      const result = await fetchCoverLetters();
      setCoverLetters(result);
    };
    load();
  }, []);

  return (
    <section>
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-mono px-10 py-2">My Cover Letters</h1>
        <button className="flex items-center gap-2 bg-green-600 hover:bg-lime-500 text-white font-semibold py-2 px-4 rounded-full mr-10">
          <Plus size={20} />
          Cover Letter
        </button>
      </div>
      <hr className="my-3 border-t-4 py-3" />
      <div className="grid grid-cols-4 gap-6 px-5">
        {coverLetters.map((cl) => (
          <CoverLetterItem
            key={cl.id}
            id={cl.id}
            jobId = {cl.jobId}
            title={"just rnadom"}
            company={"cl.job.company"}
            date={cl.createdAt}
          />
        ))}
      </div>
    </section>
  );
};

export default CoverLetter;
