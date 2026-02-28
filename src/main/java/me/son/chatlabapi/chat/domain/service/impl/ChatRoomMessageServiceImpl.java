package me.son.chatlabapi.chat.domain.service.impl;

import lombok.RequiredArgsConstructor;

import me.son.chatlabapi.chat.domain.entity.ChatMessage;
import me.son.chatlabapi.chat.domain.entity.ChatRoom;
import me.son.chatlabapi.chat.domain.entity.enums.ChatMessageType;
import me.son.chatlabapi.chat.domain.repository.ChatMessageRepository;
import me.son.chatlabapi.chat.domain.repository.ChatRoomMemberRepository;
import me.son.chatlabapi.chat.domain.repository.ChatRoomRepository;
import me.son.chatlabapi.chat.domain.service.ChatRoomMessageService;
import me.son.chatlabapi.chat.dto.MessageResponse;
import me.son.chatlabapi.chat.dto.MessageSliceResponse;
import me.son.chatlabapi.chat.dto.SendMessageRequest;
import me.son.chatlabapi.chat.exception.ChatErrorCode;
import me.son.chatlabapi.global.exception.BusinessException;
import me.son.chatlabapi.user.domain.entity.User;
import me.son.chatlabapi.user.domain.repository.UserRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomMessageServiceImpl implements ChatRoomMessageService {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public MessageSliceResponse getMessages(Long roomId, Long cursor, int size, Long userId) {
        validateRoomMember(roomId, userId);

        Pageable pageable = PageRequest.of(0, size);

        List<ChatMessage> messages = chatMessageRepository.findMessages(roomId, cursor, pageable);

        boolean hasNext = messages.size() == size;

        List<MessageResponse> dtoList = messages.stream()
                .map(MessageResponse::from)
                .sorted(Comparator.comparing(MessageResponse::id))
                .toList();

        Long nextCursor = messages.isEmpty() ? null : messages.get(messages.size() - 1).getId();

        return new MessageSliceResponse(dtoList, hasNext, nextCursor);
    }

    @Transactional
    @Override
    public MessageResponse sendMessage(Long roomId, Long userId, SendMessageRequest request) {
        validateRoomMember(roomId, userId);

        ChatMessageType type;
        try {
            type = ChatMessageType.valueOf(request.type().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ChatErrorCode.INVALID_MESSAGE_TYPE);
        }

        ChatRoom room = chatRoomRepository.getReferenceById(roomId);
        User sender = userRepository.getReferenceById(userId);

        ChatMessage message = ChatMessage.create(
                room,
                sender,
                type,
                request.content()
        );

        ChatMessage saved = chatMessageRepository.save(message);

        // Lazy 안전: sender.getUsername() 직접 사용
        return MessageResponse.of(
                saved.getId(),
                userId,
                sender.getUsername(),
                saved.getContent(),
                saved.getType(),
                saved.getCreatedAt()
        );
    }

    private void validateRoomMember(Long roomId, Long userId) {
        if (!chatRoomMemberRepository.existsByRoomIdAndUserId(roomId, userId)) {
            throw new BusinessException(ChatErrorCode.NOT_ROOM_MEMBER);
        }
    }
}
