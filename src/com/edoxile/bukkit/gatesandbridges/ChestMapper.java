package com.edoxile.bukkit.gatesandbridges;

import com.edoxile.bukkit.gatesandbridges.Exceptions.InsufficientMaterialsException;
import com.edoxile.bukkit.gatesandbridges.Exceptions.InsufficientSpaceException;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.logging.Logger;

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

    public boolean removeMaterial(ItemStack itemStack) throws InsufficientMaterialsException{
        int a = itemStack.getAmount();
        HashMap<Integer, ItemStack> hashMap = chest.getInventory().removeItem(itemStack);
        if (!hashMap.isEmpty()) {
            int amount = 0;
            for (ItemStack i : hashMap.values()) {
                amount += i.getAmount();
            }
            if ((a - amount) > 0) {
                itemStack.setAmount(a - amount);
                hashMap = chest.getInventory().addItem(itemStack);
            }
            throw new InsufficientMaterialsException();
        } else {
            return true;
        }
    }

    public boolean removeMaterial(Material m, int a) throws InsufficientMaterialsException {
        ItemStack itemStack = new ItemStack(m, a);
        HashMap<Integer, ItemStack> hashMap = chest.getInventory().removeItem(itemStack);
        if (!hashMap.isEmpty()) {
            int amount = 0;
            for (ItemStack i : hashMap.values()) {
                amount += i.getAmount();
            }
            if ((a - amount) > 0) {
                itemStack.setAmount(a - amount);
                hashMap = chest.getInventory().addItem(itemStack);
            }
            throw new InsufficientMaterialsException();
        } else {
            return true;
        }
    }

    public boolean addMaterial(Material m, int a) throws InsufficientSpaceException {
        ItemStack itemStack = new ItemStack(m, a);
        HashMap<Integer, ItemStack> hashMap = chest.getInventory().addItem(itemStack);
        if (!hashMap.isEmpty()) {
            log.info("[GatesAndBridges] Couldn't fit all items in chest.");
            throw new InsufficientSpaceException();
        } else {
            return true;
        }
    }

    public boolean addMaterial(ItemStack itemStack) throws InsufficientSpaceException {
        HashMap<Integer, ItemStack> hashMap = chest.getInventory().addItem(itemStack);
        if (!hashMap.isEmpty()) {
            log.info("[GatesAndBridges] Couldn't fit all items in chest.");
            throw new InsufficientSpaceException();
        } else {
            return true;
        }
    }
}
