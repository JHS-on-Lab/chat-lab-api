package me.son.chatlabapi.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.son.chatlabapi.global.paging.dto.PageRequestDto;
import me.son.chatlabapi.user.domain.entity.enums.Role;

@Setter // @ModelAttribute 사용 시 필수
@Getter
@ToString
public class UserSearchRequestDto extends PageRequestDto {
    private String username;
    private Role role;
}
