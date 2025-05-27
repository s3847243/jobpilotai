import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getResumeById } from '../../api/ResumeApi';
import { Resume } from '../../types/Resume';
import { getJobsByResumeId } from '../../api/ResumeApi';
import { JobSummaryDTO } from '../../types/JobSummaryDTO';
import { 
  User, 
  Mail, 
  Phone, 
  FileText, 
  Award, 
  Briefcase, 
  Code, 
  Eye,
  Download,
  ExternalLink,
  Calendar,
  MapPin,
  Star,
  TrendingUp,
  Target,
  ArrowRight,
  CheckCircle,
  Clock,
  XCircle,
  ChevronRight
} from 'lucide-react';
const ResumeDetails: React.FC = () => {
  const { resumeId } = useParams<{ resumeId: string }>();
  const [resume, setResume] = useState<Resume | null>(null);
  const [jobs, setJobs] = useState<JobSummaryDTO[]>([]);
  const [activeSection, setActiveSection] = useState('overview');
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();
  useEffect(() => {
    // Simulate loading
    setTimeout(() => setLoading(false), 1500);
  }, []);
    useEffect(() => {
    if (!resumeId) return;
    getResumeById(resumeId).then(setResume);

    getJobsByResumeId(resumeId).then(setJobs);
  }, [resumeId]);
  const getStatusIcon = (status: string) => {
    switch (status?.toLowerCase()) {
      case 'applied':
        return <CheckCircle className="w-4 h-4 text-green-500" />;
      case 'pending':
        return <Clock className="w-4 h-4 text-yellow-500" />;
      case 'rejected':
        return <XCircle className="w-4 h-4 text-red-500" />;
      default:
        return <Clock className="w-4 h-4 text-gray-400" />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status?.toLowerCase()) {
      case 'applied':
        return 'bg-green-100 text-green-800 border-green-200';
      case 'pending':
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'rejected':
        return 'bg-red-100 text-red-800 border-red-200';
      default:
        return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  if (loading || !resume) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-50 to-blue-50 flex items-center justify-center">
        <div className="text-center space-y-4">
          <div className="animate-spin rounded-full h-16 w-16 border-4 border-blue-200 border-t-blue-600 mx-auto"></div>
          <p className="text-gray-600 font-medium">Loading resume details...</p>
          <div className="flex space-x-2 justify-center">
            <div className="w-2 h-2 bg-blue-600 rounded-full animate-bounce"></div>
            <div className="w-2 h-2 bg-blue-600 rounded-full animate-bounce" style={{animationDelay: '0.1s'}}></div>
            <div className="w-2 h-2 bg-blue-600 rounded-full animate-bounce" style={{animationDelay: '0.2s'}}></div>
          </div>
        </div>
      </div>
    );
  }


  if (!resume) return <div className="p-6 text-center text-gray-500 animate-pulse">Loading resume details...</div>;
  console.log(resume);
  return (
    <div className="min-h-screen bg-white from-slate-50 to-blue-50">

      <div className="bg-white shadow-lg rounded-3xl border-b border-gray-100 animate-slideDown">
        <div className="max-w-7xl mx-auto px-6 py-6">
          <div className="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-4">
            <div className="flex items-center space-x-4 animate-fadeIn">
              <div className="p-3 bg-gradient-to-r from-blue-500 to-indigo-600 rounded-xl text-white shadow-lg">
                <FileText className="w-6 h-6" />
              </div>
              <div>
                <h1 className="text-3xl font-bold text-gray-900">Resume Analysis</h1>
                <p className="text-gray-600">Comprehensive overview and insights</p>
              </div>
            </div>
            <div className="flex items-center space-x-3  gap-3 flex-wrap">
              <button
                onClick={() => window.open(resume.s3Url, '_blank')}
                className="flex items-center space-x-2 px-5 py-3 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-xl font-medium transition-all duration-300 hover:shadow-lg transform hover:-translate-y-1"
              >
                <Eye className="w-5 h-5" />
                <span>View Resume</span>
              </button>
              <button
                onClick={() => window.open(resume.s3Url, '_blank')}
                className="flex items-center space-x-2 px-5 py-3 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white rounded-xl font-medium transition-all duration-300 hover:shadow-lg transform hover:-translate-y-1"
              >
                <Download className="w-5 h-5" />
                <span>Download</span>
              </button>
            </div>
          </div>
        </div>
      </div>


      <div className="max-w-7xl mx-auto px-1 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
          <div className="lg:col-span-1 space-y-6">
            {/* Profile Card */}
            <div className="bg-white rounded-2xl shadow-xl border border-gray-100 overflow-hidden transform transition-all duration-500 hover:shadow-2xl hover:-translate-y-1 animate-slideUp">
              <div className="bg-gradient-to-br from-blue-500 via-indigo-600 to-purple-600 p-6 text-white relative overflow-hidden">
                <div className="absolute inset-0 bg-black opacity-10"></div>
                <div className="relative z-10 flex items-center space-x-4">
                  <div className="p-3 bg-white bg-opacity-20 rounded-full backdrop-blur-sm">
                    <User className="w-8 h-8" />
                  </div>
                  <div>
                    <h3 className="text-xl font-bold">{resume.parsedName}</h3>
                    <p className="text-blue-100">Professional Profile</p>
                  </div>
                </div>
              </div>
              <div className="p-6 space-y-4">
                <div className="flex items-center space-x-3 text-gray-700 hover:text-blue-600 transition-colors cursor-pointer">
                  <Mail className="w-5 h-5 text-gray-400" />
                  <span className="text-sm">{resume.parsedEmail}</span>
                </div>
                <div className="flex items-center space-x-3 text-gray-700 hover:text-blue-600 transition-colors cursor-pointer">
                  <Phone className="w-5 h-5 text-gray-400" />
                  <span className="text-sm">{resume.parsedPhone}</span>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-6 transform transition-all duration-500 hover:shadow-2xl hover:-translate-y-1 animate-slideUp" style={{animationDelay: '0.1s'}}>
              <h3 className="text-lg font-semibold text-gray-900 mb-6 flex items-center">
                <TrendingUp className="w-5 h-5 mr-2 text-blue-600" />
                Performance Metrics
              </h3>
              <div className="space-y-6">
                <div className="relative">
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-gray-600 text-sm">Skills Proficiency</span>
                    <span className="font-bold text-blue-600 text-lg">{resume.parsedSkills?.length || 0}</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div className="bg-gradient-to-r from-blue-500 to-indigo-600 h-2 rounded-full animate-fillBar" style={{width: '85%'}}></div>
                  </div>
                </div>
                <div className="relative">
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-gray-600 text-sm">Applications</span>
                    <span className="font-bold text-indigo-600 text-lg">{jobs.length}</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div className="bg-gradient-to-r from-indigo-500 to-purple-600 h-2 rounded-full animate-fillBar" style={{width: `${Math.min(jobs.length * 20, 100)}%`, animationDelay: '0.2s'}}></div>
                  </div>
                </div>
                <div className="relative">
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-gray-600 text-sm">Success Rate</span>
                    <span className="font-bold text-green-600 text-lg">
                      {jobs.length > 0 ? Math.round((jobs.filter(j => j.status?.toLowerCase() === 'applied').length / jobs.length) * 100) : 0}%
                    </span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div className="bg-gradient-to-r from-green-500 to-emerald-600 h-2 rounded-full animate-fillBar" style={{width: `${jobs.length > 0 ? (jobs.filter(j => j.status?.toLowerCase() === 'applied').length / jobs.length) * 100 : 0}%`, animationDelay: '0.4s'}}></div>
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-6 animate-slideUp" style={{animationDelay: '0.2s'}}>
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Sections</h3>
              <nav className="space-y-2">
                {[
                  { id: 'overview', label: 'Overview', icon: User },
                  { id: 'skills', label: 'Skills', icon: Award },
                  { id: 'applications', label: 'Applications', icon: Target }
                ].map(({ id, label, icon: Icon }) => (
                  <button
                    key={id}
                    onClick={() => setActiveSection(id)}
                    className={`w-full flex items-center justify-between px-4 py-3 rounded-xl text-left transition-all duration-300 transform hover:-translate-x-1 ${
                      activeSection === id
                        ? 'bg-gradient-to-r from-blue-50 to-indigo-50 text-blue-700 border-l-4 border-blue-600 shadow-md'
                        : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                    }`}
                  >
                    <div className="flex items-center space-x-3">
                      <Icon className="w-5 h-5" />
                      <span className="font-medium">{label}</span>
                    </div>
                    <ChevronRight className={`w-4 h-4 transition-transform duration-300 ${activeSection === id ? 'rotate-90 text-blue-600' : 'text-gray-400'}`} />
                  </button>
                ))}
              </nav>
            </div>
          </div>

          {/* Main Content */}
          <div className="lg:col-span-3 space-y-8">
            {/* Overview Section */}
            {activeSection === 'overview' && (
              <div className="space-y-8 animate-fadeIn">
                <div className="bg-white rounded-3xl shadow-xl border border-gray-100 p-8 transform transition-all duration-500 hover:shadow-2xl">
                  <div className="flex items-center space-x-4 mb-8">
                    <div className="p-3 bg-gradient-to-r from-blue-100 to-indigo-100 rounded-2xl">
                      <User className="w-8 h-8 text-blue-600" />
                    </div>
                    <div>
                      <h2 className="text-3xl font-bold text-gray-900">Professional Summary</h2>
                      <p className="text-gray-500">Comprehensive career overview</p>
                    </div>
                  </div>
                  <div className="prose max-w-none">
                    <p className="text-gray-700 leading-relaxed text-lg font-light">
                      {resume.parsedSummary}
                    </p>
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                  <div className="group bg-gradient-to-br from-blue-50 via-blue-100 to-indigo-100 rounded-2xl p-6 border border-blue-200 transform transition-all duration-500 hover:scale-105 hover:shadow-xl cursor-pointer animate-slideUp">
                    <div className="flex items-center space-x-4 mb-4">
                      <div className="p-3 bg-blue-500 rounded-xl text-white group-hover:bg-blue-600 transition-colors">
                        <Award className="w-8 h-8" />
                      </div>
                      <h3 className="font-bold text-gray-900 text-lg">Technical Skills</h3>
                    </div>
                    <p className="text-4xl font-bold text-blue-600 mb-2">{resume.parsedSkills?.length || 0}</p>
                    <p className="text-gray-600">Core Competencies</p>
                  </div>

                  <div className="group bg-gradient-to-br from-green-50 via-green-100 to-emerald-100 rounded-2xl p-6 border border-green-200 transform transition-all duration-500 hover:scale-105 hover:shadow-xl cursor-pointer animate-slideUp" style={{animationDelay: '0.1s'}}>
                    <div className="flex items-center space-x-4 mb-4">
                      <div className="p-3 bg-green-500 rounded-xl text-white group-hover:bg-green-600 transition-colors">
                        <Target className="w-8 h-8" />
                      </div>
                      <h3 className="font-bold text-gray-900 text-lg">Job Applications</h3>
                    </div>
                    <p className="text-4xl font-bold text-green-600 mb-2">{jobs.length}</p>
                    <p className="text-gray-600">Active Pursuits</p>
                  </div>

                  <div className="group bg-gradient-to-br from-purple-50 via-purple-100 to-violet-100 rounded-2xl p-6 border border-purple-200 transform transition-all duration-500 hover:scale-105 hover:shadow-xl cursor-pointer animate-slideUp" style={{animationDelay: '0.2s'}}>
                    <div className="flex items-center space-x-4 mb-4">
                      <div className="p-3 bg-purple-500 rounded-xl text-white group-hover:bg-purple-600 transition-colors">
                        <Star className="w-8 h-8" />
                      </div>
                      <h3 className="font-bold text-gray-900 text-lg">Resume Score</h3>
                    </div>
                    <p className="text-4xl font-bold text-purple-600 mb-2">{Math.round(resume.atsScore/10)}</p>
                    <p className="text-gray-600">ATS Compatibility</p>
                  </div>
                </div>
              </div>
            )}

            {/* Skills Section */}
            {activeSection === 'skills' && (
              <div className="bg-white rounded-3xl shadow-xl border border-gray-100 p-8 animate-fadeIn transform transition-all duration-500 hover:shadow-2xl">
                <div className="flex items-center space-x-4 mb-8">
                  <div className="p-3 bg-gradient-to-r from-green-100 to-emerald-100 rounded-2xl">
                    <Award className="w-8 h-8 text-green-600" />
                  </div>
                  <div>
                    <h2 className="text-3xl font-bold text-gray-900">Technical Expertise</h2>
                    <p className="text-gray-500">Core competencies and technologies</p>
                  </div>
                </div>
                {resume.parsedSkills && resume.parsedSkills.length > 0 ? (
                  <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                    {resume.parsedSkills.map((skill, index) => (
                      <div
                        key={skill}
                        className="group relative bg-gradient-to-br from-blue-50 to-indigo-50 border-2 border-blue-100 hover:border-blue-300 rounded-2xl px-6 py-4 text-center transform transition-all duration-500 hover:scale-110 hover:shadow-lg cursor-pointer animate-slideUp"
                        style={{ animationDelay: `${index * 50}ms` }}
                      >
                        <div className="absolute inset-0 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-2xl opacity-0 group-hover:opacity-10 transition-opacity duration-300"></div>
                        <span className="relative z-10 text-blue-800 font-semibold group-hover:text-blue-900 transition-colors text-sm">
                          {skill}
                        </span>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="text-center py-16">
                    <Award className="w-20 h-20 text-gray-300 mx-auto mb-6" />
                    <p className="text-gray-500 font-medium text-xl mb-2">No skills extracted yet</p>
                    <p className="text-gray-400">Skills will appear here after AI processing</p>
                  </div>
                )}
              </div>
            )}

            {/* Experience Section */}
            {activeSection === 'experience' && (
              <div className="bg-white rounded-3xl shadow-xl border border-gray-100 p-8 animate-fadeIn transform transition-all duration-500 hover:shadow-2xl">
                <div className="flex items-center space-x-4 mb-8">
                  <div className="p-3 bg-gradient-to-r from-purple-100 to-violet-100 rounded-2xl">
                    <Briefcase className="w-8 h-8 text-purple-600" />
                  </div>
                  <div>
                    <h2 className="text-3xl font-bold text-gray-900">Professional Experience</h2>
                    <p className="text-gray-500">Career journey and achievements</p>
                  </div>
                </div>
                <div className="bg-gradient-to-br from-gray-50 to-gray-100 rounded-2xl p-8 border-2 border-gray-200 hover:border-gray-300 transition-all duration-300">
                  <pre className="whitespace-pre-wrap text-gray-700 font-mono text-sm leading-relaxed">
                    {resume.uploadedAt}
                  </pre>
                </div>
              </div>
            )}

            {/* Projects Section */}
            {activeSection === 'projects' && (
              <div className="bg-white rounded-3xl shadow-xl border border-gray-100 p-8 animate-fadeIn transform transition-all duration-500 hover:shadow-2xl">
                <div className="flex items-center space-x-4 mb-8">
                  <div className="p-3 bg-gradient-to-r from-indigo-100 to-purple-100 rounded-2xl">
                    <Code className="w-8 h-8 text-indigo-600" />
                  </div>
                  <div>
                    <h2 className="text-3xl font-bold text-gray-900">Featured Projects</h2>
                    <p className="text-gray-500">Key achievements and innovations</p>
                  </div>
                </div>
                <div className="text-center py-16">
                  <div className="relative">
                    <Code className="w-20 h-20 text-gray-300 mx-auto mb-6 animate-pulse" />
                    <div className="absolute inset-0 flex items-center justify-center">
                      <div className="w-16 h-16 border-4 border-blue-200 border-t-blue-600 rounded-full animate-spin"></div>
                    </div>
                  </div>
                  <p className="text-gray-500 font-medium text-xl mb-2">AI Project Extraction</p>
                  <p className="text-gray-400 mb-6">Analyzing resume for key projects and achievements</p>
                  <div className="inline-flex items-center space-x-2 bg-gradient-to-r from-blue-50 to-indigo-50 text-blue-700 px-6 py-3 rounded-full border border-blue-200">
                    <Clock className="w-5 h-5" />
                    <span className="font-medium">Processing...</span>
                  </div>
                </div>
              </div>
            )}

            {/* Applications Section */}
            {activeSection === 'applications' && (
              <div className="space-y-8 animate-fadeIn">
                <div className="bg-white rounded-3xl shadow-xl border border-gray-100 p-8 transform transition-all duration-500 hover:shadow-2xl">
                  <div className="flex items-center justify-between mb-8">
                    <div className="flex items-center space-x-4">
                      <div className="p-3 bg-gradient-to-r from-orange-100 to-red-100 rounded-2xl">
                        <Target className="w-8 h-8 text-orange-600" />
                      </div>
                      <div>
                        <h2 className="text-3xl font-bold text-gray-900">Application Tracker</h2>
                        <p className="text-gray-500">Monitor your job application progress</p>
                      </div>
                    </div>
                    <div className="bg-gradient-to-r from-blue-100 to-indigo-100 text-blue-800 px-6 py-3 rounded-full border border-blue-200">
                      <span className="font-bold text-lg">{jobs.length}</span>
                      <span className="ml-2 text-sm">Applications</span>
                    </div>
                  </div>

                  {jobs.length === 0 ? (
                    <div className="text-center py-16">
                      <Target className="w-20 h-20 text-gray-300 mx-auto mb-6" />
                      <p className="text-gray-500 font-medium text-xl mb-2">No applications yet</p>
                      <p className="text-gray-400 mb-8">Start your job search journey today</p>
                      <Link to="/dashboard/job-hub">
                      <button className="inline-flex items-center space-x-3 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white px-8 py-4 rounded-2xl font-medium transition-all duration-300 hover:shadow-lg transform hover:-translate-y-1">
                        <span className="text-lg">Start Applying</span>
                        <ArrowRight className="w-5 h-5" />
                      </button>
                      </Link>
                    </div>
                  ) : (
                    <div className="space-y-6">
                      {jobs.map((job, index) => (
                        <div
                          key={job.id}
                          className="group relative bg-gradient-to-r from-gray-50 to-white hover:from-white hover:to-blue-50 border-2 border-gray-200 hover:border-blue-300 rounded-2xl p-6 transition-all duration-500 hover:shadow-xl transform hover:-translate-y-1 animate-slideUp"
                          style={{ animationDelay: `${index * 100}ms` }}
                        >
                          <div className="absolute inset-0 bg-gradient-to-r from-blue-500 to-indigo-600 rounded-2xl opacity-0 group-hover:opacity-5 transition-opacity duration-300"></div>
                          <div className="relative z-10 flex items-center justify-between">
                            <div className="flex-1">
                              <div className="flex items-center space-x-4 mb-3">
                                <h3 className="text-xl font-bold text-gray-900 group-hover:text-blue-900 transition-colors">
                                  {job.title}
                                </h3>
                                <div className={`flex items-center space-x-2 px-4 py-2 rounded-full border-2 font-semibold ${getStatusColor(job.status)}`}>
                                  {getStatusIcon(job.status)}
                                  <span>{job.status || 'Pending'}</span>
                                </div>
                              </div>
                              <p className="text-gray-600 font-semibold text-lg mb-3">{job.company}</p>
                              <div className="flex items-center space-x-6 text-sm text-gray-500">
                                <div className="flex items-center space-x-2">
                                  <Calendar className="w-4 h-4" />
                                  <span>Applied {job.company}</span>
                                </div>
                              </div>
                            </div>
                            <button className="flex items-center space-x-2 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white px-6 py-3 rounded-xl font-medium transition-all duration-300 hover:shadow-lg transform hover:-translate-y-1">
                              <span>View Details</span>
                              <ExternalLink className="w-4 h-4" />
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>

                {/* Call to Action */}
                {jobs.length > 0 && (
                  <div className="bg-gradient-to-br from-blue-500 via-indigo-600 to-purple-600 rounded-3xl p-8 text-white text-center transform transition-all duration-500 hover:shadow-2xl hover:-translate-y-1 relative overflow-hidden">
                    <div className="absolute inset-0 bg-black opacity-10"></div>
                    <div className="relative z-10">
                      <h3 className="text-2xl font-bold mb-4">Expand Your Opportunities</h3>
                      <p className="text-blue-100 mb-8 text-lg">Discover more positions that match your expertise</p>
                      <button className="inline-flex items-center space-x-3 bg-white text-blue-600 hover:bg-gray-100 px-8 py-4 rounded-2xl font-bold transition-all duration-300 hover:shadow-lg transform hover:-translate-y-1">
                        <span className="text-lg">Explore Job Hub</span>
                        <ArrowRight className="w-5 h-5" />
                      </button>
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>

      <style>{`
        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(30px); }
          to { opacity: 1; transform: translateY(0); }
        }
        
        @keyframes slideUp {
          from { opacity: 0; transform: translateY(50px); }
          to { opacity: 1; transform: translateY(0); }
        }
        
        @keyframes slideDown {
          from { opacity: 0; transform: translateY(-30px); }
          to { opacity: 1; transform: translateY(0); }
        }
        
        @keyframes fillBar {
          from { width: 0; }
          to { width: var(--target-width); }
        }
        
        .animate-fadeIn {
          animation: fadeIn 0.8s ease-out forwards;
        }
        
        .animate-slideUp {
          animation: slideUp 0.8s ease-out forwards;
          opacity: 0;
        }
        
        .animate-slideDown {
          animation: slideDown 0.6s ease-out forwards;
        }
        
        .animate-fillBar {
          animation: fillBar 1.5s ease-out forwards;
          width: 0;
        }
      `}</style>
    </div>
  );
};

export default ResumeDetails;

