package me.son.chatlabapi.game.domain.service;

import me.son.chatlabapi.game.domain.entity.GameScore;

import java.util.List;

public interface GameScoreService {
    void saveScore(String gameName, Long userId, int score);

    List<GameScore> getTop10(String gameName);
}
