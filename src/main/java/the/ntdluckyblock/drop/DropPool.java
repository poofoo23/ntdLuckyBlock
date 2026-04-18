package the.ntdluckyblock.drop;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DropPool {

    private final List<DropEntry> entries = new ArrayList<>();
    private int totalWeight = 0;

    private final Random random = new Random();

    public void register(DropEntry entry) {
        entries.add(entry);
        totalWeight += entry.getWeight();
    }

    public void executeRandom(Player player, Location location) {
        if (entries.isEmpty() || totalWeight <= 0) return;

        int r = random.nextInt(totalWeight);
        int current = 0;

        for (DropEntry entry : entries) {
            current += entry.getWeight();
            if (r < current) {
                entry.execute(player, location);
                return;
            }
        }
    }

    public void clear() {
        entries.clear();
        totalWeight = 0;
    }

    public int size() {
        return entries.size();
    }
}