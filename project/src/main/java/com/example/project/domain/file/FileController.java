package com.example.project.domain.file;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/upload")
    public String uploadPage() {
        return "test"; // upload.html 렌더링
    }


    @PostMapping("/upload")
    @ResponseBody
    public FileDTO.Response uploadFile(@RequestPart MultipartFile file) {
        return fileService.uploadFile(file);
    }

    @GetMapping("/upload/results")
    public String resultPage() {
        return "result"; // upload.html 렌더링
    }
}
