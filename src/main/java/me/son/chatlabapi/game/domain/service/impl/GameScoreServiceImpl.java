package me.son.chatlabapi.game.domain.service.impl;

import lombok.RequiredArgsConstructor;

import me.son.chatlabapi.game.domain.entity.Game;
import me.son.chatlabapi.game.domain.entity.GameScore;
import me.son.chatlabapi.game.domain.repository.GameRepository;
import me.son.chatlabapi.game.domain.repository.GameScoreRepository;
import me.son.chatlabapi.game.domain.service.GameScoreService;
import me.son.chatlabapi.game.exception.GameErrorCode;
import me.son.chatlabapi.global.exception.BusinessException;
import me.son.chatlabapi.user.domain.entity.User;
import me.son.chatlabapi.user.domain.repository.UserRepository;
import me.son.chatlabapi.user.exception.UserErrorCode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GameScoreServiceImpl implements GameScoreService {
    private final GameScoreRepository gameScoreRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    @Override
    public void saveScore(String gameName, Long userId, int score) {
        // 1. 게임 존재 및 활성 여부 검증
        Game game = gameRepository.findById(gameName)
                .orElseThrow(() -> new BusinessException(GameErrorCode.GAME_NOT_FOUND));

        if (!game.isActive()) {
            throw new BusinessException(GameErrorCode.GAME_INACTIVE);
        }

        // 2. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // 3. 점수 저장
        GameScore gameScore = new GameScore(gameName, user, score);

        gameScoreRepository.save(gameScore);
    }

    @Override
    public List<GameScore> getTop10(String gameName) {
        Game game = gameRepository.findById(gameName)
                .orElseThrow(() -> new BusinessException(GameErrorCode.GAME_NOT_FOUND));

        if (!game.isActive()) {
            throw new BusinessException(GameErrorCode.GAME_INACTIVE);
        }

        return gameScoreRepository.findTop10UserBestScore(gameName);
    }
}
