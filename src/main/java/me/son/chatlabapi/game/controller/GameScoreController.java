package me.son.chatlabapi.game.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import me.son.chatlabapi.game.domain.service.GameScoreService;
import me.son.chatlabapi.game.dto.ScoreRequest;
import me.son.chatlabapi.game.dto.ScoreResponse;
import me.son.chatlabapi.global.response.ApiResponse;
import me.son.chatlabapi.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameScoreController {

    private final GameScoreService gameScoreService;

    @PostMapping("/{gameName}/scores")
    public ApiResponse<Void> saveScore(@PathVariable String gameName, @AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ScoreRequest request) {
        log.info("saveScore - {} saves a score of {} for the game {}.", userDetails.getUsername(), request.score(), gameName);
        gameScoreService.saveScore(gameName, userDetails.getId(), request.score());
        return ApiResponse.success(null);
    }

    @GetMapping("/{gameName}/scores/top10")
    public ApiResponse<List<ScoreResponse>> getTop10(@PathVariable String gameName) {
        log.info("getTop10 - fetch the top 10 score rankings for the {} game.", gameName);
        List<ScoreResponse> result = gameScoreService.getTop10(gameName)
                .stream()
                .map(ScoreResponse::from)
                .toList();
        return ApiResponse.success(result);
    }
}