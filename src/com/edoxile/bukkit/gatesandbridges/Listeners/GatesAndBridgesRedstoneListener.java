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
import org.bukkit.util.config.Configuration;

import java.util.logging.Logger;

public class GatesAndBridgesRedstoneListener extends BlockListener {
    private final static Logger log = Logger.getLogger("Minecraft");
    private Configuration config = null;

    public GatesAndBridgesRedstoneListener(Configuration c) {
        config = c;
    }

    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        Block tempBlock = null;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    tempBlock = block.getRelative(dx, dy, dz);
                    if (tempBlock.getType() == Material.SIGN_POST || tempBlock.getType() == Material.WALL_SIGN) {
                        BlockState state = tempBlock.getState();
                        if (state instanceof Sign) {
                            Sign s = (Sign) state;
                            if (s.getLine(1).equals("[Bridge]") || s.getLine(1).equals("[Gate]")) {
                                GatesAndBridgesSign sign = new GatesAndBridgesSign(s, null, config);
                                if (sign.getMechanicsType() == MechanicsType.GATE) {
                                    if (event.getNewCurrent() == event.getOldCurrent())
                                        return;
                                    Gate gate = sign.gateFactory();
                                    if (!gate.isValidGate())
                                        return;
                                    if (event.getNewCurrent() == 0 && gate.isClosed()) {
                                        gate.openGate();
                                    } else if (!gate.isClosed() && event.getNewCurrent() > 0) {
                                        gate.closeGate();
                                    } else {
                                        return;
                                    }
                                } else if (sign.getMechanicsType() == MechanicsType.BRIDGE) {
                                    if (event.getNewCurrent() == event.getOldCurrent())
                                        return;
                                    Bridge bridge = sign.bridgeFactory();
                                    if (!bridge.isValidBridge())
                                        return;
                                    if (event.getNewCurrent() == 0 && bridge.isClosed()) {
                                        bridge.openBridge();
                                    } else if (!bridge.isClosed() && event.getNewCurrent() > 0) {
                                        bridge.closeBridge();
                                    } else {
                                        return;
                                    }
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
