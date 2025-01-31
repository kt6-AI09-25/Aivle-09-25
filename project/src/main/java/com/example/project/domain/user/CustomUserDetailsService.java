package com.example.project.domain.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 상태 복원 로직
        if (user.getState() == 0 && user.getBan_end_time() != null && LocalDateTime.now().isAfter(user.getBan_end_time())) {
            user.setState(1);
            user.setBan_end_time(null);
            userRepository.save(user);
        }

        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getState(),
                user.getBan_end_time(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole())),
                Map.of()
        );
    }
}