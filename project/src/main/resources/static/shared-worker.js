importScripts('https://cdn.jsdelivr.net/npm/sockjs-client/dist/sockjs.min.js');

importScripts('/js/stomp.min.js');


console.log("SockJS and StompJS loaded in SharedWorker");


let stompClient = null;
const connectedPorts = []; // SharedWorker와 연결된 모든 포트

console.log("SharedWorker script loaded");

onconnect = function (event) {

    const port = event.ports[0];
    console.log("SharedWorker connected to a client");
    connectedPorts.push(port);

    port.onmessage = function (e) {
        console.log("Message received in SharedWorker:", e.data);
        const data = e.data;

        if (data.type === "connect") {
            initializeWebSocket(data.username, port);
            console.log("WebSocket initialization request for username:", data.username);
        } else if (data.type === 'navigate') {
            if (stompClient && stompClient.connected && data.username) {
                // 사용자 상태를 active로 전송
                stompClient.send('/app/user-status', {}, JSON.stringify({ userId: data.username, status: 'active' }));
            }
        } else if (data.type === "send") {
            sendMessage(data.destination, data.payload);
        } else if (data.type === "disconnect") {
            console.log("WebSocket disconnect request received");
            disconnectWebSocket();
        }
    };

    port.start();
};

function initializeWebSocket(username, port) {
    console.log("Initializing WebSocket connection for user:", username);
    if (stompClient && stompClient.connected) {
        console.log("WebSocket already connected.");
        return;
    }

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

setInterval(() => {
    if (stompClient && stompClient.connected) {
        connectedPorts.forEach((port) => {
            // 모든 포트에서 사용자 이름 가져와 상태 업데이트
            port.postMessage({ type: "requestUsername" });
        });
    }
}, 60000);
