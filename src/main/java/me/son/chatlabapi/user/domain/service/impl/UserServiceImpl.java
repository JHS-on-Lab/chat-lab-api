package me.son.chatlabapi.user.domain.service.impl;

import lombok.RequiredArgsConstructor;

import me.son.chatlabapi.global.exception.BusinessException;
import me.son.chatlabapi.user.domain.entity.User;
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
    public UserSearchResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        return UserSearchResponse.from(user);
    }

    @Override
    public Page<UserSearchResponse> getUsers(UserSearchRequest request) {
        Pageable pageable = PageRequest.of(
                request.page(),
                request.size()
        );

        return userRepository.findAll(pageable).map(UserSearchResponse::from);
    }

    @Override
    public UserSearchResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        return UserSearchResponse.from(user);
    }

    @Override
    public UserSignUpResponse addUser(UserSignUpRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }

        try {
            User user = userRepository.save(UserMapper.toEntity(request, passwordEncoder));
            return UserSignUpResponse.from(user);
        } catch (DataIntegrityViolationException e) {
            // username unique 제약 위반
            throw new BusinessException(UserErrorCode.DUPLICATE_USER);
        }
    }

    @Override
    public UserModifyResponse modifyUser(Long userId, UserModifyRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        user.updateUsername(request.username());
        user.updatePassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);

        return UserModifyResponse.from(user);
    }
}
