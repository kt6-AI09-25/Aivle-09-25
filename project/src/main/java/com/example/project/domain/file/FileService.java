package com.example.project.domain.file;

import com.example.project.domain.user.User;
import com.example.project.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://127.0.0.1:8000")
            .build();

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
        // 로그인된 사용자 정보 가져오기
        String username = getLoggedInUsername();

        // 사용자 확인
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 파일 저장
        String storedFilePath = saveFile(file);

        // FastAPI 서버로 비동기 파일 전송
        sendFileToFastAPI(storedFilePath).subscribe();

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

    private String getLoggedInUsername() {
        return org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    private String saveFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            originalFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
            if (originalFilename.length() > 255) {
                originalFilename = originalFilename.substring(0, 255);
            }
        }

        // 파일 저장 경로 생성
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;
        String storedFilePath = STORAGE_DIRECTORY + uniqueFilename;

        try {
            file.transferTo(new File(storedFilePath));
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        return storedFilePath;
    }

    private Mono<String> sendFileToFastAPI(String filePath) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(filePath)); // 저장된 파일 첨부

        return webClient.post()
                .uri("/upload") // FastAPI의 업로드 엔드포인트
                .contentType(MediaType.MULTIPART_FORM_DATA) // 멀티파트 데이터 전송 설정
                .bodyValue(body) // 요청 바디에 파일 추가
                .retrieve() // 요청 실행
                .bodyToMono(String.class) // 응답 본문을 String으로 변환
                .doOnSuccess(response -> System.out.println("FastAPI 응답: " + response))
                .doOnError(error -> System.err.println("FastAPI 서버로 파일 전송 실패: " + error.getMessage()));
    }
}
