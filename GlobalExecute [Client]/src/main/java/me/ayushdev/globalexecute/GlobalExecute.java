package me.ayushdev.globalexecute;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class GlobalExecute extends JavaPlugin {

    public static final String PREFIX = ChatColor.GOLD + "[GlobalExecute] ";
    private static GlobalExecute instance;
    protected static GEClient client;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getCommand("globalexecute").setExecutor(new GECommand());
        boolean checkIp = getConfig().isSet("IP");
        boolean checkPort = getConfig().isSet("PORT");
        boolean checkPassword = getConfig().isSet("PASSWORD");
        boolean checkName = getConfig().isSet("NAME");

        if (!checkIp) {
            LogManager.getInstance().log("IP is not configured, disabling the plugin...", MessageType.BAD);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!checkPort) {
            LogManager.getInstance().log("PORT is not configured, disabling the plugin...", MessageType.BAD);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!checkPassword) {
            LogManager.getInstance().log("PASSWORD is not configured, disabling the plugin...", MessageType.BAD);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!checkName) {
            LogManager.getInstance().log( "NAME is not configured, disabling the plugin...", MessageType.BAD);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        LogManager.getInstance().log( "Attempting to connect to the Daemon...", MessageType.NEUTRAL);

        try {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("ge-password", getConfig().getString("PASSWORD"));
            headers.put("ge-client", getConfig().getString("NAME").toLowerCase());
            client = new GEClient(new URI("ws://" + getConfig().getString("IP") + ':' + getConfig().getInt("PORT")),
                    headers);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            LogManager.getInstance().log( "There was a problem while connecting to the Daemon! Disabling the plugin...", MessageType.BAD);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        client.connect();
    }

    @Override
    public void onDisable() {
        if (client.isOpen()) {
            client.closeConnection(1, "Plugin Disabled");
        }
    }

    public static GlobalExecute getInstance() {
        return instance;
    }

}
