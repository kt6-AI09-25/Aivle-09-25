package com.example.project.domain.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception{
        //관리자 계정 확인
        if (userRepository.findByUsername("main_admin").isEmpty()){
            User admin = new User();
            admin.setUsername("main_admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setLogin_type(0);
            admin.setNickname("main admin");
            admin.setRole("ADMIN");
            admin.setLast_login(LocalDateTime.now());
            admin.setDate_join(LocalDateTime.now());
            admin.setState(1);
            admin.setLetter_state(0);
            userRepository.save(admin);
            System.out.println("Admin account created: username=main_admin, password=admin123");
        }
    }
}