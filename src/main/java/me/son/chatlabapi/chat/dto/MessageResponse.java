package me.son.chatlabapi.chat.dto;

import me.son.chatlabapi.chat.domain.entity.ChatMessage;
import me.son.chatlabapi.chat.domain.entity.enums.ChatMessageType;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        Long senderId,
        String senderName,
        String content,
        ChatMessageType type,
        LocalDateTime createdAt
) {

    public static MessageResponse of(
            Long id,
            Long senderId,
            String senderName,
            String content,
            ChatMessageType type,
            LocalDateTime createdAt
    ) {
        return new MessageResponse(
                id,
                senderId,
                senderName,
                content,
                type,
                createdAt
        );
    }

    public static MessageResponse from(ChatMessage message) {
        return new MessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getSender().getUsername(),
                message.getContent(),
                message.getType(),
                message.getCreatedAt()
        );
    }
}
