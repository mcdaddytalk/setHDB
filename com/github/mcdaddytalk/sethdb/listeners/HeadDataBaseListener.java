package com.github.mcdaddytalk.sethdb.listeners;

import com.github.mcdaddytalk.sethdb.handlers.ServerHandler;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class HeadDataBaseListener implements Listener {

    @EventHandler
    public void onDatabaseLoad(DatabaseLoadEvent e){
        HeadDatabaseAPI api = new HeadDatabaseAPI();
        try{
            ItemStack item = api.getItemHead("7129");
            ServerHandler.getServer().logInfo( api.getItemID(item) );
        }
        catch(NullPointerException npe){
            ServerHandler.getServer().logInfo( "could not find the head you were looking for" );
        }
    }
}
