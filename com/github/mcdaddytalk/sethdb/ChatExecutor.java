package com.github.mcdaddytalk.sethdb;

import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.mcdaddytalk.sethdb.handlers.*;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.arcaniax.hdb.enums.CategoryEnum;
import me.arcaniax.hdb.object.head.Head;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import com.github.mcdaddytalk.sethdb.SetHDB;
import com.github.mcdaddytalk.sethdb.utils.LanguageAPI;
import com.github.mcdaddytalk.sethdb.utils.Utils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
//import com.github.mcdaddytalk.sethdb.utils.sqlite.SQLite;
//import com.github.mcdaddytalk.sethdb.utils.sqlite.SQDrivers;

public class ChatExecutor implements CommandExecutor {

    HeadDatabaseAPI api = new HeadDatabaseAPI();

    /**
     * Called when the CommandSender executes a command.
     * @param sender - Source of the command.
     * @param command - Command which was executed.
     * @param label - Alias of the command which was used.
     * @param args - Passed command arguments.
     * @return true if the command is valid.
     */
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (Execute.DEFAULT.accept(sender, args, 0)) {
            LanguageAPI.getLang(false).dispatchMessage(sender, "&aSetHDB v" + SetHDB.getInstance().getDescription().getVersion() + "&e by MCDaddyTalk");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/SetHDB Help &afor the help menu.");
        }  else if (Execute.HELP.accept(sender, args, 1)) {
            LanguageAPI.getLang(false).dispatchMessage(sender, "");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e SetHDB &a&l]&a&l&m-----------------[");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&aSetHDB v" + SetHDB.getInstance().getDescription().getVersion() + "&e by MCDaddyTalk");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/SetHDB Help &7- &eThis help menu.");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/SetHDB Set <HDB ID> <x> <y> <z> [rotation]&7- &ePlace head at location.");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/SetHDB Reload &7- &eReloads the .yml files.");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/SetHDB Help 2 &afor the next page.");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 1/2 &a&l]&a&l&m---------------[");
            LanguageAPI.getLang(false).dispatchMessage(sender, "");
        } else if (Execute.HELP.accept(sender, args, 2)) {
            LanguageAPI.getLang(false).dispatchMessage(sender, "");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e SetHDB &a&l]&a&l&m-----------------[");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/SetHDB Updates &7- &eChecks for plugin updates.");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/SetHDB AutoUpdate &7- &eUpdate SetHDB to latest version.");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/SetHDB Permissions &7- &eLists the permissions you have.");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&aFound a bug? Report it @");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&ahttps://github.com/MCDaddyTalk/SetHDB/issues");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 2/2 &a&l]&a&l&m---------------[");
            LanguageAPI.getLang(false).dispatchMessage(sender, "");
        } else if (Execute.HELP.accept(sender, args)) {
            LanguageAPI.getLang(false).dispatchMessage(sender, "");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e SetHDB &a&l]&a&l&m-----------------[");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&aSetHDB v" + SetHDB.getInstance().getDescription().getVersion() + "&e by MCDaddyTalk");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/SetHDB Help &7- &eThis help menu.");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/SetHDB Set <HDB ID> <x> <y> <z> [rotation]&7- &ePlace head at location.");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l/SetHDB Reload &7- &eReloads the .yml files.");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/SetHDB Help 2 &afor the next page.");
            LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]----------------&a&l[&e Help Menu 1/2 &a&l]&a&l&m---------------[");
            LanguageAPI.getLang(false).dispatchMessage(sender, "");
        } else if (Execute.RELOAD.accept(sender, args)) {
            //SQLite.getLite(false).executeLaterStatements();
            ConfigHandler.getConfig(true);
            LanguageAPI.getLang(false).sendLangMessage("Commands.Default.configReload", sender);
        } else if (Execute.PERMISSIONS.accept(sender, args)) {
            this.permissions(sender, 1);
        } else if (Execute.PERMISSIONS.accept(sender, args, 1)) {
            this.permissions(sender, 1);
        } else if (Execute.UPDATE.accept(sender, args)) {
            LanguageAPI.getLang(false).sendLangMessage("Commands.Updates.checking", sender);
            ServerHandler.getServer().runAsyncThread(async -> { UpdateHandler.getUpdater(false).checkUpdates(sender, false); });
        } else if (Execute.AUTOUPDATE.accept(sender, args)) {
            LanguageAPI.getLang(false).sendLangMessage("Commands.Updates.forcing", sender);
            ServerHandler.getServer().runAsyncThread(async -> { UpdateHandler.getUpdater(false).forceUpdates(sender); });
        } else if (Execute.SET.accept(sender, args)) {
            LanguageAPI.getLang(false).sendLangMessage("Commands.Set.executing", sender);
            String hdbID = args[1];
            String xCoord = args[2];
            String yCoord = args[3];
            String zCoord = args[4];
            String rotation = "";
            ItemStack item = null;
            String texture = null;
            if(args.length > 5) {
                rotation = args[5];
            }
            try{
                item = api.getItemHead(hdbID);

                ItemMeta meta = item.getItemMeta();
                String mat = item.getType().name();
                if (meta instanceof SkullMeta) {
                    texture = SkullHandler.getTexture(item);
                    mat = texture != null ? "<skull:" + SkullHandler.getTexture(item) + ">" : mat;
                }
                ServerHandler.getServer().logDev("Attempt to find head [" + hdbID + "::" + api.isHead(hdbID) + "] returned:  " + api.getItemID(item) );
            }
            catch(NullPointerException npe){
                LanguageAPI.getLang(false).sendLangMessage("Commands.Set.headSearchFailed0", sender);
                LanguageAPI.getLang(false).sendLangMessage("Commands.Set.headSearchFailed1", sender);
                ServerHandler.getServer().logInfo( "could not find the head you were looking for using ID[" + hdbID + ")" );
            }
            try{
                if (sender instanceof Player && item != null) {
                    Location originLocation = new Location(PlayerHandler.getPlayer().getPlayerWorld(sender), Double.valueOf(xCoord), Double.valueOf(yCoord), Double.valueOf(zCoord));
                    Block targetBlock= originLocation.getBlock();
                    if(!(targetBlock.getState() instanceof Skull)) {
                        targetBlock.setType(Material.PLAYER_HEAD);
                    }

                    SkullHandler.changeSkin(targetBlock, texture, Integer.valueOf(rotation));
                }
            }
            catch(Exception e) {
                LanguageAPI.getLang(false).sendLangMessage("Commands.Set.cannotPlaceHead", sender);
                ServerHandler.getServer().logWarn("Could not place head:  " + e);
            }
        } else if (this.matchExecutor(args) == null) {
            LanguageAPI.getLang(false).sendLangMessage("Commands.Default.unknownCommand", sender);
        } else if (!this.matchExecutor(args).playerRequired(sender, args)) {
            LanguageAPI.getLang(false).sendLangMessage("Commands.Default.notPlayer", sender);
            Execute executor = this.matchExecutor(args);

        } else if (!this.matchExecutor(args).hasSyntax(args, 0)) {
            Execute executor = this.matchExecutor(args);

        } else if (!this.matchExecutor(args).hasPermission(sender, args)) {
            LanguageAPI.getLang(false).sendLangMessage("Commands.Default.noPermission", sender);
        }
        return true;
    }

    /**
     * Called when the CommandSender fails to execute a command.
     * @param args - Passed command arguments.
     * @return The found Executor.
     *
     */
    private Execute matchExecutor(final String[] args) {
        for (Execute command : Execute.values()) {
            if (command.acceptArgs(args)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Called when the CommandSender executes the Permisisons command.
     * @param sender - Source of the command.
     * @param page - The page number to be displayed.
     *
     */
    private void permissions(final CommandSender sender, final int page) {
        LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]------------------&a&l[&e SetHDB &a&l]&a&l&m-----------------[");
        int maxPage = 1;
        if (page == 1) {
            LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "SetHDB.*") ? "&a[\u2714]" : "&c[\u2718]") + " SetHDB.*");
            LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "SetHDB.all") ? "&a[\u2714]" : "&c[\u2718]") + " SetHDB.All");
            LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "SetHDB.use") ? "&a[\u2714]" : "&c[\u2718]") + " SetHDB.Use");
            LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "SetHDB.reload") ? "&a[\u2714]" : "&c[\u2718]") + " SetHDB.Reload");
            LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "SetHDB.updates") ? "&a[\u2714]" : "&c[\u2718]") + " SetHDB.Updates");
            LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "SetHDB.autoupdate") ? "&a[\u2714]" : "&c[\u2718]") + " SetHDB.AutoUpdate");
            LanguageAPI.getLang(false).dispatchMessage(sender, (PermissionsHandler.getPermissions().hasPermission(sender, "SetHDB.Permissions") ? "&a[\u2714]" : "&c[\u2718]") + " SetHDB.Permissions");

        }
        if (page != maxPage) { LanguageAPI.getLang(false).dispatchMessage(sender, "&aType &a&l/SetHDB Permissions " + (page + 1) + " &afor the next page."); }
        LanguageAPI.getLang(false).dispatchMessage(sender, "&a&l&m]-------------&a&l[&e Permissions Menu " + page + "/" + maxPage + " &a&l]&a&l&m------------[");
    }


    /**
     * Defines the config Command type for the command.
     *
     */
    public enum Execute {
        DEFAULT("", "SetHDB.use", false),
        HELP("help", "SetHDB.use", false),
        SET("set", "SetHDB.use", true),
        RELOAD("rl, reload", "SetHDB.reload", false),
        PERMISSIONS("pm, permission, permissions", "SetHDB.permissions", true),
        UPDATE("up, update, updates", "SetHDB.updates", false),
        AUTOUPDATE("au, autoupdate", "SetHDB.autoupdate", false);

        private final String command;
        private final String permission;
        private final boolean player;

        /**
         * Creates a new Execute instance.
         * @param command - The expected command argument.
         * @param permission - The expected command permission requirement.
         * @param player - If the command is specific to a player instance, cannot be executed by console.
         *
         */
        private Execute(final String command, final String permission, final boolean player) {
            this.command = command; this.permission = permission; this.player = player;
        }

        /**
         * Called when the CommandSender executes a command.
         * @param sender - Source of the command.
         * @param args - Passed command arguments.
         * @param page - The page number to be expected.
         *
         */
        public boolean accept(final CommandSender sender, final String[] args, final int page) {
            return (args.length == 0
                    || (Utils.getUtils().splitIgnoreCase(this.command, args[0])
                       && this.hasSyntax(args, page)))
                    && this.playerRequired(sender, args)
                    && this.hasPermission(sender, args);
        }

        public boolean accept(final CommandSender sender, final String[] args) {
            return (args.length == 0
                    || (Utils.getUtils().splitIgnoreCase(this.command, args[0])
                    && this.hasSyntax(args)))
                    && this.playerRequired(sender, args)
                    && this.hasPermission(sender, args);
        }

        /**
         * Checks if the executed command is the same as the executor.
         * @param args - Passed command arguments.
         *
         */
        public boolean acceptArgs(final String[] args) {
            return Utils.getUtils().splitIgnoreCase(this.command, args[0]);
        }

        /**
         * Checks if the Command being executed has the proper formatting or syntax.
         * @param args - Passed command arguments.
         * @param page - The page number to be expected.
         *
         */
        private boolean hasSyntax(final String[] args, final int page) {
            ServerHandler.getServer().logDev("Syntax parameters:  [" + args.length + "]");
            if (args.length > 1) { ServerHandler.getServer().logDev("          [Arg1: " + args[1] + "] :: [Page: " + String.valueOf(page) + "]"); }
            return (args.length >= 2 && (args[1].equalsIgnoreCase(String.valueOf(page))));
        }

        private boolean hasSyntax(final String[] args) {
            ServerHandler.getServer().logDev("Syntax parameters:  [" + args.length + "]");
            if (args.length > 1) { ServerHandler.getServer().logDev("          [Arg1: " + args[1] + "]"); }
            return (args.length >= 1);
        }

        /**
         * Checks if the Player has permission to execute the Command.
         * @param sender - Source of the command.
         * @param args - Passed command arguments.
         *
         */
        public boolean hasPermission(final CommandSender sender, final String[] args) {
            String[] permissions = this.permission.replace(" ", "").split(",");
            boolean multiPerms = this.permission.contains(",");
            ServerHandler.getServer().logDev("MultiPerms:  " + multiPerms);
            ServerHandler.getServer().logDev("HasPerms: " + PermissionsHandler.getPermissions().hasPermission(sender, this.permission));
            return (!multiPerms && PermissionsHandler.getPermissions().hasPermission(sender, this.permission));
        }

        /**
         * Checks if the Command requires the instance to be a Player.
         * @param sender - Source of the command.
         * @param args - Passed command arguments.
         *
         */
        public boolean playerRequired(final CommandSender sender, final String[] args) {
            ServerHandler.getServer().logDev("Player require:  " + this.player);
            ServerHandler.getServer().logDev("CommandSender:  " + (sender instanceof ConsoleCommandSender));
            return (!this.player
                    || (!(sender instanceof ConsoleCommandSender)));
        }
    }
}
