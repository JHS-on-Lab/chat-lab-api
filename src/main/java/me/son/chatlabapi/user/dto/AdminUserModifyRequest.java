package me.son.chatlabapi.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import me.son.chatlabapi.user.domain.entity.enums.Role;

/**
 * 관리자에 의한 사용자 정보 변경 요청. password 가 비어 있으면 비밀번호는 변경하지 않는다.
 */
public record AdminUserModifyRequest(
        @NotBlank(message = "아이디는 필수입니다.")
        @Size(max = 50, message = "아이디는 50자 이하여야 합니다.") String username,
        @Size(max = 72, message = "비밀번호는 72자 이하여야 합니다.") String password,
        @NotNull(message = "권한은 필수입니다.") Role role
) {
}
