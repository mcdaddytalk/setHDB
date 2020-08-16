package com.github.mcdaddytalk.sethdb.listeners;

import com.github.mcdaddytalk.sethdb.SetHDB;
import com.github.mcdaddytalk.sethdb.handlers.ServerHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;


public class BlockPlacementListener implements Listener {

    public SetHDB plugin;

    public BlockPlacementListener(SetHDB instance) {
        plugin = instance;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material mat = block.getType();

        player.sendMessage("You placed a block with ID : " + mat);
    }
}
