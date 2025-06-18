// src/pages/KakaoCallback.jsx
import { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

export default function KakaoCallback({ setIsLoggedIn }) {
  const navigate = useNavigate();
  const { search } = useLocation();

  useEffect(() => {
    const token = new URLSearchParams(search).get('token');
    if (!token) {
      alert('카카오 로그인에 실패했습니다.');
      return navigate('/login', { replace: true });
    }
    localStorage.setItem('token', token);
    setIsLoggedIn(true);
    navigate('/', { replace: true });
  }, [search, navigate, setIsLoggedIn]);

  return (
    <div style={{ textAlign: 'center', marginTop: '50px' }}>
      <h2>카카오 로그인 처리 중…</h2>
      <p>잠시만 기다려주세요.</p>
    </div>
  );
}
