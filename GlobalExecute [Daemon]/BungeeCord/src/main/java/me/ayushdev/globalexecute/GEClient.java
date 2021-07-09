package me.ayushdev.globalexecute;

import org.java_websocket.WebSocket;

public class GEClient {

    private String name;
    WebSocket connection;

    public GEClient(String name, WebSocket connection) {
        this.name = name;
        this.connection = connection;
    }

    public String getName() {
        return name;
    }

    public void executeCommand(String command) {
        connection.send("EXECUTE " + command);
    }

    public void sendLogMessage(String message) {
        connection.send("LOG " + message);
    }

    public void sendLogMessage(String message, MessageType type) {
        connection.send("LOG " + message);
    }

    public WebSocket getConnection() {
        return connection;
    }

    public void closeConnection() {
        connection.close();
    }
}
