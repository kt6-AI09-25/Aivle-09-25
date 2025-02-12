package com.example.project.domain.file;

import com.example.project.domain.score.Score;
import com.example.project.domain.score.ScoreService;
import com.example.project.domain.score2.Score2;
import com.example.project.domain.score2.Score2Service;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final ScoreService scoreService;
    private final Score2Service score2Service;

    @Value("${file.storage.directory}")
    private String presentationDirectory;

    @Value("${file.storage.directory2}")
    private String interviewDirectory;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://fastapi-ai9-25-e7g3g4czh0f3b3au.koreacentral-01.azurewebsites.net")
            .build();

    // 프레젠테이션 업로드 처리
    @Transactional
    public FileDTO.Response uploadFileForPresentation(MultipartFile file) {
        return processFileUpload(file, presentationDirectory, "/upload", true);
    }

    // 인터뷰 업로드 처리
    @Transactional
    public FileDTO.Response uploadFileForInterview(MultipartFile file) {
        return processFileUpload(file, interviewDirectory, "/interview", false);
    }

    private FileDTO.Response processFileUpload(MultipartFile file, String directory, String apiEndpoint, boolean isPresentation) {
        String username = getLoggedInUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String storedFilePath = saveFile(file, directory);

        com.example.project.domain.file.File fileEntity = new com.example.project.domain.file.File();
        fileEntity.setFilePath(storedFilePath);
        fileEntity.setUploadedAt(LocalDateTime.now());
        fileEntity.setUser(user);
        fileRepository.save(fileEntity);

        if (isPresentation) {
            Score inProgressScore = scoreService.createInProgressScore(user, fileEntity);
            sendFileToFastAPI(storedFilePath, inProgressScore.getScoreId(), apiEndpoint)
                    .subscribe(response -> scoreService.completeScoreData(inProgressScore.getScoreId(), response));
        } else {
            Score2 inProgressScore2 = score2Service.createInProgressScore2(user, fileEntity);
            sendFileToFastAPI(storedFilePath, inProgressScore2.getScore2Id(), apiEndpoint)
                    .subscribe(response -> score2Service.completeScore2Data(inProgressScore2.getScore2Id(), response));
        }

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

    private String saveFile(MultipartFile file, String directory) {
        createDirectoryIfNotExists(directory);

        String originalFilename = file.getOriginalFilename();
        originalFilename = (originalFilename != null) ? originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_") : "unnamed_file";
        if (originalFilename.length() > 255) {
            originalFilename = originalFilename.substring(0, 255);
        }

        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;
        String storedFilePath = directory + "/" + uniqueFilename;

        try {
            file.transferTo(new File(storedFilePath));
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        return storedFilePath;
    }

    private void createDirectoryIfNotExists(String directoryPath) {
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                System.out.println("디렉토리 생성 완료: " + directoryPath);
            } catch (IOException e) {
                throw new RuntimeException("디렉토리 생성 실패: " + directoryPath, e);
            }
        }
    }

    private Mono<Map<String, Object>> sendFileToFastAPI(String filePath, Long scoreId, String endpoint) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(filePath));
        body.add("scoreId", scoreId);

        return webClient.post()
                .uri(endpoint)
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
