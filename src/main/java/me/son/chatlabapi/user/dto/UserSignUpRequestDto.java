package me.son.chatlabapi.user.dto;

import lombok.*;
import me.son.chatlabapi.user.domain.entity.enums.Role;

@Setter // @ModelAttribute 사용 시 필수
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSignUpRequestDto {
    private String username;
    private String password;
    private Role role;
}
