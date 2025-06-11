import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getMyRooms, exitRoom } from '../api/chatRoom'; // ✅ exitRoom 추가
import { getProfile } from '../api/auth'; // ✅ userId 가져오려고 필요
import './ChatList.css';

export default function ChatList() {
  const [rooms, setRooms] = useState([]);
  const [userId, setUserId] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const load = async () => {
      try {
        const profile = await getProfile();
        setUserId(profile.data.id);
        const res = await getMyRooms();
        setRooms(res.data.content);
      } catch (err) {
        console.error(err);
      }
    };
    load();
  }, []);

  const handleExit = async (roomId) => {
    if (!window.confirm('채팅방에서 나가시겠습니까?')) return;
    try {
      await exitRoom(roomId, userId);
      setRooms((prev) => prev.filter((r) => r.id !== roomId)); // ✅ 프론트에서도 제거
    } catch (err) {
      console.error('나가기 실패:', err);
    }
  };

  return (
    <div className="chat-list">
      <h2>내 채팅방</h2>
      <ul>
        {rooms.map((room) => (
          <li key={room.id} className="chat-room-item">
            <span onClick={() => navigate(`/chat/${room.id}`)}>
              🗨️ {room.productTitle}
            </span>
            <button className="exit-btn" onClick={() => handleExit(room.id)}>
              나가기
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}
