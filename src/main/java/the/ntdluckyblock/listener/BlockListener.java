package the.ntdluckyblock.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import the.ntdluckyblock.Main;
import the.ntdluckyblock.block.LuckyBlockManager;
import the.ntdluckyblock.block.LuckyBlockStand;
import the.ntdluckyblock.drop.DropPool;

public class BlockListener implements Listener {

    private Main plugin = Main.getInstance();
    private LuckyBlockManager manager = Main.getInstance().getLuckyBlockManager();

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (!manager.isLuckyBlock(block)) return;

        event.setCancelled(true);

        if (LuckyBlockStand.hasStand(block.getLocation())) {
            LuckyBlockStand.removeStand(block.getLocation());
        }

        block.setType(Material.AIR);

        String type = manager.getType(block);
        if (type == null) type = "default";

        DropPool pool = plugin.getDropPool(type);
        if (pool != null) {
            pool.executeRandom(player, block.getLocation());
        }
    }
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (manager.isLuckyBlock(block)) {
                removeLuckyBlock(block);
            }
        }
    }
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (manager.isLuckyBlock(block)) {
                removeLuckyBlock(block);
            }
        }
    }
    @EventHandler
    public void onPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();

        if (manager.isLuckyBlock(block) && block.getType() == Material.AIR) {
            if (LuckyBlockStand.hasStand(block.getLocation())) {
                LuckyBlockStand.removeStand(block.getLocation());
            }
            block.removeMetadata(LuckyBlockManager.META_KEY, plugin);
        }
    }
    private void removeLuckyBlock(Block block) {
        if (LuckyBlockStand.hasStand(block.getLocation())) {
            LuckyBlockStand.removeStand(block.getLocation());
        }

        block.removeMetadata(LuckyBlockManager.META_KEY, plugin);
        block.setType(Material.AIR);
    }
}