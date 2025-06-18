// src/pages/ChatRoom.jsx
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
  const [previewUrl, setPreviewUrl] = useState(null);
  const [userId, setUserId] = useState(null);
  const clientRef = useRef(null);
  const subscriptionRef = useRef(null);
  const fileInputRef = useRef(null);

  // Connect & subscribe
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
            if (subscriptionRef.current) {
              subscriptionRef.current.unsubscribe();
            }
            subscriptionRef.current = client.subscribe(`/topic/chat/${roomId}`, (msg) => {
              const data = JSON.parse(msg.body);
              setMessages(prev => prev.some(m => m.id === data.id) ? prev : [...prev, data]);
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

            // mark as read
            fetch(`http://localhost:8080/api/chat/rooms/${roomId}/read?receiverId=${profile.data.id}`, {
              method: 'PUT',
              headers: { Authorization: `Bearer ${token}` },
            }).then(() => {
              localStorage.setItem('chatRead', 'true');
            });
          },
          onStompError: frame => {
            console.error('STOMP error:', frame.headers['message']);
          },
        });
        client.activate();
        clientRef.current = client;
      } catch (err) {
        console.error('Connection failed:', err);
      }
    };
    connect();
    return () => {
      subscriptionRef.current?.unsubscribe();
      clientRef.current?.deactivate();
    };
  }, [roomId]);

  // cleanup preview URL on unmount or change
  useEffect(() => {
    return () => {
      if (previewUrl) URL.revokeObjectURL(previewUrl);
    };
  }, [previewUrl]);

  const handleFileChange = e => {
    const f = e.target.files[0];
    setFile(f || null);
    if (f) {
      const url = URL.createObjectURL(f);
      setPreviewUrl(url);
    } else {
      setPreviewUrl(null);
    }
  };

  const handleClipClick = () => {
    fileInputRef.current?.click();
  };

  const handleSend = async () => {
    if (!clientRef.current?.connected) return;
    let fileUrl = null;
    if (file) {
      const form = new FormData();
      form.append('file', file);
      try {
        const res = await fetch(`http://localhost:8080/api/chat/upload`, {
          method: 'POST',
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
          body: form,
        });
        const data = await res.json();
        fileUrl = data.fileUrl;
      } catch (err) {
        console.error('Upload failed', err);
        return;
      }
    }
    clientRef.current.publish({
      destination: '/pub/chat/message',
      body: JSON.stringify({
        messageType: fileUrl ? 'IMAGE' : 'TEXT',
        roomId,
        senderId: userId,
        message: input,
        fileUrl,
      }),
    });
    setInput('');
    setFile(null);
    setPreviewUrl(null);
    fileInputRef.current.value = null;
  };

  const handleKeyPress = e => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
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
                  <img src={toAbsoluteImageUrl(msg.fileUrl)} alt="chat-img" className="chat-image"/>
                </a>
              )}
              {msg.message && <span>{msg.message}</span>}
            </div>
          </div>
        ))}
      </div>

      {/* thumbnail preview */}
      {previewUrl && (
        <div className="image-preview">
          <img src={previewUrl} alt="preview"/>
          <button onClick={() => { setFile(null); setPreviewUrl(null); }}>✕</button>
        </div>
      )}

      <div className="chat-input">
        <textarea
          value={input}
          onChange={e => setInput(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="메시지를 입력하세요"
          rows={2}
        />
        <button className="clip-button" onClick={handleClipClick}>📎</button>
        <input
          type="file"
          accept="image/*"
          ref={fileInputRef}
          onChange={handleFileChange}
          style={{ display: 'none' }}
        />
        <button onClick={handleSend}>전송</button>
      </div>
    </div>
);
}
