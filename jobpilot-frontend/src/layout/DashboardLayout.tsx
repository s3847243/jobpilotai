import React from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from '../components/sidebar/Sidebar'
import SidebarItem from '../components/sidebar/SidebarItem';    
import { BarChart3 } from 'lucide-react'
import { Settings } from 'lucide-react' 
import { File } from 'lucide-react'
import { Edit } from 'lucide-react'
import { PersonStanding } from 'lucide-react'
import { Moon } from 'lucide-react'
const DashboardLayout = () => {
  return (
    <div className="flex min-h-screen">
      <Sidebar>
            <SidebarItem icon={<BarChart3 size={20}  />} text="Job Applications" to="/dashboard/job-hub"  />
            <SidebarItem icon={<File size={20}  />} text="Resumes" to="/dashboard/resumes" />
            <SidebarItem icon={<Edit size={20}  />} text="Cover letters" to="/dashboard/resumes" />
            <SidebarItem icon={<PersonStanding size={20}  />} text="Job Interviews" to="/dashboard/job-hub" />
            <SidebarItem icon={<BarChart3 size={20}  />} text="Follow Up Email" to="/dashboard/job-hub"  />
            <hr className='my-3'/>
            <SidebarItem icon={<Settings size={20}  />} text="Settings" to="/dashboard/job-hub" />
            <SidebarItem icon={<Moon size={20}  />} text="Theme" to="/dashboard/job-hub" />

        </Sidebar>
      <div className="flex-1 bg-gray-50 p-6">
        <Outlet />
      </div>
    </div>
  );
};

export default DashboardLayout;