package com.edoxile.bukkit.gatesandbridges.Mappers;

import com.edoxile.bukkit.gatesandbridges.Listeners.GatesAndBridgesPlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import java.util.HashSet;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class BridgeMapper {
    private Block startBlock = null;
    private Block endBlock = null;
    private HashSet<Block> bridgeSet = new HashSet<Block>();
    private final static Logger log = Logger.getLogger("Minecraft");

    public boolean mapBridge(Block block, BlockFace blockFace) {
        startBlock = block;
        endBlock = getEndBlock(block, blockFace);
        if (endBlock == null) {
            return false;
        } else {
            listBlocks(startBlock, endBlock, blockFace);
            return true;
        }
    }

    public Block getEndBlock(Block block, BlockFace blockFace) {
        int d = 0;
        Block tempBlock = block;
        do {
            tempBlock = tempBlock.getRelative(blockFace);
            if (tempBlock.getType() == Material.SIGN_POST || tempBlock.getType() == Material.WALL_SIGN) {
                BlockState state = tempBlock.getState();
                if (state instanceof Sign) {
                    Sign s = (Sign) state;
                    if (s.getLine(1).equals("[Bridge]") || s.getLine(1).equals("[Bridge End]")) {
                        return tempBlock;
                    } else {
                        continue;
                    }
                }
            } else {
                d++;
            }
        } while (d <= 30);
        GatesAndBridgesPlayerListener.player.sendMessage(ChatColor.RED + "Couldn't find [Bridge] or [Bridge End].");
        return null;
    }

    public void listBlocks(Block s, Block e, BlockFace d) {
        bridgeSet.clear();
        int dy = 0;
        if (s.getRelative(BlockFace.UP).getType() == Material.WOOD) {
            dy = 1;
        } else if (s.getRelative(BlockFace.DOWN).getType() == Material.WOOD) {
            dy = -1;
        } else {
            GatesAndBridgesPlayerListener.player.sendMessage(ChatColor.RED + "Bridges need to be made of wood!");
            return;
        }

        switch (d) {
            case WEST: {
                for (int dz = 1; dz < e.getLocation().getBlockZ()-s.getLocation().getBlockZ(); dz++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        bridgeSet.add(s.getRelative(dx, dy, dz));
                    }
                }
            }
            break;
            case EAST: {
                for (int dz = -1; dz > e.getLocation().getBlockZ()-s.getLocation().getBlockZ(); dz--) {
                    for (int dx = -1; dx <= 1; dx++) {
                        bridgeSet.add(s.getRelative(dx, dy, dz));
                    }
                }
            }
            break;
            case NORTH: {
                for (int dx = -1; dx > e.getLocation().getBlockX()-s.getLocation().getBlockX(); dx--) {
                    for (int dz = -1; dz <= 1; dz++) {
                        bridgeSet.add(s.getRelative(dx, dy, dz));
                    }
                }
            }
            break;
            case SOUTH: {
                for (int dx = 1; dx < e.getLocation().getBlockX()-s.getLocation().getBlockX(); dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        bridgeSet.add(s.getRelative(dx, dy, dz));
                    }
                }
            }
            break;
            default:
                log.info("[BridgesAndGates] Not a valid BlockFace: " + d.name());
                break;
        }
    }

    public boolean isClosed() {
        for (Block b : bridgeSet) {
            return b.getType() == Material.WOOD;
        }
        log.info("[GatesAndBridges] blockSet empty!");
        dumpSet();
        return false;
    }

    public HashSet<Block> getSet() {
        return bridgeSet;
    }

    public void dumpSet() {
        log.info("[GatesAndBridges] dump: " + bridgeSet.toString());
    }
}
