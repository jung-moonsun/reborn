import axios from './axios';

export const login = (email, password) =>
  axios.post('/users/login', { email, password });

export const signup = (email, password, nickname) =>
  axios.post('/users/signup', { email, password, nickname });

export const getProfile = () =>
  axios.get('/users/me');

