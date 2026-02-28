package me.son.chatlabapi.user.domain.service.impl;

import lombok.RequiredArgsConstructor;

import me.son.chatlabapi.global.exception.BusinessException;
import me.son.chatlabapi.user.domain.entity.User;
import me.son.chatlabapi.user.domain.entity.enums.Role;
import me.son.chatlabapi.user.domain.repository.UserRepository;
import me.son.chatlabapi.user.domain.service.UserService;
import me.son.chatlabapi.user.dto.*;
import me.son.chatlabapi.user.exception.UserErrorCode;
import me.son.chatlabapi.user.mapper.UserMapper;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserSearchResponseDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        return UserSearchResponseDto.from(user);
    }

    @Override
    public Page<UserSearchResponseDto> getUsers(UserSearchRequestDto request) {
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize()
        );

        return userRepository.findAll(pageable)
                .map(UserSearchResponseDto::from);
    }

    @Override
    public UserSearchResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        return UserSearchResponseDto.from(user);
    }

    @Override
    public UserSignUpResponseDto addUser(UserSignUpRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }

        request.setRole(Role.ROLE_USER);

        try {
            User user = userRepository.save(UserMapper.toEntity(request, passwordEncoder));
            return UserSignUpResponseDto.from(user);
        } catch (DataIntegrityViolationException e) {
            // username unique 제약 위반
            throw new BusinessException(UserErrorCode.DUPLICATE_USER);
        }
    }

    @Override
    public UserModifyResponseDto modifyUser(Long userId, UserModifyRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.updateUsername(request.getUsername());
        user.updatePassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return UserModifyResponseDto.from(user);
    }
}
