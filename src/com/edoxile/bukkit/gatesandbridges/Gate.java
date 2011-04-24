package com.edoxile.bukkit.gatesandbridges;

import com.edoxile.bukkit.gatesandbridges.Listeners.GatesAndBridgesPlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashSet;
import java.util.logging.Logger;

public class Gate {
    private final static Logger log = Logger.getLogger("Minecraft");
    private Block startBlock = null;
    private HashSet<Block> fenceSet = new HashSet<Block>();
    private ChestMapper chestMapper = new ChestMapper();
    GatesAndBridgesSign sign = null;

    public Gate(GatesAndBridgesSign s) {
        sign = s;
    }

    public boolean isValidGate() {
        if (sign.getBackBlock() == null) {
            return false;
        } else {
            if (mapGate(sign.getBackBlock())) {
                if (chestMapper.mapChest(sign.getBlock())) {
                    return true;
                } else {
                    if (GatesAndBridgesPlayerListener.player != null) {
                        GatesAndBridgesPlayerListener.player.sendMessage(ChatColor.RED + "No chest found close to sign.");
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
        if (chestMapper.addMaterial(Material.FENCE, fences)) {
            return true;
        } else {
            for (Block b : fenceSet) {
                Block tempBlock = b;
                while (canPassThrough(tempBlock.getRelative(BlockFace.DOWN).getType())){
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
            while (canPassThrough(tempBlock.getRelative(BlockFace.DOWN).getType())){
                tempBlock = tempBlock.getRelative(BlockFace.DOWN);
                tempBlock.setType(Material.FENCE);
                fences++;
            }
        }
        if (chestMapper.removeMaterial(Material.FENCE, fences)) {
            return true;
        } else {
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

    public boolean isClosed(){
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
        if (startBlock == null) {
            return false;
        } else {
            fenceSet.clear();
            fenceSet.add(startBlock);
            listFences(startBlock);
            return true;
        }
    }

    private Block getStartBlock(Block startBlock) {
        Block tempBlock;
        for (int dy = -1; dy <= 64; dy++) {
            for (int dx = -1; dx <= 1; dx += 2) {
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
        int dy = 1;
        Block tempBlock = startBlock;
        while (tempBlock.getRelative(BlockFace.UP).getType() == Material.FENCE) {
            tempBlock = tempBlock.getRelative(BlockFace.UP);
        }
        return tempBlock;
    }

    private void listFences(Block s) {
        Block tempBlock;
        for (int dx = -1; dx <= 1; dx += 2) {
            tempBlock = s.getRelative(dx, 0, 0);
            if (tempBlock.getType() == Material.FENCE && (!fenceSet.contains(tempBlock))) {
                fenceSet.add(tempBlock);
                listFences(tempBlock);
            }
        }
        for (int dz = -1; dz <= 1; dz += 2) {
            tempBlock = s.getRelative(0, 0, dz);
            if (tempBlock.getType() == Material.FENCE && (!fenceSet.contains(tempBlock))) {
                fenceSet.add(tempBlock);
                listFences(tempBlock);
            }
        }
    }
}
