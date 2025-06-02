import  { useEffect } from 'react';
import FollowUpItem from './FollowUpItem';

import { useDispatch,useSelector } from 'react-redux';
import { AppDispatch,RootState } from '../../store';
import {fetchAllFollowUpsThunk } from '../../features/followup/followUpThunk';
const FollowUpAll = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { followUps } = useSelector((state: RootState) => state.followUps);

  useEffect(() => {
    dispatch(fetchAllFollowUpsThunk());
  }, [dispatch]);


return (
  <section className="min-h-screen bg-white dark:bg-slate-900">
    <div className="flex items-center justify-between">
      <h1 className="text-3xl font-mono px-10 py-2 text-gray-900 dark:text-white">My Follow Up Emails</h1>
    </div>

    <hr className="my-3 border-t-4 py-3 border-gray-300 dark:border-gray-600" />

    <div className="grid grid-cols-4 gap-6 px-5">
      {followUps.map((email) => (
        <FollowUpItem
          key={email.id}
          id={email.id}

          name={email.followUpEmailName}
          jobId={email.jobId}
          date={new Date(email.createdAt).toLocaleDateString()}
        />
      ))}
    </div>
  </section>
);
};

export default FollowUpAll;
