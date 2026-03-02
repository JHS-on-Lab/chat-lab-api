package me.son.chatlabapi.auth.jwt.dto;

public record JwtDto(
        String accessToken,
        String refreshToken
) {
}
