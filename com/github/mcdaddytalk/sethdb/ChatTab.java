package com.github.mcdaddytalk.sethdb;

import java.util.*;

import com.github.mcdaddytalk.sethdb.handlers.PlayerHandler;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import com.github.mcdaddytalk.sethdb.handlers.PermissionsHandler;
//import com.github.mcdaddytalk.sethdb.utils.sqlite.SQLite;

public class ChatTab implements TabCompleter {

    /**
     * Called when a Player tries to TabComplete.
     * @param sender - Source of the command.
     * @param command - Command which was executed.
     * @param label - Alias of the command which was used.
     * @param args - Passed command arguments.
     * @return The String List of TabComplete commands.
     */
    @Override
    public List < String > onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        final List < String > completions = new ArrayList < > ();
        final List < String > commands = new ArrayList < > ();
        if (args.length == 2 && args[0].equalsIgnoreCase("help") && PermissionsHandler.getPermissions().hasPermission(sender, "sethdb.use")) {
            commands.add("2");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("permissions") && PermissionsHandler.getPermissions().hasPermission(sender, "sethdb.permissions")) {
            commands.add("2");
        } else if (args.length == 1) {
            if (PermissionsHandler.getPermissions().hasPermission(sender, "sethdb.use")) { 		 	commands.addAll(Arrays.asList("help","set")); }
            if (PermissionsHandler.getPermissions().hasPermission(sender, "sethdb.permissions")) { 	commands.add("permissions"); }
            if (PermissionsHandler.getPermissions().hasPermission(sender, "sethdb.reload")) { 		commands.add("reload"); }
            if (PermissionsHandler.getPermissions().hasPermission(sender, "sethdb.updates")) { 		commands.add("updates"); }
            if (PermissionsHandler.getPermissions().hasPermission(sender, "sethdb.autoupdate")) { 	commands.add("autoupdate"); }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set") && PermissionsHandler.getPermissions().hasPermission(sender,"sethdb.use")) {
            commands.add("<hdbid>");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set") && PermissionsHandler.getPermissions().hasPermission(sender,"sethdb.use")) {
            PlayerHandler.getPlayer().forOnlinePlayers(player -> {
                commands.add(String.valueOf((int) player.getTargetBlock((Set<Material>) null, 3).getX()));
            });
        } else if (args.length == 4 && args[0].equalsIgnoreCase("set") && PermissionsHandler.getPermissions().hasPermission(sender,"sethdb.use")) {
            PlayerHandler.getPlayer().forOnlinePlayers(player -> {
                commands.add(String.valueOf((int) (player.getTargetBlock((Set<Material>) null, 3)).getY() + 1));
            });
        } else if (args.length == 5 && args[0].equalsIgnoreCase("set") && PermissionsHandler.getPermissions().hasPermission(sender,"sethdb.use")) {
            PlayerHandler.getPlayer().forOnlinePlayers(player -> {
                commands.add(String.valueOf((int) player.getTargetBlock((Set<Material>) null, 3).getZ()));
            });
        } else if (args.length == 6 && args[0].equalsIgnoreCase("set") && PermissionsHandler.getPermissions().hasPermission(sender,"sethdb.use")) {
            commands.addAll(Arrays.asList("0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"));
        }
        StringUtil.copyPartialMatches(args[(args.length - 1)], commands, completions);
        Collections.sort(completions);
        return completions;
    }
}
