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

    @PostMapping("/{roomId}/members")
    public ApiResponse<Void> inviteMember(@PathVariable Long roomId, @RequestBody InviteMemberRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("leaveRoom - user {} invites user {} to the room {}", userDetails.getId(), request.username(), roomId);
        chatRoomService.inviteMember(userDetails.getId(), roomId, request.username());
        return ApiResponse.success(null);
    }

    @GetMapping("/{roomId}/members")
    public ApiResponse<List<RoomMemberResponse>> getRoomMembers(@PathVariable Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("getRoomMembers - user {} gets members in the room {}", userDetails.getId(), roomId);
        List<RoomMemberResponse> response = chatRoomService.getRoomMembers(userDetails.getId(), roomId);
        return ApiResponse.success(response);
    }


    @GetMapping("/{roomId}/messages")
    public ApiResponse<MessageSliceResponse> getMessages(@PathVariable Long roomId, @RequestParam(required = false) Long cursor, @RequestParam(defaultValue = "20") int size, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("getMessages - user {} gets messages in the room {}", userDetails.getId(), roomId);
        MessageSliceResponse response = chatRoomService.getMessages(roomId, cursor, size, userDetails.getId());
        return ApiResponse.success(response);
    }

    @PostMapping("/{roomId}/messages")
    public ApiResponse<MessageResponse> sendMessage(@PathVariable Long roomId, @RequestBody SendMessageRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("sendMessage - user {} sends the message in the room {}", userDetails.getId(), roomId);
        MessageResponse response = chatRoomService.sendMessage(roomId, userDetails.getId(), request);

        return ApiResponse.success(response);
    }
}
