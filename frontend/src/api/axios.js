import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true
});

instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    } else {
      delete config.headers['Authorization']; // 🔥 토큰 없으면 헤더 삭제
    }
    return config;
  },
  (err) => Promise.reject(err)
);

instance.interceptors.response.use(
  (res) => res,
  (error) => {
    const message = error?.response?.data?.message || error.message;
    console.warn('axios error:', message);
    return Promise.reject(error);
  }
);

export default instance;
