package the.ntdluckyblock.action.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import the.ntdluckyblock.Main;
import the.ntdluckyblock.drop.DropAction;

public class ActionGiveLuckyBlock implements DropAction {

    private final String type;
    private final int amount;

    public ActionGiveLuckyBlock(String type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    @Override
    public void execute(Player player, Location location) {
        ItemStack item = Main.getInstance()
                .getLuckyBlockManager()
                .createLuckyBlockItem(type);

        item.setAmount(amount);

        location.getWorld().dropItemNaturally(location, item);
    }
}