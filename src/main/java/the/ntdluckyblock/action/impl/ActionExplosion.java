package the.ntdluckyblock.action.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import the.ntdluckyblock.drop.DropAction;

public class ActionExplosion implements DropAction {

    private final float power;
    private final boolean fire;
    private final boolean breakBlocks;

    public ActionExplosion(float power, boolean fire, boolean breakBlocks) {
        this.power = power;
        this.fire = fire;
        this.breakBlocks = breakBlocks;
    }

    @Override
    public void execute(Player player, Location location) {
        location.getWorld().createExplosion(
                location.getX(),
                location.getY(),
                location.getZ(),
                power,
                fire,
                breakBlocks
        );
    }
}