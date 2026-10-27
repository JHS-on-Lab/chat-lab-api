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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserSearchResponse getUserById(Long id) {
        return UserSearchResponse.from(findUser(id));
    }

    @Override
    public Page<UserSearchResponse> getUsers(UserSearchRequest request) {
        return userRepository.findAll(PageRequest.of(request.page(), request.size()))
                .map(UserSearchResponse::from);
    }

    @Override
    public UserSearchResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        return UserSearchResponse.from(user);
    }

    @Override
    @Transactional
    public UserSignUpResponse addUser(UserSignUpRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }

        try {
            User user = userRepository.saveAndFlush(UserMapper.toEntity(request, passwordEncoder));
            return UserSignUpResponse.from(user);
        } catch (DataIntegrityViolationException e) {
            // 동시 요청으로 인한 username unique 제약 위반
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }
    }

    @Override
    @Transactional
    public UserModifyResponse modifyUser(Long userId, UserModifyRequest request) {
        User user = findUser(userId);

        // 본인 확인: 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BusinessException(UserErrorCode.INVALID_PASSWORD);
        }

        if (userRepository.existsByUsernameAndIdNot(request.username(), userId)) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }

        user.updateUsername(request.username());
        user.updatePassword(passwordEncoder.encode(request.password()));

        try {
            // 커밋 시점이 아닌 여기서 제약 위반을 감지하도록 즉시 flush
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }

        return UserModifyResponse.from(user);
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }
}
