package me.son.chatlabapi.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import me.son.chatlabapi.chat.domain.service.ChatRoomMessageService;
import me.son.chatlabapi.chat.dto.MessageResponse;
import me.son.chatlabapi.chat.dto.SendMessageRequest;
import me.son.chatlabapi.global.exception.BusinessException;
import me.son.chatlabapi.global.exception.GlobalErrorCode;
import me.son.chatlabapi.global.response.ApiResponse;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Log4j2
@Controller
@RequiredArgsConstructor
public class ChatMessageWsController {
    private final ChatRoomMessageService chatRoomMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/rooms/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, SendMessageRequest request, Principal principal) {
        MessageResponse response = chatRoomMessageService.sendMessage(roomId, Long.valueOf(principal.getName()), request);
        messagingTemplate.convertAndSend("/topic/chat/rooms/" + roomId, ApiResponse.success(response));
    }

    // 전송 실패 시 보낸 사람에게만 에러를 알린다 (응답이 없으면 메시지가 조용히 유실됨)
    @MessageExceptionHandler(BusinessException.class)
    @SendToUser(destinations = "/queue/errors", broadcast = false)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        return ApiResponse.failure(e.getErrorCode());
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser(destinations = "/queue/errors", broadcast = false)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("Unhandled websocket exception", e);
        return ApiResponse.failure(GlobalErrorCode.INTERNAL_SERVER_ERROR);
    }
}
