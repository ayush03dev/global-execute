package me.ayush_03.globalexecute;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GECommand implements CommandExecutor {

    Main plugin;
    public GECommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("gexecute")) {
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.RED + "This command can only be executed by console!");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /gexecute <server>|all <command>");
                return true;
            }

            String serverName = args[0];
            String command = wrapArguments(args);

            sendToBungee(serverName, command);

            if (serverName.equalsIgnoreCase("all")) {
                sender.sendMessage(ChatColor.GREEN + "Command '/" + command + "' will be executed on all servers.");
            } else {
                sender.sendMessage(ChatColor.GREEN + "Command '/" + command + "' will be executed on the server " +
                        ChatColor.YELLOW + serverName + ChatColor.GREEN + ".");
            }
        }
        return true;
    }

    private String wrapArguments(String[] args) {
        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            builder.append(args[i] + " ");
        }
        return builder.toString().trim();
    }

    private void sendToBungee(String server, String command) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF("command");
            out.writeUTF(server);
            out.writeUTF(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bukkit.getServer().sendPluginMessage(plugin, Main.channel, stream.toByteArray());
    }
}
