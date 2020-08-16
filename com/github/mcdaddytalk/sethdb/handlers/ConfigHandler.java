package com.github.mcdaddytalk.sethdb.handlers;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.CopyOption;
import java.nio.file.Files;

import com.github.mcdaddytalk.sethdb.ChatExecutor;
import com.github.mcdaddytalk.sethdb.ChatTab;
import com.github.mcdaddytalk.sethdb.SetHDB;
import com.github.mcdaddytalk.sethdb.utils.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;


public class ConfigHandler {

    private YamlConfiguration configFile;
    private YamlConfiguration langFile;
    private boolean Generating = false;

    private static ConfigHandler config;

    /**
     * Registers the command executors and events.
     *
     */
    public void registerEvents() {
        DependAPI.getDepends(false).sendUtilityDepends();
        SetHDB.getInstance().getCommand("sethdb").setExecutor(new ChatExecutor());
        SetHDB.getInstance().getCommand("sethdb").setTabCompleter(new ChatTab());

    }

    /**
     * Registers new instances of the plugin classes.
     *
     */
    private void registerClasses() {
        this.copyFile("config.yml", "config-Version", 7);
        this.copyFile(LanguageAPI.getLang(true).getFile(), LanguageAPI.getLang(false).getFile().split("-")[0] + "-Version", 7);
        DependAPI.getDepends(true);
        LogFilter.getFilter(true);
        ServerHandler.getServer().runThread(main -> { Metrics.getMetrics(true); }, 100L);
    }

    

    /**
     * Gets the file from the specified path.
     *
     * @param path - The File to be fetched.
     * @return The file.
     */
    public FileConfiguration getFile(final String path) {
        final File file = new File(SetHDB.getInstance().getDataFolder(), path);
        if (this.configFile == null) { this.getSource(path); }
        return this.getLoadedConfig(file, false);
    }

    /**
     * Gets the source file from the specified path.
     *
     * @param path - The File to be loaded.
     * @return The source file.
     */
    public FileConfiguration getSource(final String path) {
        final File file = new File(SetHDB.getInstance().getDataFolder(), path);
        if (!(file).exists()) {
            try {
                InputStream source;
                final File dataDir = SetHDB.getInstance().getDataFolder();
                if (!dataDir.exists()) { dataDir.mkdir(); }
                if (!path.contains("lang.yml")) { source = SetHDB.getInstance().getResource("files/configs/" + path); }
                else { source = SetHDB.getInstance().getResource("files/locales/" + path); }
                if (!file.exists()) { Files.copy(source, file.toPath(), new CopyOption[0]); }
            } catch (Exception e) {
                ServerHandler.getServer().sendDebugTrace(e);
                ServerHandler.getServer().logWarn("Cannot save " + path + " to disk!");
                return null;
            }
        }
        return this.getLoadedConfig(file, true);
    }

    /**
     * Gets the file and loads it into memory if specified.
     *
     * @param file - The file to be loaded.
     * @param commit - If the File should be committed to memory.
     * @return The Memory loaded config file.
     */
    public YamlConfiguration getLoadedConfig(final File file, final boolean commit) {
        if (file.getName().contains("config.yml")) {
            if (commit) { this.configFile = YamlConfiguration.loadConfiguration(file); }
            return this.configFile;
        } else if (file.getName().contains("lang.yml")) {
            if (commit) { this.langFile = YamlConfiguration.loadConfiguration(file); }
            return this.langFile;
        }
        return null;
    }

    /**
     * Copies the specified config file to the data folder.
     *
     * @param configFile - The name and extension of the config file to be copied.
     * @param version - The version String to be checked in the config file.
     * @param id - The expected version id to be found in the config file.
     */
    private void copyFile(final String configFile, final String version, final int id) {
        this.getSource(configFile);
        File File = new File(SetHDB.getInstance().getDataFolder(), configFile);
        if (File.exists() && this.getFile(configFile).getInt(version) != id) {
            InputStream source;
            if (!configFile.contains("lang.yml")) { source = SetHDB.getInstance().getResource("files/configs/" + configFile); }
            else { source = SetHDB.getInstance().getResource("files/locales/" + configFile); }
            if (source != null) {
                String[] namePart = configFile.split("\\.");
                String renameFile = namePart[0] + Utils.getUtils().getRandom(1, 50000) + namePart[1];
                File renamedFile = new File(SetHDB.getInstance().getDataFolder(), renameFile);
                if (!renamedFile.exists()) {
                    File.renameTo(renamedFile);
                    File copyFile = new File(SetHDB.getInstance().getDataFolder(), configFile);
                    copyFile.delete();
                    this.getSource(configFile);
                    ServerHandler.getServer().logWarn("Your " + configFile + " is out of date and new options are available, generating a new one!");
                }
            }
        }

        this.getFile(configFile).options().copyDefaults(false);
    }

    /**
     * Checks if Debugging is enabled.
     *
     * @return If Debugging is enabled.
     */
    public boolean debugEnabled() {
        return this.getFile("config.yml").getBoolean("General.Debugging");
    }

    /**
     * Gets the defined Prevent.
     *
     * @param name - The name of the Prevent.
     * @return The defined Prevent as a String to compare later.
     */
    public String getPrevent(final String name) {
        return this.getFile("config.yml").getString("Prevent." + name);
    }

    /**
     * Checks if OP Bypass is enabled for Prevent actions.
     *
     * @return If OP is defined for the Prevent actions.
     */
    public boolean isPreventOP() {
        return Utils.getUtils().containsIgnoreCase(this.getFile("config.yml").getString("Prevent.Bypass"), "OP");
    }

    /**
     * Checks if CREATIVE Bypass is enabled for Prevent actions.
     *
     * @return If CREATIVE is defined for the Prevent actions.
     */
    public boolean isPreventCreative() {
        return Utils.getUtils().containsIgnoreCase(this.getFile("config.yml").getString("Prevent.Bypass"), "CREATIVE");
    }

    /**
     * Gets the instance of the ConfigHandler.
     *
     * @param regen - If the instance should be regenerated.
     * @return The ConfigHandler instance.
     */
    public static ConfigHandler getConfig(final boolean regen) {
        if (config == null || regen) {
            config = new ConfigHandler();
            config.registerClasses();
        }
        return config;
    }
}
