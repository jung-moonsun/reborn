import { useSearchParams, useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { searchProducts } from '../api/product';
import { toAbsoluteImageUrl } from '../utils/url';
import './Home.css';

export default function Search() {
  const [params] = useSearchParams();
  const keyword = params.get('keyword') || '';
  const [results, setResults] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetch = async () => {
      try {
        // page=0, size=20 명시
        const res = await searchProducts(keyword, 0, 20);
        setResults(res.data.data.content || []);
      } catch (e) {
        console.error('검색 API 오류', e);
      }
    };
    if (keyword) fetch();
  }, [keyword]);

  return (
    <div className="home-container">
      <h2 style={{ textAlign: 'center' }}>🔍 “{keyword}” 검색 결과</h2>
      {results.length > 0 ? (
        <div className="product-grid">
          {results.map((p) => (
            <div
              key={p.id}
              className="product-card"
              onClick={() => navigate(`/product/${p.id}`)}
            >
              <img
                src={toAbsoluteImageUrl(p.imageUrls?.[0])}
                alt={p.title}
              />
              <h3>{p.title}</h3>
              <p>{p.price.toLocaleString()}원</p>
            </div>
          ))}
        </div>
      ) : (
        <p style={{ textAlign: 'center' }}>검색 결과가 없습니다.</p>
      )}
    </div>
  );
}
