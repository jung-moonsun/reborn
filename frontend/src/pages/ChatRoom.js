// ✅ ChatRoom.jsx - 메시지 읽음 처리 후 로컬 스토리지에 플래그 저장
import { useEffect, useRef, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { getMessages } from '../api/chatRoom';
import { getProfile } from '../api/auth';
import { toAbsoluteImageUrl } from '../utils/url';
import './ChatRoom.css';

export default function ChatRoom() {
  const { roomId } = useParams();
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [file, setFile] = useState(null);
  const [userId, setUserId] = useState(null);
  const clientRef = useRef(null);
  const subscriptionRef = useRef(null);
  const fileInputRef = useRef(null);

  useEffect(() => {
    const connect = async () => {
      try {
        const token = localStorage.getItem('token');
        const profile = await getProfile();
        setUserId(profile.data.id);

        const res = await getMessages(roomId);
        setMessages(res.data.content);

        const sock = new SockJS(`http://localhost:8080/ws-chat?token=${token}`);
        const client = new Client({
          webSocketFactory: () => sock,
          reconnectDelay: 5000,
          debug: (str) => console.log('[STOMP]', str),
          onConnect: () => {
            console.log('✅ STOMP 연결됨');

            if (subscriptionRef.current) {
              subscriptionRef.current.unsubscribe();
              subscriptionRef.current = null;
            }

            subscriptionRef.current = client.subscribe(`/topic/chat/${roomId}`, (msg) => {
              const data = JSON.parse(msg.body);
              setMessages((prev) => {
                if (prev.some((m) => m.id === data.id)) return prev;
                return [...prev, data];
              });
            });

            client.publish({
              destination: '/pub/chat/message',
              body: JSON.stringify({
                messageType: 'ENTER',
                roomId,
                senderId: profile.data.id,
                message: '',
              }),
            });

            // ✅ 메시지 읽음 처리
            fetch(`http://localhost:8080/api/chat/rooms/${roomId}/read?receiverId=${profile.data.id}`, {
              method: 'PUT',
              headers: { Authorization: `Bearer ${token}` },
            }).then(() => {
              localStorage.setItem('chatRead', 'true');
            });
          },
          onStompError: (frame) => {
            console.error('💥 STOMP 오류:', frame.headers['message']);
          },
        });

        client.activate();
        clientRef.current = client;
      } catch (err) {
        console.error('💥 연결 실패:', err);
      }
    };

    connect();

    return () => {
      if (subscriptionRef.current) {
        subscriptionRef.current.unsubscribe();
        subscriptionRef.current = null;
      }
      clientRef.current?.deactivate();
      clientRef.current = null;
    };
  }, [roomId]);

  const handleSend = async () => {
    if (!clientRef.current?.connected) return;
    let fileUrl = null;

    if (file) {
      const formData = new FormData();
      formData.append('file', file);

      try {
        const res = await fetch(`http://localhost:8080/api/chat/upload`, {
          method: 'POST',
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
          },
          body: formData,
        });

        const data = await res.json();
        fileUrl = data.fileUrl;
      } catch (err) {
        console.error('이미지 업로드 실패', err);
        return;
      }
    }

    const messageType = fileUrl ? 'IMAGE' : 'TEXT';

    clientRef.current.publish({
      destination: '/pub/chat/message',
      body: JSON.stringify({
        messageType,
        roomId,
        senderId: userId,
        message: input,
        fileUrl,
      }),
    });

    setInput('');
    setFile(null);
    if (fileInputRef.current) fileInputRef.current.value = null;
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  const handleClipClick = () => {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  };

  return (
    <div className="chat-room">
      <h2>채팅방 #{roomId}</h2>
      <div className="chat-messages">
        {messages.map((msg, i) => (
          <div key={i} className={msg.senderId === userId ? 'mine' : 'theirs'}>
            <div className="chat-bubble">
              {msg.fileUrl && (
                <a href={toAbsoluteImageUrl(msg.fileUrl)} target="_blank" rel="noopener noreferrer">
                  <img
                    src={toAbsoluteImageUrl(msg.fileUrl)}
                    alt="chat-img"
                    className="chat-image"
                  />
                </a>
              )}
              {msg.message && <span>{msg.message}</span>}
            </div>
          </div>
        ))}
      </div>

      <div className="chat-input">
        <textarea
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="메시지를 입력하세요"
          rows={2}
        />
        <button className="clip-button" onClick={handleClipClick}>📎</button>
        <input
          type="file"
          accept="image/*"
          ref={fileInputRef}
          onChange={(e) => setFile(e.target.files[0])}
          style={{ display: 'none' }}
        />
        <button onClick={handleSend}>전송</button>
      </div>
    </div>
  );
}
