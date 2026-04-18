package the.ntdluckyblock.action.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import the.ntdluckyblock.drop.DropAction;

public class ActionPotion implements DropAction {

    private final PotionEffectType type;
    private final int duration;
    private final int amplifier;

    public ActionPotion(PotionEffectType type, int duration, int amplifier) {
        this.type = type;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    @Override
    public void execute(Player player, Location location) {
        player.addPotionEffect(
                new PotionEffect(type, duration, amplifier)
        );
    }
}