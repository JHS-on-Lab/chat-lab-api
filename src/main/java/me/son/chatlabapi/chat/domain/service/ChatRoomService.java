package me.son.chatlabapi.chat.domain.service;

import me.son.chatlabapi.chat.dto.*;

import java.util.List;

public interface ChatRoomService {
    CreateRoomResponse createRoom(Long userId, String roomName);
    List<MyRoomResponse> getMyRooms(Long userId);
    void leaveRoom(Long id, Long roomId);
    void inviteMember(Long inviterId, Long roomId, String username);
    List<RoomMemberResponse> getRoomMembers(Long userId, Long roomId);
    MessageSliceResponse getMessages(Long roomId, Long cursor, int size, Long userId);
    MessageResponse sendMessage(Long roomId, Long userId, SendMessageRequest request);
}
