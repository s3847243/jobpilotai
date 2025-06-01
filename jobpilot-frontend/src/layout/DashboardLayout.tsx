import {  useState } from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from '../components/sidebar/Sidebar'
import SidebarItem from '../components/sidebar/SidebarItem';    
import { BarChart3, Mail } from 'lucide-react'
import { Settings } from 'lucide-react' 
import { File } from 'lucide-react'
import { Edit } from 'lucide-react'

const DashboardLayout = () => {
    const [activeItem, setActiveItem] = useState('/dashboard/job-hub');

return (
  <div className="flex h-screen overflow-hidden bg-gray-50 dark:bg-slate-900 text-gray-800 dark:text-gray-100">
    <Sidebar>
      <div className="space-y-1">
        <SidebarItem 
          icon={<BarChart3 size={20} />} 
          text="Job Applications" 
          to="/dashboard/job-hub" 
          active={activeItem === '/dashboard/job-hub'}
          onClick={() => setActiveItem('/dashboard/job-hub')}
        />
        <SidebarItem 
          icon={<File size={20} />} 
          text="Resumes" 
          to="/dashboard/resumes" 
          active={activeItem === '/dashboard/resumes'}
          onClick={() => setActiveItem('/dashboard/resumes')}
        />
        <SidebarItem 
          icon={<Edit size={20} />} 
          text="Cover Letters" 
          to="/dashboard/cover-letters" 
          active={activeItem === '/dashboard/cover-letters'}
          onClick={() => setActiveItem('/dashboard/cover-letters')}
        />
        <SidebarItem 
          icon={<Mail size={20} />} 
          text="Follow Up" 
          to="/dashboard/follow-ups" 
          active={activeItem === '/dashboard/follow-ups'}
          onClick={() => setActiveItem('/dashboard/follow-ups')}
        />
      </div>
      
      <div className="border-t border-gray-200 dark:border-gray-700 mt-6 pt-6 space-y-1">
        <SidebarItem 
          icon={<Settings size={20} />} 
          text="Settings" 
          to="/dashboard/settings" 
          active={activeItem === '/dashboard/settings'}
          onClick={() => setActiveItem('/dashboard/settings')}
        />
      </div>
    </Sidebar>

    <div className="flex-1 overflow-y-auto bg-gray-50 dark:bg-slate-900 p-4 transition-colors duration-300">
      <Outlet />
    </div>
  </div>
);
};

export default DashboardLayout;