package me.son.chatlabapi.chat.domain.service;

import me.son.chatlabapi.chat.dto.MessageResponse;
import me.son.chatlabapi.chat.dto.MessageSliceResponse;
import me.son.chatlabapi.chat.dto.SendMessageRequest;

public interface ChatRoomMessageService {
    MessageSliceResponse getMessages(Long roomId, Long cursor, int size, Long userId);
    MessageResponse sendMessage(Long roomId, Long userId, SendMessageRequest request);
}
