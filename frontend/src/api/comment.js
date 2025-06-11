import axios from './axios';

export const getComments = (productId) =>
  axios.get('/comments', { params: { productId } });

export const addComment = (productId, content, userId, parentId = null) =>
  axios.post(
    '/comments',
    { productId, content, parentId }, // ✅ parentId 추가
    { params: { userId } }
  );