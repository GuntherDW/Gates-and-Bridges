package com.edoxile.bukkit.gatesandbridges;

import com.edoxile.bukkit.gatesandbridges.Exceptions.InsufficientMaterialsException;
import com.edoxile.bukkit.gatesandbridges.Exceptions.InsufficientSpaceException;
import com.edoxile.bukkit.gatesandbridges.Exceptions.InvalidSizeException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

import java.util.HashSet;
import java.util.logging.Logger;

public class Gate {
    private final static Logger log = Logger.getLogger("Minecraft");
    private Block startBlock = null;
    private HashSet<Block> fenceSet = new HashSet<Block>();
    private ChestMapper chestMapper = new ChestMapper();
    private GatesAndBridgesSign sign = null;
    private Player player = null;
    private Configuration config = null;
    private static int width = 0;
    private static int length = 0;

    public Gate(GatesAndBridgesSign s, Player p, Configuration c) {
        sign = s;
        player = p;
        config = c;
    }

    public boolean isValidGate() {
        if (sign.getBackBlock() == null) {
            return false;
        } else {
            if (mapGate(sign.getBackBlock())) {
                if (chestMapper.mapChest(sign.getBlock())) {
                    return true;
                } else {
                    if (player != null) {
                        player.sendMessage(ChatColor.RED + "No chest found near sign!");
                    }
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    public boolean toggleGate() {
        if (isClosed()) {
            //Open
            return openGate();
        } else {
            //Close
            return closeGate();
        }
    }

    public boolean openGate() {
        int fences = 0;
        for (Block b : fenceSet) {
            Block tempBlock = b;
            while (tempBlock.getRelative(BlockFace.DOWN).getType() == Material.FENCE) {
                tempBlock = tempBlock.getRelative(BlockFace.DOWN);
                tempBlock.setType(Material.AIR);
                fences++;
            }
        }
        try {
            chestMapper.addMaterial(Material.FENCE, fences);
            if (player != null) {
                player.sendMessage(ChatColor.YELLOW + "Gate opened!");
            }
            return true;
        } catch (InsufficientSpaceException ex) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Not enough space in chest! Items might be lost!");
            }
            log.info("[Gates and Bridges] Not enough space in chest. Gate position: {x=" + Integer.toString(sign.getBlock().getLocation().getBlockX()) + "; z=" + Integer.toString(sign.getBlock().getLocation().getBlockZ()) + "}");
            for (Block b : fenceSet) {
                Block tempBlock = b;
                while (canPassThrough(tempBlock.getRelative(BlockFace.DOWN).getType())) {
                    tempBlock = tempBlock.getRelative(BlockFace.DOWN);
                    tempBlock.setType(Material.FENCE);
                }
            }
            return false;
        }
    }

    public boolean closeGate() {
        int fences = 0;
        for (Block b : fenceSet) {
            Block tempBlock = b;
            while (canPassThrough(tempBlock.getRelative(BlockFace.DOWN).getType())) {
                tempBlock = tempBlock.getRelative(BlockFace.DOWN);
                tempBlock.setType(Material.FENCE);
                fences++;
            }
        }
        try {
            chestMapper.removeMaterial(Material.FENCE, fences);
            if (player != null) {
                player.sendMessage(ChatColor.YELLOW + "Gate closed!");
            }
            return true;
        } catch (InsufficientMaterialsException ex) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Not enough materials in chest! Still need: " + ex.toString() + " fence!");
            }
            for (Block b : fenceSet) {
                Block tempBlock = b;
                while (tempBlock.getRelative(BlockFace.DOWN).getType() == Material.FENCE) {
                    tempBlock = tempBlock.getRelative(BlockFace.DOWN);
                    tempBlock.setType(Material.AIR);
                }
            }
            return false;
        }
    }

    public boolean isClosed() {
        return startBlock.getRelative(BlockFace.DOWN).getType() == Material.FENCE;
    }

    private static boolean canPassThrough(Material m) {
        switch (m) {
            case AIR:
            case WATER:
            case STATIONARY_WATER:
            case LAVA:
            case STATIONARY_LAVA:
            case SNOW:
                return true;
            default:
                return false;
        }
    }

    private boolean mapGate(Block s) {
        startBlock = getStartBlock(s);
        width = 0;
        length = 0;
        if (startBlock == null) {
            return false;
        } else {
            fenceSet.clear();
            fenceSet.add(startBlock);
            try {
                listFences(startBlock);
                checkGateSize();
                return true;
            } catch (InvalidSizeException ex) {
                if (player != null) {
                    player.sendMessage(ChatColor.RED + "Gate is too long or too wide!");
                }
                return false;
            }
        }
    }

    private Block getStartBlock(Block startBlock) {
        Block tempBlock;
        for (int dy = -1; dy <= 64; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                tempBlock = startBlock.getRelative(dx, dy, 0);
                if (tempBlock.getType() == Material.FENCE)
                    return getTopFence(tempBlock);
            }
            for (int dz = -1; dz <= 1; dz += 2) {
                tempBlock = startBlock.getRelative(0, dy, dz);
                if (tempBlock.getType() == Material.FENCE)
                    return getTopFence(tempBlock);
            }
        }
        return null;
    }

    private Block getTopFence(Block startBlock) {
        Block tempBlock = startBlock;
        while (tempBlock.getRelative(BlockFace.UP).getType() == Material.FENCE) {
            tempBlock = tempBlock.getRelative(BlockFace.UP);
        }
        return tempBlock;
    }

    private void listFences(Block s) throws InvalidSizeException {
        Block tempBlock;
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dy = 1; dy >= -1; dy--) {
                tempBlock = s.getRelative(dx, dy, 0);
                if (tempBlock.getType() == Material.FENCE && (!fenceSet.contains(tempBlock))) {
                    fenceSet.add(tempBlock);
                    if ((Math.abs(tempBlock.getLocation().getBlockZ() - startBlock.getLocation().getBlockZ()) + 1) > width)
                        width = Math.abs(tempBlock.getLocation().getBlockZ() - startBlock.getLocation().getBlockZ()) + 1;
                    if ((Math.abs(tempBlock.getLocation().getBlockX() - startBlock.getLocation().getBlockX()) + 1) > length)
                        length = Math.abs(tempBlock.getLocation().getBlockX() - startBlock.getLocation().getBlockX()) + 1;
                    listFences(tempBlock);
                }
            }
        }
        for (int dz = -1; dz <= 1; dz += 2) {
            for (int dy = 1; dy >= -1; dy--) {
                tempBlock = s.getRelative(0, dy, dz);
                if (tempBlock.getType() == Material.FENCE && (!fenceSet.contains(tempBlock))) {
                    fenceSet.add(tempBlock);
                    if ((Math.abs(tempBlock.getLocation().getBlockZ() - startBlock.getLocation().getBlockZ()) + 1) > width)
                        width = Math.abs(tempBlock.getLocation().getBlockZ() - startBlock.getLocation().getBlockZ()) + 1;
                    if ((Math.abs(tempBlock.getLocation().getBlockX() - startBlock.getLocation().getBlockX()) + 1) > length)
                        length = Math.abs(tempBlock.getLocation().getBlockX() - startBlock.getLocation().getBlockX()) + 1;
                    listFences(tempBlock);
                }
            }
        }
    }

    private void checkGateSize() throws InvalidSizeException {
        if (width > length) {
            if (width > config.getInt("gate.max-length", 30) || length > config.getInt("gate.max-width", 3)) {
                log.info("[Gates and Bridges] Gate to large, width: " + Integer.toString(width) + ", length: " + Integer.toString(length));
                fenceSet.clear();
                throw new InvalidSizeException();
            } else {
                return;
            }
        } else {
            if (length > config.getInt("gate.max-length", 30) || width > config.getInt("gate.max-width", 3)) {
                fenceSet.clear();
                throw new InvalidSizeException();
            } else {
                return;
            }
        }
    }
}
