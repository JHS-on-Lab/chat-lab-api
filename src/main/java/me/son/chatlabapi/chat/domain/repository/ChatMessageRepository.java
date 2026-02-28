package me.son.chatlabapi.chat.domain.repository;

import me.son.chatlabapi.chat.domain.entity.ChatMessage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("""
                SELECT m
                FROM ChatMessage m
                JOIN FETCH m.sender
                WHERE m.room.id = :roomId
                  AND (:cursor IS NULL OR m.id < :cursor)
                ORDER BY m.id DESC
            """)
    List<ChatMessage> findMessages(@Param("roomId") Long roomId, @Param("cursor") Long cursor, Pageable pageable);

    void deleteByRoom_Id(Long roomId);
}
