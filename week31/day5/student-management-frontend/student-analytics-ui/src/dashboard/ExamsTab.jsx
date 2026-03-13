import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import { PlayCircle, FileText, LayoutList, Loader2, Clock } from 'lucide-react';

const ExamsTab = () => {
  // 🔥 We renamed 'exams' to 'assessments' because it now holds BOTH Quizzes and Midterms
  const [assessments, setAssessments] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    const loadContent = async () => {
      setIsLoading(true);
      try {
        // 1. Hits Course-Service to find what Sujal is studying
        const enrollRes = await api.get('/api/enrollments/my'); 
        const courseId = enrollRes.data[0]?.courseId; 
        
        if (courseId) {
          // 2. Fetch BOTH Midterms and Quizzes simultaneously using Promise.allSettled
          // This ensures that if the Quiz API fails or isn't built yet, the Midterms still load!
          const [midtermRes, quizRes] = await Promise.allSettled([
            api.get(`/api/v1/exams/course/${courseId}`),
            // Replace this URL with your actual Quiz fetching endpoint when ready
            api.get(`/api/quiz/course/${courseId}`) 
          ]);

          let combinedList = [];

          // 3. Process Midterms (If successful)
          if (midtermRes.status === 'fulfilled' && midtermRes.value.data) {
            // Safety wrapper: Forces the response into an array so .map() never crashes
            const midtermsData = Array.isArray(midtermRes.value.data) ? midtermRes.value.data : [midtermRes.value.data];
            
            // Format midterms for the unified UI
            const formattedMidterms = midtermsData.map(m => ({
              id: m.midtermId, 
              type: 'MIDTERM',
              courseId: m.courseId,
              totalQuestions: m.totalQuestions,
              title: `Midterm Exam #${m.midtermId}`,
              route: `/attempt/exam/${m.midtermId}`,
              theme: 'indigo' // Purple styling for heavy exams
            }));
            
            combinedList = [...combinedList, ...formattedMidterms];
          }

          // 4. Process Quizzes (If successful)
          if (quizRes.status === 'fulfilled' && quizRes.value.data) {
             // Safety wrapper: Forces the response into an array so .map() never crashes
            const quizzesData = Array.isArray(quizRes.value.data) ? quizRes.value.data : [quizRes.value.data];
            
            // Format quizzes for the unified UI
            const formattedQuizzes = quizzesData.map(q => ({
              id: q.quiz_id || q.quizId, 
              type: 'QUIZ',
              courseId: q.courseId || courseId,
              totalQuestions: q.totalQuestions || q.questions?.length || 0,
              title: `Quick Quiz #${q.quiz_id || q.quizId}`,
              route: `/attempt/quiz/${q.quiz_id || q.quizId}`,
              theme: 'amber' // Orange styling for quick quizzes
            }));

            combinedList = [...combinedList, ...formattedQuizzes];
          }

          // 5. Sort the combined list so the newest assessments (highest ID) are at the top
          combinedList.sort((a, b) => b.id - a.id);
          
          setAssessments(combinedList);
        }
      } catch (err) { 
        console.error("Data Load Failed", err); 
        setAssessments([]); // Failsafe: ensure array is empty on total failure
      } finally {
        setIsLoading(false); // Stop loading animation whether success or fail
      }
    };

    if (userId) {
      loadContent();
    }
  }, [userId]);

  // --- UI HELPER: Dynamic Styling based on Assessment Type ---
  const getThemeStyles = (theme) => {
    if (theme === 'amber') {
      return {
        iconBg: 'bg-amber-500',
        buttonBg: 'bg-amber-500 hover:bg-amber-600 shadow-amber-200',
        badgeText: 'text-amber-600 dark:text-amber-400',
        badgeBg: 'bg-amber-100 dark:bg-amber-900/30',
        Icon: LayoutList
      };
    }
    // Default to Indigo (Midterms)
    return {
      iconBg: 'bg-indigo-600',
      buttonBg: 'bg-indigo-600 hover:bg-indigo-700 shadow-indigo-200',
      badgeText: 'text-indigo-600 dark:text-indigo-400',
      badgeBg: 'bg-indigo-100 dark:bg-indigo-900/30',
      Icon: FileText
    };
  };

  return (
    <div className="space-y-6 text-left">
      <h2 className="text-3xl font-black text-slate-900 dark:text-white mb-8 italic uppercase tracking-tighter">
        Available Assessments
      </h2>
      
      {/* 🔥 Conditional Rendering: Loading -> Data -> Empty State */}
      {isLoading ? (
        <div className="flex justify-center items-center py-20">
          <Loader2 className="w-10 h-10 animate-spin text-indigo-600" />
        </div>
      ) : assessments.length > 0 ? (
        assessments.map((assessment, index) => {
          // Extract the dynamic styles based on if it's a MIDTERM or QUIZ
          const styles = getThemeStyles(assessment.theme);
          
          return (
            <div key={`${assessment.type}-${assessment.id}-${index}`} className="bg-white dark:bg-slate-900 p-8 rounded-[2.5rem] border border-slate-100 dark:border-slate-800 shadow-sm flex items-center justify-between hover:border-slate-300 dark:hover:border-slate-600 transition-colors">
              <div className="flex items-center gap-6">
                
                {/* Dynamic Icon (Orange for Quiz, Purple for Midterm) */}
                <div className={`w-14 h-14 ${styles.iconBg} rounded-2xl flex items-center justify-center text-white shadow-lg`}>
                  <styles.Icon className="w-7 h-7" />
                </div>
                
                <div>
                  <div className="flex items-center gap-3 mb-1">
                    <p className="text-lg font-black text-slate-800 dark:text-slate-200">{assessment.title}</p>
                    
                    {/* Visual Tag for Quiz vs Midterm */}
                    <span className={`text-[9px] font-black uppercase tracking-widest px-2 py-1 rounded-md ${styles.badgeBg} ${styles.badgeText}`}>
                      {assessment.type}
                    </span>
                  </div>
                  
                  <div className="flex items-center gap-3 text-xs text-slate-400 font-bold uppercase tracking-widest">
                    <span>Course: {assessment.courseId}</span>
                    <span>•</span>
                    <span>{assessment.totalQuestions} Questions</span>
                    
                    {/* Extra badge for Quizzes to show they are Auto-Graded */}
                    {assessment.type === 'QUIZ' && (
                      <>
                        <span>•</span>
                        <span className="flex items-center gap-1"><Clock className="w-3 h-3" /> Auto-Graded</span>
                      </>
                    )}
                  </div>
                </div>
              </div>
              
              {/* Dynamic Button Routing */}
              <button 
                onClick={() => window.location.href = assessment.route}
                className={`flex items-center gap-3 px-8 py-4 text-white rounded-2xl font-black text-xs uppercase tracking-[0.2em] transition-all shadow-xl dark:shadow-none ${styles.buttonBg}`}
              >
                Attempt Now <PlayCircle className="w-5 h-5" />
              </button>
            </div>
          );
        })
      ) : (
        <div className="bg-white dark:bg-slate-900 p-10 rounded-[2.5rem] border border-slate-100 dark:border-slate-800 shadow-sm text-center">
          <p className="text-slate-500 dark:text-slate-400 font-bold text-sm uppercase tracking-widest">
            No active assessments found for your enrolled courses.
          </p>
        </div>
      )}
    </div>
  );
};

export default ExamsTab;