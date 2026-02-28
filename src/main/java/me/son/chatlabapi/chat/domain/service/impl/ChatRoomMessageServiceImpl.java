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
import me.son.chatlabapi.chat.exception.ChatRoomErrorCode;
import me.son.chatlabapi.global.exception.BusinessException;
import me.son.chatlabapi.user.domain.entity.User;
import me.son.chatlabapi.user.domain.repository.UserRepository;
import me.son.chatlabapi.user.exception.UserErrorCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomMessageServiceImpl implements ChatRoomMessageService {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public MessageSliceResponse getMessages(Long roomId, Long cursor, int size, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow(() -> new BusinessException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND));

        boolean isMember = chatRoomMemberRepository.existsByRoomIdAndUserId(roomId, userId);
        if (!isMember) {
            throw new BusinessException(ChatRoomErrorCode.NOT_A_ROOM_MEMBER);
        }

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

    @Override
    public MessageResponse sendMessage(Long roomId, Long userId, SendMessageRequest request) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND));

        boolean isMember = chatRoomMemberRepository.existsByRoomIdAndUserId(roomId, userId);

        if (!isMember) {
            throw new BusinessException(ChatRoomErrorCode.NOT_A_ROOM_MEMBER);
        }

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        ChatMessageType type = ChatMessageType.valueOf(request.type());

        ChatMessage message = new ChatMessage(
                room,
                sender,
                type,
                request.content()
        );

        ChatMessage saved = chatMessageRepository.save(message);

        return MessageResponse.from(saved);
    }
}
