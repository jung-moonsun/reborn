package com.ms.reborn.domain.chat.repository;

import com.ms.reborn.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long roomId, Pageable pageable);

    long countByReceiverIdAndIsReadFalse(Long receiverId);

    @Modifying
    @Query("update ChatMessage m set m.isRead = true where m.chatRoom.id = :roomId and m.receiver.id = :receiverId and m.isRead = false")
    void markAllAsRead(Long roomId, Long receiverId);

    @Modifying
    @Query("""
    DELETE FROM ChatMessage m
    WHERE m.sender.id = :userId OR m.receiver.id = :userId
""")
    void deleteByUserId(@Param("userId") Long userId);
}
