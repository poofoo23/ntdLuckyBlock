package the.ntdluckyblock.block;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import the.ntdluckyblock.Main;

import java.util.*;

public class LuckyBlockStand {

    private static final Main plugin = Main.getInstance();

    private static class StandData {
        Location loc;
        ItemStack skull;
        byte glassData;
        int entityId;

        StandData(Location loc, ItemStack skull, byte glassData, int entityId) {
            this.loc = loc;
            this.skull = skull;
            this.glassData = glassData;
            this.entityId = entityId;
        }
    }

    private static final Map<String, StandData> standMap = new HashMap<>();

    private static String getKey(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

    private static byte getGlassData(String type) {
        String path = "luckyblocks." + type + ".color";
        String colorName = plugin.getLuckyBlockConfig().getString(path);

        if (colorName == null) {
            throw new IllegalStateException("[LuckyBlock] 缺少配置项: " + path);
        }

        switch (colorName.toUpperCase()) {
            case "BLACK_STAINED_GLASS":
                return 15;
            case "RED_STAINED_GLASS":
                return 14;
            case "GREEN_STAINED_GLASS":
                return 13;
            case "BROWN_STAINED_GLASS":
                return 12;
            case "BLUE_STAINED_GLASS":
                return 11;
            case "PURPLE_STAINED_GLASS":
                return 10;
            case "CYAN_STAINED_GLASS":
                return 9;
            case "LIGHT_GRAY_STAINED_GLASS":
                return 8;
            case "GRAY_STAINED_GLASS":
                return 7;
            case "PINK_STAINED_GLASS":
                return 6;
            case "LIME_STAINED_GLASS":
                return 5;
            case "YELLOW_STAINED_GLASS":
                return 4;
            case "LIGHT_BLUE_STAINED_GLASS":
                return 3;
            case "MAGENTA_STAINED_GLASS":
                return 2;
            case "ORANGE_STAINED_GLASS":
                return 1;
            case "WHITE_STAINED_GLASS":
                return 0;
            default:
                throw new IllegalStateException("[LuckyBlock] 未知玻璃颜色: " + colorName + " (" + path + ")");
        }
    }

    public static void spawnHeadStand(Location loc, ItemStack skull,String type) {
        Block block = loc.getBlock();
        byte glassData = getGlassData(type);

        block.setType(Material.STAINED_GLASS);
        block.setData(glassData);

        EntityArmorStand stand = createStand(loc);
        sendSpawnPacket(loc, stand, skull);

        standMap.put(getKey(loc), new StandData(loc.clone(), skull.clone(), glassData, stand.getId()));
    }
    public static Iterable<Location> getAllLocations() {
        List<Location> list = new ArrayList<>();
        for (String key : standMap.keySet()) {
            list.add(parseKey(key));
        }
        return list;
    }

    public static void clearAll() {
        standMap.clear();
    }

    private static Location parseKey(String key) {
        String[] parts = key.split(":");
        return new Location(plugin.getServer().getWorld(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }

    public static void removeStand(Location loc) {
        String key = getKey(loc);
        StandData data = standMap.remove(key);
        if (data == null) return;

        PacketPlayOutEntityDestroy destroy =
                new PacketPlayOutEntityDestroy(data.entityId);

        for (Player p : loc.getWorld().getPlayers()) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(destroy);
        }
    }

    public static void respawnAllFor(Player player) {
        Iterator<StandData> it = standMap.values().iterator();

        while (it.hasNext()) {
            StandData data = it.next();
            if (!player.getWorld().equals(data.loc.getWorld())) continue;

            Block b = data.loc.getBlock();
            if (b.getType() != Material.STAINED_GLASS) {
                removeStand(data.loc);
                it.remove();
                continue;
            }

            EntityArmorStand stand = createStand(data.loc);
            sendSpawnPacket(player, stand, data.skull);
            data.entityId = stand.getId();
        }
    }

    private static EntityArmorStand createStand(Location loc) {
        EntityArmorStand stand = new EntityArmorStand(((CraftWorld) loc.getWorld()).getHandle());

        stand.setLocation(loc.getBlockX() + 0.5, loc.getBlockY() - 1.25, loc.getBlockZ() + 0.5, 0, 0);

        stand.setInvisible(true);
        stand.setSmall(false);
        stand.setGravity(false);
        stand.setArms(false);
        stand.setBasePlate(false);
        stand.setCustomNameVisible(false);
        stand.setSize(0, 0);
        return stand;
    }

    private static void sendSpawnPacket(Location loc, EntityArmorStand stand, ItemStack skull) {
        PacketPlayOutSpawnEntityLiving spawn =
                new PacketPlayOutSpawnEntityLiving(stand);

        PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(stand.getId(), 4, CraftItemStack.asNMSCopy(skull));

        for (Player p : loc.getWorld().getPlayers()) {
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(spawn);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(equip);
        }
    }

    private static void sendSpawnPacket(Player p, EntityArmorStand stand, ItemStack skull) {
        PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving(stand);

        PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(stand.getId(), 4, CraftItemStack.asNMSCopy(skull));

        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(spawn);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(equip);
    }

    public static boolean hasStand(Location loc) {
        return standMap.containsKey(getKey(loc));
    }
}