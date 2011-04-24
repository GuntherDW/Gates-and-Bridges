package com.edoxile.bukkit.gatesandbridges;

import com.edoxile.bukkit.gatesandbridges.Listeners.GatesAndBridgesPlayerListener;
import com.edoxile.bukkit.gatesandbridges.Mappers.ChestMapper;
import com.edoxile.bukkit.gatesandbridges.Mappers.GateMapper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 * Date: 19-4-11
 * Time: 8:51
 * To change this template use File | Settings | File Templates.
 */
public class Gate {
    private final static Logger log = Logger.getLogger("Minecraft");
    private GateMapper gateMapper = new GateMapper();
    private ChestMapper chestMapper = new ChestMapper();
    GatesAndBridgesSign sign = null;

    public Gate(GatesAndBridgesSign s) {
        sign = s;
    }

    public boolean isValidGate() {
        if (sign.getBackBlock() == null) {
            return false;
        } else {
            if (gateMapper.mapGate(sign.getBackBlock())) {
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
        if (gateMapper.isClosed()) {
            //Open
            return openGate();
        } else {
            //Close
            return closeGate();
        }
    }

    public boolean openGate() {
        int fences = 0;
        for (Block b : gateMapper.getSet()) {
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
            for (Block b : gateMapper.getSet()) {
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
        for (Block b : gateMapper.getSet()) {
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
            for (Block b : gateMapper.getSet()) {
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
        return gateMapper.isClosed();
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
}
