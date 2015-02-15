package org.yolo.etienne.strobbe.transfertchaleur.main;

import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.WebSocketConnection;
import org.webbitserver.handler.StaticFileHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;


public class JavaWebSocketServer {
    private static final int WS_PORT = 9900;
    private static final String WS_ENTRY_POINT = "progconc";
    private static JavaWebSocketServer instance;

    private WebServer webserver;
    private Set<WebSocketConnection> connections;

    private JavaWebSocketServer() {
        this.connections = new HashSet<WebSocketConnection>();
        this.initServer();
    }

    public static JavaWebSocketServer getInstance() {
        if (JavaWebSocketServer.instance == null) {
            JavaWebSocketServer.instance = new JavaWebSocketServer();
        }

        return JavaWebSocketServer.instance;
    }

    private void initServer() {
        if (this.webserver == null) {
            try {
                this.webserver = WebServers.createWebServer(WS_PORT)
                        .add("/" + WS_ENTRY_POINT, new JavaWebSocket(this))
                        .add(new StaticFileHandler("./web"))
                        .start().get();
                System.out.println("Web Socket entry point is at : ws://" + this.webserver.getUri().getHost() + ":" + this.webserver.getUri().getPort() + "/" + WS_ENTRY_POINT);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public int getConnectionCount() {
        return this.connections.size();
    }

    public void addConnection(WebSocketConnection connection) {
        this.connections.add(connection);
    }

    public void removeConnection(WebSocketConnection connection) {
        this.connections.remove(connection);
    }

    public void broadcastMessage(String message) {
        for (WebSocketConnection connection : connections) {
            connection.send(message);
        }
    }
}
