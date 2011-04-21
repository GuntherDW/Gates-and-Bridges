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
                    GatesAndBridgesPlayerListener.player.sendMessage(ChatColor.RED + "No chest found close to sign.");
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
                GatesAndBridgesPlayerListener.player.sendMessage(ChatColor.GREEN + "Gate opened!");
            }
        } else {
            //Close
            int fences = 0;
            for (Block b : gateMapper.getSet()) {
                Block tempBlock = b;
                while (tempBlock.getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                    tempBlock = tempBlock.getRelative(BlockFace.DOWN);
                    tempBlock.setType(Material.FENCE);
                    fences++;
                }
            }
            if (chestMapper.removeMaterial(Material.FENCE, fences)) {
                GatesAndBridgesPlayerListener.player.sendMessage(ChatColor.GREEN + "Gate closed!");
            } else {
                for (Block b : gateMapper.getSet()) {
                    Block tempBlock = b;
                    while (tempBlock.getRelative(BlockFace.DOWN).getType() == Material.FENCE) {
                        tempBlock = tempBlock.getRelative(BlockFace.DOWN);
                        tempBlock.setType(Material.AIR);
                        fences++;
                    }
                }
                GatesAndBridgesPlayerListener.player.sendMessage(ChatColor.YELLOW + "Gate remains unchanged...");
            }
        }
        return false;
    }
}
