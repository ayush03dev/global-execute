package me.ayush_03.globalexecute;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class Main extends JavaPlugin implements PluginMessageListener {
	
	public void onEnable() {
		// Registering the channel..
		 Bukkit.getMessenger().registerIncomingPluginChannel(this, "Return", this);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] message) {
		
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
		
		try {
			String sub = in.readUTF();
			if (sub.equals("command")) {
				
				// Signal received.
				
				String cmd = in.readUTF();
				System.out.println("[GlobalExecute] Received a command message from BungeeCord, executing it.");
				getServer().dispatchCommand(getServer().getConsoleSender(), cmd); 
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
