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

@Controller
@RequestMapping("/files")
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

    @GetMapping("/upload/results")
    public String resultPage(@RequestParam("filePath") String filePath, Model model) {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

        // 동영상 URL 생성
        String videoUrl = "/files/upload/results/" + fileName;
        model.addAttribute("videoUrl", videoUrl);

        return "result";
    }

    @GetMapping("/upload/results/{fileName}")
    public ResponseEntity<Resource> getVideo(@PathVariable String fileName) {
        String filePath = storageDirectory + "/" + fileName; // 파일 경로 생성
        java.io.File videoFile = new java.io.File(filePath); // 파일 객체 생성

        // 파일이 존재하는지 확인
        if (!videoFile.exists()) {
            return ResponseEntity.notFound().build(); // 파일이 없으면 404 반환
        }

        Resource resource = new FileSystemResource(videoFile); // 파일 자원 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("video/webm")); // 미디어 타입 설정
        headers.setContentDispositionFormData("inline", fileName); // 브라우저에서 재생 가능하게 설정

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource); // 파일 반환
    }


}
