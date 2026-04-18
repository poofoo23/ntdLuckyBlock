package the.ntdluckyblock.action.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import the.ntdluckyblock.drop.DropAction;

public class ActionCommand implements DropAction {

    private final String command;
    private final boolean console;

    public ActionCommand(String command, boolean console) {
        this.command = command;
        this.console = console;
    }

    @Override
    public void execute(Player player, Location location) {
        String cmd = command.replace("%player%", player.getName());
        if (console) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        } else {
            player.performCommand(cmd);
        }
    }
}