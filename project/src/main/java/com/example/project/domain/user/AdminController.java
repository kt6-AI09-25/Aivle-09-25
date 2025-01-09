package com.example.project.domain.user;

import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AdminController {

    private final UserRepository userRepository;
    private final SessionRegistry sessionRegistry;

    public AdminController(UserRepository userRepository, SessionRegistry sessionRegistry) {
        this.userRepository = userRepository;
        this.sessionRegistry = sessionRegistry;
    }
    @GetMapping("/active-users")
    public List<UserDto> getActiveUsers() {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> principal instanceof CustomUserDetails) // CustomUserDetails로 변경
                .map(principal -> {
                    CustomUserDetails user = (CustomUserDetails) principal;
                    return new UserDto(user.getUsername(), user.getAuthorities().toString());
                })
                .collect(Collectors.toList());
    }
}
