package com.edoxile.bukkit.gatesandbridges;

import com.edoxile.bukkit.gatesandbridges.Listeners.GatesAndBridgesPlayerListener;
import com.edoxile.bukkit.gatesandbridges.Listeners.GatesAndBridgesRedstoneListener;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.util.logging.Logger;

public class GatesAndBridges extends JavaPlugin {
    private final static Logger log = Logger.getLogger("Minecraft");
    private Configuration config = null;
    private GatesAndBridgesPlayerListener playerListener = null;
    private GatesAndBridgesRedstoneListener redstoneListener = null;

    public void onEnable() {
        if (loadConfig()) {
            redstoneListener = new GatesAndBridgesRedstoneListener(config);
            playerListener = new GatesAndBridgesPlayerListener(config);
            getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Normal, this);
            getServer().getPluginManager().registerEvent(Event.Type.REDSTONE_CHANGE, this.redstoneListener, Event.Priority.Normal, this);
            log.info(getDescription().getName() + " version " + getDescription().getVersion() + " enabled.");
        } else {
            log.info("[Gates and Bridges] config file made. Please check the config and reload plugin.");
        }
    }

    public void onDisable() {
        log.info(getDescription().getName() + " disabled.");
    }

    protected boolean loadConfig() {
        if (getConfiguration() == null) {
            try {
                if (!getDataFolder().mkdirs()) {
                    log.severe("[Gates and Bridges] Couldn't write datafolder!");
                }
                getConfiguration().setProperty("bridge.max-length", 50);
                getConfiguration().setProperty("bridge.max-width", 11);
                getConfiguration().setProperty("bridge.default-width", 3);
                getConfiguration().setProperty("bridge.materials", "[COBBLESTONE, STONE, WOOD, STEP]");
                getConfiguration().setProperty("gate.max-length", 30);
                getConfiguration().setProperty("gate.max-width", 3);
                return false;
            } catch (Throwable e) {
                log.severe("[Gates and Bridges] There was an exception while we were saving the config, be sure to double-check!");
                return false;
            }
        } else {
            config = getConfiguration();
            return true;
        }
    }
}