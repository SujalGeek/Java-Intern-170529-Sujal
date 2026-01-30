import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from "jwt-decode";
import api from '../services/api';

const StudentDashboard = () => {
  const navigate = useNavigate();
  const [studentInfo, setStudentInfo] = useState(null);
  
  // 1. We need TWO lists: All Courses AND My Enrolled Courses
  const [courses, setCourses] = useState([]);
  const [enrolledCourseIds, setEnrolledCourseIds] = useState(new Set()); 
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadDashboardData = async () => {
      const token = localStorage.getItem('token');
      const studentId = localStorage.getItem('studentId'); // Get ID from storage

      if (!token || !studentId) {
        navigate('/login');
        return;
      }

      try {
        // Decode Token for Name
        const decoded = jwtDecode(token);
        setStudentInfo(decoded);

        // 🌟 FETCH 1: Get All Available Courses
        const allCoursesRes = await api.get('/courses/available');
        setCourses(allCoursesRes.data.data);

        // 🌟 FETCH 2: Get Courses I am ALREADY in
        // (Ensure this endpoint exists in your Java Controller!)
        const myCoursesRes = await api.get(`/students/${studentId}/courses`);
        
        // Extract IDs into a Set for fast checking
        const myIds = new Set(myCoursesRes.data.data.map(c => c.courseId));
        setEnrolledCourseIds(myIds);

      } catch (err) {
        console.error("Failed to load dashboard", err);
        // If "My Courses" endpoint fails (e.g., 404), we just ignore it so the page still loads
      } finally {
        setLoading(false);
      }
    };

    loadDashboardData();
  }, []); // Run once on load

  const handleEnroll = async (courseId) => {
    try {
      const studentId = localStorage.getItem('studentId');
      if (!studentId) return;

      // Call Backend
      await api.post(`/students/${studentId}/enroll/${courseId}`);
      
      alert("Enrolled Successfully! 🎉");

      // 🌟 INSTANT UPDATE: Add the ID to our Set (turns button Green instantly)
      setEnrolledCourseIds(prevSet => new Set(prevSet).add(courseId));

    } catch (err) {
      alert("Enrollment Failed: " + (err.response?.data?.message || "Unknown Error"));
    }
  };

  if (loading) return <div className="p-10 text-center text-blue-600 font-bold">Loading Dashboard...</div>;

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navbar */}
      <nav className="bg-blue-600 text-white p-4 shadow-md flex justify-between items-center">
        <h1 className="text-xl font-bold">🎓 Smart Student System</h1>
        <button 
          onClick={() => { localStorage.removeItem('token'); navigate('/login'); }}
          className="bg-red-500 hover:bg-red-600 px-4 py-2 rounded text-sm font-bold shadow"
        >
          Logout
        </button>
      </nav>

      <div className="max-w-6xl mx-auto mt-10 p-6">
        {/* Welcome Section */}
        <div className="bg-white p-6 rounded-lg shadow-md mb-8 border-l-4 border-blue-500">
          <h2 className="text-2xl font-bold text-gray-800">
            Welcome back, {studentInfo?.sub || "Student"}!
          </h2>
          <p className="text-gray-500">Select a course below to enroll.</p>
        </div>

        {/* Course Grid */}
        <h3 className="text-xl font-bold text-gray-700 mb-4">Available Courses</h3>
        
        {courses.length === 0 ? (
          <p className="text-gray-500 italic">No courses available right now.</p>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {courses.map((course) => {
                
                // 🌟 CHECK: Do I own this course?
                const isEnrolled = enrolledCourseIds.has(course.courseId);

                return (
                  <div key={course.courseId} className="bg-white p-5 rounded-xl shadow border hover:shadow-lg transition duration-300">
                    <div className="flex justify-between items-start">
                        <div>
                            <h4 className="font-bold text-lg text-blue-900">{course.courseName}</h4>
                            <p className="text-sm text-gray-500">
                                Instructor: <span className="font-medium text-slate-700">{course.teacherName}</span>
                            </p>
                        </div>
                        <span className="bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded font-semibold">
                            {course.credits} Credits
                        </span>
                    </div>
                    
                    <p className="mt-3 text-gray-600 text-sm line-clamp-2">
                        {course.description || "No description available."}
                    </p>

                    {/* DYNAMIC BUTTON */}
                    <button 
                      onClick={() => handleEnroll(course.courseId)}
                      disabled={isEnrolled} // Stop clicks if enrolled
                      className={`mt-4 w-full font-bold py-2 rounded transition shadow-sm ${
                        isEnrolled 
                          ? "bg-green-100 text-green-700 border border-green-200 cursor-default" // Green Style
                          : "bg-blue-600 hover:bg-blue-700 text-white shadow-blue-200" // Blue Style
                      }`}
                    >
                      {isEnrolled ? "Enrolled" : "Enroll Now"}
                    </button>
                  </div>
                );
            })}
          </div>
        )}
      </div>
    </div>
  );
};

export default StudentDashboard;