import axios from "axios";

const API = axios.create({
    baseURL: "http://localhost:8765",
});

// 1. AUTOMATICALLY ADD TOKEN TO EVERY REQUEST
API.interceptors.request.use((req) => {
    // Check if token exists in LocalStorage
    const token = localStorage.getItem("authToken");
    
    if (token) {
        // If token exists, add it to headers
        req.headers.Authorization = `Bearer ${token}`; 
        console.log("🚀 Interceptor added token:", token); // Debug log
    }
    return req;
});

// Example calls
export const getAllQuizzes = () => API.get("/quiz");
export const getQuestionsByQuizId = (id) => API.get(`/question/quiz/${id}`);
export const submitQuiz = (data) => API.post("/student/submit", data);
export const getQuizById = (id) => API.get(`/quiz/${id}`);
export const getQuestionById = (id) => API.get(`/question/${id}`);
export default API;
