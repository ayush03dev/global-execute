package me.ayushdev.globalexecute;

public class LogManager {

    private static final LogManager instance = new LogManager();

    private final String PREFIX_SPIGOT = "&6[GlobalExecute] ";

    public static LogManager getInstance() {
        return instance;
    }

    public String composeLogForSpigot(String message, MessageType type) {
        String color = null;
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

        return PREFIX_SPIGOT + color + message;
    }

}
