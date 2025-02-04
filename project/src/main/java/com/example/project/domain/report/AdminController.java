package com.example.project.domain.report;


import com.example.project.domain.comment.CommentDTO;
import com.example.project.domain.comment.CommentService;
import com.example.project.domain.letter.LetterDTO;
import com.example.project.domain.letter.LetterService;
import com.example.project.domain.noticeboard.NoticeBoardDTO;
import com.example.project.domain.noticeboard.NoticeBoardService;
import com.example.project.domain.user.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final ReportService reportService;
    private final NoticeBoardService noticeBoardService;
    private final CommentService commentService;
    private final LetterService letterService;
    private  final CustomUserDetailsService customUserDetailsService;

    @GetMapping("/report")
    public String list(Model model) {
        model.addAttribute("reports", reportService.getAllReports());
        return "admin/report/list";
    }

    @GetMapping("/report/new")
    public String createForm(Model model) {
        model.addAttribute("report", new ReportDto.Request());
        return "admin/report/createform";
    }

    @PostMapping("/report")
    public String createReport(@ModelAttribute ReportDto.Request request) {
        reportService.createReport(request);
        return "redirect:/admin/report";
    }

    @PostMapping("/report/{id}/update")
    public String updateReport(@PathVariable Long id,
                               @RequestParam ReportProcessTypes reportProcessType) {
        reportService.updateReportProcessTypeAndState(id, reportProcessType);
        return "redirect:/admin/report";
    }

    @GetMapping("/report/test/{id}")
    public String reportTest(@PathVariable Long id, Model model) {
        NoticeBoardDTO.Response post = noticeBoardService.getPostById(id);
        model.addAttribute("post", post);

        List<CommentDTO.Response> comments = commentService.getCommentsByPostId(id);
        model.addAttribute("comments", comments);

        return "admin/report/test";
    }

    @GetMapping("/report/{report_type}/{reported_id}")
    public String commentDetail(@PathVariable Integer report_type, @PathVariable Long reported_id, Model model) {
        if (report_type == 1) {
            NoticeBoardDTO.Response post = noticeBoardService.getPostById(reported_id);
            model.addAttribute("post", post);

            List<CommentDTO.Response> comments = commentService.getCommentsByPostId(reported_id);
            model.addAttribute("comments", comments);

            return "admin/report/post";
        } else if (report_type == 2){
            Long postId = commentService.getPostIdByCommentId(reported_id);

            NoticeBoardDTO.Response post = noticeBoardService.getPostById(postId);
            model.addAttribute("post", post);

            List<CommentDTO.Response> comments = commentService.getCommentsByPostId(postId);
            model.addAttribute("comments", comments);

            model.addAttribute("selectedCommentId", reported_id);

            return "admin/report/comment";
        } else if (report_type == 3) {
            LetterDTO.Response letter = letterService.getLetterById(reported_id);

            model.addAttribute("letter", letter);

            return "admin/report/letter";
        } else {
            return "admin/report/list";
        }
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>어드민 신고관리 02-04 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    @GetMapping("/report/check/{report_type}/{reported_id}")
    @ResponseBody // (또는 return 타입을 ResponseEntity로)
    public ResponseEntity<Void> checkReportedItem(@PathVariable Integer report_type,
                                                  @PathVariable Long reported_id) {
        if (report_type == 1) {
            // 게시글 존재 여부
            NoticeBoardDTO.Response post = noticeBoardService.getPostById(reported_id);
            if (post == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().build();

        } else if (report_type == 2) {
            // 댓글 존재 여부
            // commentService에 getCommentById(...)가 있어야 합니다.
            CommentDTO.Response comment = commentService.getCommentById(reported_id);
            if (comment == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().build();

        } else if (report_type == 3) {
            // 쪽지 존재 여부
            LetterDTO.Response letter = letterService.getLetterById(reported_id);
            if (letter == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().build();

        } else {
            // 그 외 타입은 없다고 가정
            return ResponseEntity.notFound().build();
        }
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>어드민 신고관리 02-04 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>



    @GetMapping("")
    public String adminMainPage() {
        return "admin/report/main";
    }

    @GetMapping("/members")
    public String membersPage(){
        return "admin/report/members";
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getUserStats() {
        long totalUsers = customUserDetailsService.getTotalUsers();
        long todayJoinedUsers = customUserDetailsService.getTodayJoinedUsers();
        long totalPosts = noticeBoardService.getTotalPosts();
        long unprocessedReports = reportService.getUnprocessedReports();

        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("todayJoinedUsers", todayJoinedUsers);
        stats.put("totalPosts", totalPosts);
        stats.put("unprocessedReports", unprocessedReports);

        return ResponseEntity.ok(stats);
    }
}