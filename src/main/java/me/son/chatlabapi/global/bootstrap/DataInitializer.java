package me.son.chatlabapi.global.bootstrap;

import lombok.RequiredArgsConstructor;

import me.son.chatlabapi.game.domain.entity.Game;
import me.son.chatlabapi.game.domain.repository.GameRepository;
import me.son.chatlabapi.global.config.AdminProperties;
import me.son.chatlabapi.user.domain.entity.User;
import me.son.chatlabapi.user.domain.entity.enums.Role;
import me.son.chatlabapi.user.domain.repository.UserRepository;
import me.son.chatlabapi.user.dto.UserSignUpRequest;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
        String username = adminProperties.username();
        String password = adminProperties.password();

        if (!userRepository.existsByUsername(username)) {
            User user = toEntity(new UserSignUpRequest(username, password, Role.ROLE_ADMIN), passwordEncoder);
            userRepository.save(user);
        }

        List<String> gameList = new ArrayList<>();
        gameList.add("dodge");
        gameList.add("grid-rush");

        for (String game : gameList) {
            if (!gameRepository.existsById(game)) {
                Game g = new Game();
                g.setName(game);
                g.setActive(true);
                gameRepository.save(g);
            }
        }
    }
}
