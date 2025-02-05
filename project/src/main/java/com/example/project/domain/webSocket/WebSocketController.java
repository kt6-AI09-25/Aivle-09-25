package com.example.project.domain.webSocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
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
    @MessageMapping("/force-logout")
    public void handleForceLogout(@Payload Map<String, String> payload) {
        String username = payload.get("username");  // JSON 객체에서 username 추출
        log.info("Received force-logout request for: {}", username);
        forceLogout(username);  // String을 받는 forceLogout() 호출
    }

    // 내부적으로 강제 로그아웃을 처리하는 메서드 (String 타입)
    public void forceLogout(String username) {
        log.info("Sending force-logout message to user: {}", username);

        // Stomp의 개인 메시지는 "/user/{username}/queue/logout" 경로로 전송해야 함
        messagingTemplate.convertAndSendToUser(username, "/queue/logout", "force-logout");
    }

    // 클라이언트가 상태를 업데이트할 때 호출
    @MessageMapping("/status")
    public synchronized void updateUserStatus(@Payload Map<String, String> payload, StompHeaderAccessor headerAccessor) {
        String username = payload.get("username");  // JSON 객체에서 username 추출
        log.info("Received username: {}", username);

        if (username == null || username.trim().isEmpty()) {
            log.error("Received empty or invalid username");
            return;
        }

        // 기존 사용자가 있다면 강제 로그아웃
        if (userStatus.containsKey(username)) {
            forceLogout(username);
        }

        userStatus.put(username, true); // 새로운 사용자 로그인 처리
        log.info("Current userStatus: {}", userStatus);

        // 활성 사용자 목록 브로드캐스트
        messagingTemplate.convertAndSend("/topic/status", userStatus);

        // 세션에 username 저장
        headerAccessor.getSessionAttributes().put("username", username);
    }


    // 활성 사용자 목록을 모든 구독자에게 전송
    @SendTo("/topic/status")
    public Map<String, Boolean> getActiveUsers() {
        log.info("Broadcasting active users: {}", userStatus);
        return userStatus;
    }

    // 사용자가 연결을 종료하면 상태 제거
    @EventListener
    public synchronized void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username != null) {
            userStatus.remove(username);
            log.info("User disconnected: {}", username);
            log.info("Active users after disconnect: {}", userStatus);

            // 변경된 활성 사용자 목록 전송
            messagingTemplate.convertAndSend("/topic/status", userStatus);
        }
    }

    // 사용자가 연결될 때 로그 출력
    @EventListener
    public void handleConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "Unknown";
        log.info("WebSocket connection established for user: {}", username);
    }
}