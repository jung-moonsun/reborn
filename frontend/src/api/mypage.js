// src/api/mypage.js
import axios from './axios';
import { getProfile } from './auth'; // ✅ 이거 추가

export const getMyProducts = () =>
  axios.get('/products/me');

export const deleteUser = async (pw) => {
  const profile = await getProfile();
  const userId = profile.data.id;
  return axios.delete(`/users/${userId}`, {  // ✅ instance → axios
    params: { pw }
  });
};

export const changePassword = (userId, oldPw, newPw) =>
  axios.put(`/users/${userId}/password`, null, {
    params: { oldPw, newPw },
  });
