package com.edoxile.bukkit.gatesandbridges.Mappers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 * Date: 19-4-11
 * Time: 8:47
 * To change this template use File | Settings | File Templates.
 */
public class GateMapper {
    private Block startBlock = null;
    private HashSet<Block> fenceSet = new HashSet<Block>();

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

    public boolean isClosed() {
        return startBlock.getRelative(BlockFace.DOWN).getType() == Material.FENCE;
    }

    public Block getStartBlock(Block startBlock) {
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

    public boolean mapGate(Block s) {
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

    public HashSet<Block> getSet() {
        return fenceSet;
    }
}
