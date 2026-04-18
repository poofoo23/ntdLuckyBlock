package the.ntdluckyblock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import the.ntdluckyblock.action.impl.*;
import the.ntdluckyblock.api.ActionRegistry;
import the.ntdluckyblock.block.LuckyBlockManager;
import the.ntdluckyblock.block.LuckyBlockStand;
import the.ntdluckyblock.command.LuckyCommand;
import the.ntdluckyblock.drop.DropLoader;
import the.ntdluckyblock.drop.DropPool;
import the.ntdluckyblock.listener.BlockListener;
import the.ntdluckyblock.listener.BlockPlaceListener;
import the.ntdluckyblock.listener.PlayerListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin {

    private static Main instance;

    private LuckyBlockManager luckyBlockManager;

    private final Map<String, DropPool> dropPools = new HashMap<>();

    private File luckyBlockFile;
    private FileConfiguration luckyBlockConfig;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResourceIfNotExists("luckyblocks.yml");

        loadLuckyBlockConfig();

        this.luckyBlockManager = new LuckyBlockManager();
        registerDefaultActions();
        loadDropPools();

        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(),this);
        Bukkit.getPluginCommand("lucky").setExecutor(new LuckyCommand());
        Bukkit.getPluginCommand("lucky").setTabCompleter(new LuckyCommand());

        getLogger().info("LuckyBlock已启动,加载Drop: " + dropPools.keySet());
    }
    @Override
    public void onDisable() {
        dropPools.clear();
        for (Location loc : LuckyBlockStand.getAllLocations()) {
            LuckyBlockStand.removeStand(loc);
            Block block = loc.getBlock();
            if (block.getType() == Material.STAINED_GLASS) {
                block.setType(Material.AIR);
            }
        }
        LuckyBlockStand.clearAll();
        getLogger().info("LuckyBlock已卸载");
    }

    private void loadLuckyBlockConfig() {
        luckyBlockFile = new File(getDataFolder(), "luckyblocks.yml");
        luckyBlockConfig = YamlConfiguration.loadConfiguration(luckyBlockFile);
    }

    private void loadDropPools() {
        dropPools.clear();

        File dropsDir = new File(getDataFolder(), "drops");
        if (!dropsDir.exists()) {
            dropsDir.mkdirs();
            saveResource("drops/yellow.yml", false);
            saveResource("drops/red.yml", false);
            saveResource("drops/blue.yml", false);
            saveResource("drops/white.yml", false);
            saveResource("drops/brown.yml", false);
            saveResource("drops/black.yml", false);
        }

        File[] files = dropsDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String poolName = file.getName().replace(".yml", "");
            DropPool pool = DropLoader.load(file);

            dropPools.put(poolName.toLowerCase(), pool);
        }
    }

    private void registerDefaultActions() {

        ActionRegistry.clear();

        ActionRegistry.register("EXP", cfg ->
                (p, l) -> p.giveExp((int) cfg.get("amount"))
        );

        ActionRegistry.register("ITEM", cfg -> {

            Material material = Material.getMaterial(
                    cfg.get("material").toString().toUpperCase()
            );

            if (material == null) {
                throw new IllegalArgumentException("Unknown material: " + cfg.get("material"));
            }

            int amount = cfg.containsKey("amount") ? (int) cfg.get("amount") : 1;

            ItemStack item = new ItemStack(material, amount);

            if (cfg.containsKey("enchantments")) {
                Map<?, ?> enchants = (Map<?, ?>) cfg.get("enchantments");
                boolean unsafe = cfg.containsKey("unsafe") && (boolean) cfg.get("unsafe");

                for (Map.Entry<?, ?> entry : enchants.entrySet()) {
                    String enchantName = entry.getKey().toString().toUpperCase();
                    int level = Integer.parseInt(entry.getValue().toString());

                    Enchantment enchant = Enchantment.getByName(enchantName);
                    if (enchant == null) {
                        throw new IllegalArgumentException("Unknown enchantment: " + enchantName);
                    }

                    if (unsafe) {
                        item.addUnsafeEnchantment(enchant, level);
                    } else {
                        item.addEnchantment(enchant, level);
                    }
                }
            }

            return new ActionDropItem(item);
        });

        ActionRegistry.register("LUCKY_BLOCK", cfg ->
                new ActionGiveLuckyBlock(
                        cfg.get("lucky-type").toString(),
                        cfg.containsKey("amount") ? (int) cfg.get("amount") : 1
                )
        );

        ActionRegistry.register("COMMAND", cfg ->
                new ActionCommand(
                        cfg.get("command").toString(),
                        cfg.get("sender").toString().equalsIgnoreCase("CONSOLE")
                )
        );

        ActionRegistry.register("POTION", cfg ->
                new ActionPotion(
                        PotionEffectType.getByName(cfg.get("potion").toString()),
                        (int) cfg.get("duration"),
                        (int) cfg.get("amplifier")
                )
        );
        ActionRegistry.register("MESSAGE", cfg -> {
            String message = cfg.get("message").toString();
            return (p, l) -> p.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));
        });


        ActionRegistry.register("EXPLOSION", cfg ->
                new ActionExplosion(
                        Float.parseFloat(cfg.get("power").toString()),
                        (boolean) cfg.get("fire"),
                        (boolean) cfg.get("break-blocks")
                )
        );

        ActionRegistry.register("TELEPORT", cfg -> {
            Map<?, ?> o = (Map<?, ?>) cfg.get("offset");
            return new ActionTeleport(
                    Double.parseDouble(o.get("x").toString()),
                    Double.parseDouble(o.get("y").toString()),
                    Double.parseDouble(o.get("z").toString()),
                    Double.parseDouble(o.get("yaw").toString()),
                    Double.parseDouble(o.get("pitch").toString())
            );
        });
    }


    public void reloadLuckyBlock() {
        reloadConfig();
        loadLuckyBlockConfig();
        loadDropPools();
    }

    public static Main getInstance() {
        return instance;
    }

    public LuckyBlockManager getLuckyBlockManager() {
        return luckyBlockManager;
    }

    public FileConfiguration getLuckyBlockConfig() {
        return luckyBlockConfig;
    }

    public DropPool getDropPool(String name) {
        return dropPools.get(name.toLowerCase());
    }


    private void saveResourceIfNotExists(String path) {
        File file = new File(getDataFolder(), path);
        if (!file.exists()) {
            saveResource(path, false);
        }
    }
}