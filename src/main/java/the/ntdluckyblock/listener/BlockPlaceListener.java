package the.ntdluckyblock.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import the.ntdluckyblock.Main;
import the.ntdluckyblock.block.LuckyBlockManager;
import the.ntdluckyblock.block.LuckyBlockStand;

public class BlockPlaceListener implements Listener {

    private LuckyBlockManager manager = Main.getInstance().getLuckyBlockManager();
    private Main plugin = Main.getInstance();

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;

        String itemType = manager.getTypeFromItem(item);
        if (itemType == null) return;

        event.setCancelled(true);

        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            event.getPlayer().getInventory().removeItem(item);
        }

        Block baseBlock = event.getBlockPlaced();
        baseBlock.setType(Material.STAINED_GLASS);

        byte glassData = 0;
        String colorName = plugin.getLuckyBlockConfig().getString("luckyblocks." + itemType + ".color");
        switch (colorName.toUpperCase()) {
            case "WHITE_STAINED_GLASS": glassData = 0; break;
            case "ORANGE_STAINED_GLASS": glassData = 1; break;
            case "MAGENTA_STAINED_GLASS": glassData = 2; break;
            case "LIGHT_BLUE_STAINED_GLASS": glassData = 3; break;
            case "YELLOW_STAINED_GLASS": glassData = 4; break;
            case "LIME_STAINED_GLASS": glassData = 5; break;
            case "PINK_STAINED_GLASS": glassData = 6; break;
            case "GRAY_STAINED_GLASS": glassData = 7; break;
            case "LIGHT_GRAY_STAINED_GLASS": glassData = 8; break;
            case "CYAN_STAINED_GLASS": glassData = 9; break;
            case "PURPLE_STAINED_GLASS": glassData = 10; break;
            case "BLUE_STAINED_GLASS": glassData = 11; break;
            case "BROWN_STAINED_GLASS": glassData = 12; break;
            case "GREEN_STAINED_GLASS": glassData = 13; break;
            case "RED_STAINED_GLASS": glassData = 14; break;
            case "BLACK_STAINED_GLASS": glassData = 15; break;
        }
        baseBlock.setData(glassData);

        Bukkit.getScheduler().runTask(plugin, () ->
                LuckyBlockStand.spawnHeadStand(baseBlock.getLocation(), item.clone(),itemType)
        );
        baseBlock.getWorld().playSound(baseBlock.getLocation(), Sound.STEP_STONE, 1.0f, 1.0f);
        manager.applyType(baseBlock, itemType);
    }
}