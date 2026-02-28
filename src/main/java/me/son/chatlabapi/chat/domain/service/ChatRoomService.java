package me.son.chatlabapi.chat.domain.service;

import me.son.chatlabapi.chat.dto.*;

import java.util.List;

public interface ChatRoomService {
    CreateRoomResponse createRoom(Long userId, String roomName);
    List<MyRoomResponse> getMyRooms(Long userId);
    void leaveRoom(Long id, Long roomId);
}
