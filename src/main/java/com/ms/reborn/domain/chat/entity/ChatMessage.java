package com.ms.reborn.domain.chat.entity;

import com.ms.reborn.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    private User receiver;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MessageType messageType = MessageType.TEXT;

    private LocalDateTime createdAt;

    @Column
    private String fileUrl;

    @Column(name = "read_flag", nullable = false)
    @Builder.Default
    private boolean isRead = false;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
