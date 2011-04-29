package com.edoxile.bukkit.gatesandbridges;

import com.edoxile.bukkit.gatesandbridges.Listeners.GatesAndBridgesPlayerListener;
import com.edoxile.bukkit.gatesandbridges.Listeners.GatesAndBridgesRedstoneListener;
import com.edoxile.bukkit.gatesandbridges.Listeners.GatesAndBridgesSignListener;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.util.logging.Logger;

public class GatesAndBridges extends JavaPlugin {
    private final static Logger log = Logger.getLogger("Minecraft");
    private Configuration config = null;
    private GatesAndBridgesPlayerListener playerListener = null;
    private GatesAndBridgesRedstoneListener redstoneListener = null;
    private GatesAndBridgesSignListener blockListener = null;
    private static PermissionHandler permissionHandler = null;
    private static WorldGuardPlugin worldGuard = null;

    public void onEnable() {
        if (loadConfig()) {
            redstoneListener = new GatesAndBridgesRedstoneListener(config);
            playerListener = new GatesAndBridgesPlayerListener(config, this);
            blockListener = new GatesAndBridgesSignListener(config, this);
            getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Normal, this);
            getServer().getPluginManager().registerEvent(Event.Type.REDSTONE_CHANGE, this.redstoneListener, Event.Priority.Normal, this);
            getServer().getPluginManager().registerEvent(Event.Type.SIGN_CHANGE, this.blockListener, Event.Priority.Normal, this);
            if(config.getBoolean("worldguard", false)) {
                setupWorldGuard();
            }
            if(config.getBoolean("permissions", false)) {
                setupPermissions();
            }
            log.info(getDescription().getName() + " version " + getDescription().getVersion() + " enabled.");
        } else {
            log.info("[Gates and Bridges] config file made. Please check the config and reload plugin.");
        }
        setupPermissions();
    }

    private void setupWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if(worldGuard == null) {
            if(plugin != null) {
                worldGuard = (WorldGuardPlugin) plugin;
            }
        }
    }

    private void setupPermissions() {
        Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

        if (this.permissionHandler == null) {
            if (permissionsPlugin != null) {
                this.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
            } else {
                log.info("[Gates and Bridges] Permission system not detected, defaulting to OP");
            }
        }
    }

    public boolean checkPermissions(Player player, String permission) {
        if(permissionHandler == null) {
            return true;
        } else {
            return player.isOp()
                    || permissionHandler.permission (player, "gatesandbridges."+permission);
        }
    }

    public boolean checkWorldGuard(Player player, Block clickedBlock) {
        if(worldGuard == null)
            return true;
        else {
            return player.isOp()
                    || worldGuard.canBuild(player, clickedBlock);
        }

    }

    public boolean check(Player player, String type, Block clickedBlock) {
        boolean allowed = false;
        if(checkPermissions(player, type)) {
            if(checkWorldGuard(player, clickedBlock)) {
                allowed = true;
            }
        }
        return allowed;
    }

    public static MechanicsType getMechanicsType(String line) {
        if (line.equalsIgnoreCase("[Gate]")) {
            return MechanicsType.GATE;
        } else if(line.equalsIgnoreCase("[DGate]")) {
            return MechanicsType.DGATE;
        } else if (line.equalsIgnoreCase("[Bridge]")) {
            return MechanicsType.BRIDGE;
        } else {
            return null;
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
                getConfiguration().setProperty("permissions", false);
                getConfiguration().setProperty("worldguard", false);
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