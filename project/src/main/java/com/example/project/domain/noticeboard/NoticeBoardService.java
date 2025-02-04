package com.example.project.domain.noticeboard;

import com.example.project.domain.user.User;
import com.example.project.domain.user.UserRepository;
import com.example.project.domain.user.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class NoticeBoardService {

    private final NoticeBoardRepository noticeBoardRepository;
    private final UserRepository userRepository;

    public List<NoticeBoardDTO.Response> getAllPosts() {
        Sort sort = Sort.by(Sort.Direction.DESC, "dateWrite");
        return noticeBoardRepository.findAll(sort)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public NoticeBoardDTO.Response getPostById(Long id) {
        NoticeBoard post = noticeBoardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        increaseViewCount(post);
        return convertToResponseDTO(post);
    }

    public NoticeBoardDTO.Response getEditablePost(Long id) {
        NoticeBoard post = noticeBoardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        checkPermission(post.getWriter().getUsername());
        return convertToResponseDTO(post);
    }

    @Transactional
    public NoticeBoardDTO.Response createPost(NoticeBoardDTO.Request request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User writer = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("현재 사용자를 찾을 수 없습니다."));
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-09 15:29 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        checkBan();
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-09 15:29 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


        NoticeBoard post = new NoticeBoard();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setWriter(writer);

        NoticeBoard savedPost = noticeBoardRepository.save(post);
        return convertToResponseDTO(savedPost);
    }


    @Transactional
    public NoticeBoardDTO.Response updatePost(Long id, NoticeBoardDTO.Request request) {
        NoticeBoard post = noticeBoardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        checkPermission(post.getWriter().getUsername());
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-09 15:29 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        checkBan();
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-09 15:29 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setIsEdited(true);
        post.setDateWrite(LocalDateTime.now());

        NoticeBoard updatedPost = noticeBoardRepository.save(post);
        return convertToResponseDTO(updatedPost);
    }

    @Transactional
    public void deletePost(Long id) {
        NoticeBoard post = noticeBoardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        checkPermission(post.getWriter().getUsername());
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-09 15:29 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        checkBan();
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-09 15:29 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        noticeBoardRepository.delete(post);
    }

    private void increaseViewCount(NoticeBoard post) {
        post.setViewCount(post.getViewCount() + 1);
        noticeBoardRepository.save(post);
    }

    private void checkPermission(String writerUsername) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-09 11:25 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        String currentUserRole = UserUtils.getCurrentUserRole();
        if ((!Objects.equals(currentUsername, writerUsername))&&(!Objects.equals(currentUserRole, "[ADMIN]"))) {
            throw new RuntimeException("권한이 없습니다.");

        }
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-09 11:25 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    }


    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-09 15:29 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public void checkBan() {
        Integer state = UserUtils.getCurrentUserState();
        LocalDateTime banEndTime = UserUtils.getCurrentUserBanEndTime();
        if (state == 0) {
            if (banEndTime != null) {
                throw new RuntimeException(String.format("%tY-%<tm-%<td %<tH:%<tM 까지 작성, 수정이 금지된 사용자 입니다.", banEndTime));
            } else {
                throw new RuntimeException("작성, 수정이 금지된 사용자 입니다.");
            }
        }
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-09 15:29 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    private NoticeBoardDTO.Response convertToResponseDTO(NoticeBoard post) {
        return NoticeBoardDTO.Response.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .writerId(post.getWriter().getId())
                .writerName(post.getWriter().getUsername())
                .dateWrite(post.getDateWrite())
                .isEdited(post.getIsEdited())
                .build();
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-09 11:25 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public User getWriterByPostId(Long postId) {
        // postId로 NoticeBoard 엔티티 조회
        NoticeBoard post = noticeBoardRepository.findByPostId(postId);

        if (post == null) {
            throw new IllegalArgumentException("Post with ID " + postId + " not found");
        }

        // 작성자 반환
        return post.getWriter();
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-09 11:25 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-16 11:05 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public List<NoticeBoardDTO.Response> searchPostsByTitleOrContentKeyword(String keyword) {
        return noticeBoardRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<NoticeBoardDTO.Response> searchPostsByWriterKeyword(String keyword) {
        return noticeBoardRepository.findByWriter_UsernameContainingIgnoreCase(keyword)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-16 11:05 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-02-03 11:05 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    public long getTotalPosts() {
        return noticeBoardRepository.countAllPosts();
    }

    public Page<NoticeBoardDTO.Response> getPagedPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateWrite"));
        return noticeBoardRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-02-03 11:05 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}
