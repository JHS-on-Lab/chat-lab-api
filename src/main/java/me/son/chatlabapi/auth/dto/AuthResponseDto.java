package me.son.chatlabapi.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthResponseDto {
    private String accessToken;
    private String username;
}
