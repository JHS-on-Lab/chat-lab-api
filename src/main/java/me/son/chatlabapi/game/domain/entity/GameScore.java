package me.son.chatlabapi.game.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.son.chatlabapi.user.domain.entity.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(
        name = "game_scores",
        indexes = {
                @Index(name = "idx_game_score", columnList = "game_name, score DESC"),
                @Index(name = "idx_user_game", columnList = "user_id, game_name")
        }
)
@Getter
@NoArgsConstructor
public class GameScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 게임 식별자
    @Column(name = "game_name", nullable = false, length = 50)
    private String gameName;

    // 유저 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int score;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public GameScore(String gameName, User user, int score) {
        this.gameName = gameName;
        this.user = user;
        this.score = score;
    }
}