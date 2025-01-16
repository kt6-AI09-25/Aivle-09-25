package com.example.project.domain.noticeboard;

import com.example.project.domain.comment.CommentDTO;
import com.example.project.domain.comment.CommentService;
import com.example.project.domain.noticeboard.NoticeBoardDTO.*;
import com.example.project.domain.report.ReportDto;
import com.example.project.domain.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/noticeboard")
public class NoticeBoardController {

    private final NoticeBoardService noticeBoardService;
    private final CommentService commentService;
    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-10 14:41 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    private final ReportService reportService;
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-10 14:41 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    // 게시글 목록
    @GetMapping
    public String list(Model model) {
        model.addAttribute("posts", noticeBoardService.getAllPosts());
        return "noticeboard/list";
    }

    // 게시글 작성 폼
    @GetMapping("/new")
    public String createForm(Model model) {
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-09 15:55 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        noticeBoardService.checkBan();
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-09 15:55 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
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
        // 게시글 데이터
        NoticeBoardDTO.Response post = noticeBoardService.getPostById(id);
        model.addAttribute("post", post);

        // 댓글 데이터
        List<CommentDTO.Response> comments = commentService.getCommentsByPostId(id);
        model.addAttribute("comments", comments);

        return "noticeboard/detail";
    }

    // 게시글 수정
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-09 15:55 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        noticeBoardService.checkBan();
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-09 15:55 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
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

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-10 14:37 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    // 게시글 신고
    @PostMapping("/{id}/report")
    public String reportPost(@PathVariable Long id, @ModelAttribute ReportDto.Request request) {

        request.setReport_type(1);
        request.setReported_id(id);
        reportService.createReport(request);
        return "redirect:/noticeboard/" + id;
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-10 14:37 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    // 댓글 작성
    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id, @ModelAttribute CommentDTO.Request request) {
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-09 15:55 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        noticeBoardService.checkBan();
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-09 15:55 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        request.setPostId(id);
        commentService.addComment(request);
        return "redirect:/noticeboard/" + id;
    }

    // 댓글 수정
    @PostMapping("/{id}/comments/{commentId}/edit")
    public String updateComment(@PathVariable Long id, @PathVariable Long commentId, @ModelAttribute CommentDTO.Request request) {
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-09 15:55 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        noticeBoardService.checkBan();
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-09 15:55 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        request.setPostId(id);
        commentService.updateComment(commentId, request);
        return "redirect:/noticeboard/" + id;
    }

    // 댓글 삭제
    @PostMapping("/{id}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long id, @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return "redirect:/noticeboard/" + id;
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-10 14:37 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    // 댓글 신고
    @PostMapping("/{id}/comments/{commentId}/report")
    public String reportComment(@PathVariable Long id, @PathVariable Long commentId, @ModelAttribute ReportDto.Request request) {

        request.setReport_type(2);
        request.setReported_id(commentId);
        reportService.createReport(request);
        return "redirect:/noticeboard/" + id;
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-10 14:37 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>



    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-16 11:05 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    @GetMapping("/search/title")
    public List<NoticeBoardDTO.Response> searchPostsTitleOrContent(@RequestParam String keyword) {
        return noticeBoardService.searchPostsByTitleOrContentKeyword(keyword);
    }

    @GetMapping("/search/writer")
    public List<NoticeBoardDTO.Response> searchPostsWriter(@RequestParam String keyword) {
        return noticeBoardService.searchPostsByWriterKeyword(keyword);
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-16 11:05 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}
