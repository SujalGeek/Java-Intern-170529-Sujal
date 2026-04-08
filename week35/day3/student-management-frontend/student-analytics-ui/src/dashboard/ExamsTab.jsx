import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import api from '../api/axios';
import { PlayCircle, FileText, LayoutList, Loader2, Clock, Sparkles, ChevronRight } from 'lucide-react';

const ExamsTab = () => {
  const [assessments, setAssessments] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    const loadContent = async () => {
      setIsLoading(true);
      try {
        const enrollRes = await api.get('/api/enrollments/student/' + userId); 
        const courseId = enrollRes.data[0]?.courseId; 
        
        if (courseId) {
          const [midtermRes, quizRes] = await Promise.allSettled([
            api.get(`/api/v1/exams/course/${courseId}`),
            api.get(`/api/quiz/course/${courseId}`) 
          ]);

          let combinedList = [];

          if (midtermRes.status === 'fulfilled' && midtermRes.value.data) {
            const midtermsData = Array.isArray(midtermRes.value.data) ? midtermRes.value.data : [midtermRes.value.data];
            const formattedMidterms = midtermsData.map(m => ({
              id: m.midtermId || m.examId, 
              type: 'MIDTERM',
              courseId: m.courseId,
              totalQuestions: m.totalQuestions || m.questionsCount || 0,
              title: m.examTitle || `Midterm Exam #${m.midtermId || m.examId}`,
              route: `/attempt/exam/${m.midtermId || m.examId}`,
              theme: 'indigo'
            }));
            combinedList = [...combinedList, ...formattedMidterms];
          }

          if (quizRes.status === 'fulfilled' && quizRes.value.data) {
            const quizzesData = Array.isArray(quizRes.value.data) ? quizRes.value.data : [quizRes.value.data];
            const formattedQuizzes = quizzesData.map(q => ({
              id: q.quiz_id || q.quizId, 
              type: 'QUIZ',
              courseId: q.courseId || courseId,
              totalQuestions: q.totalQuestions || q.questions?.length || 0,
              title: `Quick Quiz #${q.quiz_id || q.quizId}`,
              route: `/attempt/quiz/${q.quiz_id || q.quizId}`,
              theme: 'amber'
            }));
            combinedList = [...combinedList, ...formattedQuizzes];
          }

          combinedList.sort((a, b) => b.id - a.id);
          setAssessments(combinedList);
        }
      } catch (err) { 
        console.error("Data Load Failed", err); 
        setAssessments([]); 
      } finally {
        setIsLoading(false);
      }
    };

    if (userId) loadContent();
  }, [userId]);

  const getThemeStyles = (theme) => {
    if (theme === 'amber') {
      return {
        iconBg: 'bg-amber-500',
        buttonBg: 'bg-amber-500 hover:bg-amber-600 shadow-amber-200 dark:shadow-none',
        badgeText: 'text-amber-600 dark:text-amber-400',
        badgeBg: 'bg-amber-50 dark:bg-amber-900/20',
        Icon: LayoutList
      };
    }
    return {
      iconBg: 'bg-indigo-600',
      buttonBg: 'bg-indigo-600 hover:bg-indigo-700 shadow-indigo-200 dark:shadow-none',
      badgeText: 'text-indigo-600 dark:text-indigo-400',
      badgeBg: 'bg-indigo-50 dark:bg-indigo-900/20',
      Icon: FileText
    };
  };

  return (
    <div className="space-y-8 text-left pb-10">
      <div className="flex items-center gap-4">
          <div className="w-10 h-10 bg-indigo-600 rounded-xl flex items-center justify-center text-white shadow-lg">
             <Sparkles className="w-5 h-5" />
          </div>
          <h2 className="text-2xl lg:text-3xl font-black text-slate-900 dark:text-white uppercase italic tracking-tighter">
            Available Assessments
          </h2>
      </div>
      
      {isLoading ? (
        <div className="flex flex-col justify-center items-center py-32 gap-4">
          <Loader2 className="w-12 h-12 animate-spin text-indigo-600" />
          <p className="text-[10px] font-black text-slate-400 uppercase tracking-[0.3em]">Syncing Assessment Node...</p>
        </div>
      ) : assessments.length > 0 ? (
        <div className="space-y-4 lg:space-y-6">
          {assessments.map((assessment, index) => {
            const styles = getThemeStyles(assessment.theme);
            
            return (
              <motion.div 
                key={`${assessment.type}-${assessment.id}-${index}`}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: index * 0.05 }}
                className="group bg-white dark:bg-slate-900 p-5 lg:p-8 rounded-[2rem] lg:rounded-[3rem] border border-slate-100 dark:border-slate-800 shadow-sm flex flex-col md:flex-row md:items-center justify-between hover:border-indigo-200 dark:hover:border-indigo-900/50 transition-all relative overflow-hidden"
              >
                <div className="flex items-center gap-4 lg:gap-6 relative z-10">
                  {/* Dynamic Icon Wrapper */}
                  <div className={`w-14 h-14 lg:w-16 lg:h-16 ${styles.iconBg} rounded-2xl flex items-center justify-center text-white shrink-0 shadow-lg shadow-black/5`}>
                    <styles.Icon className="w-7 h-7 lg:w-8 lg:h-8" />
                  </div>
                  
                  <div className="min-w-0">
                    <div className="flex items-center gap-3 mb-1 lg:mb-2">
                      <p className="text-lg lg:text-xl font-black text-slate-800 dark:text-white truncate uppercase tracking-tight">
                        {assessment.title}
                      </p>
                      <span className={`hidden xs:inline-block text-[9px] font-black uppercase tracking-widest px-2.5 py-1 rounded-full ${styles.badgeBg} ${styles.badgeText}`}>
                        {assessment.type}
                      </span>
                    </div>
                    
                    <div className="flex flex-wrap items-center gap-y-1 gap-x-3 text-[10px] lg:text-xs text-slate-400 font-bold uppercase tracking-wider">
                      <span className="flex items-center gap-1.5">COURSE ID: <span className="text-slate-600 dark:text-slate-300">{assessment.courseId}</span></span>
                      <span className="hidden xs:block w-1 h-1 bg-slate-300 rounded-full"></span>
                      <span>{assessment.totalQuestions} Questions</span>
                      
                      {assessment.type === 'QUIZ' && (
                        <>
                          <span className="hidden xs:block w-1 h-1 bg-slate-300 rounded-full"></span>
                          <span className="flex items-center gap-1 text-emerald-500"><Clock className="w-3 h-3" /> Auto-Graded</span>
                        </>
                      )}
                    </div>
                  </div>
                </div>
                
                {/* Responsive Button Section */}
                <div className="mt-6 md:mt-0 flex items-center gap-3 relative z-10 border-t md:border-none pt-4 md:pt-0 border-slate-50 dark:border-slate-800/50">
                  <button 
                    onClick={() => window.location.href = assessment.route}
                    className={`flex-1 md:flex-none flex items-center justify-center gap-3 px-6 lg:px-8 py-3.5 lg:py-4 text-white rounded-xl lg:rounded-2xl font-black text-[10px] uppercase tracking-[0.2em] transition-all shadow-lg active:scale-95 ${styles.buttonBg}`}
                  >
                    Attempt Now <PlayCircle className="w-4 h-4 lg:w-5 lg:h-5" />
                  </button>
                  <button className="hidden lg:flex p-4 bg-slate-50 dark:bg-slate-800 text-slate-400 rounded-2xl hover:text-indigo-600 transition-colors">
                    <ChevronRight className="w-5 h-5" />
                  </button>
                </div>

                {/* Background Decorative Element */}
                <span className="absolute -right-6 -bottom-6 text-7xl lg:text-9xl font-black text-slate-50 dark:text-slate-800/20 -rotate-12 select-none pointer-events-none group-hover:rotate-0 transition-transform duration-700">
                  #{assessment.id}
                </span>
              </motion.div>
            );
          })}
        </div>
      ) : (
        <div className="bg-white dark:bg-slate-900 p-16 lg:p-24 rounded-[3rem] border-2 border-dashed border-slate-100 dark:border-slate-800 text-center">
          <div className="w-16 h-16 bg-slate-50 dark:bg-slate-800 rounded-full flex items-center justify-center mx-auto mb-6">
             <FileText className="w-8 h-8 text-slate-300" />
          </div>
          <p className="text-slate-500 dark:text-slate-400 font-black text-sm uppercase tracking-[0.2em] italic">
            Zero Assessments Synchronized
          </p>
          <p className="text-[10px] text-slate-400 mt-2 uppercase tracking-widest font-bold">Waiting for curriculum deployment from Faculty Core</p>
        </div>
      )}
    </div>
  );
};

export default ExamsTab;