// src/pages/MyPage.jsx
import { useEffect, useState } from 'react';
import { getProfile } from '../api/auth';
import { getMyProducts, deleteUser, changePassword } from '../api/mypage';
import { useNavigate } from 'react-router-dom';
import './MyPage.css';
import { toAbsoluteImageUrl } from '../utils/url';

export default function MyPage() {
  const [user, setUser] = useState(null);
  const [products, setProducts] = useState([]);
  const [page, setPage] = useState(0);
  const size = 3;
  const [totalPages, setTotalPages] = useState(1);
  const [showPasswordForm, setShowPasswordForm] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    (async () => {
      try {
        const { data } = await getProfile();
        setUser(data);
      } catch (err) {
        console.error('프로필 로드 실패', err);
      }
    })();
  }, []);

  useEffect(() => {
    (async () => {
      try {
        const res = await getMyProducts(page, size);
        const pageData = res.data.data;
        setProducts(pageData.content || []);
        setTotalPages(pageData.totalPages || 1);
      } catch (err) {
        console.error('마이페이지 상품 로드 실패', err);
        setProducts([]);
      }
    })();
  }, [page]);

  const handleWithdraw = async () => {
    const isLocal = !user?.provider || user.provider === 'local';
    let confirmed = window.confirm('정말 탈퇴하시겠습니까?');
    if (!confirmed) return;

    let pw = null;
    if (isLocal) {
      pw = prompt('비밀번호를 입력해주세요');
      if (!pw) return;
    }

    try {
      await deleteUser(pw);
      localStorage.removeItem('token');
      navigate('/');
    } catch (err) {
      alert('탈퇴 실패: ' + (err.response?.data?.message || err.message));
    }
  };

  const handlePasswordChangeSubmit = async (e) => {
    e.preventDefault();
    const oldPw = e.target.oldPw.value;
    const newPw = e.target.newPw.value;
    try {
      await changePassword(user.id, oldPw, newPw);
      alert('비밀번호 변경 완료!');
      e.target.reset();
      setShowPasswordForm(false);
    } catch (err) {
      alert('변경 실패: ' + (err.response?.data?.message || err.message));
    }
  };

  const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit' });
  };

  const isLocal = !user?.provider || user.provider === 'local';

  return (
    <div className="mypage-container">
      <div className="mypage-header">
        <h2>{user?.nickname}님의 마이페이지</h2>
        <p className="email">{user?.email}</p>
      </div>

      <h3 className="my-product">내가 올린 상품</h3>
      <div className="product-grid">
        {products.length ? (
          products.map((p) => (
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
              <p className="meta">{formatDate(p.createdAt)}</p>
            </div>
          ))
        ) : (
          <p>등록된 상품이 없습니다.</p>
        )}
      </div>

      <div className="pagination">
        <button
          onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
          disabled={page === 0}
        >
          이전
        </button>
        <span>{page + 1} / {totalPages}</span>
        <button
          onClick={() => setPage((prev) => Math.min(prev + 1, totalPages - 1))}
          disabled={page >= totalPages - 1}
        >
          다음
        </button>
      </div>

      <div className="account-action-section">
        {isLocal && (
          <button
            onClick={() => setShowPasswordForm((v) => !v)}
            className="toggle-password-btn"
          >
            {showPasswordForm ? '비밀번호 변경 취소' : '비밀번호 변경'}
          </button>
        )}
        <button onClick={handleWithdraw} className="withdraw-btn small">
          회원 탈퇴
        </button>
      </div>

      {showPasswordForm && isLocal && (
        <form className="password-form" onSubmit={handlePasswordChangeSubmit}>
          <input type="password" name="oldPw" placeholder="현재 비밀번호" required />
          <input type="password" name="newPw" placeholder="새 비밀번호" required />
          <button type="submit">변경하기</button>
        </form>
      )}
    </div>
  );
}
