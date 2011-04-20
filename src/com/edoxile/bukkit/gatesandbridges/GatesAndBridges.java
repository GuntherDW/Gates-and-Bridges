package com.edoxile.bukkit.gatesandbridges;

import com.edoxile.bukkit.gatesandbridges.Listeners.GatesAndBridgesPlayerListener;
import com.edoxile.bukkit.gatesandbridges.Listeners.GatesAndBridgesRedstoneListener;
import com.edoxile.bukkit.gatesandbridges.Listeners.GatesAndBridgesSignListener;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 * Date: 18-4-11
 * Time: 21:48
 * To change this template use File | Settings | File Templates.
 */
public class GatesAndBridges extends JavaPlugin {
    private final static Logger log = Logger.getLogger("Minecraft");
    GatesAndBridgesSignListener singListener = new GatesAndBridgesSignListener();
    GatesAndBridgesPlayerListener playerListener = new GatesAndBridgesPlayerListener();
    GatesAndBridgesRedstoneListener redstoneListener = new GatesAndBridgesRedstoneListener();

    public void onEnable() {
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACE, this.singListener, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.SIGN_CHANGE, this.singListener, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.REDSTONE_CHANGE, this.redstoneListener, Event.Priority.Normal, this);
        log.info(getDescription().getName() + " version " + getDescription().getVersion() + " enabled.");
    }

    public void onDisable() {
        log.info(getDescription().getName() + " disabled.");
    }
}
