package me.son.chatlabapi.chat.domain.service.impl;

import lombok.RequiredArgsConstructor;
import me.son.chatlabapi.chat.domain.entity.ChatRoom;
import me.son.chatlabapi.chat.domain.entity.ChatRoomMember;
import me.son.chatlabapi.chat.domain.repository.ChatRoomMemberRepository;
import me.son.chatlabapi.chat.domain.repository.ChatRoomRepository;
import me.son.chatlabapi.chat.dto.MyRoomResponse;
import me.son.chatlabapi.chat.exception.ChatRoomErrorCode;
import me.son.chatlabapi.chat.domain.service.ChatRoomService;
import me.son.chatlabapi.chat.dto.CreateRoomResponse;
import me.son.chatlabapi.global.exception.BusinessException;
import me.son.chatlabapi.user.domain.entity.User;
import me.son.chatlabapi.user.domain.repository.UserRepository;
import me.son.chatlabapi.user.exception.UserErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

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
    public void leaveRoom(Long userId, Long roomId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND));

        ChatRoomMember membership = chatRoomMemberRepository.findByUserAndRoom(user, room)
                .orElseThrow(() -> new BusinessException(ChatRoomErrorCode.NOT_A_ROOM_MEMBER));

        chatRoomMemberRepository.delete(membership);

        // (선택) 방에 아무도 없으면 삭제
        if (chatRoomMemberRepository.countByRoom(room) == 0) {
            chatRoomRepository.delete(room);
        }
    }
}
