package com.example.project.domain.testpage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {


    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/admin")
    public String admin() {return "admin";}

    @GetMapping("/test")
    public String testPage() {
        return "test"; // templates/test.html을 렌더링
    }

    @GetMapping("/result")
    public String result() {
        return "result"; // result.html 반환
    }


    @GetMapping("/board")  // test.html로 가는 경로 추가
    public String board() {
        return "board"; // test.html 반환
    }

    @GetMapping("/myresult")
    public String myResult() {
        return "myresult"; // myresult.html 반환
    }
}


