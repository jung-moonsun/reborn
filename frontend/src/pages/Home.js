import { useEffect, useState } from 'react';
import { searchProducts } from '../api/product';
import { useNavigate } from 'react-router-dom';
import './Home.css';
import { toAbsoluteImageUrl } from '../utils/url';

export default function Home() {
  const [products, setProducts]     = useState([]);
  const [page, setPage]             = useState(0);
  const size = 8;                    // 한 페이지당 8개
  const [totalPages, setTotalPages] = useState(1);
  const navigate = useNavigate();

  useEffect(() => {
    const load = async () => {
      try {
        const res = await searchProducts('', page, size);
        // ApiResponse 구조: { data: { content, totalPages, ... } }
        const pageData = res.data.data;
        setProducts(pageData.content || []);
        setTotalPages(pageData.totalPages || 1);
      } catch (err) {
        console.error('제품 로드 실패', err);
      }
    };
    load();
  }, [page]);

  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric', month: '2-digit', day: '2-digit'
    });
  };

  return (
    <div className="home-container">
      <h2 style={{ textAlign: 'center' }}>최신 상품</h2>
      <div className="product-grid">
        {products.map(p => (
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

      {/* 페이지 네비게이션 */}
      <div className="pagination">
        <button
          onClick={() => setPage(prev => Math.max(prev - 1, 0))}
          disabled={page === 0}
        >
          이전
        </button>
        <span>{page + 1} / {totalPages}</span>
        <button
          onClick={() => setPage(prev => Math.min(prev + 1, totalPages - 1))}
          disabled={page >= totalPages - 1}
        >
          다음
        </button>
      </div>
    </div>
  );
}
