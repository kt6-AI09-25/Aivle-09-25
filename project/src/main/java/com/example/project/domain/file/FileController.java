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
    private String storageDirectory;

    @GetMapping("/upload")
    public String uploadPage() {
        return "test";
    }

    @PostMapping("/upload/results")
    @ResponseBody
    public FileDTO.Response uploadFile(@RequestPart MultipartFile file) {
        return fileService.uploadFile(file);
    }

    @GetMapping("/upload/results/{fileId}")
    public ResponseEntity<Resource> getVideo(@PathVariable Long fileId) {
        // 파일 정보 조회
        com.example.project.domain.file.File fileEntity = fileService.getFileById(fileId);
        if (fileEntity == null) {
            return ResponseEntity.notFound().build();
        }

        String filePath = fileEntity.getFilePath();
        File videoFile = new File(filePath);

        // 파일이 존재하는지 확인
        if (!videoFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(videoFile);

        // 동영상의 미디어 타입을 자동 감지
        String contentType;
        try {
            Path path = Paths.get(filePath);
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            contentType = "video/webm"; // 기본값 설정
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
            return "error"; // 파일이 없으면 에러 페이지로 이동
        }

        String videoUrl = "/myresults/upload/results/" + fileId;
        model.addAttribute("videoUrl", videoUrl);

        return "myresult";
    }
}
