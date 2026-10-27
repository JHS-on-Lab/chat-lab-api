package me.son.chatlabapi.auth.jwt.service;

import io.jsonwebtoken.Claims;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import me.son.chatlabapi.auth.jwt.dto.JwtDto;
import me.son.chatlabapi.auth.jwt.JwtProvider;
import me.son.chatlabapi.auth.jwt.dto.ParsedToken;
import me.son.chatlabapi.auth.jwt.exception.CustomJwtException;
import me.son.chatlabapi.auth.jwt.exception.JwtErrorCode;
import me.son.chatlabapi.global.security.CustomUserDetails;
import me.son.chatlabapi.user.domain.entity.enums.Role;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.expiration.access}")
    private long accessTokenExpirationMs;
    @Value("${jwt.expiration.refresh}")
    private long refreshTokenExpirationMs;

    private final JwtProvider jwtProvider;

    /**
     * 인증된 사용자 정보를 기반으로 JWT 토큰을 생성한다.
     *
     * @param id       토큰에 포함될 사용자 식별자
     * @param username 토큰 클레임에 포함될 사용자명
     * @param role     토큰 클레임에 포함될 사용자 권한 정보
     * @return 생성된 JWT 토큰 정보
     */
    public JwtDto createTokens(Long id, String username, Role role) {
        return new JwtDto(jwtProvider.createAccessToken(id, username, role, accessTokenExpirationMs), jwtProvider.createRefreshToken(id, refreshTokenExpirationMs));
    }

    /**
     * Refresh Token 쿠키의 만료 시간을 초 단위로 반환한다.
     *
     * @return Refresh Token 만료 시간(초)
     */
    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpirationMs / 1000;
    }

    /**
     * Refresh Token 을 검증하여 토큰의 Subject(User's ID) 를 반환한다.
     * Access Token 을 Refresh Token 으로 사용하는 것을 막기 위해 토큰 타입을 확인한다.
     *
     * @param refreshToken Refresh Token 문자열
     * @return 인증된 사용자 ID
     */
    public Long getSubjectFromRefreshToken(String refreshToken) {
        try {
            ParsedToken parsed = jwtProvider.parseToken(refreshToken);
            if (!isRefreshToken(parsed.claims())) {
                throw new CustomJwtException(JwtErrorCode.JWT_INVALID);
            }
            return Long.valueOf(parsed.subject());
        } catch (Exception e) {
            throw new CustomJwtException(JwtErrorCode.JWT_INVALID);
        }
    }

    /**
     * JWT 토큰을 검증하여 CustomUserDetails 를 반환한다
     *
     * @param token JWT 문자열
     * @return 인증된 사용자 정보
     */
    public CustomUserDetails getCustomUserDetails(String token) {
        try {
            // 파싱 및 검증
            ParsedToken parsed = jwtProvider.parseToken(token);
            Claims claims = parsed.claims();
            if (isRefreshToken(claims)) {
                throw new CustomJwtException(JwtErrorCode.JWT_INVALID);
            }
            Long id = Long.valueOf(parsed.subject());
            String username = claims.get("username", String.class);
            Role role = Role.valueOf(claims.get("role", String.class));

            return new CustomUserDetails(id, username, role);
        } catch (CustomJwtException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomJwtException(JwtErrorCode.JWT_INVALID);
        }
    }

    // type 클레임 도입 이전에 발급된 토큰은 role 클레임 유무로 구분한다 (refresh 토큰에는 role 이 없음)
    private boolean isRefreshToken(Claims claims) {
        String type = claims.get(JwtProvider.CLAIM_TYPE, String.class);
        if (type != null) {
            return JwtProvider.TYPE_REFRESH.equals(type);
        }
        return claims.get("role") == null;
    }
}