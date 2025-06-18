import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import axios from 'axios'; // ✅ 이거 꼭 있어야 함

export default function OAuthCallback() {
  const [params] = useSearchParams();
  const token = params.get('token');
  const error = params.get('error');
  const navigate = useNavigate();

  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token);
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`; // ✅ 요기 추가
      console.log('✅ JWT 저장 + axios 설정 완료:', token);
      navigate('/');
    } else if (error) {
      alert('소셜 로그인 실패: ' + decodeURIComponent(error));
      navigate('/login');
    } else {
      alert('예상치 못한 오류가 발생했습니다.');
      navigate('/login');
    }
  }, [token, error]);

  return <p>로그인 중입니다...</p>;
}
