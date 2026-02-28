package me.son.chatlabapi.chat.controller;

import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;

import me.son.chatlabapi.chat.domain.service.ChatRoomService;
import me.son.chatlabapi.chat.dto.*;
import me.son.chatlabapi.global.response.ApiResponse;
import me.son.chatlabapi.global.security.CustomUserDetails;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping
    public ApiResponse<CreateRoomResponse> createRoom(@RequestBody CreateRoomRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("createRoom - user {} creates new room named {}", userDetails.getId(), request.name());
        CreateRoomResponse response = chatRoomService.createRoom(userDetails.getId(), request.name());
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<List<MyRoomResponse>> getMyRooms(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("getMyRooms - user {} gets his rooms", userDetails.getId());
        List<MyRoomResponse> response = chatRoomService.getMyRooms(userDetails.getId());
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{roomId}")
    public ApiResponse<Void> leaveRoom(@PathVariable Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("leaveRoom - user {} leaves the room {}", userDetails.getId(), roomId);
        chatRoomService.leaveRoom(userDetails.getId(), roomId);
        return ApiResponse.success(null);
    }


}
