package me.ayush_03.globalexecute;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;

public class Main extends Plugin implements Listener {

    // Already initialized as the default channel name
    public static String channel = "ge:cmdchannel";


    public void onEnable() {
        saveDefaultConfig();
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandClass());
        BungeeCord.getInstance().getPluginManager().registerListener(this, this);

        try {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
            if (configuration.getString("channel") != null) {
                String channelName = configuration.getString("channel");
                if (channelName.contains(":")) {
                    channel = channelName;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        BungeeCord.getInstance().registerChannel(channel);
    }

    @EventHandler
    public void onMessage(PluginMessageEvent e) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
        String incomingChannel = e.getTag();

        if (!incomingChannel.equalsIgnoreCase(channel)) return;

        try {
            String subChannel = in.readUTF();

            if (subChannel.equalsIgnoreCase("command")) {
                String serverName = in.readUTF();
                String command = in.readUTF();

                if (serverName.equalsIgnoreCase("all")) {
                    for (Map.Entry<String, ServerInfo> entry : BungeeCord.getInstance().getServers().entrySet()) {
                        ServerInfo server = entry.getValue();
                        sendToBukkit(command, server);
                    }
                } else {
                    ServerInfo server = BungeeCord.getInstance().getServerInfo(serverName);
                    if (server != null) {
                        sendToBukkit(command, server);
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected static void sendToBukkit(String message, ServerInfo server) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF("command");
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.sendData(channel, stream.toByteArray());
    }

    private void saveDefaultConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");


        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
