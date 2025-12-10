import axios from "axios";

export const GATEWAY_URL = "http://localhost:8765";


const API = axios.create({
    baseURL: GATEWAY_URL,
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
export const getAllQuizzes = () => API.get("/quiz-service/quiz/all");
export const getQuizById = (id) => API.get(`/quiz-service/quiz/${id}`);
export const submitQuiz = (data) => API.post("/quiz-service/submit", data); // Verify endpoint if different

// Question Service
export const getQuestionsByQuizId = (id) => API.get(`/question-service/question/quiz/${id}`);
export const getQuestionById = (id) => API.get(`/question-service/question/${id}`);

// User Service / Auth
// Assuming your auth endpoints are in user-service
export const loginUser = (credentials) => API.post("/user-service/auth/login", credentials);
export const registerUser = (userData) => API.post("/user-service/auth/register", userData);

export default API;
