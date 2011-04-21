package com.edoxile.bukkit.gatesandbridges;

import com.edoxile.bukkit.gatesandbridges.Listeners.GatesAndBridgesPlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 * Date: 19-4-11
 * Time: 9:07
 * To change this template use File | Settings | File Templates.
 */
public class GatesAndBridgesSign {
    Sign sign = null;

    public GatesAndBridgesSign(Sign s) {
        sign = s;
    }

    private boolean isBridgeOrGateSign() {
        String line = sign.getLine(1);
        return (line.equals("[Gate]") || line.equals("[Bridge]"));
    }

    public MechanicsType getMechanicsType() {
        String line = sign.getLine(1);
        if (line.equals("[Gate]")) {
            return MechanicsType.GATE;
        } else if (line.equals("[Bridge]")) {
            return MechanicsType.BRIDGE;
        } else {
            return null;
        }
    }

    public Gate gateFactory() {
        if (getMechanicsType() == MechanicsType.GATE)
            return new Gate(this);
        else
            return null;
    }

    public Bridge bridgeFactory() {
        if (getMechanicsType() == MechanicsType.BRIDGE)
            return new Bridge(this);
        else
            return null;
    }

    public BlockFace getSignBack() {
        if (sign.getType() == Material.WALL_SIGN) {
            switch (sign.getData().getData()) {
                case 0x2:
                    return BlockFace.WEST;
                case 0x3:
                    return BlockFace.EAST;
                case 0x4:
                    return BlockFace.SOUTH;
                case 0x5:
                    return BlockFace.NORTH;
                default:
                    return null;
            }
        } else if (sign.getType() == Material.SIGN_POST) {
            switch (sign.getData().getData()){
                case 0:
                    return BlockFace.EAST;
                case 4:
                    return BlockFace.SOUTH;
                case 8:
                    return BlockFace.WEST;
                case 12:
                    return BlockFace.NORTH;
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    public BlockFace getSignFront() {
        if (sign.getType() == Material.WALL_SIGN) {
            switch (sign.getData().getData()) {
                case 0x2:
                    return BlockFace.EAST;
                case 0x3:
                    return BlockFace.WEST;
                case 0x4:
                    return BlockFace.NORTH;
                case 0x5:
                    return BlockFace.SOUTH;
                default:
                    return null;
            }
        } else if (sign.getType() == Material.SIGN_POST) {
            switch (sign.getData().getData()){
                case 0:
                    return BlockFace.WEST;
                case 4:
                    return BlockFace.NORTH;
                case 8:
                    return BlockFace.EAST;
                case 12:
                    return BlockFace.SOUTH;
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    public boolean isPowered(){
        return sign.getBlock().isBlockPowered() || sign.getBlock().isBlockIndirectlyPowered();
    }

    public Block getBackBlock(){
        if(getSignBack() == null){
            GatesAndBridgesPlayerListener.player.sendMessage(ChatColor.YELLOW + "Sign is placed incorrect. Direction should be: North, East, South or West.");
        }
        return sign.getBlock().getRelative(getSignBack());
    }

    public Block getFrontBlock(){
        if(getSignFront() == null){
            GatesAndBridgesPlayerListener.player.sendMessage(ChatColor.YELLOW + "Sign is placed incorrect. Direction should be: North, East, South or West.");
        }
        return sign.getBlock().getRelative(getSignFront());
    }

    public Block getBlock(){
        return sign.getBlock();
    }
}
