package me.son.chatlabapi.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.son.chatlabapi.global.paging.dto.PageResponseDto;
import me.son.chatlabapi.global.response.ApiResponse;
import me.son.chatlabapi.user.domain.service.UserService;
import me.son.chatlabapi.user.dto.UserSearchRequestDto;
import me.son.chatlabapi.user.dto.UserSearchResponseDto;
import me.son.chatlabapi.user.dto.UserSignUpRequestDto;
import me.son.chatlabapi.user.dto.UserSignUpResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ApiResponse<PageResponseDto<UserSearchResponseDto>> getUsers(@ModelAttribute UserSearchRequestDto request) {
        log.info("getUsers request: {}", request);
        Page<UserSearchResponseDto> page = userService.getUsers(request);
        return ApiResponse.success(PageResponseDto.from(page));
    }

    @GetMapping("/{username}")
    public ApiResponse<UserSearchResponseDto> getUserByUsername(@PathVariable String username) {
        log.info("getUserByUsername request username: {}", username);
        UserSearchResponseDto user = userService.getUserByUsername(username);
        return ApiResponse.success(user);
    }

    @PostMapping
    public ApiResponse<UserSignUpResponseDto> addUser(@RequestBody UserSignUpRequestDto request) {
        log.info("addUser request: {}", request);
        UserSignUpResponseDto user = userService.addUser(request);
        return ApiResponse.success(user);
    }
}
