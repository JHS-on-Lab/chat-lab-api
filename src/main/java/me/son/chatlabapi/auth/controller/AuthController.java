package me.son.chatlabapi.auth.controller;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import me.son.chatlabapi.auth.domain.service.AuthService;
import me.son.chatlabapi.auth.dto.AuthRequestDto;
import me.son.chatlabapi.auth.dto.JwtDto;
import me.son.chatlabapi.global.response.ApiResponse;
import me.son.chatlabapi.global.security.CustomUserDetails;

import org.springframework.web.bind.annotation.*;

import static me.son.chatlabapi.global.util.CookieUtil.addHttpOnlyCookie;

@Log4j2
@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-in")
    public ApiResponse<String> signIn(@RequestBody AuthRequestDto request, HttpServletResponse response) {
        log.info("signIn username: {}", request.getUsername());
        CustomUserDetails user = authService.authenticate(request.getUsername(), request.getPassword());

        JwtDto tokens = authService.createTokensByUser(user.getId(), user.getUsername(), user.getRole());
        // Refresh Token 은 HTTP Only Cookie 저장
        addHttpOnlyCookie(response, "refreshToken", tokens.getRefreshToken());

        return ApiResponse.success(tokens.getAccessToken());
    }

    @PostMapping("/reissue")
    public ApiResponse<String> reissue(@CookieValue(value = "refreshToken") String refreshToken, HttpServletResponse response) {
        log.info("reissue refreshToken: {}", refreshToken);
        // refresh token 유효성 검사
        CustomUserDetails user = authService.validateToken(refreshToken);

        JwtDto tokens = authService.createTokensByUser(user.getId(), user.getUsername(), user.getRole());
        // Refresh Token 은 HTTP Only Cookie 저장
        addHttpOnlyCookie(response, "refreshToken", tokens.getRefreshToken());

        return ApiResponse.success(tokens.getAccessToken());
    }

}
