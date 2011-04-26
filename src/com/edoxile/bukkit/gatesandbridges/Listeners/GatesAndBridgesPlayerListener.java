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
import org.bukkit.util.config.Configuration;

public class GatesAndBridgesPlayerListener extends PlayerListener {

    private Configuration config = null;

    public GatesAndBridgesPlayerListener(Configuration c){
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
                default:
                    return;
            }

        }
    }
}
