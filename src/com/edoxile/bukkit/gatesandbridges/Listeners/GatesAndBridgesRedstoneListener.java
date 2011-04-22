package com.edoxile.bukkit.gatesandbridges.Listeners;

import com.edoxile.bukkit.gatesandbridges.Bridge;
import com.edoxile.bukkit.gatesandbridges.Gate;
import com.edoxile.bukkit.gatesandbridges.GatesAndBridgesSign;
import com.edoxile.bukkit.gatesandbridges.MechanicsType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 * Date: 19-4-11
 * Time: 8:45
 * To change this template use File | Settings | File Templates.
 */
public class GatesAndBridgesRedstoneListener extends BlockListener {
    private final static Logger log = Logger.getLogger("Minecraft");
    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        Block tempBlock = null;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    tempBlock = block.getRelative(dx, dy, dz);
                    if (tempBlock.getType() == Material.SIGN_POST || tempBlock.getType() == Material.WALL_SIGN) {
                        //Check for valid type ([Bridge] or [Gate])
                        BlockState state = tempBlock.getState();
                        if (state instanceof Sign) {
                            Sign s = (Sign) state;
                            if (s.getLine(1).equals("[Bridge]") || s.getLine(1).equals("[Gate]")) {
                                GatesAndBridgesSign sign = new GatesAndBridgesSign(s);
                                if (sign.getMechanicsType() == MechanicsType.GATE) {
                                    Gate gate = sign.gateFactory();
                                    if (!gate.isValidGate())
                                        return;
                                    gate.toggleGate();
                                } else if (sign.getMechanicsType() == MechanicsType.BRIDGE) {
                                    log.info("Toggling Bridge...");
                                    Bridge bridge = sign.bridgeFactory();
                                    if (!bridge.isValidBridge())
                                        return;
                                    bridge.toggleBridge();
                                } else {
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
