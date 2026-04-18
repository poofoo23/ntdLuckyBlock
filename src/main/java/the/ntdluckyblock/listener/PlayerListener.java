package the.ntdluckyblock.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import the.ntdluckyblock.Main;
import the.ntdluckyblock.block.LuckyBlockManager;
import the.ntdluckyblock.block.LuckyBlockStand;

public class PlayerListener implements Listener {

    private Main plugin = Main.getInstance();
    private LuckyBlockManager manager = Main.getInstance().getLuckyBlockManager();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                LuckyBlockStand.respawnAllFor(e.getPlayer()), 1L);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                LuckyBlockStand.respawnAllFor(e.getPlayer()), 1L);
    }
}

