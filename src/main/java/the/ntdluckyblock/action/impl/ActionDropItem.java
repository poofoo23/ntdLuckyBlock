package the.ntdluckyblock.action.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import the.ntdluckyblock.drop.DropAction;

public class ActionDropItem implements DropAction {

    private final ItemStack item;

    public ActionDropItem(ItemStack item) {
        this.item = item;
    }
    @Override
    public void execute(Player player, Location location) {
        location.getWorld().dropItemNaturally(location, item.clone());
    }
}