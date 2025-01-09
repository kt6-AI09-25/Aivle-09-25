package com.example.project.domain.noticeboard;

import com.example.project.domain.comment.Comment;
import com.example.project.domain.noticeboard.NoticeBoardDTO;
import com.example.project.domain.user.User;
import com.example.project.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeBoardService {

    private final NoticeBoardRepository noticeBoardRepository;
    private final UserRepository userRepository;

    public List<NoticeBoardDTO.Response> getAllPosts() {
        return noticeBoardRepository.findAll()
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
        noticeBoardRepository.delete(post);
    }

    private void increaseViewCount(NoticeBoard post) {
        post.setViewCount(post.getViewCount() + 1);
        noticeBoardRepository.save(post);
    }

    private void checkPermission(String writerUsername) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserRole = auth.getAuthorities().iterator().next().getAuthority();
        if ((!Objects.equals(currentUsername, writerUsername))&&(!Objects.equals(currentUserRole, "ROLE_ADMIN"))) {
            throw new RuntimeException("권한이 없습니다.");
        }
    }

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

    //=============================2025-01-09 11:25 박청하=====================================
    public User getWriterByPostId(Long postId) {
        // commentId로 Comment 엔티티 조회
        NoticeBoard post = noticeBoardRepository.findByPostId(postId);

        if (post == null) {
            throw new IllegalArgumentException("Post with ID " + postId + " not found");
        }

        // 관련된 postId 반환
        return post.getWriter();
    }
    //=============================2025-01-09 11:25 박청하=====================================
}
