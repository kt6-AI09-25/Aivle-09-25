package com.example.project.domain.noticeboard;

import com.example.project.domain.noticeboard.NoticeBoardDTO.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping("/noticeboard")
public class NoticeBoardController {

    private final NoticeBoardService noticeBoardService;

    // 게시글 목록
    @GetMapping
    public String list(Model model) {
        model.addAttribute("posts", noticeBoardService.getAllPosts());
        return "noticeboard/list";
    }

    // 게시글 작성 폼
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("post", new NoticeBoardDTO.Request());
        return "noticeboard/createform";
    }

    // 게시글 등록
    @PostMapping
    public String createPost(@ModelAttribute NoticeBoardDTO.Request request) {
        noticeBoardService.createPost(request);
        return "redirect:/noticeboard";
    }

    // 게시글 상세 보기
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        NoticeBoardDTO.Response post = noticeBoardService.getPostById(id);
        model.addAttribute("post", post);
        return "noticeboard/detail";
    }

    // 게시글 수정
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        NoticeBoardDTO.Response post = noticeBoardService.getEditablePost(id);
        model.addAttribute("post", post);
        return "noticeboard/edit";
    }

    @PostMapping("/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute NoticeBoardDTO.Request request) {
        noticeBoardService.updatePost(id, request);
        return "redirect:/noticeboard/" + id;
    }

    // 게시글 삭제
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        noticeBoardService.deletePost(id);
        return "redirect:/noticeboard";
    }
}
