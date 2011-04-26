package com.edoxile.bukkit.gatesandbridges;

import com.edoxile.bukkit.gatesandbridges.Exceptions.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.config.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

public class Bridge {
    private final static Logger log = Logger.getLogger("Minecraft");
    private ChestMapper chestMapper = new ChestMapper();
    private GatesAndBridgesSign sign = null;
    private Player player = null;
    private ItemStack bridgeMaterial = null;
    private Configuration config = null;

    private Block startBlock = null;
    private Block endBlock = null;
    private HashSet<Block> bridgeSet = new HashSet<Block>();

    public boolean mapBridge(Block block, BlockFace blockFace) {
        startBlock = block;
        try {
            endBlock = getEndBlock(block, blockFace);
            listBlocks(startBlock, endBlock, blockFace);
            return true;
        } catch (InvalidSizeException ex) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Bridge is too long! Maximum length: " + Integer.toString(config.getInt("bridge.max-length", 30)));
            }
            return false;
        } catch (AsymmetricalBridgeException ex) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Bridge needs to be made of the same materials on both sides!");
            }
            return false;
        } catch (InvalidBridgeMaterialException ex) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Bridge is not made of an allowed bridge material!");
            }
            return false;
        } catch (InvalidNotationException ex) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "There is a typo on your sign!");
            }
            return false;
        }
    }

    private Block getEndBlock(Block block, BlockFace blockFace) throws InvalidSizeException {
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
        } while (d <= config.getInt("bridge.max-length", 50));
        throw new InvalidSizeException();
    }

    /**
     * TODO Check for [Width x], set width;
     */
    private void listBlocks(Block s, Block e, BlockFace d) throws InvalidNotationException, InvalidBridgeMaterialException, AsymmetricalBridgeException {
        bridgeSet.clear();
        Block tempBlock;
        int dy = 0;
        if (allowedBridgeMaterial(s.getRelative(BlockFace.UP).getType())) {
            if (s.getRelative(BlockFace.UP).getType() != e.getRelative(BlockFace.UP).getType()) {
                throw new AsymmetricalBridgeException();
            }
            dy = 1;
            bridgeMaterial = new MaterialData(s.getRelative(BlockFace.UP).getType(), s.getRelative(BlockFace.UP).getData()).toItemStack();
        } else if (allowedBridgeMaterial(s.getRelative(BlockFace.DOWN).getType())) {
            if (s.getRelative(BlockFace.UP).getType() != e.getRelative(BlockFace.UP).getType()) {
                throw new AsymmetricalBridgeException();
            }
            dy = -1;
            bridgeMaterial = new MaterialData(s.getRelative(BlockFace.UP).getType(), s.getRelative(BlockFace.UP).getData()).toItemStack();
        } else {
            throw new InvalidBridgeMaterialException();
        }
        switch (d) {
            case WEST: {
                for (int dz = 1; dz < e.getLocation().getBlockZ() - s.getLocation().getBlockZ(); dz++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        tempBlock = s.getRelative(dx, dy, dz);
                        if (canPassThrough(tempBlock.getType()) || (tempBlock.getType() == bridgeMaterial.getType() && tempBlock.getData() == bridgeMaterial.getData().getData()))
                            bridgeSet.add(tempBlock);
                    }
                }
            }
            break;
            case EAST: {
                for (int dz = -1; dz > e.getLocation().getBlockZ() - s.getLocation().getBlockZ(); dz--) {
                    for (int dx = -1; dx <= 1; dx++) {
                        tempBlock = s.getRelative(dx, dy, dz);
                        if (canPassThrough(tempBlock.getType()) || (tempBlock.getType() == bridgeMaterial.getType() && tempBlock.getData() == bridgeMaterial.getData().getData()))
                            bridgeSet.add(tempBlock);
                    }
                }
            }
            break;
            case NORTH: {
                for (int dx = -1; dx > e.getLocation().getBlockX() - s.getLocation().getBlockX(); dx--) {
                    for (int dz = -1; dz <= 1; dz++) {
                        tempBlock = s.getRelative(dx, dy, dz);
                        if (canPassThrough(tempBlock.getType()) || (tempBlock.getType() == bridgeMaterial.getType() && tempBlock.getData() == bridgeMaterial.getData().getData()))
                            bridgeSet.add(tempBlock);
                    }
                }
            }
            break;
            case SOUTH: {
                for (int dx = 1; dx < e.getLocation().getBlockX() - s.getLocation().getBlockX(); dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        tempBlock = s.getRelative(dx, dy, dz);
                        if (canPassThrough(tempBlock.getType()) || (tempBlock.getType() == bridgeMaterial.getType() && tempBlock.getData() == bridgeMaterial.getData().getData()))
                            bridgeSet.add(tempBlock);
                    }
                }
            }
            break;
            default:
                log.info("[BridgesAndGates] Not a valid BlockFace: " + d.name());
                break;
        }
    }

    public Bridge(GatesAndBridgesSign s, Player p, Configuration c) {
        sign = s;
        player = p;
        config = c;
    }

    public boolean isValidBridge() {
        if (mapBridge(sign.getBlock(), sign.getSignBack())) {
            try {
                if (chestMapper.mapChest(getEndBlock(sign.getBlock(), sign.getSignBack()))) {
                    return true;
                } else {
                    if (chestMapper.mapChest(sign.getBlock())) {
                        return true;
                    } else {
                        if (player != null) {
                            player.sendMessage(ChatColor.RED + "No chest found near sign!");
                        }
                    }
                }
            } catch (InvalidSizeException ex) {
                player.sendMessage(ChatColor.RED + "Bridge is too long or too wide (or both)!");
            }
        }
        return false;
    }

    public boolean toggleBridge() {
        if (isClosed()) {
            //Open
            return openBridge();
        } else {
            //Close
            return closeBridge();
        }
    }

    public boolean openBridge() {
        int blocks = 0;
        for (Block b : bridgeSet) {
            Block tempBlock = b;
            tempBlock.setType(Material.AIR);
            blocks++;
        }
        try {
            bridgeMaterial.setAmount(blocks);
            chestMapper.addMaterial(bridgeMaterial);
            return true;
        } catch (InsufficientSpaceException ex) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Not enough space in chest! Items lost!");
            }
            log.info("[Gates and Bridges] Not enough space in chest. Bridge position: {x=" + Integer.toString(sign.getBlock().getLocation().getBlockX()) + "; z=" + Integer.toString(sign.getBlock().getLocation().getBlockZ()));
            for (Block b : bridgeSet) {
                Block tempBlock = b;
                tempBlock.setType(bridgeMaterial.getType());
                tempBlock.setData(bridgeMaterial.getData().getData());
            }
            return false;
        }
    }

    public boolean closeBridge() {
        int blocks = 0;
        for (Block b : bridgeSet) {
            Block tempBlock = b;
                tempBlock.setType(bridgeMaterial.getType());
                tempBlock.setData(bridgeMaterial.getData().getData());
            blocks++;
        }
        try {
            bridgeMaterial.setAmount(blocks);
            chestMapper.removeMaterial(bridgeMaterial);
            return true;
        } catch (InsufficientMaterialsException ex) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Not enough materials in chest! Bridge not closed!");
            }
            for (Block b : bridgeSet) {
                Block tempBlock = b;
                tempBlock.setType(Material.AIR);
            }
            return false;
        }
    }

    public boolean isClosed() {
        for (Block b : bridgeSet) {
            return b.getType() == bridgeMaterial.getType();
        }
        log.info("[GatesAndBridges] blockSet empty!");
        return false;
    }

    private boolean canPassThrough(Material m) {
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

    private boolean allowedBridgeMaterial(Material m) {
        String[] materials = config.getString("bridge.materials").split(",");
        for (String s : materials) {
            if (m.toString().equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
}
