package com.edoxile.bukkit.gatesandbridges.Listeners;

import com.edoxile.bukkit.gatesandbridges.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.util.config.Configuration;

public class GatesAndBridgesPlayerListener extends PlayerListener {
    private GatesAndBridges plugin;
    private Configuration config = null;

    public GatesAndBridgesPlayerListener(Configuration c, GatesAndBridges instance) {
        config = c;
        plugin = instance;
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            switch (event.getClickedBlock().getType()) {
                case SIGN_POST:
                case WALL_SIGN: {
                    Sign s;
                    BlockState state = event.getClickedBlock().getState();
                    if (state instanceof Sign) {
                        s = (Sign) state;
                    } else {
                        return;
                    }
                    GatesAndBridgesSign sign = new GatesAndBridgesSign(s, event.getPlayer(), config);
                    if (sign.getMechanicsType() == MechanicsType.GATE ||
                            sign.getMechanicsType() == MechanicsType.DGATE) {
                        if(!plugin.check(event.getPlayer(), "gate.toggle", event.getClickedBlock())) {
                            event.getPlayer().sendMessage(ChatColor.RED+"You do not have permission to toggle gates!");
                            return;
                        }
                        Gate gate = sign.gateFactory();
                        if (!gate.isValidGate()) {
                            return;
                        }
                        gate.toggleGate();
                    } else if (sign.getMechanicsType() == MechanicsType.BRIDGE) {
                        if(!plugin.check(event.getPlayer(), "bridge.toggle", event.getClickedBlock())) {
                            event.getPlayer().sendMessage(ChatColor.RED+"You do not have permission to toggle bridges!");
                            return;
                        }
                        Bridge bridge = sign.bridgeFactory();
                        if (!bridge.isValidBridge()) {
                            return;
                        }
                        bridge.toggleBridge();
                    } else {
                        return;
                    }
                }
                break;
                default: {
                    Block tempBlock = event.getClickedBlock();
                    BlockFace blockFace = null;
                    switch (event.getBlockFace()) {
                        case WEST:
                            blockFace = BlockFace.EAST;
                            break;
                        case EAST:
                            blockFace = BlockFace.WEST;
                            break;
                        case NORTH:
                            blockFace = BlockFace.SOUTH;
                            break;
                        case SOUTH:
                            blockFace = BlockFace.NORTH;
                            break;
                    }
                    if (blockFace == null)
                        return;
                    if (tempBlock.getRelative(blockFace).getType() == Material.SIGN_POST || tempBlock.getRelative(blockFace).getType() == Material.WALL_SIGN) {
                        BlockState state = tempBlock.getRelative(blockFace).getState();
                        if (state instanceof Sign) {
                            Sign sign = (Sign) state;
                            if (sign.getLine(1).equalsIgnoreCase("[X]")) {
                                for (int dx = -1; dx <= 1; dx++) {
                                    for (int dy = -1; dy <= 1; dy++) {
                                        for (int dz = -1; dz <= 1; dz++) {
                                            if (dx == 0 && dy == 0 && dz == 0)
                                                continue;
                                            if (sign.getBlock().getRelative(dx, dy, dz).getType() == Material.LEVER) {
                                                Block lever = sign.getBlock().getRelative(dx, dy, dz);
                                                lever.setData((byte) (lever.getData() ^ 0x8));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
