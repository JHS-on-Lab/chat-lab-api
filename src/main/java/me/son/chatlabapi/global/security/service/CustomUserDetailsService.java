package me.son.chatlabapi.global.security.service;

import lombok.RequiredArgsConstructor;
import me.son.chatlabapi.global.security.CustomUserDetails;
import me.son.chatlabapi.user.domain.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(u -> CustomUserDetails.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .password(u.getPassword())
                        .role(u.getRole())
                        .build()
                ).orElseThrow(() -> new UsernameNotFoundException("User not found. " + username))
                ;
    }
}
