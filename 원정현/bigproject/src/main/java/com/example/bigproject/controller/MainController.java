package com.example.bigproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/test")  // test.html로 가는 경로 추가
    public String test() {
        return "test"; // test.html 반환
    }

    @GetMapping("/result")
    public String result() {
        return "result"; // result.html 반환
    }

    @GetMapping("/board")  // test.html로 가는 경로 추가
    public String board() {
        return "board"; // test.html 반환
    }
}
