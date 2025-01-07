package com.example.project.domain.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    /** 특정 게시글의 댓글 목록 조회 */
    @GetMapping
    public String getCommentsByPost(@PathVariable Long postId, Model model) {
        List<CommentDTO.Response> comments = commentService.getCommentsByPostId(postId);
        model.addAttribute("comments", comments);
        return "comments/list"; // 댓글 리스트
    }

    /** 댓글 작성 */
    @PostMapping
    public String addComment(@PathVariable Long postId, @ModelAttribute CommentDTO.Request request) {
        request.setPostId(postId);
        commentService.addComment(request);
        return "redirect:/posts/" + postId; // 게시글 상세보기로 리다이렉트
    }

    /** 댓글 수정 */
    @PostMapping("/{commentId}/edit")
    public String updateComment(@PathVariable Long postId, @PathVariable Long commentId,
                                @ModelAttribute CommentDTO.Request request) {
        request.setPostId(postId);
        commentService.updateComment(commentId, request);
        return "redirect:/posts/" + postId; // 게시글 상세보기로 리다이렉트
    }

    /** 댓글 삭제 */
    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return "redirect:/posts/" + postId; // 게시글 상세보기로 리다이렉트
    }
}
