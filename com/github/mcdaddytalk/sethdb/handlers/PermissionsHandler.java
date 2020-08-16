package com.github.mcdaddytalk.sethdb.handlers;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class PermissionsHandler {

    private static PermissionsHandler permissions;

    /**
     * Gives all sethdb items to the specified player.
     *
     * @param sender - The entity that is having their permissions checked.
     * @param permission - The permission the sender is expected to have.
     * @return If the entity has the proper permission.
     */
    public boolean hasPermission(final CommandSender sender, final String permission) {
        if (sender.hasPermission(permission) || sender.hasPermission("sethdb.*") || sender.hasPermission("sethdb.all") || isDeveloper(sender) || (sender instanceof ConsoleCommandSender)) {
            return true;
        } else if (!ConfigHandler.getConfig(false).getFile("config.yml").getBoolean("Permissions.Commands-OP") && sender.isOp()) {
            if (permission.equalsIgnoreCase("sethdb.use") || permission.equalsIgnoreCase("sethdb.reload") || permission.equalsIgnoreCase("sethdb.updates")
                    || permission.equalsIgnoreCase("sethdb.permissions")
                    || permission.equalsIgnoreCase("sethdb.autoupdate")) {
                return true;
            }
        }
        return false;
    }

    /**
     * If Debugging Mode is enabled, the plugin developer will be allowed to execute ONLY this plugins commands for help and support purposes.
     *
     * @param sender - The entity executing the plugin command.
     * @return If the command sender is the developer of the plugin.
     */
    private boolean isDeveloper(final CommandSender sender) {
        if (ConfigHandler.getConfig(false).debugEnabled()) {
            if (sender instanceof Player) {
                try {
                    if (((Player)sender).getUniqueId().toString().equalsIgnoreCase("7eea9b7b-6226-4738-a8c2-e9d1d5191fbb")) { return true; }
                } catch (Exception e) { if (sender.getName().equalsIgnoreCase("MCDaddyTalk")) { return true; } }
            }
        }
        return false;
    }

    /**
     * Gets the instance of the PermissionsHandler.
     *
     * @return The PermissionsHandler instance.
     */
    public static PermissionsHandler getPermissions() {
        if (permissions == null) { permissions = new PermissionsHandler(); }
        return permissions;
    }
}
