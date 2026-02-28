package me.son.chatlabapi.chat.domain.service;

import me.son.chatlabapi.chat.dto.RoomMemberResponse;

import java.util.List;

public interface ChatRoomMemberService {
    void inviteMember(Long inviterId, Long roomId, String username);
    List<RoomMemberResponse> getRoomMembers(Long userId, Long roomId);
}
