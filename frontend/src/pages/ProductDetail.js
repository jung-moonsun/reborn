import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getProduct, deleteProduct } from '../api/product';
import { getComments, addComment, updateComment, deleteComment } from '../api/comment';
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
  const [replyContent, setReplyContent] = useState('');
  const [userId, setUserId] = useState(null);
  const [openReplyFormId, setOpenReplyFormId] = useState(null);
  const [editCommentId, setEditCommentId] = useState(null);
  const [editContent, setEditContent] = useState('');

  useEffect(() => {
    const load = async () => {
      try {
        const [prodRes, commentRes, userRes] = await Promise.all([
          getProduct(id),
          getComments(id),
          getProfile(),
        ]);
        setProduct(prodRes.data.data);
        setUserId(userRes.data.id);
        setComments(commentRes.data.data || []);
      } catch (err) {
        console.error('로드 실패', err);
      }
    };
    load();
  }, [id]);

  const handleAddComment = async (e) => {
    e.preventDefault();
    if (!commentText.trim()) return;
    try {
      await addComment(id, commentText, userId);
      const res = await getComments(id);
      setComments(res.data.data || []);
      setCommentText('');
    } catch (err) {
      console.error('댓글 등록 실패', err);
    }
  };

  const handleReplySubmit = async (e, parentId) => {
    e.preventDefault();
    if (!replyContent.trim()) return;
    try {
      await addComment(id, replyContent, userId, parentId);
      const res = await getComments(id);
      setComments(res.data.data || []);
      setReplyContent('');
      setOpenReplyFormId(null);
    } catch (err) {
      console.error('답글 등록 실패', err);
    }
  };

const handleChat = async () => {
  if (!userId) {
    alert('로그인 정보가 없습니다.');
    return;
  }
  try {
    const res = await createOrGetRoom(id, userId);
    const roomId = res?.data?.id;
    if (!roomId) {
      console.error('채팅방 생성 응답 이상함:', res);
      alert('채팅방 생성에 실패했습니다.');
      return;
    }
    navigate(`/chat/${roomId}`);
  } catch (err) {
    console.error('채팅방 생성 실패', err.response || err);
    alert('채팅방 생성 중 오류가 발생했습니다.');
  }
};

  const handleUpdate = () => navigate(`/product/edit/${id}`);
  const handleDelete = async () => {
    if (!window.confirm('정말 삭제하시겠습니까?')) return;
    try {
      await deleteProduct(id);
      navigate('/');
    } catch (err) {
      console.error('상품 삭제 실패', err);
      alert('삭제에 실패했습니다.');
    }
  };

  const renderComment = (comment, depth = 0, index = 0) => (
    <li key={`${comment.id}-${depth}-${index}`} className={`comment-item depth-${depth}`}>
      <strong>{comment.writerNickname}</strong>: {comment.content}

      {userId === comment.writerId && (
        <>
          <button
            className="comment-edit-btn"
            onClick={() => {
              setEditCommentId(comment.id);
              setEditContent(comment.content);
            }}
          >
            수정
          </button>
          <button
            className="comment-delete-btn"
            onClick={async () => {
              if (window.confirm('댓글을 삭제하시겠습니까?')) {
                try {
                  await deleteComment(comment.id, userId);
                  const res = await getComments(id);
                  setComments(res.data.data || []);
                } catch (err) {
                  console.error('댓글 삭제 실패', err);
                }
              }
            }}
          >
            삭제
          </button>
        </>
      )}

      <button
        onClick={() => {
          setOpenReplyFormId(comment.id);
          setReplyContent('');
        }}
        className="reply-btn"
      >
        ↩ 답글 달기
      </button>

      {openReplyFormId === comment.id && (
        <form onSubmit={(e) => handleReplySubmit(e, comment.id)} className="comment-form" style={{ marginTop: '8px' }}>
          <textarea
            value={replyContent}
            onChange={(e) => setReplyContent(e.target.value)}
            rows={2}
            placeholder="답글을 입력하세요"
            required
          />
          <button type="submit">답글 등록</button>
          <button type="button" onClick={() => setOpenReplyFormId(null)} style={{ marginLeft: '8px' }}>
            취소
          </button>
        </form>
      )}

      {editCommentId === comment.id && (
        <form
          onSubmit={async (e) => {
            e.preventDefault();
            try {
              await updateComment(comment.id, editContent, userId);
              const res = await getComments(id);
              setComments(res.data.data || []);
              setEditCommentId(null);
            } catch (err) {
              console.error('댓글 수정 실패', err);
            }
          }}
          className="comment-form"
          style={{ marginTop: '8px' }}
        >
          <textarea
            value={editContent}
            onChange={(e) => setEditContent(e.target.value)}
            rows={2}
            required
          />
          <button type="submit">수정 완료</button>
          <button type="button" onClick={() => setEditCommentId(null)} style={{ marginLeft: '8px' }}>
            취소
          </button>
        </form>
      )}

      {comment.replies && comment.replies.length > 0 && (
        <ul className="reply-list">
          {comment.replies.map((reply, idx) => renderComment(reply, depth + 1, idx))}
        </ul>
      )}
    </li>
  );

  if (!product) return <div className="product-detail">로딩중...</div>;
  const isOwner = userId === product.userId;

  return (
    <div className="product-detail">
      <a href={toAbsoluteImageUrl(product.imageUrls?.[0])} target="_blank" rel="noopener noreferrer">
        <img src={toAbsoluteImageUrl(product.imageUrls?.[0])} alt={product.title} className="image" />
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
        {comments.map((c, idx) => renderComment(c, 0, idx))}
      </ul>
    </div>
  );
}
