package me.son.chatlabapi.game.domain.service;

import me.son.chatlabapi.game.dto.ScoreResponse;

import java.util.List;

public interface GameScoreService {
    void saveScore(String gameName, Long userId, int score);

    List<ScoreResponse> getTop10(String gameName);

    void deleteAllByUser(Long userId);
}
