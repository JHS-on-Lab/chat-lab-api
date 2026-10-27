package me.son.chatlabapi.user.domain.service.impl;

import lombok.RequiredArgsConstructor;

import me.son.chatlabapi.chat.domain.service.ChatRoomService;
import me.son.chatlabapi.filebox.domain.service.FolderService;
import me.son.chatlabapi.game.domain.service.GameScoreService;
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
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ChatRoomService chatRoomService;
    private final FolderService folderService;
    private final GameScoreService gameScoreService;

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

    @Override
    @Transactional
    public UserSearchResponse modifyUserByAdmin(Long adminId, Long targetId, AdminUserModifyRequest request) {
        validateNotSelf(adminId, targetId);
        User user = findUser(targetId);

        if (userRepository.existsByUsernameAndIdNot(request.username(), targetId)) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }

        user.updateUsername(request.username());
        user.updateRole(request.role());
        if (StringUtils.hasText(request.password())) {
            user.updatePassword(passwordEncoder.encode(request.password()));
        }

        try {
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }

        return UserSearchResponse.from(user);
    }

    @Override
    @Transactional
    public UserSearchResponse changeUserStatus(Long adminId, Long targetId, boolean enabled) {
        validateNotSelf(adminId, targetId);
        User user = findUser(targetId);
        user.updateEnabled(enabled);
        return UserSearchResponse.from(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long adminId, Long targetId) {
        validateNotSelf(adminId, targetId);
        findUser(targetId);

        // FK 참조하는 데이터를 먼저 정리한 뒤 사용자 삭제
        chatRoomService.deleteAllByUser(targetId);
        folderService.deleteAllByUser(targetId);
        gameScoreService.deleteAllByUser(targetId);
        userRepository.deleteById(targetId);
    }

    // 관리자가 실수로 본인을 비활성화 / 삭제 / 강등하여 관리자 계정이 사라지는 것을 방지
    private void validateNotSelf(Long adminId, Long targetId) {
        if (adminId.equals(targetId)) {
            throw new BusinessException(UserErrorCode.CANNOT_MANAGE_SELF);
        }
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }
}
