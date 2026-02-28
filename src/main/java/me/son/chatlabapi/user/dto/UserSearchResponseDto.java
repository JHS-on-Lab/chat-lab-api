package me.son.chatlabapi.user.dto;

import lombok.Builder;
import lombok.Getter;
import me.son.chatlabapi.user.domain.entity.User;
import me.son.chatlabapi.user.domain.entity.enums.Role;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserSearchResponseDto {
    private Long id;
    private String username;
    private Role role;
    private LocalDateTime createdAt;

    public static UserSearchResponseDto from(User user) {
        return UserSearchResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build()
                ;
    }
}
