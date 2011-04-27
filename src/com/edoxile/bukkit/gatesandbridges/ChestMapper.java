package com.edoxile.bukkit.gatesandbridges;

import com.edoxile.bukkit.gatesandbridges.Exceptions.InsufficientMaterialsException;
import com.edoxile.bukkit.gatesandbridges.Exceptions.InsufficientSpaceException;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    public boolean removeMaterial(ItemStack itemStack) throws InsufficientMaterialsException {
        if (safeRemoveItems(itemStack)) {
            return true;
        } else {
            throw new InsufficientMaterialsException();
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
            log.info("[Gates and Bridges] Couldn't fit all items in chest.");
            throw new InsufficientSpaceException();
        } else {
            return true;
        }
    }

    public boolean addMaterial(ItemStack itemStack) throws InsufficientSpaceException {
        HashMap<Integer, ItemStack> hashMap = chest.getInventory().addItem(itemStack);
        if (!hashMap.isEmpty()) {
            log.info("[Gates and Bridges] Couldn't fit all items in chest.");
            throw new InsufficientSpaceException();
        } else {
            return true;
        }
    }

    private boolean safeRemoveItems(ItemStack itemStack) {
        if (itemStack.getData() != null) {
            List<ItemStack> stacks = Arrays.asList(chest.getInventory().getContents());
            ItemStack tempStack;
            for (int i = 0; i < stacks.size(); i++) {
                tempStack = stacks.get(i);
                if (tempStack == null)
                    continue;
                if (tempStack.getType() == itemStack.getType() && tempStack.getData().getData() == itemStack.getData().getData()) {
                    if (tempStack.getAmount() > itemStack.getAmount()) {
                        tempStack.setAmount(tempStack.getAmount() - itemStack.getAmount());
                        itemStack.setAmount(0);
                        stacks.set(i, tempStack);
                        break;
                    } else if (tempStack.getAmount() < itemStack.getAmount()) {
                        stacks.remove(i);
                        itemStack.setAmount(itemStack.getAmount() - tempStack.getAmount());
                        continue;
                    } else {
                        stacks.remove(i);
                        itemStack.setAmount(0);
                        break;
                    }
                }
            }
            if (itemStack.getAmount() > 0) {
                return false;
            } else {
                chest.getInventory().setContents((ItemStack[]) stacks.toArray());
                return true;
            }
        } else {
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
                return false;
            } else {
                return true;
            }
        }
    }
}
