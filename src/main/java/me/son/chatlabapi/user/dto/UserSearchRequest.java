package me.son.chatlabapi.user.dto;

import me.son.chatlabapi.user.domain.entity.enums.Role;

public record UserSearchRequest(
        String username,
        Role role,
        Integer page,
        Integer size
) {
    private static final int MAX_SIZE = 100;

    public UserSearchRequest {
        if (page == null || page < 0) page = 0;
        if (size == null || size < 1) size = 10;
        if (size > MAX_SIZE) size = MAX_SIZE;
    }
}
