import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { signup, login } from '../api/auth';
import './Signup.css';

export default function Signup() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [nickname, setNickname] = useState('');
  const [errorList, setErrorList] = useState([]);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorList([]);

    try {
      await signup(email, password, nickname);
      const res = await login(email, password);
      localStorage.setItem('token', res.data.token);
      navigate('/');
    } catch (err) {
      console.error(err);

      const validationErrors = err.response?.data?.validation;
      const fallbackMsg = err.response?.data?.message || '회원가입 실패';

      if (validationErrors && typeof validationErrors === 'object') {
        const msgs = Object.values(validationErrors);
        setErrorList(msgs);
      } else {
        setErrorList([fallbackMsg]);
      }
    }
  };

  return (
    <div className="signup-container">
      <h2>회원가입</h2>
      <form onSubmit={handleSubmit} className="signup-form">
        <input
          type="email"
          placeholder="이메일"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="비밀번호 (대/소문자, 숫자, 특수문자 포함)"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <input
          type="text"
          placeholder="닉네임"
          value={nickname}
          onChange={(e) => setNickname(e.target.value)}
          required
        />
        {errorList.length > 0 && (
          <ul className="error-list">
            {errorList.map((msg, idx) => (
              <li key={idx}>{msg}</li>
            ))}
          </ul>
        )}
        <button type="submit">가입하기</button>
      </form>
    </div>
  );
}
