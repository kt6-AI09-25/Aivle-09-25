package com.example.project.domain.comment;

import com.example.project.domain.noticeboard.NoticeBoard;
import com.example.project.domain.noticeboard.NoticeBoardDTO;
import com.example.project.domain.noticeboard.NoticeBoardRepository;
import com.example.project.domain.user.User;
import com.example.project.domain.user.UserRepository;
import com.example.project.domain.user.UserUtils;
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
    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-09 11:25 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public Long getPostIdByCommentId(Long commentId) {
        // commentId로 Comment 엔티티 조회
        Comment comment = commentRepository.findByCommentId(commentId);

        if (comment == null) {
            throw new IllegalArgumentException("Comment with ID " + commentId + " not found");
        }

        // 관련된 postId 반환
        NoticeBoard post = comment.getPost();
        return post.getPostId();
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-09 11:25 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    @Transactional
    public CommentDTO.Response addComment(CommentDTO.Request request) {
        // 현재 로그인한 사용자 가져오기
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User commenter = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("현재 사용자를 찾을 수 없습니다."));
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-09 11:25 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        checkBan();
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-09 11:25 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

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
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-09 15:29 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        checkBan();
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>22025-01-09 15:29 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

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
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-09 15:29 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        checkBan();
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-09 15:29 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


        commentRepository.deleteById(commentId);
    }

    private void checkPermission(String commenterUsername) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-09 11:25 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        String currentUserRole = UserUtils.getCurrentUserRole();
        if ((!Objects.equals(currentUsername, commenterUsername))&&(!Objects.equals(currentUserRole, "[ADMIN]"))) {
            throw new RuntimeException("권한이 없습니다.");
        }
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-09 11:25 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-09 15:29 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    private void checkBan() {
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

    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-09 11:25 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public User getWriterByCommentId(Long commentId) {
        Comment comment = commentRepository.findByCommentId(commentId);

        if (comment == null) {
            throw new IllegalArgumentException("Comment with ID " + commentId + " not found");
        }

        return comment.getCommenter();
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-09 11:25 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>신고게시판 관리 02 - 04>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public CommentDTO.Response getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        return convertToResponseDTO(comment);
    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>신고게시판 관리 02 - 04>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}


