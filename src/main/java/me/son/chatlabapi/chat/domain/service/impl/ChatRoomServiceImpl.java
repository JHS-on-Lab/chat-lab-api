package me.son.chatlabapi.chat.domain.service.impl;

import lombok.RequiredArgsConstructor;
import me.son.chatlabapi.chat.domain.entity.ChatMessage;
import me.son.chatlabapi.chat.domain.entity.ChatRoom;
import me.son.chatlabapi.chat.domain.entity.ChatRoomMember;
import me.son.chatlabapi.chat.domain.entity.enums.ChatMessageType;
import me.son.chatlabapi.chat.domain.repository.ChatMessageRepository;
import me.son.chatlabapi.chat.domain.repository.ChatRoomMemberRepository;
import me.son.chatlabapi.chat.domain.repository.ChatRoomRepository;
import me.son.chatlabapi.chat.dto.*;
import me.son.chatlabapi.chat.exception.ChatRoomErrorCode;
import me.son.chatlabapi.chat.domain.service.ChatRoomService;
import me.son.chatlabapi.global.exception.BusinessException;
import me.son.chatlabapi.user.domain.entity.User;
import me.son.chatlabapi.user.domain.repository.UserRepository;
import me.son.chatlabapi.user.exception.UserErrorCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public CreateRoomResponse createRoom(Long userId, String roomName) {
        if (roomName == null || roomName.isBlank()) {
            throw new BusinessException(ChatRoomErrorCode.INVALID_ROOM_NAME);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        ChatRoom room = new ChatRoom(roomName);
        chatRoomRepository.save(room);

        ChatRoomMember member = new ChatRoomMember(user, room);

        chatRoomMemberRepository.save(member);

        return new CreateRoomResponse(
                room.getId(),
                room.getName()
        );
    }

    @Override
    public List<MyRoomResponse> getMyRooms(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        List<ChatRoomMember> memberships = chatRoomMemberRepository.findByUser(user);

        return memberships.stream()
                .map(member -> {
                    ChatRoom room = member.getRoom();
                    return new MyRoomResponse(
                            room.getId(),
                            room.getName()
                    );
                })
                .toList();
    }

    @Override
    @Transactional
    public void leaveRoom(Long userId, Long roomId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND));

        ChatRoomMember membership = chatRoomMemberRepository.findByUserAndRoom(user, room)
                .orElseThrow(() -> new BusinessException(ChatRoomErrorCode.NOT_A_ROOM_MEMBER));

        // 멤버 삭제
        chatRoomMemberRepository.delete(membership);

        // 남은 멤버 존재 여부 확인
        boolean hasRemaining = chatRoomMemberRepository.existsByRoom_Id(roomId);

        if (!hasRemaining) {
            chatMessageRepository.deleteByRoom_Id(roomId);
            chatRoomRepository.deleteById(roomId);
        }
    }

    @Override
    public void inviteMember(Long inviterId, Long roomId, String username) {
        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND));

        // 초대한 사람이 방 멤버인지 확인
        boolean isMember = chatRoomMemberRepository.existsByUserAndRoom(inviter, room);
        if (!isMember) {
            throw new BusinessException(ChatRoomErrorCode.NOT_A_ROOM_MEMBER);
        }

        // 초대 대상 유저 조회
        User target = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // 이미 멤버인지 체크
        if (chatRoomMemberRepository.existsByUserAndRoom(target, room)) {
            throw new BusinessException(ChatRoomErrorCode.ALREADY_ROOM_MEMBER);
        }

        // 멤버 추가
        ChatRoomMember newMember = new ChatRoomMember(target, room);

        chatRoomMemberRepository.save(newMember);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomMemberResponse> getRoomMembers(Long userId, Long roomId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND));

        boolean isMember = chatRoomMemberRepository.existsByUserAndRoom(user, room);
        if (!isMember) {
            throw new BusinessException(ChatRoomErrorCode.NOT_A_ROOM_MEMBER);
        }

        List<ChatRoomMember> members = chatRoomMemberRepository.findByRoom(room);

        return members.stream()
                .map(member -> new RoomMemberResponse(member.getUser().getUsername()))
                .toList();
    }

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
