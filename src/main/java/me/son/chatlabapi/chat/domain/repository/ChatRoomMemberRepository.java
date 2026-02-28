package me.son.chatlabapi.chat.domain.repository;

import me.son.chatlabapi.chat.domain.entity.ChatRoom;
import me.son.chatlabapi.chat.domain.entity.ChatRoomMember;
import me.son.chatlabapi.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    List<ChatRoomMember> findByUser(User user);

    Optional<ChatRoomMember> findByUserAndRoom(User user, ChatRoom room);

    boolean existsByUserAndRoom(User user, ChatRoom room);

    List<ChatRoomMember> findByRoom(ChatRoom room);

    boolean existsByRoomIdAndUserId(Long roomId, Long userId);

    Optional<ChatRoomMember> findByRoom_IdAndUser_Id(Long roomId, Long userId);

    boolean existsByRoom_Id(Long roomId);
}
