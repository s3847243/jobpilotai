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
    <section>
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-mono px-10 py-2">My Follow Up Emails</h1>
        {/* <button className="flex items-center gap-2 bg-green-600 hover:bg-lime-500 text-white font-semibold py-2 px-4 rounded-full mr-10">
          <Plus size={20} />
          Follow Up Emails
        </button> */}
      </div>

      <hr className="my-3 border-t-4 py-3" />

      <div className="grid grid-cols-4 gap-6 px-5">
        {emails.map((email) => (
          <FollowUpItem
            key={email.id}
            id={email.id}
            onDelete={handleDeleteFollowUp}
            name={email.followUpEmailName}
            jobId = {email.jobId}
            date={new Date(email.createdAt).toLocaleDateString()}
          />
        ))}
      </div>
    </section>
  );
};

export default FollowUpAll;
