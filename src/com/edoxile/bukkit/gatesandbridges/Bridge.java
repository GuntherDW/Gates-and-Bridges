package com.edoxile.bukkit.gatesandbridges;

import com.edoxile.bukkit.gatesandbridges.Listeners.GatesAndBridgesPlayerListener;
import com.edoxile.bukkit.gatesandbridges.Mappers.BridgeMapper;
import com.edoxile.bukkit.gatesandbridges.Mappers.ChestMapper;
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
public class Bridge {
    private final static Logger log = Logger.getLogger("Minecraft");
    private BridgeMapper bridgeMapper = new BridgeMapper();
    private ChestMapper chestMapper = new ChestMapper();
    GatesAndBridgesSign sign = null;

    public Bridge(GatesAndBridgesSign s) {
        sign = s;
    }

    public boolean isValidBridge() {
        if (bridgeMapper.mapBridge(sign.getBlock(), sign.getSignBack())) {
            return chestMapper.mapChest(sign.getBlock());
        } else {
            return false;
        }
    }

    public boolean toggleBridge() {
        if (bridgeMapper.isClosed()) {
            //Open
            int fences = 0;
            for (Block b : bridgeMapper.getSet()) {
                Block tempBlock = b;
                tempBlock.setType(Material.AIR);
                fences++;
            }
            if (chestMapper.addMaterial(Material.WOOD, fences)) {
                GatesAndBridgesPlayerListener.player.sendMessage(ChatColor.GREEN + "Bridge opened!");
            }
        } else {
            //Close
            int fences = 0;
            for (Block b : bridgeMapper.getSet()) {
                Block tempBlock = b;
                tempBlock.setType(Material.WOOD);
                fences++;
            }
            if (chestMapper.removeMaterial(Material.WOOD, fences)) {
                GatesAndBridgesPlayerListener.player.sendMessage(ChatColor.GREEN + "Bridge closed!");
            } else {
                for (Block b : bridgeMapper.getSet()) {
                    Block tempBlock = b;
                    tempBlock.setType(Material.AIR);
                    fences++;
                }
                GatesAndBridgesPlayerListener.player.sendMessage(ChatColor.YELLOW + "Bridge remains unchanged...");
            }
        }
        return false;
    }
}
