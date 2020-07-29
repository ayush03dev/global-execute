package me.ayush_03.globalexecute;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Main extends JavaPlugin implements PluginMessageListener {

    @Override
    public void onEnable() {
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "globalexecute:channel", this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] message) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
        try {
            String subChannel = in.readUTF();

            if (subChannel.equalsIgnoreCase("command")) {
                String command = in.readUTF();
                System.out.println("[GlobalExecute] Received a command request from BungeeCord, executing it.");
                getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
