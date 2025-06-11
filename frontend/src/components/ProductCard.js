// src/components/ProductCard.js
import { useNavigate } from 'react-router-dom';
import './ProductCard.css';

export default function ProductCard({ product }) {
  const navigate = useNavigate();

  return (
    <div className="product-card" onClick={() => navigate(`/product/${product.id}`)}>
      <img
        src={product.thumbnail || '/placeholder.png'}
        alt={product.title}
        className="thumbnail"
      />
      <div className="info">
        <h3>{product.title}</h3>
        <p>{product.price.toLocaleString()}원</p>
        <p className="location">{product.location}</p>
      </div>
    </div>
  );
}
