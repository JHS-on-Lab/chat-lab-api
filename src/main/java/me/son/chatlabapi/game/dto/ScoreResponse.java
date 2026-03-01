package me.son.chatlabapi.game.dto;

import me.son.chatlabapi.game.domain.entity.GameScore;

public record ScoreResponse(
        String username,
        int score
) {
    public static ScoreResponse from(GameScore entity) {
        return new ScoreResponse(
                entity.getUser().getUsername(),
                entity.getScore()
        );
    }
}