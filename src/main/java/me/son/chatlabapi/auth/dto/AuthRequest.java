package me.son.chatlabapi.auth.dto;

public record AuthRequest(
        String username,
        String password
) {
}
