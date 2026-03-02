package me.son.chatlabapi.auth.jwt.dto;

import io.jsonwebtoken.Claims;

public record ParsedToken(
        String subject,
        Claims claims
) {
}
