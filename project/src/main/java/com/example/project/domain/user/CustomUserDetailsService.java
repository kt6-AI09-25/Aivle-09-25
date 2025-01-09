package com.example.project.domain.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){this.userRepository = userRepository;}

    @Override
    public UserDetails loadUserByUsername(String username)  throws UsernameNotFoundException{
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("User not found"));

        if (user.getState() == 0 && LocalDateTime.now().isAfter(user.getBan_end_time())) {
            user.setState(1);
            user.setBan_end_time(null);
            userRepository.save(user);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}

