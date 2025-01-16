let stompClient = null;
const connectedPorts = []; // SharedWorker와 연결된 모든 포트

onconnect = function (event) {
    const port = event.ports[0];
    connectedPorts.push(port);

    port.onmessage = function (e) {
        const data = e.data;

        if (data.type === "connect") {
            initializeWebSocket(data.username, port);
        } else if (data.type === "send") {
            sendMessage(data.destination, data.payload);
        } else if (data.type === "disconnect") {
            disconnectWebSocket();
        }
    };

    port.start();
};

function initializeWebSocket(username, port) {
    if (stompClient && stompClient.connected) {
        console.log("WebSocket already connected.");
        return;
    }

    console.log("Initializing WebSocket connection...");
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        console.log("WebSocket connected.");

        // 서버에 사용자 이름 전송
        stompClient.send('/app/status', {}, username);

        // 활성 사용자 목록 구독
        stompClient.subscribe('/topic/status', (message) => {
            const activeUsers = JSON.parse(message.body);
            console.log("Active users received:", activeUsers);

            // 모든 연결된 페이지에 활성 사용자 목록 전송
            connectedPorts.forEach((p) => p.postMessage({ type: "activeUsers", data: activeUsers }));
        });

        // 연결 완료 이벤트 전송
        port.postMessage({ type: "connected" });
    }, (error) => {
        console.error("WebSocket connection error:", error);
    });
}

function sendMessage(destination, payload) {
    if (stompClient && stompClient.connected) {
        stompClient.send(destination, {}, JSON.stringify(payload));
    } else {
        console.error("WebSocket is not connected.");
    }
}

function disconnectWebSocket() {
    if (stompClient) {
        stompClient.disconnect(() => {
            console.log("WebSocket disconnected.");
        });
        stompClient = null;
    }
}
