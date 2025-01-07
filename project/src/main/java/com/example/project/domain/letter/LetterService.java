package com.example.project.domain.letter;

import com.example.project.domain.user.User;
import com.example.project.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
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
        return letterRepository.findByReceiver_UserId(receiverId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<LetterDTO.Response> getSentLetters(Long senderId) {
        return letterRepository.findBySender_UserId(senderId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LetterDTO.Response sendLetter(LetterDTO.Request request, Long senderId) {
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다."));
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
                .senderName(letter.getSender().getUsername())
                .letterContent(letter.getLetterContent())
                .dateSend(letter.getDateSend())
                .state(letter.getState())
                .build();
    }
}
