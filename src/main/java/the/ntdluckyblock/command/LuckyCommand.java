package the.ntdluckyblock.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import the.ntdluckyblock.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LuckyCommand implements CommandExecutor , TabCompleter {

    private Main plugin = Main.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("lucky.admin")) {
            sender.sendMessage(ChatColor.RED + "你没有权限执行此命令");
            return true;
        }

        if (args.length == 0) {
            String path = "messages.prefix";
            String prefix = ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString(path));
            String author = plugin.getDescription().getAuthors().get(0);
            sender.sendMessage(prefix + ChatColor.YELLOW + "作者: " + author);
            sender.sendMessage(prefix + ChatColor.YELLOW + "/lucky reload - 重新加载掉落配置");
            sender.sendMessage(prefix + ChatColor.YELLOW + "/lucky give <玩家> <类型> - 给玩家 LuckyBlock");
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "reload":
                plugin.reloadLuckyBlock();
                sender.sendMessage(ChatColor.GREEN + "LuckyBlock 配置已重新加载");
                break;

            case "give": {
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "用法: /lucky give <玩家> <类型> [数量]");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "玩家未在线");
                    return true;
                }

                String type = args[2].toLowerCase();
                if (!plugin.getLuckyBlockConfig().isConfigurationSection("luckyblocks." + type)) {
                    sender.sendMessage(ChatColor.RED + "未知 LuckyBlock 类型: " + type);
                    return true;
                }

                int amount = 1;
                if (args.length >= 4) {
                    try {
                        amount = Integer.parseInt(args[3]);
                        if (amount <= 0) {
                            sender.sendMessage(ChatColor.RED + "数量必须大于 0");
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "数量必须是数字");
                        return true;
                    }
                }

                ItemStack item = plugin.getLuckyBlockManager().createLuckyBlockItem(type);
                item.setAmount(amount);

                target.getInventory().addItem(item);

                String displayName = item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                        ? item.getItemMeta().getDisplayName()
                        : type;

                sender.sendMessage(ChatColor.GREEN + "已给予 "
                        + target.getName() + " "
                        + amount + " 个 "
                        + displayName + ChatColor.GREEN + " 幸运方块");
                break;
            }

            default:
                sender.sendMessage(ChatColor.RED + "未知子命令");
        }

        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("lucky.admin")) return Collections.emptyList();

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if ("reload".startsWith(args[0].toLowerCase())) completions.add("reload");
            if ("give".startsWith(args[0].toLowerCase())) completions.add("give");

        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(p.getName());
                }
            }

        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            for (String type : plugin.getLuckyBlockConfig().getConfigurationSection("luckyblocks").getKeys(false)) {
                if (type.toLowerCase().startsWith(args[2].toLowerCase())) {
                    completions.add(type);
                }
            }

        } else if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
            completions.add("1");
            completions.add("8");
            completions.add("16");
            completions.add("32");
            completions.add("64");
        }

        return completions;
    }
}