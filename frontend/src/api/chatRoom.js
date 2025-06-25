// src/api/chatRoom.js
import axios from './axios';
import { getProfile } from './auth';

export const createOrGetRoom = (productId, buyerId) =>
  axios.post('/chat/rooms', { productId, buyerId });

export const getChatRooms = () =>
  axios.get('/chat/rooms');

export const getMessages = (roomId) =>
  axios.get(`/chat/rooms/${roomId}/messages`);

export const getMyRooms = async () => {
  const profileRes = await getProfile();
  const userId = profileRes.data.id;
  return axios.get(`/chat/rooms/user/${userId}`, {
    headers: {
      Authorization: `Bearer ${localStorage.getItem('token')}`
    },
    withCredentials: true
  });
};

export const getUnreadCount = (userId) =>
  axios.get('/chat/rooms/unread/count', { params: { receiverId: userId } });

export const exitRoom = (roomId, userId) =>
  axios.put(`/chat/rooms/${roomId}/exit`, null, {
    params: { userId },
  });