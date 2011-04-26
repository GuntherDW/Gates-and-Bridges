package com.edoxile.bukkit.gatesandbridges;

import com.edoxile.bukkit.gatesandbridges.Exceptions.InvalidNotationException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.config.Configuration;

import java.util.logging.Logger;

public class GatesAndBridgesSign {
    private Sign sign = null;
    private Player player = null;
    private Configuration config = null;
    private final static Logger log = Logger.getLogger("Minecraft");

    public GatesAndBridgesSign(Sign s, Player p, Configuration c) {
        sign = s;
        player = p;
        config = c;
    }

    private boolean isBridgeOrGateSign() {
        String line = sign.getLine(1);
        return (line.equals("[Gate]") || line.equals("[Bridge]"));
    }

    public MechanicsType getMechanicsType() {
        String line = sign.getLine(1);
        if (line.equals("[Gate]")) {
            return MechanicsType.GATE;
        } else if (line.equals("[Bridge]")) {
            return MechanicsType.BRIDGE;
        } else {
            return null;
        }
    }

    public Gate gateFactory() {
        if (getMechanicsType() == MechanicsType.GATE)
            return new Gate(this, player, config);
        else
            return null;
    }

    public Bridge bridgeFactory() {
        if (getMechanicsType() == MechanicsType.BRIDGE)
            return new Bridge(this, player, config);
        else
            return null;
    }

    public BlockFace getSignBack() {
        if (sign.getType() == Material.WALL_SIGN) {
            switch (sign.getData().getData()) {
                case 0x2:
                    return BlockFace.WEST;
                case 0x3:
                    return BlockFace.EAST;
                case 0x4:
                    return BlockFace.SOUTH;
                case 0x5:
                    return BlockFace.NORTH;
                default:
                    return null;
            }
        } else if (sign.getType() == Material.SIGN_POST) {
            switch (sign.getData().getData()) {
                case 0:
                    return BlockFace.EAST;
                case 4:
                    return BlockFace.SOUTH;
                case 8:
                    return BlockFace.WEST;
                case 12:
                    return BlockFace.NORTH;
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    public BlockFace getSignFront() {
        if (sign.getType() == Material.WALL_SIGN) {
            switch (sign.getData().getData()) {
                case 0x2:
                    return BlockFace.EAST;
                case 0x3:
                    return BlockFace.WEST;
                case 0x4:
                    return BlockFace.NORTH;
                case 0x5:
                    return BlockFace.SOUTH;
                default:
                    return null;
            }
        } else if (sign.getType() == Material.SIGN_POST) {
            switch (sign.getData().getData()) {
                case 0:
                    return BlockFace.WEST;
                case 4:
                    return BlockFace.NORTH;
                case 8:
                    return BlockFace.EAST;
                case 12:
                    return BlockFace.SOUTH;
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    public boolean isPowered() {
        return sign.getBlock().isBlockPowered() || sign.getBlock().isBlockIndirectlyPowered();
    }

    public Block getBackBlock() {
        if (getSignBack() == null) {
            if (player != null) {
                player.sendMessage(ChatColor.YELLOW + "Sign is placed incorrect. Direction should be: North, East, South or West.");
            }
        }
        return sign.getBlock().getRelative(getSignBack());
    }

    public Block getFrontBlock() {
        if (getSignFront() == null) {
            if (player != null) {
                player.sendMessage(ChatColor.YELLOW + "Sign is placed incorrect. Direction should be: North, East, South or West.");
            }
        }
        return sign.getBlock().getRelative(getSignFront());
    }

    public Block getBlock() {
        return sign.getBlock();
    }

    public int getBridgeWidth() throws InvalidNotationException {
        int width = 0;
        if (getMechanicsType() == MechanicsType.BRIDGE) {
            String material = "";
            if (sign.getLine(2).contains("[Width ")) {
                String str = sign.getLine(2).substring(7, sign.getLine(3).length() - 1);
                width = Integer.getInteger(str);
                if (width > 0 && width < config.getInt("bridge.max-width", config.getInt("bridge.default-width", 3))) {
                    return width;
                } else {
                    throw new InvalidNotationException("Width greater than max or 0!");
                }
            } else if (sign.getLine(3).contains("[Width ")) {
                String str = sign.getLine(3).substring(7, sign.getLine(3).length() - 1);
                width = Integer.getInteger(str);
                if (width > 0 && width < config.getInt("bridge.max-width", config.getInt("bridge.default-width", 3))) {
                    return width;
                } else {
                    throw new InvalidNotationException("Width greater than max or 0!");
                }
            } else {
                return config.getInt("bridge.default-width", 3);
            }
        }
        return -1;
    }
}
