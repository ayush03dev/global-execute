package me.ayushdev.globalexecute;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class GlobalExecute extends JavaPlugin {

    public static final String PREFIX = ChatColor.GOLD + "[GlobalExecute] ";
    private static GlobalExecute instance;
    protected static GEClient client;
    protected static boolean shouldLogCommands = true;
    
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

        instantiateClient();

        client.connect();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (client.isClosed()) {
                    startAutoReconnectionAttempt();
                }
            }
        }.runTaskLater(this, 20*10);
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

    private void instantiateClient() {
        try {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("ge-password", getConfig().getString("PASSWORD"));
            headers.put("ge-client", getConfig().getString("NAME").toLowerCase());
            client = new GEClient(new URI("ws://" + getConfig().getString("IP") + ':' + getConfig().getInt("PORT")),
            headers);
            if(getConfig().isSet("log.command-executed"))
            {
                shouldLogCommands = getConfig().getBoolean("log.command-executed");
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            LogManager.getInstance().log( "There was a problem while instantiating the client! Disabling the plugin...", MessageType.BAD);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void startAutoReconnectionAttempt() {
        ConfigurationSection section = getConfig().getConfigurationSection("auto-reconnect");

        boolean enabled = section.getBoolean("enabled");
        int interval = section.getInt("interval");
        int maxAttempts = section.getInt("attempts");

        if (enabled) {
            new BukkitRunnable() {
                int attempts = 0;
                @Override
                public void run() {
                    if (client.isClosed()) {
                        if ((maxAttempts == -1)
                                || (maxAttempts > 0 && attempts <= maxAttempts)) {
                            instantiateClient();
                            LogManager.getInstance().log("Attempting to re-connect to the Daemon...", MessageType.NEUTRAL);
                            client.connect();
                            attempts++;
                        } else {
                            cancel();
                        }
                    } else {
                        cancel();
                    }
                }
            }.runTaskTimerAsynchronously(this, 0, 20 * interval);
        }
    }
}
