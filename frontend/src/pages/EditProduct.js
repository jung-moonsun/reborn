import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from '../api/axios';
import { getProduct, updateProduct } from '../api/product';
import './CreateProduct.css';

export default function EditProduct() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [price, setPrice] = useState('');
  const [description, setDescription] = useState('');

  useEffect(() => {
    const load = async () => {
      const res = await axios.get(`/products/${id}`);
      setTitle(res.data.title);
      setPrice(res.data.price);
      setDescription(res.data.description);
    };
    load();
  }, [id]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    await axios.put(`/products/${id}`, { title, price, description });
    navigate(`/product/${id}`);
  };

  return (
    <div className="create-product">
      <h2>상품 수정</h2>
      <form onSubmit={handleSubmit}>
        <input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="상품명" required />
        <input value={price} onChange={(e) => setPrice(e.target.value)} placeholder="가격" required />
        <textarea value={description} onChange={(e) => setDescription(e.target.value)} placeholder="설명" rows={5} required />
        <button type="submit">수정하기</button>
      </form>
    </div>
  );
}
