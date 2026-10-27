package me.son.chatlabapi.chat.domain.service.impl;

import lombok.RequiredArgsConstructor;

import me.son.chatlabapi.chat.domain.entity.ChatRoom;
import me.son.chatlabapi.chat.domain.entity.ChatRoomMember;
import me.son.chatlabapi.chat.domain.repository.ChatRoomMemberRepository;
import me.son.chatlabapi.chat.domain.repository.ChatRoomRepository;
import me.son.chatlabapi.chat.domain.service.ChatRoomMemberService;
import me.son.chatlabapi.chat.dto.RoomInvitePayload;
import me.son.chatlabapi.chat.dto.RoomMemberResponse;
import me.son.chatlabapi.chat.exception.ChatErrorCode;
import me.son.chatlabapi.global.exception.BusinessException;
import me.son.chatlabapi.user.domain.entity.User;
import me.son.chatlabapi.user.domain.repository.UserRepository;
import me.son.chatlabapi.user.exception.UserErrorCode;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomMemberServiceImpl implements ChatRoomMemberService {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    @Override
    public void inviteMember(Long inviterId, Long roomId, String username) {
        // 마지막 멤버의 나가기(방 삭제)와 초대가 경합하지 않도록 방 row 를 잠근다.
        ChatRoom room = chatRoomRepository.findByIdForUpdate(roomId)
                .orElseThrow(() -> new BusinessException(ChatErrorCode.CHAT_ROOM_NOT_FOUND));

        // 초대한 사람이 방 멤버인지 확인
        if (!chatRoomMemberRepository.existsByRoom_IdAndUser_Id(roomId, inviterId)) {
            throw new BusinessException(ChatErrorCode.NOT_ROOM_MEMBER);
        }

        // 초대 대상 유저 조회
        User target = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // 이미 멤버인지 체크
        if (chatRoomMemberRepository.existsByRoom_IdAndUser_Id(roomId, target.getId())) {
            throw new BusinessException(ChatErrorCode.ALREADY_ROOM_MEMBER);
        }

        chatRoomMemberRepository.save(new ChatRoomMember(target, room));

        // 커밋 전에 알림을 보내면 대상이 방 목록을 다시 조회해도 새 방이 보이지 않을 수 있으므로 커밋 이후에 전송
        String targetUserId = target.getId().toString();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                messagingTemplate.convertAndSendToUser(targetUserId, "/queue/rooms", new RoomInvitePayload(roomId));
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomMemberResponse> getRoomMembers(Long userId, Long roomId) {
        if (!chatRoomRepository.existsById(roomId)) {
            throw new BusinessException(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
        }

        if (!chatRoomMemberRepository.existsByRoom_IdAndUser_Id(roomId, userId)) {
            throw new BusinessException(ChatErrorCode.NOT_ROOM_MEMBER);
        }

        return chatRoomMemberRepository.findUsernamesByRoomId(roomId).stream()
                .map(RoomMemberResponse::new)
                .toList();
    }

}
