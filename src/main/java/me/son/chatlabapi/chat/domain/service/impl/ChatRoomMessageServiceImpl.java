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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomMessageServiceImpl implements ChatRoomMessageService {
    private static final int MAX_PAGE_SIZE = 100;
    // 클라이언트 인코딩(Base64) 후 길이 기준. 프론트 입력 제한(30,000자)을 수용하는 크기
    // 한글 30,000자 = UTF-8 90,000B -> Base64 120,000자 (WebSocket 메시지 한도 128KB 이내)
    private static final int MAX_CONTENT_LENGTH = 125_000;

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public MessageSliceResponse getMessages(Long roomId, Long cursor, int size, Long userId) {
        validateRoomMember(roomId, userId);

        // 과도한 조회 방지: 1 ~ MAX_PAGE_SIZE 로 제한
        int pageSize = Math.clamp(size, 1, MAX_PAGE_SIZE);

        // id DESC 로 조회된 결과
        List<ChatMessage> messages = chatMessageRepository.findMessages(roomId, cursor, PageRequest.of(0, pageSize));

        boolean hasNext = messages.size() == pageSize;
        Long nextCursor = messages.isEmpty() ? null : messages.getLast().getId();

        // 화면 표시용으로 오래된 순(id ASC) 정렬
        List<MessageResponse> dtoList = messages.reversed().stream()
                .map(MessageResponse::from)
                .toList();

        return new MessageSliceResponse(dtoList, hasNext, nextCursor);
    }

    @Transactional
    @Override
    public MessageResponse sendMessage(Long roomId, Long userId, SendMessageRequest request) {
        validateRoomMember(roomId, userId);

        // WebSocket 경로는 @Valid 가 적용되지 않으므로 서비스에서 직접 검증
        if (request.type() == null || request.content() == null || request.content().isBlank()) {
            throw new BusinessException(ChatErrorCode.INVALID_MESSAGE_TYPE);
        }

        if (request.content().length() > MAX_CONTENT_LENGTH) {
            throw new BusinessException(ChatErrorCode.MESSAGE_TOO_LONG);
        }

        ChatMessageType type;
        try {
            type = ChatMessageType.valueOf(request.type().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ChatErrorCode.INVALID_MESSAGE_TYPE);
        }

        ChatRoom room = chatRoomRepository.getReferenceById(roomId);
        User sender = userRepository.getReferenceById(userId);

        ChatMessage saved = chatMessageRepository.save(ChatMessage.create(room, sender, type, request.content()));

        // 트랜잭션 안이므로 sender 프록시 초기화(username 조회) 안전
        return MessageResponse.from(saved);
    }

    private void validateRoomMember(Long roomId, Long userId) {
        if (!chatRoomMemberRepository.existsByRoom_IdAndUser_Id(roomId, userId)) {
            throw new BusinessException(ChatErrorCode.NOT_ROOM_MEMBER);
        }
    }
}
