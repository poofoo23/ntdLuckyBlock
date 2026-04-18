package the.ntdluckyblock.drop;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface DropAction {
    void execute(Player player, Location location);
}