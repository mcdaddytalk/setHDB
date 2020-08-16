package com.github.mcdaddytalk.sethdb.utils;

import org.bukkit.Bukkit;

import com.mojang.authlib.properties.Property;

import com.github.mcdaddytalk.sethdb.handlers.ServerHandler;

public class DependAPI {

    private boolean headDatabase = false;
    private boolean placeHolderAPI = false;

    private static DependAPI depends;

    /**
     * Creates a new DependAPI instance.
     *
     */
    public DependAPI() {
        this.setDatabaseStatus(Bukkit.getServer().getPluginManager().getPlugin("HeadDatabase") != null);
        this.setPlaceHolderStatus(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null);
    }

    /**
     * Checks if HeadDatabase is Enabled.
     *
     * @return If HeadDatabase is Enabled.
     */
    public boolean databaseEnabled() {
        return this.headDatabase;
    }

    /**
     * Checks if PlaceHolderAPI is Enabled.
     *
     * @return If PlaceHolderAPI is Enabled.
     */
    public boolean placeHolderEnabled() {
        return this.placeHolderAPI;
    }


    /**
     * Sets the status of PlaceHolderAPI.
     *
     * @param bool - If PlaceHolderAPI is enabled.
     */
    public void setPlaceHolderStatus(final boolean bool) {
        this.placeHolderAPI = bool;
    }


    /**
     * Sets the status of HeadDatabase.
     *
     * @param bool - If HeadDatabase is enabled.
     */
    public void setDatabaseStatus(final boolean bool) {
        this.headDatabase = bool;
    }

    /**
     * Sends a logging message of the found and enabled soft dependencies.
     *
     */
    public void sendUtilityDepends() {
        String enabledPlugins = (this.databaseEnabled() ? "HeadDatabase, " : "") + (this.placeHolderEnabled() ? "PlaceholderAPI, " : "");
        if (!enabledPlugins.isEmpty()) { ServerHandler.getServer().logInfo("Hooked into { " + enabledPlugins + "}"); }
    }

    /**
     * Gets the instance of the DependAPI.
     *
     * @param regen - If the DependAPI should have a new instance created.
     * @return The DependAPI instance.
     */
    public static DependAPI getDepends(final boolean regen) {
        if (depends == null || regen) { depends = new DependAPI(); }
        return depends;
    }
}
