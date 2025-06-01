import React, { useEffect, useState } from 'react';
import { Plus } from 'lucide-react';
import FollowUpItem from './FollowUpItem';
import { getAllFollowUpsForUser } from '../../api/FollowUpEmailApi';
import { FollowUpEmail } from '../../types/FollowUpEmail';
import { deleteFollowUpEmailById } from '../../api/FollowUpEmailApi';
const FollowUpAll = () => {
  const [emails, setEmails] = useState<FollowUpEmail[]>([]);

  useEffect(() => {
    const loadResumes = async () => {
      try {
        const data = await getAllFollowUpsForUser();
        setEmails(data);
      } catch (err) {
        console.error('Failed to fetch emails:', err);
      }
    };
    loadResumes();
  }, []);
  const handleDeleteFollowUp= async (id: string) => {

    try {
      await deleteFollowUpEmailById(id);
      setEmails((prev) => prev.filter((r) => r.id !== id));
      console.log("Email deleted");
    } catch (err) {
      console.error("Failed to delete email:", err);
    }
  };
return (
  <section className="min-h-screen bg-white dark:bg-slate-900">
    <div className="flex items-center justify-between">
      <h1 className="text-3xl font-mono px-10 py-2 text-gray-900 dark:text-white">My Follow Up Emails</h1>
    </div>

    <hr className="my-3 border-t-4 py-3 border-gray-300 dark:border-gray-600" />

    <div className="grid grid-cols-4 gap-6 px-5">
      {emails.map((email) => (
        <FollowUpItem
          key={email.id}
          id={email.id}
          onDelete={handleDeleteFollowUp}
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
