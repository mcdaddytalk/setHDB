package com.github.mcdaddytalk.sethdb.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import com.github.mcdaddytalk.sethdb.utils.DependAPI;
import com.github.mcdaddytalk.sethdb.utils.LanguageAPI;
import com.github.mcdaddytalk.sethdb.utils.Reflection;

public class PlayerHandler {

    private static PlayerHandler player;

    /**
     * Checks if the player is currently in creative mode.
     *
     * @param player - The player to be checked.
     * @return If the player is currently in creative mode.
     */
    public boolean isCreativeMode(final Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the player is currently in adventure mode.
     *
     * @param player - The player to be checked.
     * @return If the player is currently in adventure mode.
     */
    public boolean isAdventureMode(final Player player) {
        if (player.getGameMode() == GameMode.ADVENTURE) {
            return true;
        }
        return false;
    }

    public World getPlayerWorld(final CommandSender sender) {
        if (sender instanceof Player) {
            try {
                return ((Player)sender).getWorld();
            } catch (Exception e) {

            }
        }
        return null;
    }

    /**
     * Gets the current skull owner of the specified item.
     *
     * @param item - The item to have its skull owner fetched.
     * @return The ItemStacks current skull owner.
     */
    /*public String getSkullOwner(final ItemStack item) {
        if (ServerHandler.getServer().hasSpecificUpdate("1_12") && item != null && item.hasItemMeta() && ItemHandler.getItem().isSkull(item.getType())
                && ((SkullMeta) item.getItemMeta()).hasOwner() && ItemHandler.getItem().usesOwningPlayer() != false) {
            String owner =  ((SkullMeta) item.getItemMeta()).getOwningPlayer().getName();
            if (owner != null) { return owner; }
        } else if (item != null && item.hasItemMeta()
                && ItemHandler.getItem().isSkull(item.getType())
                && ((SkullMeta) item.getItemMeta()).hasOwner()) {
            String owner = LegacyAPI.getLegacy().getSkullOwner(((SkullMeta) item.getItemMeta()));
            if (owner != null) { return owner; }
        }
        return "NULL";
    }
    */

    /**
     * Gets the Player instance from their String name.
     *
     * @param playerName - The player name to be transformed.
     * @return The fetched Player instance.
     */
    public Player getPlayerString(final String playerName) {
        Player args = null;
        try { args = Bukkit.getPlayer(UUID.fromString(playerName)); } catch (Exception e) {}
        return args;
    }

    /**
     * Gets the UUID of the Player.
     * If the UUID does not exist it will fetch their String name.
     *
     * @param player - The player to have their UUID fetched.
     * @return The UUID of the player or if not found, their String name.
     */
    public String getPlayerID(final Player player) {
        if (player != null && player.getUniqueId() != null) {
            return player.getUniqueId().toString();
        } else if (player != null) {
            return player.getName();
        }
        return "";
    }

    /**
     * Gets the UUID of the OfflinePlayer.
     * If the UUID does not exist it will fetch their String name.
     *
     * @param player - The OfflinePlayer instance to have their UUID fetched.
     * @return The UUID of the player or if not found, their String name.
     */
    public String getOfflinePlayerID(final OfflinePlayer player) {
        if (player != null && player.getUniqueId() != null) {
            return player.getUniqueId().toString();
        } else if (player != null) {
            return player.getName();
        }
        return "";
    }


    /**
     * Executes an input of methods for the currently online players.
     *
     * @param input - The methods to be executed.
     */
    public void forOnlinePlayers(final Consumer<Player> input) {
        try {
            /** New method for getting the current online players.
             * This is for MC 1.12+
             */
            if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
                for (Object objPlayer: ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]))) {
                    input.accept(((Player) objPlayer));
                }
            }
            /** New old for getting the current online players.
             * This is for MC versions below 1.12.
             *
             * @deprecated Legacy version of getting online players.
             */
            else {
                for (Player player: ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]))) {
                    input.accept(player);
                }
            }
        } catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); }
    }

    /**
     * Gets the instance of the PlayerHandler.
     *
     * @return The PlayerHandler instance.
     */
    public static PlayerHandler getPlayer() {
        if (player == null) { player = new PlayerHandler(); }
        return player;
    }
}
