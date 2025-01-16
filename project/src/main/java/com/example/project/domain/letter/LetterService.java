package com.example.project.domain.letter;

import com.example.project.domain.noticeboard.NoticeBoard;
import com.example.project.domain.user.User;
import com.example.project.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LetterService {

    private final LetterRepository letterRepository;
    private final UserRepository userRepository;

    public List<LetterDTO.Response> getReceivedLetters(Long receiverId) {
        return letterRepository.findByReceiver_Id(receiverId, Sort.by(Sort.Direction.DESC, "dateSend"))
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<LetterDTO.Response> getSentLetters(Long senderId) {
        return letterRepository.findBySender_Id(senderId, Sort.by(Sort.Direction.DESC, "dateSend"))
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LetterDTO.Response sendLetter(LetterDTO.Request request, Long senderId) {
        // username을 기반으로 수신자 조회
        User receiver = userRepository.findByUsername(request.getReceiverUsername())
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다: " + request.getReceiverUsername()));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("발신자를 찾을 수 없습니다."));

        Letter letter = new Letter();
        letter.setReceiver(receiver);
        letter.setSender(sender);
        letter.setLetterContent(request.getLetterContent());
        letter.setDateSend(LocalDateTime.now());
        letter.setState(false);

        Letter savedLetter = letterRepository.save(letter);
        return convertToResponse(savedLetter);
    }

    private LetterDTO.Response convertToResponse(Letter letter) {
        return LetterDTO.Response.builder()
                .letterId(letter.getLetterId())
                .senderId(letter.getSender().getId())
                .senderName(letter.getSender().getUsername()) // Sender username
                .receiverId(letter.getReceiver().getId()) // Receiver ID
                .receiverUsername(letter.getReceiver().getUsername()) // Receiver username 추가
                .letterContent(letter.getLetterContent())
                .dateSend(letter.getDateSend())
                .state(letter.getState())
                .build();
    }


    //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2025-01-10 13:35 박청하<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public User getSenderByLetterId(Long letterId) {
        // letterId로 Letter 엔티티 조회
        Letter letter = letterRepository.findByLetterId(letterId);

        if (letter == null) {
            throw new IllegalArgumentException("Letter with ID " + letterId + " not found");
        }

        // 송신자 반환
        return letter.getSender();
    }

    public LetterDTO.Response getLetterById(Long letterId) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new RuntimeException("해당 쪽지를 찾을 수 없습니다: " + letterId));
        return convertToResponse(letter);
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2025-01-10 13:35 박청하>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}
