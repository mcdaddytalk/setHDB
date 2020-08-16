package com.github.mcdaddytalk.sethdb.handlers;
import com.github.mcdaddytalk.sethdb.SetHDB;
import com.github.mcdaddytalk.sethdb.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Directional;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

public class SkullHandler {

    private static HashMap<String, String> playerTextures = new HashMap<>();
    private static HashMap<String, ItemStack> skulls = new HashMap<>();

    /**
     *
     * @param texture
     * @return
     */
    public static ItemStack getCustomSkull(String texture) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        return skulls.computeIfAbsent(texture, x -> setTexture(skull, texture));
    }

    /**
     *
     * @param skull
     * @param texture
     * @return
     */
    private static ItemStack setTexture(ItemStack skull, String texture) {
        Bukkit.getScheduler().runTaskAsynchronously(SetHDB.getInstance(), () -> {
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            Field field;
            try {
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                profile.getProperties().put("textures", new Property("textures", texture, null));
                assert meta != null;
                field = meta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(meta, profile);
                skull.setItemMeta(meta);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
        return skull;
    }

    public static SkullMeta getSkullByValue(@Nonnull SkullMeta head, @Nonnull String value) {
        Validate.notEmpty(value, "Skull value cannot be null or empty");
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", value));
        try {
            Field profileField = head.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(head, profile);
        } catch (SecurityException | NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return head;
    }

    /**
     * @param skull
     * @return
     */
    public static String getTexture(ItemStack skull) {
        GameProfile profile;
        ItemMeta meta = skull.getItemMeta();
        Field field;
        try {
            field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            profile = (GameProfile) field.get(meta);
            if (profile != null) {
                for (Property prop : profile.getProperties().values()) {
                    if ("textures".equals(prop.getName())) {
                        return prop.getValue();
                    }
                }
            }
            return null;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void changeSkin(Block b, String base64Str, Integer rotation) {
        if (b.getType() != Material.PLAYER_HEAD) return;    // to avoid spurious exceptions
        final Skull skull = (Skull)b.getState();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures",base64Str));
        try {
            Field profileField = skull.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skull, profile);
        }catch (NoSuchFieldException | IllegalAccessException e) { e.printStackTrace(); }

        /*BlockData blockData = skull.getBlockData();
        if(blockData instanceof Directional) {
            ((Directional) blockData).setFacingDirection(Utils.getRotation(Integer.valueOf(rotation)));
            skull.setBlockData(blockData);
        }else {
            ServerHandler.getServer().logDev("Could not set direction of head");
        }
        */
        skull.setRotation(Utils.getRotation(Integer.valueOf(rotation)));
        skull.update(); // so that the result can be seen
    }

    public static ItemStack getPlayerSkull(String id) {
        String texture = getPlayerTexture(id);
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        if (texture != null) {
            return skulls.computeIfAbsent(id, x -> setTexture(skull, texture));
        } else {
            return skull;
        }
    }
    private static String getPlayerTexture(String id) {
        if (playerTextures.containsKey(id)) {
            return playerTextures.get(id);
        } else {
            playerTextures.put(id, null);
            Bukkit.getScheduler().runTaskAsynchronously(SetHDB.getInstance(), () -> {
                try {
                    JsonObject userProfile = (JsonObject) new JsonParser().parse(Utils.readFromURL("https://api.mojang.com/users/profiles/minecraft/" + id));
                    JsonArray textures = ((JsonObject) new JsonParser().parse(Utils.readFromURL("https://sessionserver.mojang.com/session/minecraft/profile/" + userProfile.get("id").getAsString()))).getAsJsonArray("properties");
                    for (JsonElement element : textures) {
                        if ("textures".equals(element.getAsJsonObject().get("name").getAsString())) {
                            playerTextures.put(id, element.getAsJsonObject().get("value").getAsString());
                        }
                    }
                } catch (Throwable ignored) {
                }
            });
        }
        return playerTextures.get(id);
    }

    public static HashMap<String, ItemStack> getSkulls() {
        return skulls;
    }

    private static SkullHandler instance = new SkullHandler();

    public static SkullHandler getInst() {
        return instance;
    }

    public ItemStack getSkull(String id) {
        return id.length() > 150 ? getCustomSkull(id) : getPlayerSkull(id);
    }

}
