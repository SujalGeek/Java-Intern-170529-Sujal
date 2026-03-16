import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8091",
  headers: {
    "Content-Type": "application/json"
  }
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  const userId = localStorage.getItem('userId');
  const role = localStorage.getItem('userRole');

  // 🛡️ Logic Isolation: Is this an Auth-related request?
  const isAuthRequest = config.url.includes('/api/auth');

  if (!isAuthRequest) {
    // 1. ATTACH CREDENTIALS: Only for protected resources
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`; 
    }
    if (userId) {
      config.headers['X-User-Id'] = userId; //
    }
    if (role) {
      config.headers['X-User-Role'] = role; //
    }
  } else {
    // 2. FORCED PURGE: Ensure login/register calls are 100% clean
    delete config.headers['Authorization'];
    delete config.headers['X-User-Id'];
    delete config.headers['X-User-Role'];
  }

  return config;
}, (error) => {
  return Promise.reject(error);
});

export default api;