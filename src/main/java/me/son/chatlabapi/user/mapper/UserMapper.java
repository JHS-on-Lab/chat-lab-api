package me.son.chatlabapi.user.mapper;

import me.son.chatlabapi.user.domain.entity.User;
import me.son.chatlabapi.user.dto.UserSignUpRequest;

import org.springframework.security.crypto.password.PasswordEncoder;

public class UserMapper {

    /**
     * 사용자 등록 요청 DTO 를 User Entity 로 변환한다.
     *
     * @param record          사용자 등록에 필요한 정보
     * @param passwordEncoder 비밀번호 해시 처리를 위한 PasswordEncoder
     * @return User Entity
     */
    public static User toEntity(UserSignUpRequest record, PasswordEncoder passwordEncoder) {
        return User.builder()
                .username(record.username())
                .password(passwordEncoder.encode(record.password()))
                .role(record.role())
                .build();
    }
}
