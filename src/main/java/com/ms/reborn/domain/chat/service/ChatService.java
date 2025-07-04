package com.ms.reborn.domain.chat.service;

import com.ms.reborn.global.exception.CustomException;
import com.ms.reborn.global.exception.ErrorCode;
import com.ms.reborn.domain.chat.dto.ChatMessageRequest;
import com.ms.reborn.domain.chat.dto.ChatMessageResponse;
import com.ms.reborn.domain.chat.entity.ChatMessage;
import com.ms.reborn.domain.chat.entity.ChatRoom;
import com.ms.reborn.domain.chat.entity.MessageType;
import com.ms.reborn.domain.chat.repository.ChatMessageRepository;
import com.ms.reborn.domain.chat.repository.ChatRoomRepository;
import com.ms.reborn.domain.user.entity.User;
import com.ms.reborn.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest request) {
        ChatRoom chatRoom = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!sender.getId().equals(chatRoom.getBuyer().getId()) &&
                !sender.getId().equals(chatRoom.getSeller().getId())) {
            throw new CustomException(ErrorCode.CHAT_ACCESS_DENIED);
        }

        if (sender.isDeleted()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        User receiver = sender.getId().equals(chatRoom.getSeller().getId())
                ? chatRoom.getBuyer() : chatRoom.getSeller();

        if (request.getMessageType() == MessageType.IMAGE && StringUtils.isBlank(request.getFileUrl())) {
            throw new CustomException(ErrorCode.IMAGE_MESSAGE_NEEDS_URL);
        }

        if (receiver.getId().equals(chatRoom.getBuyer().getId()) && chatRoom.isExitedByBuyer()) {
            chatRoom.setExitedByBuyer(false);
        } else if (receiver.getId().equals(chatRoom.getSeller().getId()) && chatRoom.isExitedBySeller()) {
            chatRoom.setExitedBySeller(false);
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .receiver(receiver)
                .message(request.getMessage())
                .fileUrl(request.getFileUrl())
                .messageType(request.getMessageType())
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        chatMessageRepository.save(message);

        chatRoom.setLastMessageAt(message.getCreatedAt());
        chatRoomRepository.save(chatRoom);

        messagingTemplate.convertAndSend(
                "/topic/chat/" + request.getRoomId(),
                ChatMessageResponse.from(message)
        );

        return ChatMessageResponse.from(message);
    }

    @Transactional(readOnly = true)
    public Page<ChatMessageResponse> getMessages(Long roomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId, pageable);
        return messages.map(ChatMessageResponse::from);
    }

    @Transactional
    public void markMessageAsRead(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_MESSAGE_NOT_FOUND));
        if (!message.isRead()) {
            message.setRead(true);
            chatMessageRepository.save(message);
        }
    }

    @Transactional(readOnly = true)
    public long getUnreadMessageCount(Long receiverId) {
        return chatMessageRepository.countByReceiverIdAndIsReadFalse(receiverId);
    }

    @Transactional
    public void markAllMessagesAsRead(Long roomId, Long receiverId) {
        chatMessageRepository.markAllAsRead(roomId, receiverId);
    }
}
