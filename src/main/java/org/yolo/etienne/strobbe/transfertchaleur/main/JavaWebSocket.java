package org.yolo.etienne.strobbe.transfertchaleur.main;

import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;

public class JavaWebSocket extends BaseWebSocketHandler {

    private JavaWebSocketServer server;

    public JavaWebSocket(JavaWebSocketServer jwss) {
        super();
        this.server = jwss;
    }

    @Override
    public void onOpen(WebSocketConnection connection) throws Exception {
        this.server.addConnection(connection);
        System.out.println("New Connection => Number of connections : " + this.server.getConnectionCount());
    }

    @Override
    public void onClose(WebSocketConnection connection) throws Exception {
        this.server.removeConnection(connection);
        System.out.println("Remove Connection => Number of connections : " + this.server.getConnectionCount());
    }

    @Override
    public void onMessage(WebSocketConnection connection, String message) throws Exception {
        System.out.println("Received new message : " + message); // In this case, nothing to do...
    }
}