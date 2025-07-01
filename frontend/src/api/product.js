import axios from './axios';

export const searchProducts = (keyword = '', page = 0, size = 20) =>
  axios.get('/products/search', { params: { keyword, page, size } });

export const getProduct = (id) =>
  axios.get(`/products/${id}`);

export const updateProduct = (id, data) =>
  axios.put(`/products/${id}`, data);

export const deleteProduct = (id) =>
  axios.delete(`/products/${id}`);

 export const updateProductStatus = (id, status) =>
   axios.put(`/products/${id}/status`, null, { params: { status } });