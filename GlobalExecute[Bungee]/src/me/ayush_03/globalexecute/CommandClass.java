package me.ayush_03.globalexecute;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class CommandClass extends Command {

    public CommandClass() {
        super("gexecute", "gexecute.admin", "ge");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /gexecute <server>|all <command>"));
            return;
        }

        String serverName = args[0];
        String command = wrapArguments(args);

        if (serverName.equalsIgnoreCase("all")) {

            Map<String, ServerInfo> servers = BungeeCord.getInstance().getServers();

            for (Map.Entry<String, ServerInfo> en : servers.entrySet()) {
                String name = en.getKey();
                ServerInfo server = BungeeCord.getInstance().getServerInfo(name);
                sendToBukkit(command, server);
            }

            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Command: '/" + command + "' will be executed on all servers."));

        } else {
            ServerInfo server = BungeeCord.getInstance().getServerInfo(serverName);

            if (server == null) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid server name!"));
                return;
            }

            sendToBukkit(command, server);
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Command: '/" + command + "' will be executed on the server "
                    + ChatColor.YELLOW + server.getName() + ChatColor.GREEN +"."));
        }
    }

    private void sendToBukkit(String message, ServerInfo server) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF("command");
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.sendData("globalexecute:channel", stream.toByteArray());
    }

    private String wrapArguments(String[] args) {
        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            builder.append(args[i] + " ");
        }
        return builder.toString().trim();
    }
}
