package me.ayushdev.globalexecute;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.Map;

public class GECommand extends Command {

    public GECommand() {
        super("globalexecute", "gexecute.admin", "ge");    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(new TextComponent(GlobalExecute.PREFIX + ChatColor.GRAY + "Available commands:"));
            sender.sendMessage(new TextComponent(ChatColor.GRAY + "/ge execute <server>|all <command>"));
            sender.sendMessage(new TextComponent(ChatColor.GRAY + "/ge list"));
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                StringBuilder stringBuilder = new StringBuilder("");

                if (GlobalExecute.CLIENTS.isEmpty()) {
                    sender.sendMessage(new TextComponent(GlobalExecute.PREFIX
                            + ChatColor.RED + "There are no available client servers :("));
                    return;
                }

                for (String str : GlobalExecute.CLIENTS.keySet()) {
                    stringBuilder.append(str + ", ");
                }

                String result = stringBuilder.toString();
                result = result.trim(); // To remove whitespaces
                result = result.substring(0, result.length()-1); // To ignore the last comma
                sender.sendMessage(new TextComponent(GlobalExecute.PREFIX
                + ChatColor.GRAY + "Available Servers: " + ChatColor.DARK_AQUA + result));
            } else {
                if (args[0].equalsIgnoreCase("execute")) {
                    sender.sendMessage(new TextComponent(GlobalExecute.PREFIX + ChatColor.RED + "Usage: /ge execute <server>|all <command>"));
                    return;
                }

              sendHelp(sender);
            }

        } else if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("execute")) {
                String serverName = args[1].toLowerCase();
                String command = wrapArguments(args, 2);

                if (serverName.equalsIgnoreCase("all")) {
                    for (Map.Entry<String, GEClient> entry : GlobalExecute.CLIENTS.entrySet()) {
                        GEClient client = entry.getValue();
                        if (client.getConnection().isOpen()) {
                            client.executeCommand(command);
                        } else {
                            GlobalExecute.CLIENTS.remove(serverName);
                        }
                    }
                    sender.sendMessage(new TextComponent(GlobalExecute.PREFIX + ChatColor.GREEN + "Command `" +
                            ChatColor.GRAY + '/' + command + ChatColor.GREEN + "' has been executed on all client servers!"));
                } else {
                    if (!GlobalExecute.CLIENTS.containsKey(serverName)) {
                        sender.sendMessage(new TextComponent(GlobalExecute.PREFIX + ChatColor.RED + "Server named "
                                + ChatColor.GRAY + "" + serverName + ChatColor.RED + " could not be found!"));
                        sender.sendMessage(new TextComponent(GlobalExecute.PREFIX + ChatColor.GRAY + "Use " +
                                ChatColor.DARK_AQUA + "/ge list" + ChatColor.GRAY + " command to get the list of all client server names."));
                        return;
                    }

                    GEClient client = GlobalExecute.CLIENTS.get(serverName);
                    if (client.getConnection().isOpen()) {
                        client.executeCommand(command);
                    } else {
                        GlobalExecute.CLIENTS.remove(serverName);
                    }

                    sender.sendMessage(new TextComponent(GlobalExecute.PREFIX + ChatColor.GREEN + "Command `" +
                            ChatColor.GRAY + '/' + command + ChatColor.GREEN + "' has been executed on client server named "
                            + ChatColor.GRAY + "" + serverName + ChatColor.GREEN + '!'));
                }
            } else {
               sendHelp(sender);
            }
        } else {
            if (args[0].equalsIgnoreCase("execute")) {
                sender.sendMessage(new TextComponent(GlobalExecute.PREFIX + ChatColor.RED + "Usage: /ge execute <server>|all <command>"));
            } else {
              sendHelp(sender);
            }
        }
    }

    private String wrapArguments(String[] args, int start) {
        StringBuilder builder = new StringBuilder();

        for (int i = start; i < args.length; i++) {
            builder.append(args[i] + " ");
        }
        return builder.toString().trim();
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(new TextComponent(GlobalExecute.PREFIX + ChatColor.RED + "Command not found!"));
        sender.sendMessage(new TextComponent(GlobalExecute.PREFIX + ChatColor.GRAY + "Available commands:"));
        sender.sendMessage(new TextComponent(ChatColor.GRAY + "/ge execute <server>|all <command>"));
        sender.sendMessage(new TextComponent(ChatColor.GRAY + "/ge list"));
    }
}
