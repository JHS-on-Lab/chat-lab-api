package me.son.chatlabapi.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import me.son.chatlabapi.global.paging.dto.PageResponse;
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
    public ApiResponse<PageResponse<UserSearchResponse>> getUsers(@ModelAttribute UserSearchRequest request) {
        log.info("getUsers - gets users");
        Page<UserSearchResponse> page = userService.getUsers(request);
        return ApiResponse.success(PageResponse.from(page));
    }

    @GetMapping("/{username}")
    public ApiResponse<UserSearchResponse> getUserByUsername(@PathVariable String username) {
        log.info("getUserByUsername - gets user {}.", username);
        UserSearchResponse user = userService.getUserByUsername(username);
        return ApiResponse.success(user);
    }

    @PostMapping
    public ApiResponse<UserSignUpResponse> addUser(@RequestBody UserSignUpRequest request) {
        log.info("addUser - admin user adds user {}.", request.username());
        UserSignUpResponse user = userService.addUser(request);
        return ApiResponse.success(user);
    }

    @PutMapping
    public ApiResponse<UserModifyResponse> modifyUser(@RequestBody UserModifyRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("modifyUser - {} modifies my info.", userDetails.getUsername());
        UserModifyResponse user = userService.modifyUser(userDetails.getId(), request);
        return ApiResponse.success(user);
    }

    @GetMapping("/me")
    public ApiResponse<UserSearchResponse> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("getMyInfo - {} gets my info.", userDetails.getUsername());
        UserSearchResponse user = userService.getUserById(userDetails.getId());
        return ApiResponse.success(user);
    }
}
