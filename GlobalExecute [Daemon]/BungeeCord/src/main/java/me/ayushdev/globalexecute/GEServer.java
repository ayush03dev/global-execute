package me.ayushdev.globalexecute;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GEServer extends WebSocketServer {

    private static final String PASSWORD_FIELD = "ge-password";
    private static final String CLIENT_NAME_FIELD = "ge-client";

    private final int port;

    public GEServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        setReuseAddr(true);
        this.port = port;

    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        if (!clientHandshake.hasFieldValue(PASSWORD_FIELD)) {
            webSocket.send("LOG " +
                    LogManager.getInstance().composeLogForSpigot("Please specify a password, authorization denied!",
                            MessageType.BAD));
            webSocket.close();
            return;
        }

        if (!clientHandshake.hasFieldValue(CLIENT_NAME_FIELD)) {
            webSocket.send("LOG " +
                    LogManager.getInstance().composeLogForSpigot("Please specify the client server name, authorization denied!",
                            MessageType.BAD));
            webSocket.close();
            return;
        }

        String password = clientHandshake.getFieldValue(PASSWORD_FIELD);
        String instanceName = clientHandshake.getFieldValue(CLIENT_NAME_FIELD).toLowerCase();

        if (GlobalExecute.CLIENTS.containsKey(instanceName)) {
            GEClient client = GlobalExecute.CLIENTS.get(instanceName);

            if (client.getConnection().isOpen()) {
                webSocket.send("LOG " +
                        LogManager.getInstance().composeLogForSpigot(
                                "A GE client with that name is already connected to the Daemon, authorization denied!",
                                MessageType.BAD));
                webSocket.close();
                return;
            } else {
                GlobalExecute.CLIENTS.remove(instanceName);
            }
        }

        if (!password.equals(GlobalExecute.getConfig().getString("PASSWORD"))) {
            webSocket.send("LOG " +
                    LogManager.getInstance().composeLogForSpigot(
                            "Incorrect password, authorization denied!",
                            MessageType.BAD));
            webSocket.close();
            return;
        }

        GEClient client = new GEClient(instanceName, webSocket);
        GlobalExecute.CLIENTS.put(instanceName, client);

        client.sendLogMessage(LogManager.getInstance().composeLogForSpigot("Successfully authenticated!",
                MessageType.GOOD));
        ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(
               GlobalExecute.PREFIX + ChatColor.GREEN + "GE client with name " +
                        ChatColor.YELLOW + instanceName + ChatColor.GREEN + " has connected to the Daemon!"
        ));
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, GEClient> entry : GlobalExecute.CLIENTS.entrySet()) {
            WebSocket conn = entry.getValue().getConnection();
            if (conn.toString().equals(webSocket.toString()) || conn.isClosed()) {
                toRemove.add(entry.getKey());
            }
        }

        for (String key : toRemove) {
            ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(
                    GlobalExecute.PREFIX + ChatColor.RED + "GE client with name " +
                            ChatColor.YELLOW + key + ChatColor.RED + " has disconnected from the Daemon!"
            ));
            GlobalExecute.CLIENTS.remove(key);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        if (s.equals("CLIENTS_LIST")) {

            if (GlobalExecute.CLIENTS.isEmpty()) {
                webSocket.send( "LIST_ERROR There are no available client servers :(");
                return;
            }
            StringBuilder stringBuilder = new StringBuilder("");

            for (String str : GlobalExecute.CLIENTS.keySet()) {
                stringBuilder.append(str + ", ");
            }

            String result = stringBuilder.toString();
            result = result.trim(); // To remove whitespaces
            result = result.substring(0, result.length()-1); // To ignore the last comma
            webSocket.send("LIST_SUCCESS " + result);
        } else if (s.startsWith("EXECUTE ")) {
            String[] args = s.split(" ");
            String target = args[1];
            String command = wrapArguments(args, 2);

            if (target.equalsIgnoreCase("all")) {
                for (Map.Entry<String, GEClient> entry : GlobalExecute.CLIENTS.entrySet()) {
                    GEClient client = entry.getValue();
                    if (client.getConnection().isOpen()) {
                        client.executeCommand(command);
                    } else {
                        GlobalExecute.CLIENTS.remove(target);
                    }
                }
                webSocket.send("EXEC_ALL_SUCCESS " + command);
            } else {
                if (!GlobalExecute.CLIENTS.containsKey(target)) {
                  webSocket.send("EXEC_ONE_FAIL " + target );
                    return;
                }

                GEClient client = GlobalExecute.CLIENTS.get(target);
                if (client.getConnection().isOpen()) {
                    client.executeCommand(command);
                    webSocket.send("EXEC_ONE_SUCCESS " + target + ' ' + command);
                } else {
                    GlobalExecute.CLIENTS.remove(target);
                }
            }

        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
        ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(GlobalExecute.PREFIX + ChatColor.RED +
                "An error occurred!"));
    }

    public void onStart() {
        ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(GlobalExecute.PREFIX + ChatColor.GREEN +
                "Daemon started listening on port " + port));
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    private String wrapArguments(String[] args, int start) {
        StringBuilder builder = new StringBuilder();

        for (int i = start; i < args.length; i++) {
            builder.append(args[i] + " ");
        }
        return builder.toString().trim();
    }
}
