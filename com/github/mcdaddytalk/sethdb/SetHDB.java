package com.github.mcdaddytalk.sethdb;


import com.github.mcdaddytalk.sethdb.handlers.ConfigHandler;
import com.github.mcdaddytalk.sethdb.handlers.ServerHandler;
import com.github.mcdaddytalk.sethdb.handlers.UpdateHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class SetHDB extends JavaPlugin implements Listener {

    private static SetHDB instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        ConfigHandler.getConfig(true).registerEvents();
        ServerHandler.getServer().runAsyncThread(async -> { UpdateHandler.getUpdater(true); });
        ServerHandler.getServer().logInfo("has been Enabled.");

    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        ServerHandler.getServer().logInfo("has been Disabled.");
    }

    public File getPlugin() {
        return this.getFile();
    }

    public static SetHDB getInstance() {
        return instance;
    }

}
