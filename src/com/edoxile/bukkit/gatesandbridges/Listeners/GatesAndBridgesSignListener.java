package com.edoxile.bukkit.gatesandbridges.Listeners;

import com.edoxile.bukkit.gatesandbridges.GatesAndBridges;
import com.edoxile.bukkit.gatesandbridges.GatesAndBridgesSign;
import com.edoxile.bukkit.gatesandbridges.MechanicsType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 * Date: 18-4-11
 * Time: 21:51
 * To change this template use File | Settings | File Templates.
 */
public class GatesAndBridgesSignListener extends BlockListener {

    private Configuration c;
    private GatesAndBridges plugin;

    public GatesAndBridgesSignListener(Configuration config, GatesAndBridges instance) {
        this.c = config;
        this.plugin = instance;
    }

    public void onSignChange(SignChangeEvent event) {
        if(event.isCancelled())
            return;

        MechanicsType mt = GatesAndBridges.getMechanicsType(event.getLine(1));

        if(mt!=null) {
            if(!plugin.checkPermissions(event.getPlayer(), mt.toString().toLowerCase()+".create")) {
                if(event.getBlock().getType() == Material.WALL_SIGN ||
                   event.getBlock().getType() == Material.SIGN_POST ) { // Is it still a sign?
                    event.getBlock().setTypeId(0);
                    event.getPlayer().sendMessage(ChatColor.RED+"You do not have permission to create a bridge or gate!");
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.SIGN, 1));
                    event.setCancelled(true);
                }
            }
        }
    }
}
