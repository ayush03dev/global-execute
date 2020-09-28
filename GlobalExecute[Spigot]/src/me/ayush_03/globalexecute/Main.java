package me.ayush_03.globalexecute;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Main extends JavaPlugin implements PluginMessageListener {

    public static final String channel = "gexecute:channel";

    @Override
    public void onEnable() {
        getCommand("gexecute").setExecutor(new GECommand(this));
        Bukkit.getMessenger().registerIncomingPluginChannel(this, channel, this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, channel);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] message) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

        try {
            String subChannel = in.readUTF();

            if (subChannel.equalsIgnoreCase("command")) {
                String command = in.readUTF();
                getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
