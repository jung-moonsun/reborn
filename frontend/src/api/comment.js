import axios from './axios';

export const getComments = (productId) =>
  axios.get('/comments/tree', { params: { productId } });

export const addComment = (productId, content, userId, parentId = null) =>
  axios.post(
    '/comments',
    { productId, content, parentId }, // ✅ parentId 추가
    { params: { userId } }
  );

 export const updateComment = (commentId, content, userId) =>
    axios.put(`/comments/${commentId}`, { content }, { params: { userId } });

 export const deleteComment = (commentId, userId) =>
    axios.delete(`/comments/${commentId}`, { params: { userId } });