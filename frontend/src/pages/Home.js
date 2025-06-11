import { useEffect, useState } from 'react';
import { searchProducts } from '../api/product';
import { useNavigate } from 'react-router-dom';
import './Home.css';
import { toAbsoluteImageUrl } from '../utils/url';

export default function Home() {
  const [products, setProducts] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const load = async () => {
      try {
        const res = await searchProducts('', 0, 20);
        setProducts(res.data.content);
      } catch (err) {
        console.error(err);
      }
    };
    load();
  }, []);

  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    });
  };

  return (
    <div className="home-container">
      <h2>최신 상품</h2>
      <div className="product-grid">
        {products.map((p) => (
          <div
            key={p.id}
            className="product-card"
            onClick={() => navigate(`/product/${p.id}`)}
          >
            <img src={toAbsoluteImageUrl(p.imageUrls?.[0])} alt={p.title} />
            <h3>{p.title}</h3>
            <p>{p.price.toLocaleString()}원</p>
            <p className="meta">
              {p.userNickname} · {formatDate(p.createdAt)}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
}
