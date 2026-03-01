package me.son.chatlabapi.global.bootstrap;

import lombok.RequiredArgsConstructor;

import me.son.chatlabapi.game.domain.entity.Game;
import me.son.chatlabapi.game.domain.repository.GameRepository;
import me.son.chatlabapi.global.config.AdminProperties;
import me.son.chatlabapi.user.domain.entity.User;
import me.son.chatlabapi.user.domain.entity.enums.Role;
import me.son.chatlabapi.user.domain.repository.UserRepository;
import me.son.chatlabapi.user.dto.UserSignUpRequestDto;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static me.son.chatlabapi.user.mapper.UserMapper.toEntity;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;
    private final GameRepository gameRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String username = adminProperties.getUsername();
        String password = adminProperties.getPassword();

        if (userRepository.existsByUsername(username)) {
            return;
        }

        User user = toEntity(UserSignUpRequestDto.builder()
                .username(username)
                .password(password)
                .role(Role.ROLE_ADMIN)
                .build(), passwordEncoder);
        userRepository.save(user);

        Game game = new Game();
        game.setName("dodge");
        game.setActive(true);
        gameRepository.save(game);
    }
}
