package com.example.project.domain.file;

import com.example.project.domain.user.User;
import com.example.project.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    private static final String STORAGE_DIRECTORY = "C:\\Users\\User\\Downloads\\"; // 파일 저장 경로
    private static final String FASTAPI_URL = "http://127.0.0.1:8000/upload"; // FastAPI 서버
    // 파일 폴더 생성
    static {
        File directory = new File(STORAGE_DIRECTORY);
        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            if (!isCreated) {
                throw new RuntimeException("파일 저장 경로를 생성할 수 없습니다: " + STORAGE_DIRECTORY);
            }
        }
    }

    public FileDTO.Response uploadFile(MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));


        // 파일 경로 유효성 검사
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            originalFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
            if (originalFilename.length() > 255) {
                originalFilename = originalFilename.substring(0, 255);
            }
        }

        // 파일 저장 경로 생성
        String uniqueFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String storedFilePath = STORAGE_DIRECTORY + uniqueFilename;

        // 파일 저장
        try {
            file.transferTo(new File(storedFilePath));
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        sendFileToFastAPI(storedFilePath);
        
        // File 엔티티 생성 및 저장
        com.example.project.domain.file.File fileEntity = new com.example.project.domain.file.File();
        fileEntity.setFilePath(storedFilePath);
        fileEntity.setUploadedAt(LocalDateTime.now());
        fileEntity.setUser(user);
        fileRepository.save(fileEntity);

        // DTO 반환
        return FileDTO.Response.builder()
                .fileId(fileEntity.getFileId())
                .filePath(fileEntity.getFilePath())
                .uploadedAt(fileEntity.getUploadedAt())
                .build();
    }
}
