package me.son.chatlabapi.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.son.chatlabapi.chat.domain.service.ChatRoomMessageService;
import me.son.chatlabapi.chat.domain.service.ChatRoomService;
import me.son.chatlabapi.chat.dto.MessageResponse;
import me.son.chatlabapi.chat.dto.MessageSliceResponse;
import me.son.chatlabapi.chat.dto.SendMessageRequest;
import me.son.chatlabapi.global.response.ApiResponse;
import me.son.chatlabapi.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatRoomMessageService chatRoomMessageService;

    @GetMapping("/{roomId}/messages")
    public ApiResponse<MessageSliceResponse> getMessages(@PathVariable Long roomId, @RequestParam(required = false) Long cursor, @RequestParam(defaultValue = "20") int size, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("getMessages - user {} gets messages in the room {}", userDetails.getId(), roomId);
        MessageSliceResponse response = chatRoomMessageService.getMessages(roomId, cursor, size, userDetails.getId());
        return ApiResponse.success(response);
    }

    @PostMapping("/{roomId}/messages")
    public ApiResponse<MessageResponse> sendMessage(@PathVariable Long roomId, @RequestBody SendMessageRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("sendMessage - user {} sends the message in the room {}", userDetails.getId(), roomId);
        MessageResponse response = chatRoomMessageService.sendMessage(roomId, userDetails.getId(), request);

        return ApiResponse.success(response);
    }
}
