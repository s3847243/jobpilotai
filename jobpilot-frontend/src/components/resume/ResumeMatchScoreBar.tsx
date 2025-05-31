import { Target } from "lucide-react";
import { useEffect, useState } from "react";
// Resume Match Score Bar Component
const ResumeMatchScoreBar = ({ score }: { score: number }) => {
  const [animatedScore, setAnimatedScore] = useState(0);
  
  useEffect(() => {
    const timer = setTimeout(() => setAnimatedScore(score), 300);
    return () => clearTimeout(timer);
  }, [score]);

  const getScoreColor = (score:number) => {
    if (score >= 80) return 'from-emerald-500 to-green-400';
    if (score >= 60) return 'from-amber-500 to-yellow-400';
    return 'from-red-500 to-rose-400';
  };

  const getScoreText = (score:number) => {
    if (score >= 80) return 'Excellent Match';
    if (score >= 60) return 'Good Match';
    return 'Needs Improvement';
  };

  return (
    <div className="mb-8 p-6 bg-gradient-to-br from-slate-50 to-slate-100 rounded-2xl border border-slate-200">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold text-slate-800 flex items-center gap-2">
          <Target className="w-5 h-5 text-blue-600" />
          Resume Match Score
        </h3>
        <div className="flex items-center gap-2">
          <span className="text-3xl font-bold text-slate-800">{score}%</span>
          <span className="text-sm font-medium text-slate-600">{getScoreText(score)}</span>
        </div>
      </div>
      
      <div className="relative w-full h-3 bg-slate-200 rounded-full overflow-hidden">
        <div 
          className={`h-full bg-gradient-to-r ${getScoreColor(score)} rounded-full transition-all duration-1000 ease-out relative overflow-hidden`}
          style={{ width: `${animatedScore}%` }}
        >
          <div className="absolute inset-0 bg-white/20 animate-pulse"></div>
        </div>
      </div>
    </div>
  );
};
  
  export default ResumeMatchScoreBar;
  