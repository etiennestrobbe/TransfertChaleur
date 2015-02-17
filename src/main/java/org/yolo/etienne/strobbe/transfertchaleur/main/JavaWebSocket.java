package org.yolo.etienne.strobbe.transfertchaleur.main;

import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaWebSocket extends BaseWebSocketHandler {

    private JavaWebSocketServer server;
    private static final Logger LOGGER = Logger.getLogger("JavaWebSocker");

    public JavaWebSocket(JavaWebSocketServer jwss) {
        super();
        this.server = jwss;
    }

    @Override
    public void onOpen(WebSocketConnection connection) throws Exception {
        this.server.addConnection(connection);
        LOGGER.log(Level.INFO, "New Connection => Number of connections : " + this.server.getConnectionCount());
    }

    @Override
    public void onClose(WebSocketConnection connection) throws Exception {
        this.server.removeConnection(connection);
        LOGGER.log(Level.INFO, "Remove Connection => Number of connections : " + this.server.getConnectionCount());
    }

    @Override
    public void onMessage(WebSocketConnection connection, String message) throws Exception {
        LOGGER.log(Level.INFO, "Received new message : " + message); // In this case, nothing to do...
    }
}