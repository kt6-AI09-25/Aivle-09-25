package com.example.project.domain.user;

import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AdminController {

    private final SessionRegistry sessionRegistry;

    public AdminController(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @GetMapping("/active-users")
    public List<String> getActiveUsers() {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> principal instanceof org.springframework.security.core.userdetails.User)
                .map(principal -> ((org.springframework.security.core.userdetails.User) principal).getUsername())
                .collect(Collectors.toList());
    }
}
