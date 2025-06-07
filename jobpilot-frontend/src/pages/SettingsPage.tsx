import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { AppDispatch, RootState } from '../store'
import { updateUserThunk, logoutUserThunk, deleteAccountThunk } from '../features/user/userThunk';
import { Moon, Sun, LogOut, Trash2, MapPin, Palette, Shield, User ,Workflow} from 'lucide-react';
import { toggleTheme } from '../features/theme/themeSlice';
const SettingsPage = () => {
  const dispatch = useDispatch<AppDispatch>();
  const user = useSelector((state: RootState) => state.users);
  const theme = useSelector((state: RootState) => state.theme.mode);
    const handleToggleTheme = () => {
    dispatch(toggleTheme());
    };
  const [name, setName] = useState(user.fullName || '');
  const [jobTitle, setJobTitle] = useState(user.jobTitle || '');
  const [location, setLocation] = useState(user.location||'');
  const [darkMode, setDarkMode] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleUpdateProfile = () => {
    setLoading(true);
    dispatch(updateUserThunk({ name,location, jobTitle }))
      .unwrap()
      .then(() => console.log('Profile updated!'))
      .catch((err) => console.log('Update failed: ' + err))
      .finally(() => setLoading(false));
  };

  const handleLogout = () => {
    dispatch(logoutUserThunk())
      .unwrap()
      .then(() => alert('Logged out!'));
  };

  const handleDeleteAccount = () => {
    if (confirm('Are you sure you want to delete your account?')) {
      dispatch(deleteAccountThunk())
        .unwrap()
        .then(() => alert('Account deleted!'))
        .catch((err) => alert('Delete failed: ' + err));
    }
  };

 return (
  <div className="h-screen overflow-y-auto bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-100 dark:from-slate-900 dark:via-slate-800 dark:to-indigo-900">
    <div className="max-w-4xl mx-auto p-6 space-y-6">
      {/* Header */}
      <div className="text-center space-y-2 pt-4">
        <h1 className="text-4xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent dark:from-indigo-400 dark:to-purple-500">
          Settings
        </h1>
        <p className="text-gray-600 dark:text-gray-400">Customize your experience and manage your account</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        {/* Profile Section */}
        <div className="lg:col-span-2 space-y-4">
          <div className="bg-white/70 dark:bg-slate-800/70 backdrop-blur-lg rounded-2xl p-4 shadow-xl border border-white/20 dark:border-slate-700/50 hover:shadow-2xl transition-all duration-300">
            <div className="flex items-center space-x-3 mb-4">
              <div className="p-2 bg-gradient-to-r from-blue-500 to-indigo-500 dark:from-indigo-500 dark:to-purple-600 rounded-lg">
                <User className="text-white" size={20} />
              </div>
              <h2 className="text-xl font-semibold text-gray-800 dark:text-white">Profile Information</h2>
            </div>
            
            <div className="space-y-3">
              {/* Inputs */}
              {[
                { icon: <User size={18} />, value: name, setValue: setName, placeholder: "Full Name" },
                { icon: <Workflow size={18} />, value: jobTitle, setValue: setJobTitle, placeholder: "Job Title" },
                { icon: <MapPin size={18} />, value: location, setValue: setLocation, placeholder: "Location/City" }
              ].map((field, index) => (
                <div key={index} className="relative group">
                  <span className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 group-focus-within:text-blue-500 transition-colors">
                    {field.icon}
                  </span>
                  <input
                    type="text"
                    value={field.value}
                    onChange={(e) => field.setValue(e.target.value)}
                    placeholder={field.placeholder}
                    className="w-full pl-12 pr-4 py-3 bg-white/50 dark:bg-slate-700/50 border border-gray-200 dark:border-slate-600 text-gray-800 dark:text-white rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 backdrop-blur-sm"
                  />
                </div>
              ))}
              
              <button
                onClick={handleUpdateProfile}
                disabled={loading}
                className="w-full bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white py-3 px-6 rounded-xl font-medium transform hover:scale-[1.02] transition-all duration-200 shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
              >
                {loading ? (
                  <div className="flex items-center justify-center space-x-2">
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                    <span>Updating...</span>
                  </div>
                ) : (
                  'Update Profile'
                )}
              </button>
            </div>
          </div>

          {/* Preferences */}
          <div className="bg-white/70 dark:bg-slate-800/70 backdrop-blur-lg rounded-2xl p-4 shadow-xl border border-white/20 dark:border-slate-700/50 hover:shadow-2xl transition-all duration-300">
            <div className="flex items-center space-x-3 mb-4">
              <div className="p-2 bg-gradient-to-r from-purple-500 to-pink-500 dark:from-purple-600 dark:to-pink-600 rounded-lg">
                <Palette className="text-white" size={20} />
              </div>
              <h2 className="text-xl font-semibold text-gray-800 dark:text-white">Preferences</h2>
            </div>
            
            <div className="space-y-3">
              {/* Theme Toggle */}
              <div className="flex items-center justify-between p-4 bg-white/50 dark:bg-slate-700/50 rounded-xl border border-gray-200/50 dark:border-slate-600/50">
                <div className="flex items-center space-x-3">
                  {darkMode ? (
                    <Moon className="text-indigo-500" size={20} />
                  ) : (
                    <Sun className="text-amber-500" size={20} />
                  )}
                  <div>
                    <span className="font-medium text-gray-800 dark:text-white">Theme</span>
                    <p className="text-sm text-gray-500 dark:text-gray-400">
                      {localStorage.getItem('theme') =='dark' ? 'Dark' : 'Light'} mode
                    </p>
                  </div>
                </div>
                <button
                onClick={handleToggleTheme}
                className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors duration-200 ${
                    theme === 'dark' ? 'bg-gradient-to-r from-blue-600 to-indigo-600' : 'bg-gray-300'
                }`}
                >
                <span
                    className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform duration-200 ${
                    theme === 'dark' ? 'translate-x-6' : 'translate-x-1'
                    } shadow-lg`}
                />
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Sidebar */}
        <div className="space-y-4">
          <div className="bg-white/70 dark:bg-slate-800/70 backdrop-blur-lg rounded-2xl p-4 shadow-xl border border-white/20 dark:border-slate-700/50 hover:shadow-2xl transition-all duration-300">
            <div className="flex items-center space-x-3 mb-4">
              <div className="p-2 bg-gradient-to-r from-orange-500 to-red-500 dark:from-orange-600 dark:to-red-600 rounded-lg">
                <Shield className="text-white" size={20} />
              </div>
              <h2 className="text-xl font-semibold text-gray-800 dark:text-white">Account</h2>
            </div>
            
            <div className="space-y-3">
              <button
                onClick={handleLogout}
                className="w-full flex items-center space-x-3 p-3 text-amber-600 dark:text-amber-400 hover:bg-amber-50 dark:hover:bg-amber-900/20 rounded-xl transition-all duration-200 group"
              >
                <LogOut size={18} className="group-hover:scale-110 transition-transform" />
                <span className="font-medium">Sign Out</span>
              </button>
              
              <button
                onClick={handleDeleteAccount}
                className="w-full flex items-center space-x-3 p-3 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-xl transition-all duration-200 group"
              >
                <Trash2 size={18} className="group-hover:scale-110 transition-transform" />
                <span className="font-medium">Delete Account</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
);
};

export default SettingsPage;