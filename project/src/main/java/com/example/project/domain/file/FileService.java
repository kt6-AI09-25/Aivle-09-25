package com.example.project.domain.file;

import com.example.project.domain.score.Score;
import com.example.project.domain.score.ScoreService;
import com.example.project.domain.user.User;
import com.example.project.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final ScoreService scoreService;

    // 파일 저장 경로를 application.yml 등에서 주입
    @Value("${file.storage.directory}")
    private String storageDirectory;

    // FastAPI 서버용 WebClient
    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://127.0.0.1:8000")
            .build();


    @Transactional
    public FileDTO.Response uploadFile(MultipartFile file) {
        // 1) 로그인된 사용자 정보 가져오기
        String username = getLoggedInUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2) 파일 물리적 저장
        String storedFilePath = saveFile(file);

        // 3) DB에 File 엔티티 생성
        com.example.project.domain.file.File fileEntity = new com.example.project.domain.file.File();
        fileEntity.setFilePath(storedFilePath);
        fileEntity.setUploadedAt(LocalDateTime.now());
        fileEntity.setUser(user);
        fileRepository.save(fileEntity);

        // 4) ScoreService를 통해 "IN_PROGRESS" 상태 Score 미리 생성
        Score inProgressScore = scoreService.createInProgressScore(user, fileEntity);

        // 5) FastAPI 서버로 비동기 파일 전송 (scoreId도 함께 보내고 싶으면 sendFileToFastAPI(storedFilePath, inProgressScore.getScoreId()) 형태로 구현)
        sendFileToFastAPI(storedFilePath, inProgressScore.getScoreId())
                .subscribe(response -> {
                    // 6) FastAPI 응답이 오면, 해당 scoreId로 DB조회하여 "COMPLETED" 및 점수데이터 저장
                    scoreService.completeScoreData(inProgressScore.getScoreId(), response);
                });

        // 업로드 후 프론트에 돌려줄 응답(파일 정보)
        return FileDTO.Response.builder()
                .fileId(fileEntity.getFileId())
                .filePath(fileEntity.getFilePath())
                .uploadedAt(fileEntity.getUploadedAt())
                .build();
    }


    private String getLoggedInUsername() {
        return org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
    }


    private String saveFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            // 파일명 특수문자 제거 및 길이 제한
            originalFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
            if (originalFilename.length() > 255) {
                originalFilename = originalFilename.substring(0, 255);
            }
        } else {
            // 파일명이 없는 경우 대비
            originalFilename = "unnamed_file";
        }

        // 고유한 파일명 생성
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;
        String storedFilePath = storageDirectory + uniqueFilename;

        try {
            file.transferTo(new File(storedFilePath));
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        return storedFilePath;
    }


    private Mono<Map<String, Object>> sendFileToFastAPI(String filePath, Long scoreId) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(filePath));
        body.add("scoreId", scoreId);

        return webClient.post()
                .uri("/upload") // FastAPI의 업로드 엔드포인트
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnSuccess(response -> System.out.println("FastAPI 응답: " + response))
                .doOnError(error -> System.err.println("FastAPI 서버로 파일 전송 실패: " + error.getMessage()));
    }


    public com.example.project.domain.file.File getFileById(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));
    }
}
