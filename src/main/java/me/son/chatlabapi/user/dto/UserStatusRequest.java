package me.son.chatlabapi.user.dto;

import jakarta.validation.constraints.NotNull;

public record UserStatusRequest(
        @NotNull(message = "활성화 여부는 필수입니다.") Boolean enabled
) {
}
