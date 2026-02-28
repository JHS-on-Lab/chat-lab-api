package me.son.chatlabapi.chat.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.son.chatlabapi.chat.domain.entity.enums.ChatMessageType;
import me.son.chatlabapi.user.domain.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message", indexes = {@Index(name = "idx_room_id_id_desc", columnList = "room_id, id DESC")})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id")
    private ChatRoom room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatMessageType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;   // TEXT: 실제 내용 / IMAGE: 이미지 URL

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public ChatMessage(ChatRoom room, User sender, ChatMessageType type, String content) {
        this.room = room;
        this.sender = sender;
        this.type = type;
        this.content = content;
    }

    public static ChatMessage create(ChatRoom room, User sender, ChatMessageType type, String content) {
        ChatMessage message = new ChatMessage();
        message.room = room;
        message.sender = sender;
        message.type = type;
        message.content = content;
        return message;
    }
}
