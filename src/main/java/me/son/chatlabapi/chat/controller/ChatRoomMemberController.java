package me.son.chatlabapi.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.son.chatlabapi.chat.domain.service.ChatRoomMemberService;
import me.son.chatlabapi.chat.domain.service.ChatRoomService;
import me.son.chatlabapi.chat.dto.InviteMemberRequest;
import me.son.chatlabapi.chat.dto.RoomMemberResponse;
import me.son.chatlabapi.global.response.ApiResponse;
import me.son.chatlabapi.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomMemberController {
    private final ChatRoomMemberService chatRoomMemberService;

    @GetMapping("/{roomId}/members")
    public ApiResponse<List<RoomMemberResponse>> getRoomMembers(@PathVariable Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("getRoomMembers - user {} gets members in the room {}", userDetails.getId(), roomId);
        List<RoomMemberResponse> response = chatRoomMemberService.getRoomMembers(userDetails.getId(), roomId);
        return ApiResponse.success(response);
    }

    @PostMapping("/{roomId}/members")
    public ApiResponse<Void> inviteMember(@PathVariable Long roomId, @RequestBody InviteMemberRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("leaveRoom - user {} invites user {} to the room {}", userDetails.getId(), request.username(), roomId);
        chatRoomMemberService.inviteMember(userDetails.getId(), roomId, request.username());
        return ApiResponse.success(null);
    }
}
