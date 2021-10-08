package me.ayushdev.globalexecute;

import org.bukkit.ChatColor;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.ConnectException;
import java.net.URI;
import java.util.Map;

public class GEClient extends WebSocketClient {

    public GEClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        LogManager.getInstance().log("Connected to the Daemon, waiting for authentication..", MessageType.NEUTRAL);
    }

    @Override
    public void onMessage(String s) {
        if (s.startsWith("EXECUTE ")) {
            s = s.replace("EXECUTE ", "");
            final String command = s;
            GlobalExecute.getInstance().getServer().getScheduler().callSyncMethod(GlobalExecute.getInstance(),
                    () -> GlobalExecute.getInstance().getServer().dispatchCommand
                            (GlobalExecute.getInstance().getServer().getConsoleSender(), command));
        } else if (s.startsWith("LOG ")) {
            s = s.replace("LOG ", "");
            s = ChatColor.translateAlternateColorCodes('&', s);
            GlobalExecute.getInstance().getServer().getConsoleSender().sendMessage(s);
        } else if (s.startsWith("EXEC_ONE_SUCCESS")) {
            String[] args = s.split(" ");
            String target = args[1];
            String command = wrapArguments(args, 2);
            GlobalExecute.getInstance().getServer().getConsoleSender().sendMessage(
                    GlobalExecute.PREFIX + ChatColor.GREEN + "Command `" +
                            ChatColor.GRAY + '/' + command + ChatColor.GREEN + "' has been executed on client server named "
                            + ChatColor.GRAY + "" + target + ChatColor.GREEN + '!');
        } else if (s.startsWith("EXEC_ONE_FAIL")) {
            String[] args = s.split(" ");
            String target = args[1];
            GlobalExecute.getInstance().getServer().getConsoleSender().sendMessage(
                    GlobalExecute.PREFIX +
                            ChatColor.RED + "Client server named "
                            +
                            ChatColor.GRAY + "" + target +
                            ChatColor.RED + " could not be found!");

            GlobalExecute.getInstance().getServer().getConsoleSender().sendMessage(
                    GlobalExecute.PREFIX +
                            ChatColor.GRAY + "Use " +
                            ChatColor.DARK_AQUA + "/ge list" +
                            ChatColor.GRAY + " command to get the list of all client server names."
            );
        } else if (s.startsWith("EXEC_ALL_SUCCESS")) {
            String command = s.replace("EXEC_ALL_SUCCESS ", "");
            GlobalExecute.getInstance().getServer().getConsoleSender().sendMessage(
                    GlobalExecute.PREFIX + ChatColor.GREEN + "Command `" +
                            ChatColor.GRAY + '/' + command + ChatColor.GREEN + "' has been executed on all client servers!"
            );
        } else if (s.startsWith("LIST_SUCCESS")) {
            String result = s.replace("LIST_SUCCESS ", "");
            GlobalExecute.getInstance().getServer().getConsoleSender().sendMessage(
                    GlobalExecute.PREFIX + ChatColor.YELLOW + "Available Servers: " + ChatColor.GRAY
                    + result);
        } else if (s.startsWith("LIST_ERROR")) {
            String message = s.replace("LIST_ERROR ", "");
            GlobalExecute.getInstance().getServer().getConsoleSender().sendMessage(GlobalExecute.PREFIX +
                    ChatColor.RED + "" + message);
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        if (b) {
            LogManager.getInstance().log("Connection closed with Daemon!",
                    MessageType.BAD);

            GlobalExecute.getInstance().startAutoReconnectionAttempt();
        }
    }

    @Override
    public void onError(Exception e) {
        if (e instanceof ConnectException) {
            LogManager.getInstance().log( "Could not connect to the Daemon, it's probably offline!",
                    MessageType.BAD);
//            LogManager.getInstance().log("Attempting to reconnect in 5 seconds", MessageType.NEUTRAL);
            return;
        }
        e.printStackTrace();
        LogManager.getInstance().log( "Could not connect to the Daemon!",
                MessageType.BAD);
    }

    private String wrapArguments(String[] args, int start) {
        StringBuilder builder = new StringBuilder();

        for (int i = start; i < args.length; i++) {
            builder.append(args[i] + " ");
        }
        return builder.toString().trim();
    }

}
