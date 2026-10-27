package me.son.chatlabapi.user.domain.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import me.son.chatlabapi.user.domain.entity.enums.Role;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "users")
@Getter
@Builder
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // 비활성화된 계정은 로그인 / 토큰 재발급 / WebSocket 연결이 차단된다.
    // ddl-auto=update 로 컬럼이 추가될 때 기존 사용자가 비활성화되지 않도록 DB 기본값을 true 로 둔다.
    @Builder.Default
    @ColumnDefault("true")
    @Column(nullable = false)
    private boolean enabled = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected User() {
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateRole(Role role) {
        this.role = role;
    }

    public void updateEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
