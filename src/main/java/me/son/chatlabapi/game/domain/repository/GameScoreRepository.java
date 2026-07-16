package me.son.chatlabapi.game.domain.repository;

import me.son.chatlabapi.game.domain.entity.GameScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameScoreRepository extends JpaRepository<GameScore, Long> {
    @Query(value = """
                SELECT ranked.*
                FROM (
                    SELECT gs.*,
                           ROW_NUMBER() OVER (PARTITION BY gs.user_id ORDER BY gs.score DESC, gs.id ASC) AS rn
                    FROM game_scores gs
                    WHERE gs.game_name = :gameName
                ) ranked
                WHERE ranked.rn = 1
                ORDER BY ranked.score DESC
                LIMIT 10
            """, nativeQuery = true)
    List<GameScore> findTop10UserBestScore(@Param("gameName") String gameName);
}
