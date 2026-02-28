package me.son.chatlabapi.chat.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.son.chatlabapi.user.domain.entity.User;

@Entity
@Table(name = "chat_room_members", uniqueConstraints = { @UniqueConstraint(columnNames = {"user_id", "room_id"}) })
@Getter
@NoArgsConstructor
public class ChatRoomMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // N:1 → User
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // N:1 → ChatRoom
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id")
    private ChatRoom room;

    public ChatRoomMember(User user, ChatRoom room) {
        this.user = user;
        this.room = room;
    }
}