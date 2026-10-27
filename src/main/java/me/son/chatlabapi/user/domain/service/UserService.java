package me.son.chatlabapi.user.domain.service;

import me.son.chatlabapi.user.dto.*;

import org.springframework.data.domain.Page;

public interface UserService {

    /**
     * 사용자 ID로 사용자 정보 단건 조회합니다.
     *
     * @param id 사용자 목록 조회 조건 및 페이징 정보
     * @return 페이징 정보가 포함된 사용자 목록
     */
    UserSearchResponse getUserById(Long id);

    /**
     * 사용자 목록을 페이징하여 조회한다.
     *
     * @param request 사용자 목록 조회 조건 및 페이징 정보
     * @return 페이징 정보가 포함된 사용자 목록
     */
    Page<UserSearchResponse> getUsers(UserSearchRequest request);

    /**
     * 사용자명으로 사용자 정보 단건 조회합니다.
     *
     * @param username 사용자명
     * @return 조회된 사용자 정보
     */
    UserSearchResponse getUserByUsername(String username);

    /**
     * 새로운 사용자를 등록한다.
     *
     * @param request 사용자 등록에 필요한 정보
     * @return 등록된 사용자 정보
     */
    UserSignUpResponse addUser(UserSignUpRequest request);

    /**
     * 기존 사용자 정보를 변경한다.
     *
     * @param request 변경하려는 사용자 정보
     * @return 변경된 사용자 정보
     */
    UserModifyResponse modifyUser(Long userId, UserModifyRequest request);

    /**
     * 관리자가 다른 사용자의 아이디 / 비밀번호 / 권한을 변경한다.
     *
     * @param adminId  요청한 관리자 ID (본인 계정은 변경 불가)
     * @param targetId 변경할 사용자 ID
     * @param request  변경 정보 (password 가 비어 있으면 유지)
     * @return 변경된 사용자 정보
     */
    UserSearchResponse modifyUserByAdmin(Long adminId, Long targetId, AdminUserModifyRequest request);

    /**
     * 관리자가 사용자를 활성화 / 비활성화한다.
     *
     * @param adminId  요청한 관리자 ID (본인 계정은 변경 불가)
     * @param targetId 대상 사용자 ID
     * @param enabled  활성화 여부
     * @return 변경된 사용자 정보
     */
    UserSearchResponse changeUserStatus(Long adminId, Long targetId, boolean enabled);

    /**
     * 관리자가 사용자를 삭제한다. 사용자의 메시지 / 채팅방 참여 / 파일 / 게임 기록을 모두 함께 삭제한다.
     *
     * @param adminId  요청한 관리자 ID (본인 계정은 삭제 불가)
     * @param targetId 삭제할 사용자 ID
     */
    void deleteUser(Long adminId, Long targetId);
}
