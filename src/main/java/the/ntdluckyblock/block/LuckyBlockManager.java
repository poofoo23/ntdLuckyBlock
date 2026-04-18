package the.ntdluckyblock.block;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import the.ntdluckyblock.Main;

import java.lang.reflect.Field;
import java.util.UUID;

public class LuckyBlockManager {

    public static final String META_KEY = "luckyblock-type";
    private Main plugin = Main.getInstance();

    public ItemStack createLuckyBlockItem(String type) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        String rawName = plugin.getLuckyBlockConfig().getString("luckyblocks." + type + ".name", "&6Lucky Block");
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', rawName));

        String texture = plugin.getLuckyBlockConfig().getString("luckyblocks." + type + ".skull-texture");

        if (texture != null && !texture.isEmpty()) {
            applyTexture(meta, texture, type);
        } else {
            plugin.getLogger().warning("LuckyBlock skull-texture for type '" + type + "' is missing! Using default skull.");
        }

        skull.setItemMeta(meta);

        return skull;
    }

    public void applyType(Block block, String type) {
        block.setMetadata(META_KEY, new FixedMetadataValue(plugin, type));
    }

    public boolean isLuckyBlock(Block block) {
        return block.hasMetadata(META_KEY);
    }

    public String getTypeFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return "default";
        if (!(item.getItemMeta() instanceof SkullMeta)) return "default";
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        for (String type : plugin.getLuckyBlockConfig().getConfigurationSection("luckyblocks").getKeys(false)) {
            String configTexture = plugin.getLuckyBlockConfig().getString("luckyblocks." + type + ".skull-texture");
            if (configTexture == null || configTexture.isEmpty()) continue;
            try {
                Field profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                Object profile = profileField.get(meta);
                if (profile == null) continue;
                Field propertiesField = profile.getClass().getDeclaredField("properties");
                propertiesField.setAccessible(true);
                PropertyMap properties = (PropertyMap) propertiesField.get(profile);
                if (properties.containsKey("textures")) {
                    for (Property p : properties.get("textures")) {
                        if (p.getValue().equals(configTexture)) {
                            return type;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "default";
    }

    public String getType(Block block) {
        if (!block.hasMetadata(META_KEY)) return null;
        return block.getMetadata(META_KEY).get(0).asString();
    }

    private void applyTexture(SkullMeta meta, String texture, String type) {
        try {
            UUID uuid = UUID.nameUUIDFromBytes(type.getBytes());
            GameProfile profile = new GameProfile(uuid, null);
            profile.getProperties().put("textures", new Property("textures", texture));

            Field f = meta.getClass().getDeclaredField("profile");
            f.setAccessible(true);
            f.set(meta, profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}