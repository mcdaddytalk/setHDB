package com.github.mcdaddytalk.sethdb.handlers;

import com.github.mcdaddytalk.sethdb.SetHDB;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

public class UpdateHandler {

    private final String AUTOQUERY = "sethdb/files/latest";
    private final String AUTOHOST = "http://www.kaje.org:3000/";
    private final int PROJECTID = 527526;
    //private final String HOST = "https://api.spigotmc.org/legacy/update.php?resource=" + this.PROJECTID;
    private final String HOST = "http://www.kaje.org:3000/versions?resource=" + this.PROJECTID;

    private String versionExact = SetHDB.getInstance().getDescription().getVersion();
    private String localeVersionRaw = this.versionExact.split("-")[0];
    private String latestVersionRaw;
    private double localeVersion = (this.localeVersionRaw.equals("${project.version}") ? -1 : Double.parseDouble(localeVersionRaw.replace(".", "")));
    private double latestVersion;
    private boolean betaVersion = this.versionExact.contains("-SNAPSHOT") || this.versionExact.contains("-BETA") || this.versionExact.contains("-ALPHA");
    private boolean devVersion = this.localeVersion == -1;

    private File jarLink;
    private int BYTE_SIZE = 2048;

    private boolean updatesAllowed = ConfigHandler.getConfig(false).getFile("config.yml").getBoolean("General.CheckforUpdates");

    private static UpdateHandler updater;

    /**
     * Initializes the UpdateHandler and Checks for Updates upon initialization.
     *
     */
    public UpdateHandler() {
        this.jarLink = SetHDB.getInstance().getPlugin();
        this.checkUpdates(SetHDB.getInstance().getServer().getConsoleSender(), true);
    }

    /**
     * If the spigotmc host has an available update, redirects to download the jar file from devbukkit.
     * Downloads and write the new data to the plugin jar file.
     *
     * @param sender - The executor of the update checking.
     */
    public void forceUpdates(final CommandSender sender) {
        if (this.updateNeeded(sender, false)) {
            ServerHandler.getServer().messageSender(sender, "&aAn update has been found!");
            ServerHandler.getServer().messageSender(sender, "&aAttempting to update from " + "&ev" + this.localeVersionRaw + " &ato the new "  + "&ev" + this.latestVersionRaw);
            try {
                URL downloadUrl = new URL(this.AUTOHOST + this.AUTOQUERY);
                HttpURLConnection httpConnection = (HttpURLConnection) downloadUrl.openConnection();
                httpConnection.setRequestProperty("User-Agent", "Mozilla/5.0...");
                BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
                FileOutputStream fos = new FileOutputStream(this.jarLink);
                BufferedOutputStream bout = new BufferedOutputStream(fos, this.BYTE_SIZE);
                String progressBar = "&a::::::::::::::::::::::::::::::";
                byte[] data = new byte[this.BYTE_SIZE];
                long cloudFileSize = httpConnection.getContentLength();
                long fetchedSize = 0;
                int bytesRead;
                while ((bytesRead = in .read(data, 0, this.BYTE_SIZE)) >= 0) {
                    bout.write(data, 0, bytesRead);
                    fetchedSize += bytesRead;
                    final int currentProgress = (int)(((double) fetchedSize / (double) cloudFileSize) * 30);
                    if ((((fetchedSize * 100) / cloudFileSize) % 25) == 0 && currentProgress > 10) {
                        ServerHandler.getServer().messageSender(sender, progressBar.substring(0, currentProgress + 2) + "&c" + progressBar.substring(currentProgress + 2));
                    }
                }
                bout.close(); in.close(); fos.close();
                ServerHandler.getServer().messageSender(sender, "&aSuccessfully updated to v" + this.latestVersionRaw + "!");
                ServerHandler.getServer().messageSender(sender, "&aYou must restart your server for this to take affect.");
            } catch (Exception e) {
                ServerHandler.getServer().messageSender(sender, "&cAn error has occurred while trying to update the plugin SetHDB.");
                ServerHandler.getServer().messageSender(sender, "&cPlease try again later, if you continue to see this please contact the plugin developer.");
                ServerHandler.getServer().sendDebugTrace(e);
            }
        } else {
            if (this.betaVersion) {
                ServerHandler.getServer().messageSender(sender, "&aYou are running a SNAPSHOT!");
                ServerHandler.getServer().messageSender(sender, "&aIf you find any bugs please report them!");
            }
            ServerHandler.getServer().messageSender(sender, "&aYou are up to date!");
        }
    }

    /**
     * Checks to see if an update is required, notifying the console window and online op players.
     *
     * @param sender - The executor of the update checking.
     * @param onStart - If it is checking for updates on start.
     */
    public void checkUpdates(final CommandSender sender, final boolean onStart) {
        if (this.updateNeeded(sender, onStart) && this.updatesAllowed) {
            if (this.betaVersion) {
                ServerHandler.getServer().messageSender(sender, "&cYour current version: &bv" + this.localeVersionRaw + "-SNAPSHOT");
                ServerHandler.getServer().messageSender(sender, "&cThis &bSNAPSHOT &cis outdated and a release version is now available.");
            } else {
                ServerHandler.getServer().messageSender(sender, "&cYour current version: &bv" + this.localeVersionRaw);
            }
            ServerHandler.getServer().messageSender(sender, "&cA new version is available: " + "&av" + this.latestVersionRaw);
            ServerHandler.getServer().messageSender(sender, "&aGet it from: https://www.kaje.org:3000/sethdb/files/latest");
            ServerHandler.getServer().messageSender(sender, "&aIf you wish to auto update, please type /setHDB AutoUpdate");
            this.sendNotifications();
        } else if (this.updatesAllowed) {
            if (this.betaVersion) {
                ServerHandler.getServer().messageSender(sender, "&aYou are running a SNAPSHOT!");
                ServerHandler.getServer().messageSender(sender, "&aIf you find any bugs please report them!");
            } else if (this.devVersion) {
                ServerHandler.getServer().messageSender(sender, "&aYou are running a DEVELOPER SNAPSHOT!");
                ServerHandler.getServer().messageSender(sender, "&aIf you find any bugs please report them!");
                ServerHandler.getServer().messageSender(sender, "&aYou will not receive any updates requiring you to manually update.");
            }
            ServerHandler.getServer().messageSender(sender, "&aYou are up to date!");
        }
    }

    /**
     * Directly checks to see if the spigotmc host has an update available.
     *
     * @param sender - The executor of the update checking.
     * @param onStart - If it is checking for updates on start.
     * @return If an update is needed.
     */
    private Boolean updateNeeded(final CommandSender sender, final boolean onStart) {
        if (this.updatesAllowed) {
            ServerHandler.getServer().messageSender(sender, "&aChecking for updates...");
            try {
                InputStream input = (InputStream) new URL(this.HOST).openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String version = reader.readLine();
                reader.close();
                if (version.length() <= 7) {
                    this.latestVersionRaw = version.replaceAll("[a-z]", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace("-ALPHA", "").replace("-RELEASE", "");
                    this.latestVersion = Double.parseDouble(this.latestVersionRaw.replace(".", ""));
                    if (this.devVersion) {
                        return false;
                    } else if (this.latestVersion == this.localeVersion && this.betaVersion || this.localeVersion > this.latestVersion && !this.betaVersion || this.latestVersion > this.localeVersion) {
                        return true;
                    }
                }
            } catch (Exception e) {
                ServerHandler.getServer().messageSender(sender, "&cFailed to check for updates, connection could not be made.");
                return false;
            }
        } else if (!onStart) {
            ServerHandler.getServer().messageSender(sender, "&cUpdate checking is currently disabled in the config.yml");
            ServerHandler.getServer().messageSender(sender, "&cIf you wish to use the auto update feature, you will need to enable it.");
        }
        return false;
    }

    /**
     * Sends out notifications to all online op players that
     * an update is available at the time of checking for updates.
     *
     */
    private void sendNotifications() {
        try {
            Collection < ? > playersOnline = null;
            Player[] playersOnlineOld = null;
            if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
                if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
                    playersOnline = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
                    for (Object objPlayer: playersOnline) {
                        if (((Player) objPlayer).isOp()) {
                            ServerHandler.getServer().messageSender(((Player) objPlayer), "&eAn update has been found!");
                            ServerHandler.getServer().messageSender(((Player) objPlayer), "&ePlease update to the latest version: v" + this.latestVersionRaw);
                        }
                    }
                }
            } else {
                playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
                for (Player objPlayer: playersOnlineOld) {
                    if (objPlayer.isOp()) {
                        ServerHandler.getServer().messageSender(objPlayer, "&eAn update has been found!");
                        ServerHandler.getServer().messageSender(objPlayer, "&ePlease update to the latest version: v" + this.latestVersionRaw);
                    }
                }
            }
        } catch (Exception e) { ServerHandler.getServer().sendDebugTrace(e); }
    }


    /**
     * Gets the exact string version from the plugin yml file.
     *
     * @return The exact server version.
     */
    public String getVersion() {
        return this.versionExact;
    }

    /**
     * Gets the plugin jar file directly.
     *
     * @return The plugins jar file.
     */
    public File getJarLink() {
        return this.jarLink;
    }

    /**
     * Gets the instance of the UpdateHandler.
     *
     * @param regen - If the instance should be regenerated.
     * @return The UpdateHandler instance.
     */
    public static UpdateHandler getUpdater(boolean regen) {
        if (updater == null || regen) { updater = new UpdateHandler(); }
        return updater;
    }
}