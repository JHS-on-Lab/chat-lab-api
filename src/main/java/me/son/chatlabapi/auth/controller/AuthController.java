package me.son.chatlabapi.auth.controller;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import me.son.chatlabapi.auth.domain.service.AuthService;
import me.son.chatlabapi.auth.dto.AuthRequest;
import me.son.chatlabapi.auth.exception.AuthErrorCode;
import me.son.chatlabapi.auth.jwt.dto.JwtDto;
import me.son.chatlabapi.auth.jwt.service.JwtService;
import me.son.chatlabapi.global.config.CookieProperties;
import me.son.chatlabapi.global.exception.BusinessException;
import me.son.chatlabapi.global.response.ApiResponse;
import me.son.chatlabapi.global.security.CustomUserDetails;
import me.son.chatlabapi.user.domain.service.UserService;
import me.son.chatlabapi.user.dto.UserSearchResponse;

import org.springframework.web.bind.annotation.*;

import static me.son.chatlabapi.global.util.CookieUtil.addHttpOnlyCookie;
import static me.son.chatlabapi.global.util.CookieUtil.deleteCookie;

@Log4j2
@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;
    private final CookieProperties cookieProperties;

    @PostMapping("/sign-in")
    public ApiResponse<String> signIn(@RequestBody AuthRequest request, HttpServletResponse response) {
        log.info("signIn - user {} signs in.", request.username());
        CustomUserDetails user = authService.authenticate(request.username(), request.password());

        JwtDto tokens = jwtService.createTokens(user.getId(), user.getUsername(), user.getRole());
        // Refresh Token 은 HTTP Only Cookie 저장
        addHttpOnlyCookie(response, REFRESH_TOKEN_COOKIE, tokens.refreshToken(), jwtService.getRefreshTokenExpirationSeconds(), cookieProperties.secure());

        return ApiResponse.success(tokens.accessToken());
    }

    @PostMapping("/reissue")
    public ApiResponse<String> reissue(@CookieValue(value = REFRESH_TOKEN_COOKIE) String refreshToken, HttpServletResponse response) {
        Long id = jwtService.getSubjectFromRefreshToken(refreshToken);
        log.info("reissue - user {} reissues tokens.", id);
        // "DB 접속 최소화"보다 "토큰 재발급의 신뢰성"이 더 중요하다 판단되어 User 정보 재조회
        UserSearchResponse user = userService.getUserById(id);
        if (!user.enabled()) {
            throw new BusinessException(AuthErrorCode.ACCOUNT_DISABLED);
        }

        JwtDto tokens = jwtService.createTokens(user.id(), user.username(), user.role());
        // Refresh Token 은 HTTP Only Cookie 저장
        addHttpOnlyCookie(response, REFRESH_TOKEN_COOKIE, tokens.refreshToken(), jwtService.getRefreshTokenExpirationSeconds(), cookieProperties.secure());

        return ApiResponse.success(tokens.accessToken());
    }

    @PostMapping("/sign-out")
    public ApiResponse<Void> signOut(HttpServletResponse response) {
        // Refresh Token 쿠키 제거
        deleteCookie(response, REFRESH_TOKEN_COOKIE, cookieProperties.secure());
        return ApiResponse.success(null);
    }

}
