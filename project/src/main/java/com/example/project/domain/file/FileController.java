package com.example.project.domain.file;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

@Controller
@RequestMapping("/myresults")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Value("${file.storage.directory}")
    private String presentationDirectory;

    @Value("${file.storage.directory2}")
    private String interviewDirectory;

    @GetMapping("/upload")
    public String uploadPage() {
        return "test";
    }

    @PostMapping("/upload/presentation")
    @ResponseBody
    public FileDTO.Response uploadPresentationFile(@RequestPart MultipartFile file) {
        return fileService.uploadFileForPresentation(file);
    }

    @PostMapping("/upload/interview")
    @ResponseBody
    public FileDTO.Response uploadInterviewFile(@RequestPart MultipartFile file) {
        return fileService.uploadFileForInterview(file);
    }

    @GetMapping("/upload/results/{fileId}")
    public ResponseEntity<Resource> getVideo(@PathVariable Long fileId) {
        com.example.project.domain.file.File fileEntity = fileService.getFileById(fileId);
        if (fileEntity == null) {
            return ResponseEntity.notFound().build();
        }

        String filePath = fileEntity.getFilePath();
        File videoFile = new File(filePath);

        if (!videoFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(videoFile);

        String contentType;
        try {
            Path path = Paths.get(filePath);
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            contentType = "video/webm";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType != null ? contentType : "video/webm"));
        headers.setContentDispositionFormData("inline", videoFile.getName());

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @GetMapping("/upload/results")
    public String resultPage(@RequestParam("fileId") Long fileId, Model model) {
        com.example.project.domain.file.File fileEntity = fileService.getFileById(fileId);
        if (fileEntity == null) {
            return "error";
        }

        String videoUrl = "/myresults/upload/results/" + fileId;
        model.addAttribute("videoUrl", videoUrl);

        return "myresult";
    }
}
