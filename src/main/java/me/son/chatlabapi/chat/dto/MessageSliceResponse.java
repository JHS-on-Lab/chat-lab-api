package me.son.chatlabapi.chat.dto;

import java.util.List;

public record MessageSliceResponse(
        List<MessageResponse> messages,
        boolean hasNext,
        Long nextCursor
) {}
