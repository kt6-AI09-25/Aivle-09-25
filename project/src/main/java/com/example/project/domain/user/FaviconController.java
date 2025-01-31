package com.example.project.domain.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FaviconController {
    @GetMapping("favicon.ico")
    @ResponseBody
    public void returnNoFavicon() {
        // 빈 응답을 반환하여 401 방지
    }
}