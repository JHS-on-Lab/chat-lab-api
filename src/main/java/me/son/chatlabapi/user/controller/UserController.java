package me.son.chatlabapi.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import me.son.chatlabapi.global.paging.dto.PageResponseDto;
import me.son.chatlabapi.global.response.ApiResponse;
import me.son.chatlabapi.global.security.CustomUserDetails;
import me.son.chatlabapi.user.domain.service.UserService;
import me.son.chatlabapi.user.dto.*;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ApiResponse<PageResponseDto<UserSearchResponseDto>> getUsers(@ModelAttribute UserSearchRequestDto request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("getUsers - user {} gets users", userDetails.getUsername());
        Page<UserSearchResponseDto> page = userService.getUsers(request);
        return ApiResponse.success(PageResponseDto.from(page));
    }

    @GetMapping("/{username}")
    public ApiResponse<UserSearchResponseDto> getUserByUsername(@PathVariable String username, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("getUserByUsername - user {} gets user {}", userDetails.getUsername(), username);
        UserSearchResponseDto user = userService.getUserByUsername(username);
        return ApiResponse.success(user);
    }

    @PostMapping
    public ApiResponse<UserSignUpResponseDto> addUser(@RequestBody UserSignUpRequestDto request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("addUser - admin user {} adds user {}", userDetails.getUsername(), request.getUsername());
        UserSignUpResponseDto user = userService.addUser(request);
        return ApiResponse.success(user);
    }

    @PutMapping
    public ApiResponse<UserModifyResponseDto> modifyUser(@RequestBody UserModifyRequestDto request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("modifyUser - {} modifies user info.", userDetails.getUsername());
        UserModifyResponseDto user = userService.modifyUser(userDetails.getId(), request);
        return ApiResponse.success(user);
    }

    @GetMapping("/me")
    public ApiResponse<UserSearchResponseDto> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("getMyInfo - {} gets user info.", userDetails.getUsername());
        UserSearchResponseDto user = userService.getUserById(userDetails.getId());
        return ApiResponse.success(user);
    }
}
