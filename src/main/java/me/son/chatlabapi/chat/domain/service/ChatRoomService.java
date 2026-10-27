package me.son.chatlabapi.chat.domain.service;

import me.son.chatlabapi.chat.dto.*;

import java.util.List;

public interface ChatRoomService {
    CreateRoomResponse createRoom(Long userId, String roomName);

    List<MyRoomResponse> getMyRooms(Long userId);

    void leaveRoom(Long id, Long roomId);

    /**
     * 회원 삭제 시 사용자가 보낸 모든 메시지와 참여 정보를 삭제하고, 멤버가 남지 않은 방은 함께 정리한다.
     */
    void deleteAllByUser(Long userId);
}
