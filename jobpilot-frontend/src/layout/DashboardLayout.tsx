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
    <div className="flex h-screen overflow-hidden bg-gray-50">
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
        
        <div className="border-t border-gray-200 mt-6 pt-6 space-y-1">
          <SidebarItem 
            icon={<Settings size={20} />} 
            text="Settings" 
            to="/dashboard/settings" 
            active={activeItem === '/dashboard/settings'}
            onClick={() => setActiveItem('/dashboard/settings')}
          />

        </div>
      </Sidebar>
      
      {/* <div className="flex-1 overflow-y-auto">
        <div className="p-8">
          <div className="max-w-7xl mx-auto">
            <h1 className="text-3xl font-bold text-gray-900 mb-8">Dashboard</h1>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">Recent Applications</h3>
                <p className="text-gray-600">Track your latest job applications</p>
              </div>
              <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">Resume Stats</h3>
                <p className="text-gray-600">View your resume performance</p>
              </div>
              <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">Quick Actions</h3>
                <p className="text-gray-600">Create new documents</p>
              </div>
            </div>
          </div>
        </div>
      </div> */}
      <div className="flex-1 overflow-y-auto bg-gray-50 ">
        <Outlet />
      </div>
    </div>
  );
};

export default DashboardLayout;