package com.edoxile.bukkit.gatesandbridges;

import com.edoxile.bukkit.gatesandbridges.Listeners.GatesAndBridgesPlayerListener;
import com.edoxile.bukkit.gatesandbridges.Listeners.GatesAndBridgesRedstoneListener;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class GatesAndBridges extends JavaPlugin {
    private final static Logger log = Logger.getLogger("Minecraft");
    GatesAndBridgesPlayerListener playerListener = new GatesAndBridgesPlayerListener();
    GatesAndBridgesRedstoneListener redstoneListener = new GatesAndBridgesRedstoneListener();

    public void onEnable() {
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.REDSTONE_CHANGE, this.redstoneListener, Event.Priority.Normal, this);
        log.info(getDescription().getName() + " version " + getDescription().getVersion() + " enabled.");
    }

    public void onDisable() {
        log.info(getDescription().getName() + " disabled.");
    }
}