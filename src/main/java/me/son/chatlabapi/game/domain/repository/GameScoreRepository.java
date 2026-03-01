package me.son.chatlabapi.game.domain.repository;

import me.son.chatlabapi.game.domain.entity.GameScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameScoreRepository extends JpaRepository<GameScore, Long> {
    @Query(value = """
                SELECT gs.*
                FROM game_scores gs
                JOIN (
                    SELECT user_id, MAX(score) AS max_score
                    FROM game_scores
                    WHERE game_name = :gameName
                    GROUP BY user_id
                ) t
                ON gs.user_id = t.user_id
                AND gs.score = t.max_score
                WHERE gs.game_name = :gameName
                ORDER BY gs.score DESC
                LIMIT 10
            """, nativeQuery = true)
    List<GameScore> findTop10UserBestScore(@Param("gameName") String gameName);
}
