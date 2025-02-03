package com.example.project.domain.testpage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

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

    @GetMapping("/choice")
    public String choice() {
        return "choice"; // choice.html 반환
    }

    @GetMapping("/pr_test")
    public String pr_test() {
        return "pr_test"; // pr_test.html 반환 (발표연습 페이지)
    }

}
