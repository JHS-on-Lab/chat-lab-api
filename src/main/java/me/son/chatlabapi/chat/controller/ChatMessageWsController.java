package me.son.chatlabapi.chat.controller;

import lombok.RequiredArgsConstructor;

import me.son.chatlabapi.chat.domain.service.ChatRoomMessageService;
import me.son.chatlabapi.chat.dto.MessageResponse;
import me.son.chatlabapi.chat.dto.SendMessageRequest;
import me.son.chatlabapi.global.response.ApiResponse;
import me.son.chatlabapi.global.security.CustomUserDetails;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatMessageWsController {
    private final ChatRoomMessageService chatRoomMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/rooms/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, SendMessageRequest request, Principal principal) {
        Authentication authentication = (Authentication) principal;
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        MessageResponse response = chatRoomMessageService.sendMessage(roomId, userDetails.getId(), request);

        messagingTemplate.convertAndSend("/topic/chat/rooms/" + roomId, ApiResponse.success(response));
    }
}