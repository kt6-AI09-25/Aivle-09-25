package com.example.project.domain.user;

import jakarta.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        user.setLast_login(LocalDateTime.now());
        userRepository.save(user);

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

    public long getTotalUsers() {
        return userRepository.countAllUsers();
    }

    public long getTodayJoinedUsers() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay(); // 오늘 00:00:00
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59); // 오늘 23:59:59

        return userRepository.countUsersByDateJoinBetween(startOfDay, endOfDay);
    }

    @Transactional
    public void updateUserRole(User user, UpdateUserRole userRole) {

        if (userRole == UpdateUserRole.관리자로_변경) {
            user.setRole("ADMIN");
        } else if (userRole == UpdateUserRole.일반유저로_변경) {
            user.setRole("USER");
        } else {
            throw new IllegalArgumentException("Unknown UpdateUserRoleType");
        }
        userRepository.save(user);
    }
}