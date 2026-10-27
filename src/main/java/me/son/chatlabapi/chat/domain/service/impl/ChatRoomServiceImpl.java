package me.son.chatlabapi.chat.domain.service.impl;

import lombok.RequiredArgsConstructor;

import me.son.chatlabapi.chat.domain.entity.ChatRoom;
import me.son.chatlabapi.chat.domain.entity.ChatRoomMember;
import me.son.chatlabapi.chat.domain.repository.ChatMessageRepository;
import me.son.chatlabapi.chat.domain.repository.ChatRoomMemberRepository;
import me.son.chatlabapi.chat.domain.repository.ChatRoomRepository;
import me.son.chatlabapi.chat.dto.*;
import me.son.chatlabapi.chat.exception.ChatErrorCode;
import me.son.chatlabapi.chat.domain.service.ChatRoomService;
import me.son.chatlabapi.global.exception.BusinessException;
import me.son.chatlabapi.user.domain.entity.User;
import me.son.chatlabapi.user.domain.repository.UserRepository;
import me.son.chatlabapi.user.exception.UserErrorCode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    @Transactional
    public CreateRoomResponse createRoom(Long userId, String roomName) {
        if (roomName == null || roomName.isBlank()) {
            throw new BusinessException(ChatErrorCode.INVALID_ROOM_NAME);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        ChatRoom room = chatRoomRepository.save(new ChatRoom(roomName.trim()));
        chatRoomMemberRepository.save(new ChatRoomMember(user, room));

        return new CreateRoomResponse(room.getId(), room.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyRoomResponse> getMyRooms(Long userId) {
        return chatRoomMemberRepository.findRoomsByUserId(userId).stream()
                .map(room -> new MyRoomResponse(room.getId(), room.getName()))
                .toList();
    }

    @Override
    @Transactional
    public void leaveRoom(Long userId, Long roomId) {
        // 방 row 를 잠궈 동시에 나가는 마지막 멤버들 간의 정리 로직 경합을 방지한다.
        chatRoomRepository.findByIdForUpdate(roomId)
                .orElseThrow(() -> new BusinessException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));

        ChatRoomMember membership = chatRoomMemberRepository.findByRoom_IdAndUser_Id(roomId, userId)
                .orElseThrow(() -> new BusinessException(ChatErrorCode.NOT_ROOM_MEMBER));

        chatRoomMemberRepository.delete(membership);

        // 남은 멤버가 없으면 메시지와 방을 함께 정리
        if (!chatRoomMemberRepository.existsByRoom_Id(roomId)) {
            chatMessageRepository.deleteAllByRoomId(roomId);
            chatRoomRepository.deleteById(roomId);
        }
    }

    @Override
    @Transactional
    public void deleteAllByUser(Long userId) {
        List<Long> roomIds = chatRoomMemberRepository.findRoomIdsByUserId(userId);

        // leaveRoom / inviteMember 와의 경합 방지를 위해 참여 중인 방을 id 오름차순으로 잠근다 (데드락 방지)
        roomIds.forEach(chatRoomRepository::findByIdForUpdate);

        chatMessageRepository.deleteAllBySenderId(userId);
        chatRoomMemberRepository.deleteAllByUserId(userId);

        // 남은 멤버가 없는 방은 다른 사용자의 메시지까지 포함해 방째로 정리
        for (Long roomId : roomIds) {
            if (!chatRoomMemberRepository.existsByRoom_Id(roomId)) {
                chatMessageRepository.deleteAllByRoomId(roomId);
                chatRoomRepository.deleteById(roomId);
            }
        }
    }

}
