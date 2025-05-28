import React, { useContext } from 'react'
import { SidebarContext } from './Sidebar'
import { Link, useNavigate } from 'react-router-dom';
const SidebarItem = ({ icon, text,  active = false, to ,onClick}: { icon: React.ReactNode; text: string; active?: boolean ; to:string; onClick: () => void}) => {
    const { expanded } = useContext(SidebarContext)!
    const navigate = useNavigate();
    const handleClick = (e: { preventDefault: () => void; }) => {
      e.preventDefault();
      if (onClick) onClick();
      navigate(to); // uncomment this if using useNavigate hook
    };
 return (
<li className="group relative">
      <a
        href={to}
        onClick={handleClick}
        className={`
          flex items-center px-3 py-2.5 rounded-lg transition-all duration-200 ease-in-out
          font-medium text-sm group relative cursor-pointer
          ${active 
            ? 'bg-gradient-to-r from-blue-500 to-blue-600 text-white shadow-lg shadow-blue-500/25' 
            : 'text-gray-700 hover:bg-gray-100 hover:text-gray-900'
          }
        `}
      >
        <div className={`
          flex-shrink-0 transition-colors duration-200
          ${active ? 'text-white' : 'text-gray-500 group-hover:text-gray-700'}
        `}>
          {icon}
        </div>
        
        <span className={`
          overflow-hidden transition-all duration-300 ease-in-out
          ${expanded ? 'w-52 ml-3 opacity-100' : 'w-0 ml-0 opacity-0'}
        `}>
          {text}
        </span>
        
        {active && expanded && (
          <div className="ml-auto">
            <div className="w-2 h-2 bg-white rounded-full opacity-75"></div>
          </div>
        )}
        
    {/* Tooltip inside the item, clipped within the Sidebar */}
    {!expanded && (
      <div className={`
        absolute left-full top-1/2 transform -translate-y-1/2 ml-3 px-3 py-1.5 rounded-lg text-sm font-medium
        bg-gray-900 text-white whitespace-nowrap shadow-lg opacity-0 group-hover:opacity-100 transition-all duration-200 z-50
      `}>
        {text}
        <div className="absolute left-0 top-1/2 transform -translate-y-1/2 -translate-x-2 border-4 border-transparent border-r-gray-900"></div>
      </div>
    )}
      </a>
    </li>
  );
};


export default SidebarItem
