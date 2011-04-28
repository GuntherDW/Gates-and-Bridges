package com.edoxile.bukkit.gatesandbridges.Listeners;

import com.edoxile.bukkit.gatesandbridges.Bridge;
import com.edoxile.bukkit.gatesandbridges.Gate;
import com.edoxile.bukkit.gatesandbridges.GatesAndBridgesSign;
import com.edoxile.bukkit.gatesandbridges.MechanicsType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.util.config.Configuration;

public class GatesAndBridgesPlayerListener extends PlayerListener {

    private Configuration config = null;

    public GatesAndBridgesPlayerListener(Configuration c) {
        config = c;
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            switch (event.getClickedBlock().getType()) {
                case SIGN_POST:
                case WALL_SIGN: {
                    Sign s;
                    BlockState state = event.getClickedBlock().getState();
                    if (state instanceof Sign) {
                        s = (Sign) state;
                    } else {
                        return;
                    }
                    GatesAndBridgesSign sign = new GatesAndBridgesSign(s, event.getPlayer(), config);
                    if (sign.getMechanicsType() == MechanicsType.GATE) {
                        Gate gate = sign.gateFactory();
                        if (!gate.isValidGate()) {
                            return;
                        }
                        gate.toggleGate();
                    } else if (sign.getMechanicsType() == MechanicsType.BRIDGE) {
                        Bridge bridge = sign.bridgeFactory();
                        if (!bridge.isValidBridge()) {
                            return;
                        }
                        bridge.toggleBridge();
                    } else {
                        return;
                    }
                }
                break;
                default: {
                    Block tempBlock = event.getClickedBlock();
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                if (dx == 0 && dy == 0 && dz == 0)
                                    continue;
                                if (tempBlock.getRelative(dx, dy, dz).getType() == Material.SIGN_POST || tempBlock.getRelative(dx, dy, dz).getType() == Material.WALL_SIGN) {
                                    BlockState state = tempBlock.getRelative(dx, dy, dz).getState();
                                    if (state instanceof Sign) {
                                        Sign sign = (Sign) state;
                                        if (sign.getLine(1).equalsIgnoreCase("[X]")) {
                                            for (int sdx = -1; sdx <= 1; sdx++) {
                                                for (int sdy = -1; sdy <= 1; sdy++) {
                                                    for (int sdz = -1; sdz <= 1; sdz++) {
                                                        if(sdx == 0 && sdy == 0 && sdz == 0)
                                                            continue;
                                                        if(sign.getBlock().getRelative(sdx,sdy,sdz).getType() == Material.LEVER){
                                                            Block lever = sign.getBlock().getRelative(sdx,sdy,sdz);
                                                            lever.setData((byte)(lever.getData() ^ 0x8));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
