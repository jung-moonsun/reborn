// src/pages/ProductDetail.jsx
import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getProduct, deleteProduct } from '../api/product';
import { getComments, addComment } from '../api/comment';
import { getProfile } from '../api/auth';
import { createOrGetRoom } from '../api/chatRoom';
import { toAbsoluteImageUrl } from '../utils/url';
import './ProductDetail.css';

export default function ProductDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [product, setProduct] = useState(null);
  const [comments, setComments] = useState([]);
  const [commentText, setCommentText] = useState('');
  const [userId, setUserId] = useState(null);
  const [replyTo, setReplyTo] = useState(null);


  useEffect(() => {
    const load = async () => {
      try {
        const [prodRes, commentRes, userRes] = await Promise.all([
          getProduct(id),
          getComments(id),
          getProfile(),
        ]);
        setProduct(prodRes.data);
        setComments(commentRes.data.content);
        setUserId(userRes.data.id);
      } catch (err) {
        console.error(err);
      }
    };
    load();
  }, [id]);

    const handleAddComment = async (e) => {
      e.preventDefault();
      if (!commentText.trim()) return;
      try {
        await addComment(id, commentText.trim(), userId, replyTo);
        const res = await getComments(id);
        setComments(res.data.content);
        setCommentText('');
        setReplyTo(null); // ✅ 입력 후 초기화
      } catch (err) {
        console.error('댓글 등록 실패', err);
      }
    };

  const handleChat = async () => {
    try {
      if (!userId) {
        alert('로그인 정보가 없습니다.');
        return;
      }
      const res = await createOrGetRoom(id, userId);
      navigate(`/chat/${res.data.id}`);
    } catch (err) {
      console.error('채팅방 생성 실패', err);
    }
  };

  const handleUpdate = () => {
    navigate(`/product/edit/${id}`);
  };

  const handleDelete = async () => {
    if (!window.confirm('정말 이 상품을 삭제하시겠습니까?')) return;
    try {
      await deleteProduct(id);
      navigate('/');
    } catch (err) {
      console.error('상품 삭제 실패', err);
      alert('삭제에 실패했습니다.');
    }
  };

  if (!product) return <div className="product-detail">로딩중...</div>;

  const isOwner = userId === product.userId;

  return (
    <div className="product-detail">
      <a
        href={toAbsoluteImageUrl(product.imageUrls?.[0])}
        target="_blank"
        rel="noopener noreferrer"
      >
        <img
          src={toAbsoluteImageUrl(product.imageUrls?.[0])}
          alt={product.title}
          className="image"
        />
      </a>
      <h2>{product.title}</h2>
      <div className="meta">
        <span>판매자: <strong>{product.userNickname}</strong></span><br />
        <span>등록일: {new Date(product.createdAt).toLocaleString()}</span><br />
        {product.updatedAt && product.updatedAt !== product.createdAt && (
          <span>수정일: {new Date(product.updatedAt).toLocaleString()}</span>
        )}
      </div>
      <p className="price">{product.price.toLocaleString()}원</p>
      <p className="description">{product.description}</p>

      {isOwner ? (
        <div className="owner-actions">
          <button onClick={handleUpdate}>수정</button>
          <button onClick={handleDelete}>삭제</button>
        </div>
      ) : (
        <button className="chat-btn" onClick={handleChat}>채팅하기</button>
      )}

      <hr style={{ margin: '20px 0' }} />
      <h3>댓글</h3>
      <form onSubmit={handleAddComment} className="comment-form">
        <textarea
          value={commentText}
          onChange={(e) => setCommentText(e.target.value)}
          rows={3}
          placeholder="댓글을 입력하세요"
          required
        />
        <button type="submit">등록</button>
      </form>
        <ul className="comment-list">
          {comments
            .filter((c) => !c.parentId)
            .map((parent) => (
              <li key={parent.id} className="comment-item">
                <strong>{parent.writerNickname}</strong>: {parent.content}

                <button
                  onClick={() => {
                    setReplyTo(parent.id);
                    setCommentText(`@${parent.writerNickname} `);
                  }}
                  className="reply-btn"
                >
                  ↩ 답글 달기
                </button>

                <ul className="reply-list">
                  {parent.replies?.map((reply) => (
                    <li key={reply.id} className="reply-item">
                      ↳ <strong>{reply.writerNickname}</strong>: {reply.content}
                    </li>
                  ))}
                </ul>
              </li>
            ))}
        </ul>
    </div>
  );
}
