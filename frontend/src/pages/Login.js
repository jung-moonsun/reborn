// src/components/Login.jsx
import { useState } from 'react';
import { login } from '../api/auth';
import './Login.css';

export default function Login({ setIsLoggedIn }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  // 이메일/비밀번호 로그인
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await login(email, password);
      localStorage.setItem('token', res.data.token);
      setIsLoggedIn(true);
      window.location.href = '/';
    } catch (err) {
      console.error('로그인 실패:', err);
      alert('이메일 또는 비밀번호가 올바르지 않습니다.');
    }
  };

  // 카카오 로그인 시작
  const handleKakaoLogin = () => {
    window.location.href = 'http://localhost:8080/api/users/oauth/kakao';
  };

  return (
    <div className="login-container">
      <h2>로그인</h2>
      <form onSubmit={handleSubmit} className="login-form">
        <input
          type="email"
          placeholder="이메일"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="비밀번호"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">로그인</button>
      </form>
      <hr />
      <button onClick={handleKakaoLogin} className="kakao-login-btn">
        <img
          src="/kakao_login_medium_narrow.png"
          alt="카카오 로그인"
          style={{ height: '45px' }}
        />
      </button>
    </div>
  );
}
