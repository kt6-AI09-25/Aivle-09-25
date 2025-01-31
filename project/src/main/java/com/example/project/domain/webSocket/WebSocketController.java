package com.example.project.domain.webSocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
public class WebSocketController {

    private final Map<String, Boolean> userStatus = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, String> userConnections = new ConcurrentHashMap<>();


    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 기존 사용자를 강제로 로그아웃
    private void forceLogout(String username) {
        String connectionId = userConnections.get(username);
        if (connectionId != null) {
            messagingTemplate.convertAndSendToUser(connectionId, "/queue/logout", "force");
            userConnections.remove(username);
            userStatus.remove(username);
            log.info("User forcibly logged out: {}", username);
        }
    }



    // 클라이언트가 상태를 업데이트할 때 호출
    @MessageMapping("/status")
    public synchronized void updateUserStatus(@Payload String username, StompHeaderAccessor headerAccessor) {
        log.info("Received username: {}", username);
        if (username == null || username.trim().isEmpty()) {
            log.error("Received empty or invalid username");
            return;
        }

        // 고유 식별자 생성
        String connectionId = UUID.randomUUID().toString();

        // 고유 식별자 저장
        userConnections.put(username, connectionId);

        // 기존 사용자 강제 로그아웃
        if (userStatus.containsKey(username)) {
            forceLogout(username);
        }

        userConnections.put(username, connectionId); // 고유 식별자 저장
        userStatus.put(username, true);
        // 클라이언트로 connectionId 전송
        messagingTemplate.convertAndSendToUser(connectionId, "/queue/connectionId", connectionId);

        log.info("Current userStatus: {}", userStatus); // 현재 상태 출력
        // 강제로 활성 사용자 목록 브로드캐스트
        messagingTemplate.convertAndSend("/topic/status", userStatus);
        // 현재 세션에 사용자 정보 저장
        headerAccessor.getSessionAttributes().put("username", username);
    }

    // 활성 사용자 목록을 모든 구독자에게 전송
    @SendTo("/topic/status")
    public Map<String, Boolean> getActiveUsers() {
        log.info("Broadcasting active users: {}", userStatus); // 브로드캐스트 직전 상태 출력
        return userStatus;
    }

    // 연결 해제된 사용자를 처리하기 위한 메서드
    @EventListener
    public synchronized void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String connectionId = headerAccessor.getSessionId();
        String username = userConnections.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(connectionId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (username != null) {
            userConnections.remove(username);
            userStatus.remove(username);
            log.info("User disconnected: {}", username);
            log.info("Active users after disconnect: {}", userStatus);
        }
    }


    @EventListener
    public void handleConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = headerAccessor.getUser().getName();
        log.info("WebSocket connection established for user: {}", username);
    }
}