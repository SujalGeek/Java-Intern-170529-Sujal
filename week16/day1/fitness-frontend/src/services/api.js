import axios from "axios"

const API_URL = 'http://localhost:8080/api'

const api = axios.create({
    baseURL: API_URL
});

api.interceptors.request.use((config) => {
    const userId = localStorage.getItem('userId');
    const token = localStorage.getItem('token');

    // 1. Add Token
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }

    // 2. Add User ID to Header (Optional, Backend ignores this currently but harmless)
    if (userId) {
        config.headers['X-User-ID'] = userId;
    }

    // 3. AUTOMATICALLY ADD USER ID TO BODY (The Fix)
    // This checks if we are sending data (POST/PUT) and automatically adds the userId
    if (userId && (config.method === 'post' || config.method === 'put')) {
        config.data = {
            ...config.data, // Keep existing form data (calories, type, etc.)
            userId: userId  // Inject the ID automatically
        };
    }

    return config;
}, (error) => {
    return Promise.reject(error);
});

export const getActivities = () => api.get('/activities');
export const addActivity = (activity) => api.post('/activities', activity);
export const getActivityDetail = (id) => api.get(`/recommendations/activity/${id}`);

export default api;