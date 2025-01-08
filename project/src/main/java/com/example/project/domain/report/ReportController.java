package com.example.project.domain.report;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/report")
public class ReportController {
    private final ReportService reportService;

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
        return "redirect:/admin/report/list";
    }
}
