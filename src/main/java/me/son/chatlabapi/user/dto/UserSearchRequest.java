package me.son.chatlabapi.user.dto;

import me.son.chatlabapi.user.domain.entity.enums.Role;

public record UserSearchRequest(
        String username,
        Role role,
        Integer page,
        Integer size
) {
    public UserSearchRequest {
        if (page == null) page = 0;
        if (size == null) size = 10;
    }
}
