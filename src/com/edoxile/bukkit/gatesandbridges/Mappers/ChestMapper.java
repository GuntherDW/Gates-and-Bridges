package com.edoxile.bukkit.gatesandbridges.Mappers;

import com.edoxile.bukkit.gatesandbridges.Listeners.GatesAndBridgesPlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class ChestMapper {
    private final static Logger log = Logger.getLogger("Minecraft");
    Block startBlock = null;
    Chest chest = null;

    public boolean mapChest(Block s) {
        startBlock = s;
        Block tempBlock;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0)
                        continue;
                    tempBlock = startBlock.getRelative(dx, dy, dz);
                    if (tempBlock.getType() == Material.CHEST) {
                        BlockState state = tempBlock.getState();
                        if (state instanceof Chest) {
                            chest = (Chest) state;
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean containsMaterial(Material m, int a) {
        return chest.getInventory().contains(m, a);
    }

    public boolean containsMaterial(Material m) {
        return chest.getInventory().contains(m);
    }

    public boolean removeMaterial(Material m, int a) {
        ItemStack itemStack = new ItemStack(m, a);
        HashMap<Integer, ItemStack> hashMap = chest.getInventory().removeItem(itemStack);
        if (!hashMap.isEmpty()) {
            int amount = 0;
            for (ItemStack i : hashMap.values()) {
                amount += i.getAmount();
            }
            itemStack = new ItemStack(m, amount);
            hashMap = chest.getInventory().addItem(itemStack);
            if (!hashMap.isEmpty()) {
                log.info("[GatesAndBridges] Strange things are happening with chests...");
                GatesAndBridgesPlayerListener.player.sendMessage(ChatColor.RED + "Something is going wrong!");
                return false;
            } else {
                GatesAndBridgesPlayerListener.player.sendMessage(ChatColor.RED + "Not enough items of type: " + m.name() + " in chest!");
                return false;
            }
        } else {
            return true;
        }
    }

    public boolean addMaterial(Material m, int a) {
        ItemStack itemStack = new ItemStack(m, a);
        HashMap<Integer, ItemStack> hashMap = chest.getInventory().addItem(itemStack);
        if (!hashMap.isEmpty()) {
            log.info("[GatesAndBridges] Couldn't fit all items in chest.");
            GatesAndBridgesPlayerListener.player.sendMessage(ChatColor.AQUA + "Couldn't fit all items in chest, placed the rest in your inventory.");
            for (ItemStack i : hashMap.values()) {
                GatesAndBridgesPlayerListener.player.getInventory().addItem(i);
            }
            return false;
        } else {
            return true;
        }
    }
}
