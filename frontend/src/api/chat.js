// src/api/chat.js
let socket = null;

export const connectSocket = (roomId, token) => {
  socket = new WebSocket(`ws://localhost:8080/ws/chat?token=${token}&roomId=${roomId}`);
};

export const sendMessage = (messageObj) => {
  if (socket && socket.readyState === WebSocket.OPEN) {
    socket.send(JSON.stringify(messageObj));
  }
};

export const closeSocket = () => {
  if (socket) socket.close();
};
