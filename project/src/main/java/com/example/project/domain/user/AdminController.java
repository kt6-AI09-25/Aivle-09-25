package com.example.project.domain.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(user.getUsername(), user.getRole()))
                .collect(Collectors.toList());
    }
    @GetMapping("/current-user")
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            org.springframework.security.core.userdetails.User principal =
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            return new UserDto(principal.getUsername(), principal.getAuthorities().toString());
        }
        throw new RuntimeException("No authenticated user found");
    }

    @GetMapping("/active-users")
    public List<UserDto> getActiveUsers() {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> principal instanceof org.springframework.security.core.userdetails.User)
                .map(principal -> {
                    org.springframework.security.core.userdetails.User user =
                            (org.springframework.security.core.userdetails.User) principal;
                    return new UserDto(user.getUsername(), user.getAuthorities().toString());
                })
                .collect(Collectors.toList());
    }
}
