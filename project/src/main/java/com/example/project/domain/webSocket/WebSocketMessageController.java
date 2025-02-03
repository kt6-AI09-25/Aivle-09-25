package com.example.project.domain.webSocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class WebSocketMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, Boolean> activeUsers = new ConcurrentHashMap<>();

    public WebSocketMessageController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("user/status")
    public void handleUserStatus(String username) {
        // 사용자를 활성 사용자 목록에 추가
        activeUsers.put(username, true);

        // 활성 사용자 목록을 모든 클라이언트에 브로드캐스트
        messagingTemplate.convertAndSend("/topic/status", activeUsers);
    }

    public void removeUser(String username) {
        // 사용자를 활성 사용자 목록에서 제거
        activeUsers.remove(username);

        // 활성 사용자 목록을 모든 클라이언트에 브로드캐스트
        messagingTemplate.convertAndSend("/topic/status", activeUsers);
    }
}