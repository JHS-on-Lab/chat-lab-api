package me.son.chatlabapi.chat.domain.service.impl;

import lombok.RequiredArgsConstructor;

import me.son.chatlabapi.chat.domain.entity.ChatRoom;
import me.son.chatlabapi.chat.domain.entity.ChatRoomMember;
import me.son.chatlabapi.chat.domain.repository.ChatRoomMemberRepository;
import me.son.chatlabapi.chat.domain.repository.ChatRoomRepository;
import me.son.chatlabapi.chat.domain.service.ChatRoomMemberService;
import me.son.chatlabapi.chat.dto.RoomMemberResponse;
import me.son.chatlabapi.chat.exception.ChatRoomErrorCode;
import me.son.chatlabapi.global.exception.BusinessException;
import me.son.chatlabapi.user.domain.entity.User;
import me.son.chatlabapi.user.domain.repository.UserRepository;
import me.son.chatlabapi.user.exception.UserErrorCode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomMemberServiceImpl implements ChatRoomMemberService {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

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

}
