import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { getProfile } from '../api/auth';
import { getUnreadCount } from '../api/chatRoom';
import './Header.css';

export default function Header({ isLoggedIn, setIsLoggedIn }) {
  const navigate = useNavigate();
  const location = useLocation();
  const [user, setUser] = useState(null);
  const [keyword, setKeyword] = useState('');
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    const fetchProfileAndUnread = async () => {
      if (!isLoggedIn) {
        setUser(null);
        return;
      }
      try {
        const res = await getProfile();
        setUser(res.data);

        const localFlag = localStorage.getItem('chatRead');
        if (localFlag === 'true') {
          setUnreadCount(0);
          localStorage.removeItem('chatRead');
        } else {
          const unreadRes = await getUnreadCount(res.data.id);
          setUnreadCount(unreadRes.data);
        }
      } catch (_) {
        setUser(null);
        setUnreadCount(0);
      }
    };
    fetchProfileAndUnread();
  }, [isLoggedIn, location.key]); // isLoggedIn 바뀌면 다시 불러옴

  const logout = () => {
    localStorage.removeItem('token');
    setIsLoggedIn(false); // 리렌더링 트리거
    setUser(null);
    setUnreadCount(0);
    navigate('/');
  };

  const handleSearch = (e) => {
    e.preventDefault();
    if (keyword.trim()) {
      navigate(`/search?keyword=${encodeURIComponent(keyword.trim())}`);
    }
  };

  return (
    <header className="header">
      <Link to="/" className="logo">RE:BORN</Link>
      <form onSubmit={handleSearch} className="search-form">
        <input
          type="text"
          placeholder="상품 검색..."
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
        />
        <button type="submit">검색</button>
      </form>
      <nav>
        {user ? (
          <>
            <span>{user.nickname}님</span>
            <Link to="/chat" className="chat-link">
              채팅
              {unreadCount > 0 && <span className="badge">{unreadCount}</span>}
            </Link>
            <Link to="/create">상품 등록</Link>
            <Link to="/mypage">마이페이지</Link>
            <button onClick={logout}>로그아웃</button>
          </>
        ) : (
          <>
            <Link to="/login">로그인</Link>
            <Link to="/signup">회원가입</Link>
          </>
        )}
      </nav>
    </header>
  );
}
