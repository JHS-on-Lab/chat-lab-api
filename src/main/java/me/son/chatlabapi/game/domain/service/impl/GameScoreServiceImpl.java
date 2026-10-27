package me.son.chatlabapi.game.domain.service.impl;

import lombok.RequiredArgsConstructor;

import me.son.chatlabapi.game.domain.entity.Game;
import me.son.chatlabapi.game.domain.entity.GameScore;
import me.son.chatlabapi.game.domain.repository.GameRepository;
import me.son.chatlabapi.game.domain.repository.GameScoreRepository;
import me.son.chatlabapi.game.domain.service.GameScoreService;
import me.son.chatlabapi.game.dto.ScoreResponse;
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
        validateActiveGame(gameName);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        gameScoreRepository.save(new GameScore(gameName, user, score));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScoreResponse> getTop10(String gameName) {
        validateActiveGame(gameName);

        // user 가 LAZY 이므로 트랜잭션 안에서 DTO 로 변환
        return gameScoreRepository.findTop10UserBestScore(gameName).stream()
                .map(ScoreResponse::from)
                .toList();
    }

    @Override
    public void deleteAllByUser(Long userId) {
        gameScoreRepository.deleteAllByUserId(userId);
    }

    private void validateActiveGame(String gameName) {
        Game game = gameRepository.findById(gameName)
                .orElseThrow(() -> new BusinessException(GameErrorCode.GAME_NOT_FOUND));

        if (!game.isActive()) {
            throw new BusinessException(GameErrorCode.GAME_INACTIVE);
        }
    }
}
