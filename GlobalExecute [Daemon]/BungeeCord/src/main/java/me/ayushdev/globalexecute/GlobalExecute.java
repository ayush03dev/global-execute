package me.ayushdev.globalexecute;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class GlobalExecute extends Plugin {

    protected static final Map<String, GEClient> CLIENTS = new HashMap<>();
    public static final String PREFIX = ChatColor.GOLD + "[GlobalExecute] ";
    private GEServer server;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new GECommand());

        int port = getConfig().getInt("PORT");

        if (port <= 0) {
            getProxy().getPluginManager().unregisterCommands(this);
            getProxy().getConsole().sendMessage(new TextComponent(PREFIX + ChatColor.RED +
                    "Port is not configured correctly, disabling the plugin..."));
            return;
        }

        if (getConfig().getString("PASSWORD") == null) {
            getProxy().getPluginManager().unregisterCommands(this);
            getProxy().getConsole().sendMessage(new TextComponent(PREFIX + ChatColor.RED +
                    "Password is not configured, disabling the plugin..."));
            return;
        }

        try {

            server = new GEServer(port);

        } catch (UnknownHostException e) {
            e.printStackTrace();
            getProxy().getConsole().sendMessage(new TextComponent(PREFIX + ChatColor.RED +
                    "There was a problem while starting the Daemon! Please check the IP and Port."));
            return;
        }

        server.start();

    }

    @Override
    public void onDisable() {
        try {
            server.stop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveDefaultConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");


        if (!file.exists()) {
            try {
                InputStream in = getResourceAsStream("config.yml");
                Files.copy(in, file.toPath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    public static Configuration getConfig() {
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(
                    new File(ProxyServer.getInstance().getPluginsFolder() + File.separator + "GlobalExecute",
                            "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
