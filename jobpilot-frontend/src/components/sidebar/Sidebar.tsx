import { createContext, useState,ReactNode } from 'react'
import { MoreVertical, ChevronLast, ChevronFirst, BarChart3, User } from "lucide-react"
type SidebarContextType = {
    expanded: boolean;
};
import { RootState } from '../../store';
import { useSelector } from 'react-redux';

export const SidebarContext = createContext<SidebarContextType | null>(null);
  
type SidebarProps = {
    children: ReactNode;
};
const Sidebar = ({ children }: SidebarProps) => {
    const [expanded,setExpanded] = useState(true);
      const user = useSelector((state: RootState) => state.users); // Assuming your slice name is "users"
      console.log(user);
return (
  <aside className={`
    transition-all duration-300 ease-in-out
    bg-white dark:bg-slate-900
    ${expanded ? 'w-72' : 'w-20'}
    border-r border-gray-200 dark:border-gray-700
    shadow-lg
  `}>
    <nav className="h-full flex flex-col">
      {/* Header */}
      <div className="p-4 border-b border-gray-200 dark:border-gray-700">
        <div className="flex items-center justify-between">
          <div className={`
            overflow-hidden transition-all duration-300 ease-in-out
            ${expanded ? 'w-auto opacity-100' : 'w-0 opacity-0'}
          `}>
            <div className="flex items-center space-x-3">
              <div className="w-8 h-8 bg-gradient-to-r from-blue-500 to-blue-600 dark:from-indigo-500 dark:to-purple-600 rounded-lg flex items-center justify-center">
                <BarChart3 size={20} className="text-white" />
              </div>
              <div>
                <h1 className="text-lg font-bold text-gray-900 dark:text-gray-100">JobTracker</h1>
                <p className="text-xs text-gray-500 dark:text-gray-400">Career Dashboard</p>
              </div>
            </div>
          </div>

          <button
            onClick={() => setExpanded(curr => !curr)}
            className={`
              p-2 rounded-lg transition-all duration-200
              hover:bg-gray-100 dark:hover:bg-gray-800
              active:bg-gray-200 dark:active:bg-gray-700
              ${!expanded ? 'mx-auto' : ''}
            `}
          >
            {expanded ? (
              <ChevronFirst size={20} className="text-gray-600 dark:text-gray-300" />
            ) : (
              <ChevronLast size={20} className="text-gray-600 dark:text-gray-300" />
            )}
          </button>
        </div>
      </div>

      {/* Navigation */}
      <div className="flex-1 p-4">
        <SidebarContext.Provider value={{ expanded }}>
          <ul className="space-y-1">
            {children}
          </ul>
        </SidebarContext.Provider>
      </div>

      {/* User Profile */}
      <div className="border-t border-gray-200 dark:border-gray-700 p-4">
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 bg-gradient-to-r from-purple-500 to-pink-500 dark:from-purple-600 dark:to-pink-600 rounded-full flex items-center justify-center flex-shrink-0">
            <User size={20} className="text-white" />
          </div>

          <div className={`
            overflow-hidden transition-all duration-300 ease-in-out flex-1
            ${expanded ? 'w-auto opacity-100' : 'w-0 opacity-0'}
          `}>
            <div className="flex items-center justify-between">
              <div className="min-w-0 flex-1">
                <h4 className="text-sm font-semibold text-gray-900 dark:text-gray-100 truncate">
                  {user.fullName || 'Guest User'}
                </h4>
                <p className="text-xs text-gray-500 dark:text-gray-400 truncate">
                  {user.email || 'Not logged in'}
                </p>
              </div>
              <button className="p-1 rounded-md hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors">
                <MoreVertical size={16} className="text-gray-400 dark:text-gray-300" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </nav>
  </aside>
);
};

export default Sidebar
