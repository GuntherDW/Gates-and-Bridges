package com.edoxile.bukkit.gatesandbridges.Listeners;

import com.edoxile.bukkit.gatesandbridges.Bridge;
import com.edoxile.bukkit.gatesandbridges.Gate;
import com.edoxile.bukkit.gatesandbridges.GatesAndBridgesSign;
import com.edoxile.bukkit.gatesandbridges.MechanicsType;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 * Date: 18-4-11
 * Time: 21:50
 * To change this template use File | Settings | File Templates.
 */
public class GatesAndBridgesPlayerListener extends PlayerListener {
    public static Player player = null;

    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            switch (event.getClickedBlock().getType()) {
                case SIGN_POST:
                case WALL_SIGN: {
                    Sign s = null;
                    BlockState state = event.getClickedBlock().getState();
                    if (state instanceof Sign) {
                        s = (Sign) state;
                    } else {
                        return;
                    }
                    player = event.getPlayer();
                    GatesAndBridgesSign sign = new GatesAndBridgesSign(s);
                    if (sign.getMechanicsType() == MechanicsType.GATE) {
                        Gate gate = sign.gateFactory();
                        if (!gate.isValidGate()) {
                            player = null;
                            return;
                        }
                        gate.toggleGate();
                        player = null;
                    } else if (sign.getMechanicsType() == MechanicsType.BRIDGE) {
                        Bridge bridge = sign.bridgeFactory();
                        if (!bridge.isValidBridge()) {
                            player = null;
                            return;
                        }
                        bridge.toggleBridge();
                        player = null;
                    } else {
                        player = null;
                        return;
                    }
                }
                break;
                default:
                    player = null;
                    return;
            }

        }
    }
}
