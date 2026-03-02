package me.son.chatlabapi.auth.controller;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import me.son.chatlabapi.auth.domain.service.AuthService;
import me.son.chatlabapi.auth.dto.AuthRequest;
import me.son.chatlabapi.auth.jwt.dto.JwtDto;
import me.son.chatlabapi.auth.jwt.service.JwtService;
import me.son.chatlabapi.global.response.ApiResponse;
import me.son.chatlabapi.global.security.CustomUserDetails;
import me.son.chatlabapi.user.domain.service.UserService;
import me.son.chatlabapi.user.dto.UserSearchResponse;

import org.springframework.web.bind.annotation.*;

import static me.son.chatlabapi.global.util.CookieUtil.addHttpOnlyCookie;

@Log4j2
@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/sign-in")
    public ApiResponse<String> signIn(@RequestBody AuthRequest request, HttpServletResponse response) {
        log.info("signIn - user {} signs in.", request.username());
        CustomUserDetails user = authService.authenticate(request.username(), request.password());

        JwtDto tokens = jwtService.createTokens(user.getId(), user.getUsername(), user.getRole());
        // Refresh Token 은 HTTP Only Cookie 저장
        addHttpOnlyCookie(response, "refreshToken", tokens.refreshToken());

        return ApiResponse.success(tokens.accessToken());
    }

    @PostMapping("/reissue")
    public ApiResponse<String> reissue(@CookieValue(value = "refreshToken") String refreshToken, HttpServletResponse response) {
        log.info("reissue - user reissues tokens. refresh token: {}", refreshToken);
        Long id = jwtService.getSubject(refreshToken);
        // "DB 접속 최소화"보다 "토큰 재발급의 신뢰성"이 더 중요하다 판단되어 User 정보 재조회
        UserSearchResponse user = userService.getUserById(id);

        JwtDto tokens = jwtService.createTokens(user.id(), user.username(), user.role());
        // Refresh Token 은 HTTP Only Cookie 저장
        addHttpOnlyCookie(response, "refreshToken", tokens.refreshToken());

        return ApiResponse.success(tokens.accessToken());
    }

}
