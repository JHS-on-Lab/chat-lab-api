package me.son.chatlabapi.chat.domain.repository;

import me.son.chatlabapi.chat.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
