package the.ntdluckyblock.drop;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;


public class DropEntry {

    private final int weight;
    private final List<DropAction> actions;

    public DropEntry(int weight, List<DropAction> actions) {
        this.weight = weight;
        this.actions = actions;
    }

    public int getWeight() {
        return weight;
    }

    public void execute(Player player, Location location) {
        for (DropAction action : actions) {
            action.execute(player, location);
        }
    }
}