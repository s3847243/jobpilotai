import React from 'react'
import Sidebar from '../components/sidebar/Sidebar'
import SidebarItem from '../components/sidebar/SidebarItem'
import { LayoutDashboard }  from 'lucide-react'
import { BarChart3 } from 'lucide-react'
import { Boxes } from 'lucide-react'
import { Package } from 'lucide-react'
import { Receipt } from 'lucide-react'
import { Settings } from 'lucide-react' 
import { LifeBuoy } from 'lucide-react'
import { File } from 'lucide-react'
import { Edit } from 'lucide-react'
import { PersonStanding } from 'lucide-react'
import { Moon } from 'lucide-react'
import JobApp from '../components/job-applications/JobApp'
const Home = () => {
  return (
    <div className="flex">
        <Sidebar>
            <SidebarItem icon={<BarChart3 size={20}  />} text="Job Applications"  />
            <SidebarItem icon={<File size={20}  />} text="Resumes"  />
            <SidebarItem icon={<Edit size={20}  />} text="Cover letters"  />
            <SidebarItem icon={<PersonStanding size={20}  />} text="Job Interviews"  />
            <SidebarItem icon={<BarChart3 size={20}  />} text="Follow Up Email"  />
            <hr className='my-3'/>
            <SidebarItem icon={<Settings size={20}  />} text="Settings"  />
            <SidebarItem icon={<Moon size={20}  />} text="Theme"  />

        </Sidebar>
        <div className="flex-1 p-6"> {/* Add padding for nicer look */}
          <JobApp />
        </div>

    </div>
  )
}

export default Home
