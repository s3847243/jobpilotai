import React, { useState, useEffect } from 'react';
import { ArrowRight, FileText, Mail, BarChart3, Zap, Shield, Target, ChevronDown } from 'lucide-react';
import './home.styles.css'
import { Link } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { RootState } from '../store';

interface User {
  isLoggedIn: boolean;
  name?: string;
}

const JobPilotLanding: React.FC = () => {
  // const [user, setUser] = useState<User>({ isLoggedIn: false });
  const [isScrolled, setIsScrolled] = useState(false);
  const isAuthenticated = useSelector((state: RootState) => state.users.isAuthenticated);

  // // Simulate checking authentication status
  // useEffect(() => {
  //   // In a real app, you'd check httpOnly cookies here
  //   // For demo purposes, randomly set login status
  //   const checkAuth = () => {
  //     const isLoggedIn = Math.random() > 0.5; // Random for demo
  //     setUser({ 
  //       isLoggedIn, 
  //       name: isLoggedIn ? 'John Doe' : undefined 
  //     });
  //   };
    
  //   checkAuth();
  // }, []);

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 50);
    };
    
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const features = [
    {
      icon: <FileText className="w-8 h-8" />,
      title: "Smart Cover Letters",
      description: "AI-powered cover letter generation tailored to each job posting. Upload your resume and job URL, get a personalized cover letter in seconds."
    },
    {
      icon: <Mail className="w-8 h-8" />,
      title: "Follow-up Emails",
      description: "Automated follow-up email sequences that keep you top-of-mind with recruiters. Professional, timely, and personalized."
    },
    {
      icon: <BarChart3 className="w-8 h-8" />,
      title: "Application Tracking",
      description: "Never lose track of your applications again. Comprehensive dashboard to monitor status, interviews, and responses."
    }
  ];

  const FloatingCard: React.FC<{ delay: number; children: React.ReactNode; className?: string }> = ({ delay, children, className = "" }) => (
    <div 
      className={`absolute bg-white/10 backdrop-blur-xl border border-white/20 rounded-2xl p-6 shadow-2xl ${className}`}
      style={{
        animation: `float 6s ease-in-out infinite`,
        animationDelay: `${delay}s`
      }}
    >
      {children}
    </div>
  );

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-purple-900 to-slate-900 text-white overflow-x-hidden">

      {/* Navigation */}
      <nav className={`fixed top-0 w-full z-50 transition-all duration-300 ${
        isScrolled ? 'bg-slate-900/90 backdrop-blur-xl border-b border-white/10' : 'bg-transparent'
      }`}>
        <div className="max-w-7xl mx-auto px-6 py-4">
          <div className="flex items-center justify-between">
            <div className="text-2xl font-bold gradient-text">
              JobPilot.AI
            </div>
            
            <div className="flex items-center gap-4">
              {isAuthenticated ? (
                <Link to="/dashboard/job-hub">
                <button className="px-6 py-3 bg-gradient-to-r from-emerald-500 to-emerald-600 hover:from-emerald-600 hover:to-emerald-700 rounded-xl font-semibold transition-all duration-300 hover:scale-105 hover:shadow-lg hover:shadow-emerald-500/25">
                  Go to Dashboard
                </button>
                </Link>
              ) : (
                <>
                  <Link to="/login">
                  <button className="px-6 py-3 glass-effect hover:bg-white/20 rounded-xl font-semibold transition-all duration-300 hover:scale-105">
                    Login
                  </button>
                  </Link>
                  <Link to="/register">
                  <button className="px-6 py-3 bg-gradient-to-r from-cyan-500 to-purple-600 hover:from-cyan-600 hover:to-purple-700 rounded-xl font-semibold transition-all duration-300 hover:scale-105 pulse-glow">
                    Sign Up
                  </button>
                  </Link>
                </>
              )}
            </div>
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="min-h-screen flex items-center relative overflow-hidden">
        {/* Animated Background Elements */}
        <div className="absolute inset-0">
          <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-cyan-500/10 rounded-full blur-3xl animate-pulse"></div>
          <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-purple-500/10 rounded-full blur-3xl animate-pulse" style={{ animationDelay: '2s' }}></div>
        </div>

        <div className="max-w-7xl mx-auto px-6 grid lg:grid-cols-2 gap-12 items-center relative z-10">
          <div className="space-y-8">
            <h1 className="text-5xl lg:text-7xl font-black leading-tight">
              <span className="gradient-text">Land Your</span>
              <br />
              <span className="text-white">Dream Job</span>
              <br />
              <span className="gradient-text">Faster</span>
            </h1>
            
            <p className="text-xl text-gray-300 leading-relaxed max-w-2xl">
              Upload your resume and job URL. Our AI creates personalized cover letters, 
              follow-up emails, and tracks your applications. Turn job hunting into job landing.
            </p>
            
            <div className="flex flex-col sm:flex-row gap-4">
              <Link to ="/login">
              <button className="px-8 py-4 bg-gradient-to-r from-cyan-500 to-purple-600 hover:from-cyan-600 hover:to-purple-700 rounded-xl font-bold text-lg transition-all duration-300 hover:scale-105 pulse-glow flex items-center justify-center gap-2">
                Get Started
                <ArrowRight className="w-5 h-5" />
              </button>
              </Link>
              
            </div>
            
            {/* <div className="flex items-center gap-8 pt-8">
              <div className="text-center">
                <div className="text-3xl font-bold gradient-text">10K+</div>
                <div className="text-gray-400">Jobs Applied</div>
              </div>
              <div className="text-center">
                <div className="text-3xl font-bold gradient-text">85%</div>
                <div className="text-gray-400">Response Rate</div>
              </div>
              <div className="text-center">
                <div className="text-3xl font-bold gradient-text">2.5x</div>
                <div className="text-gray-400">Faster Hiring</div>
              </div>
            </div> */}
          </div>

          {/* Floating Cards Animation */}
          <div className="relative h-96 hidden lg:block">
            <FloatingCard delay={0} className="top-0 left-0 w-64">
              <div className="flex items-center gap-3 mb-2">
                <FileText className="w-6 h-6 text-cyan-400" />
                <span className="font-semibold">Cover Letter</span>
              </div>
              <div className="text-sm text-gray-300">
                AI-generated for Software Engineer role...
              </div>
            </FloatingCard>

            <FloatingCard delay={2} className="top-12 right-0 w-56">
              <div className="flex items-center gap-3 mb-2">
                <Mail className="w-6 h-6 text-purple-400" />
                <span className="font-semibold">Follow-up</span>
              </div>
              <div className="text-sm text-gray-300">
                Generate Emails on the go
              </div>
            </FloatingCard>

            <FloatingCard delay={4} className="bottom-0 left-12 w-60">
              <div className="flex items-center gap-3 mb-2">
                <BarChart3 className="w-6 h-6 text-emerald-400" />
                <span className="font-semibold">Applications</span>
              </div>
              <div className="text-sm text-gray-300">
                12 active • 3 interviews scheduled
              </div>
            </FloatingCard>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-24 bg-black/20">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center mb-16">
            <h2 className="text-4xl lg:text-5xl font-bold mb-6">
              <span className="gradient-text">Everything You Need</span>
              <br />
              <span className="text-white">To Get Hired</span>
            </h2>
            <p className="text-xl text-gray-300 max-w-3xl mx-auto">
              Stop spending hours on each application. Let AI handle the heavy lifting 
              while you focus on what matters most.
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            {features.map((feature, index) => (
              <div 
                key={index}
                className="group p-8 glass-effect rounded-2xl hover:bg-white/20 transition-all duration-500 hover:scale-105 hover:shadow-2xl hover:shadow-cyan-500/10"
              >
                <div className="w-16 h-16 bg-gradient-to-r from-cyan-500 to-purple-600 rounded-xl flex items-center justify-center mb-6 group-hover:scale-110 transition-transform duration-300">
                  {feature.icon}
                </div>
                <h3 className="text-2xl font-bold mb-4 group-hover:gradient-text transition-all duration-300">
                  {feature.title}
                </h3>
                <p className="text-gray-300 leading-relaxed">
                  {feature.description}
                </p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Stats Section */}
      {/* <section className="py-24">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid md:grid-cols-4 gap-8 text-center">
            <div className="space-y-2">
              <div className="text-4xl font-bold gradient-text">50K+</div>
              <div className="text-gray-400">Active Users</div>
            </div>
            <div className="space-y-2">
              <div className="text-4xl font-bold gradient-text">1M+</div>
              <div className="text-gray-400">Cover Letters Generated</div>
            </div>
            <div className="space-y-2">
              <div className="text-4xl font-bold gradient-text">95%</div>
              <div className="text-gray-400">User Satisfaction</div>
            </div>
            <div className="space-y-2">
              <div className="text-4xl font-bold gradient-text">24/7</div>
              <div className="text-gray-400">AI Assistance</div>
            </div>
          </div>
        </div>
      </section> */}

      {/* CTA Section */}
      <section className="py-24 relative">
        <div className="absolute inset-0 bg-gradient-to-r from-cyan-500/10 to-purple-600/10"></div>
        <div className="max-w-4xl mx-auto px-6 text-center relative z-10">
          <h2 className="text-4xl lg:text-5xl font-bold mb-6">
            <span className="gradient-text">Ready to Transform</span>
            <br />
            <span className="text-white">Your Job Search?</span>
          </h2>
          <p className="text-xl text-gray-300 mb-12 max-w-2xl mx-auto">
            Join thousands of job seekers who've accelerated their career with jobPilot.AI. 
            Start your free trial today.
          </p>
          
          <div className="flex flex-col sm:flex-row gap-6 justify-center">
            <Link to='/login'>
            <button className="px-10 py-4 bg-gradient-to-r from-cyan-500 to-purple-600 hover:from-cyan-600 hover:to-purple-700 rounded-xl font-bold text-lg transition-all duration-300 hover:scale-105 pulse-glow flex items-center justify-center gap-2">
              Get Started Free
              <ArrowRight className="w-5 h-5" />
            </button>
            </Link>

          </div>
          
          
        </div>
      </section>

      {/* Footer */}
      <footer className="py-12 bg-black/30 border-t border-white/10">
        <div className="max-w-7xl mx-auto px-6 text-center">
          <div className="text-2xl font-bold gradient-text mb-4">JobPilot.AI</div>
          <p className="text-gray-400 mb-6">
            Empowering job seekers with AI-driven career acceleration tools.
          </p>
          <div className="flex justify-center gap-8 text-sm text-gray-400">
            <a href="#" className="hover:text-white transition-colors">Privacy Policy</a>
            <a href="#" className="hover:text-white transition-colors">Terms of Service</a>
            <a href="#" className="hover:text-white transition-colors">Support</a>
            <a href="#" className="hover:text-white transition-colors">Contact</a>
          </div>
          <div className="mt-8 text-sm text-gray-500">
            © 2025 JobPilot.AI. All rights reserved.
          </div>
        </div>
      </footer>
    </div>
  );
};

export default JobPilotLanding;