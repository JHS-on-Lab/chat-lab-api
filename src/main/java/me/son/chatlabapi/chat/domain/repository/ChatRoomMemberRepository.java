package me.son.chatlabapi.chat.domain.repository;

import me.son.chatlabapi.chat.domain.entity.ChatRoom;
import me.son.chatlabapi.chat.domain.entity.ChatRoomMember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    boolean existsByRoom_IdAndUser_Id(Long roomId, Long userId);

    boolean existsByRoom_Id(Long roomId);

    Optional<ChatRoomMember> findByRoom_IdAndUser_Id(Long roomId, Long userId);

    // 최근에 참여한 방이 먼저 오도록 정렬 (N+1 방지를 위해 방 엔티티를 직접 조회)
    @Query("SELECT m.room FROM ChatRoomMember m WHERE m.user.id = :userId ORDER BY m.id DESC")
    List<ChatRoom> findRoomsByUserId(@Param("userId") Long userId);

    @Query("SELECT m.user.username FROM ChatRoomMember m WHERE m.room.id = :roomId ORDER BY m.id ASC")
    List<String> findUsernamesByRoomId(@Param("roomId") Long roomId);
}
