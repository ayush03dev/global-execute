package me.ayushdev.globalexecute;

import org.bukkit.ChatColor;

public class LogManager {

    private static LogManager instance = new LogManager();

    public static  LogManager getInstance() {
        return instance;
    }

    public String composeLog(String message, MessageType type) {
        String color;
        switch (type) {
            case BAD:
                color = "&c";
                break;

            case GOOD:
                color = "&a";
                break;

            case NEUTRAL:
                color = "&7";
                break;

            default:
                color = "&f";
                break;
        }

        return GlobalExecute.PREFIX + ChatColor.translateAlternateColorCodes('&',color + message);
    }

    public void log(String message, MessageType type) {
        GlobalExecute.getInstance().getServer().getConsoleSender().sendMessage(
                composeLog(message, type));
    }

}
