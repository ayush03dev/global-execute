package me.ayush_03.globalexecute;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

    public void onEnable() {
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new CommandClass());
        BungeeCord.getInstance().registerChannel("globalexecute:channel");
    }
}
