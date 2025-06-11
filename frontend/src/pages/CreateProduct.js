import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from '../api/axios';
import './CreateProduct.css';

export default function CreateProduct() {
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [price, setPrice] = useState('');
  const [description, setDescription] = useState('');
  const [images, setImages] = useState([]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData();
    formData.append('product', JSON.stringify({ title, price, description }));
    for (let img of images) {
      formData.append('images', img);
    }
    await axios.post('/products', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    navigate('/');
  };

  return (
    <div className="create-product">
      <h2>상품 등록</h2>
      <form onSubmit={handleSubmit}>
        <input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="상품명" required />
        <input value={price} onChange={(e) => setPrice(e.target.value)} placeholder="가격" required />
        <textarea value={description} onChange={(e) => setDescription(e.target.value)} placeholder="설명" rows={5} required />
        <input type="file" multiple onChange={(e) => setImages([...e.target.files])} />
        <button type="submit">등록하기</button>
      </form>
    </div>
  );
}
