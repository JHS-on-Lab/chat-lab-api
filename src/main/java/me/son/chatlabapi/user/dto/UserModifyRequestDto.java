package me.son.chatlabapi.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserModifyRequestDto {
    private String username;
    private String password;
}
