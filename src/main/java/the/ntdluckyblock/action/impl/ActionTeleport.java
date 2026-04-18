package the.ntdluckyblock.action.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import the.ntdluckyblock.drop.DropAction;

public class ActionTeleport implements DropAction {

    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final double offsetYaw;
    private final double offsetPitch;

    public ActionTeleport(double offsetX, double offsetY, double offsetZ,double offsetYaw,double offsetPitch) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.offsetYaw = offsetYaw;
        this.offsetPitch = offsetPitch;
    }

    @Override
    public void execute(Player player, Location location) {
        Location target = location.clone();
        target.add(offsetX, offsetY, offsetZ);
        target.setYaw(target.getYaw() + (float) offsetYaw);
        target.setPitch(target.getPitch() + (float) offsetPitch);
        player.teleport(target);
    }
}