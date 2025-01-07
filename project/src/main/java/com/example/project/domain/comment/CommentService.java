package com.example.project.domain.comment;

import com.example.project.domain.noticeboard.NoticeBoard;
import com.example.project.domain.noticeboard.NoticeBoardRepository;
import com.example.project.domain.user.User;
import com.example.project.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final NoticeBoardRepository noticeBoardRepository;
    private final UserRepository userRepository;

    public List<CommentDTO.Response> getCommentsByPostId(Long postId) {
        return commentRepository.findByPost_PostId(postId)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDTO.Response addComment(CommentDTO.Request request) {
        // 현재 로그인한 사용자 가져오기
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User commenter = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("현재 사용자를 찾을 수 없습니다."));

        NoticeBoard post = noticeBoardRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setCommenter(commenter);
        comment.setComment(request.getComment());
        comment.setDateComment(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return convertToResponseDTO(savedComment);
    }

    @Transactional
    public CommentDTO.Response updateComment(Long commentId, CommentDTO.Request request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        checkPermission(comment.getCommenter().getUsername());

        comment.setComment(request.getComment());
        comment.setIsEdited(true);
        commentRepository.save(comment);
        return convertToResponseDTO(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        checkPermission(comment.getCommenter().getUsername());

        commentRepository.deleteById(commentId);
    }

    private void checkPermission(String commenterUsername) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!Objects.equals(currentUsername, commenterUsername)) {
            throw new RuntimeException("권한이 없습니다.");
        }
    }

    private CommentDTO.Response convertToResponseDTO(Comment comment) {
        return CommentDTO.Response.builder()
                .commentId(comment.getCommentId())
                .postId(comment.getPost().getPostId())
                .commenterId(comment.getCommenter().getId())
                .commenterName(comment.getCommenter().getUsername())
                .comment(comment.getComment())
                .dateComment(comment.getDateComment())
                .isEdited(comment.getIsEdited())
                .build();
    }
}
