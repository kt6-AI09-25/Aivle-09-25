package com.example.project.domain.report;


import com.example.project.domain.comment.CommentDTO;
import com.example.project.domain.comment.CommentService;
import com.example.project.domain.letter.LetterDTO;
import com.example.project.domain.letter.LetterService;
import com.example.project.domain.noticeboard.NoticeBoardDTO;
import com.example.project.domain.noticeboard.NoticeBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/report")
public class ReportController {
    private final ReportService reportService;
    private final NoticeBoardService noticeBoardService;
    private final CommentService commentService;
    private final LetterService letterService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("reports", reportService.getAllReports());
        return "admin/report/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("report", new ReportDto.Request());
        return "admin/report/createform";
    }

    @PostMapping
    public String createReport(@ModelAttribute ReportDto.Request request) {
        reportService.createReport(request);
        return "redirect:/admin/report";
    }

    @PostMapping("/{id}/update")
    public String updateReport(@PathVariable Long id,
                               @RequestParam ReportProcessTypes reportProcessType) {
        reportService.updateReportProcessTypeAndState(id, reportProcessType);
        return "redirect:/admin/report";
    }

    @GetMapping("/test/{id}")
    public String reportTest(@PathVariable Long id, Model model) {
        NoticeBoardDTO.Response post = noticeBoardService.getPostById(id);
        model.addAttribute("post", post);

        List<CommentDTO.Response> comments = commentService.getCommentsByPostId(id);
        model.addAttribute("comments", comments);

        return "admin/report/test";
    }

    @GetMapping("/{report_type}/{reported_id}")
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
}
