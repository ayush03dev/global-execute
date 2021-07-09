package me.ayushdev.globalexecute;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GECommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("globalexecute")) {
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.RED + "This command can only be executed by console!");
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage(GlobalExecute.PREFIX + ChatColor.GRAY + "Available commands:");
                sender.sendMessage(ChatColor.GRAY + "/ge execute <server>|all <command>");
                sender.sendMessage(ChatColor.GRAY + "/ge list");
                return true;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    if (GlobalExecute.client.isOpen()) {
                        GlobalExecute.client.send("CLIENTS_LIST");
                    } else {
                        sender.sendMessage(GlobalExecute.PREFIX + ChatColor.RED + "Client is not connected to the Daemon!");
                        return true;
                    }
                } else {
                    if (args[0].equalsIgnoreCase("execute")) {
                        sender.sendMessage(GlobalExecute.PREFIX + ChatColor.RED + "Usage: /ge execute <server>|all <command>");
                        return true;
                    }

                    sender.sendMessage( GlobalExecute.PREFIX + ChatColor.RED + "Command not found!");
                    sender.sendMessage(GlobalExecute.PREFIX + ChatColor.GRAY + "Available commands:");
                    sender.sendMessage(ChatColor.GRAY + "/ge execute <server>|all <command>");
                    sender.sendMessage(ChatColor.GRAY + "/ge list");
                }
            } else if (args.length >= 3) {
                if (args[0].equalsIgnoreCase("execute")) {
                    String target = args[1];
                    String command = wrapArguments(args, 2);
                    if (GlobalExecute.client.isOpen()) {
                        GlobalExecute.client.send("EXECUTE " + target + ' ' + command);
                    } else {
                        sender.sendMessage(GlobalExecute.PREFIX + ChatColor.RED + "Client is not connected to the Daemon!");
                        return true;
                    }
                } else {
                    sender.sendMessage( GlobalExecute.PREFIX + ChatColor.RED + "Command not found!");
                    sender.sendMessage(GlobalExecute.PREFIX + ChatColor.GRAY + "Available commands:");
                    sender.sendMessage(ChatColor.GRAY + "/ge execute <server>|all <command>");
                    sender.sendMessage(ChatColor.GRAY + "/ge list");
                }
            } else {
                if (args[0].equalsIgnoreCase("execute")) {
                    sender.sendMessage(GlobalExecute.PREFIX + ChatColor.RED + "Usage: /ge execute <server>|all <command>");
                    return true;
                } else {
                    sender.sendMessage( GlobalExecute.PREFIX + ChatColor.RED + "Command not found!");
                    sender.sendMessage(GlobalExecute.PREFIX + ChatColor.GRAY + "Available commands:");
                    sender.sendMessage(ChatColor.GRAY + "/ge execute <server>|all <command>");
                    sender.sendMessage(ChatColor.GRAY + "/ge list");
                }
            }
        }
        return true;
    }

    private String wrapArguments(String[] args, int start) {
        StringBuilder builder = new StringBuilder();

        for (int i = start; i < args.length; i++) {
            builder.append(args[i] + " ");
        }
        return builder.toString().trim();
    }
}
