// src/api/mypage.js
import axios from './axios';
import { getProfile } from './auth';

// '/products/me' 로만 요청 (axios가 baseURL에 '/api'를 붙여 줍니다)
export function getMyProducts(page = 0, size = 3) {
  return axios.get('/products/me', {
    params: { page, size },
  });
}

// 이하 삭제·비번 변경은 그대로
export async function deleteUser(pw) {
  const { data: profile } = await getProfile();
  return axios.delete(`/users/${profile.id}`, {
    params: { pw },
  });
}

export function changePassword(userId, oldPw, newPw) {
  return axios.put(
    `/users/${userId}/password`,
    null,
    { params: { oldPw, newPw } }
  );
}
