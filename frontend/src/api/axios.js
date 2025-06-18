// src/axios.js
import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080/api',
});

// ✅ 매 요청마다 localStorage에서 토큰 읽어서 헤더에 넣어줌
instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default instance;
