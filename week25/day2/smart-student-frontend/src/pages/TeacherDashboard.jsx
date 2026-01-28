import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import api from '../services/api';

const TeacherDashboard = () => {
    const navigate = useNavigate();
    const [teacherInfo, setTeacherInfo] = useState(null);
    const [myCourses, setMyCourses] = useState([]); 
    
    // State for Modals
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [showStudentModal, setShowStudentModal] = useState(false); // 🌟 New Modal State
    
    const [selectedStudents, setSelectedStudents] = useState([]); // 🌟 Stores list of students
    const [loading, setLoading] = useState(true);

    const [newCourse, setNewCourse] = useState({
        courseName:  '',
        courseCode: '',
        credits: 3,
    });

    useEffect(() => {
        console.group("Dashboard Access Check"); 
        
        const token = localStorage.getItem('token');
        const teacherId = localStorage.getItem('teacherId');
        
        console.log("1. Storage Check:", { token: token ? "Exists" : "Missing", teacherId });

        if (!token || !teacherId) {
            console.warn("Missing Credentials. Redirecting to Login.");
            console.groupEnd();
            navigate('/login');
            return;
        }

        try {
            const decoded = jwtDecode(token);
            console.log("2. Decoded Token:", decoded);

            // --- DEBUGGING THE ROLE LOGIC ---
            const role = (decoded.role || '').toString().toUpperCase();
            console.log("3. Extracted Role (String):", role);

            // Handle Authorities (Array or String)
            let hasAuthority = false;
            
            if (decoded.authorities) {
                if (Array.isArray(decoded.authorities)) {
                    hasAuthority = decoded.authorities.some(auth => {
                        const val = (typeof auth === 'object' && auth.authority) ? auth.authority : auth;
                        return val.toString().toUpperCase() === 'TEACHER';
                    });
                } else {
                    hasAuthority = decoded.authorities.toString().toUpperCase().includes('TEACHER');
                }
            }

            const isTeacher = role === 'TEACHER' || hasAuthority;
            console.log("5. FINAL DECISION (isTeacher):", isTeacher);

            if (!isTeacher) {
                console.error(" ACCESS DENIED. User is not a Teacher.");
                alert('Access Denied: Teachers Only');
                localStorage.clear();
                console.groupEnd();
                navigate('/login');
                return;
            }
            
            console.log(" Access Granted. Loading Dashboard...");
            setTeacherInfo(decoded);
            fetchMyCourses(teacherId);

        } catch (error) {
            console.error(" Token Decode Error:", error);
            localStorage.clear();
            navigate('/login');
        } finally {
            console.groupEnd();
        }
    }, []);

    const fetchMyCourses = async (teacherId) => {
        console.log(`Fetching courses for Teacher ID: ${teacherId}...`);
        try {
            const response = await api.get(`/courses/teacher?teacherId=${teacherId}`);
            console.log("API Response (My Courses):", response.data);
            
            if (Array.isArray(response.data.data)) {
                setMyCourses(response.data.data);
                console.log(`Loaded ${response.data.data.length} courses.`);
            } else {
                console.warn("Data format warning: response.data.data is not an array:", response.data.data);
                setMyCourses([]);
            }
        } catch (err) {
            console.error("Failed to fetch courses:", err);
            setMyCourses([]); 
        } finally {
            setLoading(false);
        }
    };

    // 🌟 NEW FUNCTION: Fetch Students for a specific course
    const handleViewStudents = async (courseId) => {
        try {
            console.log("Fetching students for Course ID:", courseId);
            // Updated Endpoint based on your TeacherController fix
            const response = await api.get(`/teachers/courses/${courseId}/students`);
            
            console.log("Students Fetched:", response.data.data);
            setSelectedStudents(response.data.data); // Save students to state
            setShowStudentModal(true); // Open the modal

        } catch (err) {
            console.error("Failed to fetch students:", err);
            alert("Failed to load students: " + (err.response?.data?.message || "Error"));
        }
    };

    const handleCreateCourse = async (e) => {
        e.preventDefault();
        console.group("Create Course Attempt");
        
        try {
            const teacherId = localStorage.getItem('teacherId');
            console.log("1. Teacher ID:", teacherId);

            const payload = {
                courseName: newCourse.courseName,
                courseCode: newCourse.courseCode,
                credits: parseInt(newCourse.credits),
                teacher: {
                    teacherId: teacherId
                }
            };
            console.log("2. Sending Payload:", payload);
            
            const res = await api.post('/courses', payload);
            console.log("3. Success Response:", res.data);
            
            alert("Course added Successfully!");
            
            setShowCreateModal(false);
            setNewCourse({ courseName: '', courseCode: '', credits: 3 });
            
            // Refresh list
            console.log("4. Refreshing List...");
            fetchMyCourses(teacherId);

        } catch (err) {
            console.error("Create Course Failed:", err);
            const msg = err.response?.data?.message || "Error creating course";
            alert("Failed to create course: " + msg);
        } finally {
            console.groupEnd();
        }
    };

    if (loading) return <div className="p-10 text-center text-gray-500 font-mono">Loading... (Check Console F12)</div>;

    return (
        <div className="min-h-screen bg-slate-50">
            {/* Navbar */}
            <nav className="bg-slate-800 text-white p-4 shadow-md flex justify-between items-center">
                <h1 className="text-xl font-bold">👨‍🏫 Teacher Portal</h1>
                <div className="flex gap-4">
                    <span className="text-sm self-center text-gray-300">
                        {teacherInfo?.sub || "Teacher"}
                    </span>
                    <button 
                        onClick={() => { 
                            console.log("👋 Logging out...");
                            localStorage.clear(); 
                            navigate('/login'); 
                        }}
                        className="bg-red-500 hover:bg-red-600 px-4 py-2 rounded text-sm font-bold transition"
                    >
                        Logout
                    </button>
                </div>
            </nav>

            <div className="max-w-6xl mx-auto mt-10 p-6">
                
                {/* Header Section */}
                <div className="flex justify-between items-end mb-8">
                    <div>
                        <h2 className="text-3xl font-bold text-slate-800">My Classroom</h2>
                        <p className="text-slate-500">Manage your courses and exams.</p>
                    </div>
                    <button 
                        onClick={() => setShowCreateModal(true)}
                        className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg font-semibold shadow-lg transition transform hover:-translate-y-1"
                    >
                        + Create New Course
                    </button>
                </div>

                {/* Courses Grid */}
                {myCourses.length === 0 ? (
                    <div className="text-center py-20 bg-white rounded-xl shadow-sm border border-dashed border-gray-300">
                        <p className="text-gray-500 text-lg">You haven't created any courses yet.</p>
                        <button onClick={() => setShowCreateModal(true)} className="text-blue-600 font-semibold mt-2 hover:underline">
                            Create your first course
                        </button>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {myCourses.map(course => (
                            <div key={course.courseId} className="bg-white p-6 rounded-xl shadow-sm border border-slate-200 hover:shadow-md transition">
                                <div className="flex justify-between items-start mb-4">
                                    <div className="bg-blue-100 text-blue-800 text-xs font-bold px-2 py-1 rounded">
                                        {course.courseCode}
                                    </div>
                                    <span className="text-slate-400 text-sm">{course.credits} Credits</span>
                                </div>
                                <h3 className="text-xl font-bold text-slate-800 mb-2">{course.courseName}</h3>
                                <p className="text-sm text-slate-500 mb-6">
                                    Manage content, assignments, and view enrolled students.
                                </p>
                                
                                <div className="flex gap-2">
                                    <button 
                                        onClick={() => handleViewStudents(course.courseId)}
                                        className="flex-1 bg-slate-100 hover:bg-slate-200 text-slate-700 py-2 rounded text-sm font-semibold transition"
                                    >
                                        👥 View Students
                                    </button>
                                    <button 
                                        onClick={() => console.log("Clicked Add Exam for:", course.courseId)}
                                        className="flex-1 bg-blue-50 hover:bg-blue-100 text-blue-700 py-2 rounded text-sm font-semibold transition"
                                    >
                                        📝 Add Exam
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}

                {/* 🌟 1. STUDENT LIST MODAL */}
                {showStudentModal && (
                    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                        <div className="bg-white rounded-xl shadow-2xl max-w-2xl w-full p-6 animate-fade-in-up">
                            <div className="flex justify-between items-center mb-4 border-b pb-2">
                                <h3 className="text-xl font-bold text-slate-800">Enrolled Students</h3>
                                <button onClick={() => setShowStudentModal(false)} className="text-gray-500 hover:text-red-500 text-2xl font-bold">&times;</button>
                            </div>

                            {selectedStudents.length === 0 ? (
                                <div className="text-center py-8 bg-gray-50 rounded-lg border border-dashed border-gray-200">
                                    <p className="text-gray-500">No students enrolled yet.</p>
                                </div>
                            ) : (
                                <div className="overflow-x-auto">
                                    <table className="w-full text-left border-collapse">
                                        <thead>
                                            <tr className="bg-gray-100 text-gray-600 text-sm uppercase font-semibold">
                                                <th className="p-3 border-b">Name</th>
                                                <th className="p-3 border-b">Email</th>
                                                <th className="p-3 border-b">Department</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {selectedStudents.map((student, index) => (
                                                <tr key={index} className="border-b hover:bg-blue-50 transition-colors">
                                                    <td className="p-3 font-medium text-slate-800">{student.name}</td>
                                                    <td className="p-3 text-slate-600">{student.email}</td>
                                                    <td className="p-3 text-slate-500 text-sm">{student.department}</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            )}
                            
                            <div className="mt-6 text-right">
                                <button 
                                    onClick={() => setShowStudentModal(false)} 
                                    className="px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded text-gray-700 font-semibold transition"
                                >
                                    Close
                                </button>
                            </div>
                        </div>
                    </div>
                )}

                {/* 🌟 2. CREATE COURSE MODAL */}
                {showCreateModal && (
                    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
                        <div className="bg-white rounded-xl shadow-2xl max-w-md w-full p-6 animate-fade-in-up">
                            <h3 className="text-xl font-bold text-slate-800 mb-4">Create New Course</h3>
                            <form onSubmit={handleCreateCourse} className="space-y-4">
                                <div>
                                    <label className="block text-sm font-medium text-slate-700 mb-1">Course Name</label>
                                    <input 
                                        required
                                        type="text" 
                                        placeholder="e.g. Advanced Java Programming"
                                        className="w-full border border-gray-300 p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none transition"
                                        value={newCourse.courseName}
                                        onChange={e => setNewCourse({...newCourse, courseName: e.target.value})}
                                    />
                                </div>
                                <div className="grid grid-cols-2 gap-4">
                                    <div>
                                        <label className="block text-sm font-medium text-slate-700 mb-1">Course Code</label>
                                        <input 
                                            required
                                            type="text" 
                                            placeholder="e.g. CS-101"
                                            className="w-full border border-gray-300 p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none transition"
                                            value={newCourse.courseCode}
                                            onChange={e => setNewCourse({...newCourse, courseCode: e.target.value})}
                                        />
                                    </div>
                                    <div>
                                        <label className="block text-sm font-medium text-slate-700 mb-1">Credits</label>
                                        <input 
                                            required
                                            type="number" 
                                            min="1" max="10"
                                            className="w-full border border-gray-300 p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none transition"
                                            value={newCourse.credits}
                                            onChange={e => setNewCourse({...newCourse, credits: e.target.value})}
                                        />
                                    </div>
                                </div>
                                
                                <div className="flex justify-end gap-3 mt-6">
                                    <button 
                                        type="button"
                                        onClick={() => setShowCreateModal(false)}
                                        className="px-4 py-2 text-slate-600 font-semibold hover:bg-slate-100 rounded transition"
                                    >
                                        Cancel
                                    </button>
                                    <button 
                                        type="submit"
                                        className="px-6 py-2 bg-blue-600 text-white font-semibold rounded hover:bg-blue-700 shadow transition"
                                    >
                                        Create Course
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                )}

            </div>
        </div>
    );
};

export default TeacherDashboard;