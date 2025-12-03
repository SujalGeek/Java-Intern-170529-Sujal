import axios from "axios";

const API = axios.create({
    baseURL: "http://localhost:8765",  // API Gateway URL
});

// Example calls
export const getAllQuizzes = () => API.get("/quiz");
export const getQuestionsByQuizId = (id) => API.get(`/question/quiz/${id}`);
export const submitQuiz = (data) => API.post("/student/submit", data);
export const getQuizById = (id) => API.get(`/quiz/${id}`);
export const getQuestionById = (id) => API.get(`/question/${id}`);
export default API;
