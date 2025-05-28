package com.ms.reborn.chat.service;

import com.ms.reborn.chat.dto.ChatMessageRequest;
import com.ms.reborn.chat.dto.ChatMessageResponse;
import com.ms.reborn.chat.entity.ChatMessage;
import com.ms.reborn.chat.entity.ChatRoom;
import com.ms.reborn.chat.repository.ChatMessageRepository;
import com.ms.reborn.chat.repository.ChatRoomRepository;
import com.ms.reborn.user.entity.User;
import com.ms.reborn.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest request) {
        ChatRoom chatRoom = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("채팅방 없음"));

        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new RuntimeException("보낸 사람 없음"));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .message(request.getMessage())
                .messageType(request.getMessageType())
                .createdAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(message);

        return ChatMessageResponse.from(message);
    }
}
