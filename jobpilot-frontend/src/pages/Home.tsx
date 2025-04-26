import React from 'react'
import Sidebar from '../components/Sidebar'
import SidebarItem from '../components/SidebarItem'
import { LayoutDashboard }  from 'lucide-react'
import { BarChart3 } from 'lucide-react'
import { Boxes } from 'lucide-react'
import { Package } from 'lucide-react'
import { Receipt } from 'lucide-react'
import { Settings } from 'lucide-react' 
import { LifeBuoy } from 'lucide-react'

const Home = () => {
  return (
    <div>
        <Sidebar>
            <SidebarItem icon={<BarChart3 size={20}  />} text="Statics"  />
            <SidebarItem icon={<BarChart3 size={20}  />} text="Statics"  />
            <SidebarItem icon={<BarChart3 size={20}  />} text="Statics"  />
            <SidebarItem icon={<BarChart3 size={20}  />} text="Statics"  />
            <SidebarItem icon={<BarChart3 size={20}  />} text="Statics"  />
            <hr className='my-3'/>
            <SidebarItem icon={<BarChart3 size={20}  />} text="Statics"  />
            <SidebarItem icon={<BarChart3 size={20}  />} text="Statics"  />

        </Sidebar>
    </div>
  )
}

export default Home
