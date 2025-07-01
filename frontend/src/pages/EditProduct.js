import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getProduct, updateProduct } from '../api/product';
import './CreateProduct.css';

export default function EditProduct() {
  const { id } = useParams();
  const navigate = useNavigate();

  // 로딩 상태 관리
  const [loading, setLoading] = useState(true);

  // 폼 필드 상태
  const [title, setTitle] = useState('');
  const [price, setPrice] = useState('');
  const [description, setDescription] = useState('');

  useEffect(() => {
    (async () => {
      try {
        const res = await getProduct(id);
        const { title, price, description } = res.data.data;

        // 기존 값을 그대로 세팅
        setTitle(title);
        setPrice(price.toString());       // 숫자 → 문자열
        setDescription(description);
      } catch (err) {
        console.error(err);
        // 에러 처리 (예: 알림 띄우고 뒤로 보내기)
      } finally {
        setLoading(false);
      }
    })();
  }, [id]);

  // 로딩 중엔 스켈레톤 또는 로더
  if (loading) return <div>로딩 중…</div>;

  const handleSubmit = async (e) => {
    e.preventDefault();
    // 문자열 → 숫자 변환
    await updateProduct(id, {
      title,
      price: Number(price),
      description,
    });
    navigate(`/product/${id}`);
  };

  return (
    <div className="create-product">
      <h2>상품 수정</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          value={title}
          onChange={e => setTitle(e.target.value)}
          placeholder="상품명"
          required
          maxLength={100}
        />

        <input
          type="number"
          value={price}
          onChange={e => setPrice(e.target.value)}
          placeholder="가격"
          required
          min={0}
        />

        <textarea
          value={description}
          onChange={e => setDescription(e.target.value)}
          placeholder="설명"
          rows={5}
          required
          maxLength={1000}
        />

        <button type="submit">수정하기</button>
      </form>
    </div>
  );
}
