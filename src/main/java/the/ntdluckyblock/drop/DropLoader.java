package the.ntdluckyblock.drop;

import org.bukkit.configuration.file.YamlConfiguration;
import the.ntdluckyblock.Main;
import the.ntdluckyblock.api.ActionRegistry;
import the.ntdluckyblock.api.DropActionFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DropLoader {

    private static final Main plugin = Main.getInstance();

    public static DropPool load(File file) {
        DropPool pool = new DropPool();
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        for (Map<?, ?> sec : cfg.getMapList("drops")) {
            int weight = (int) sec.get("weight");

            List<DropAction> actions = new ArrayList<>();
            List<?> actionList = (List<?>) sec.get("actions");

            for (Object obj : actionList) {
                Map<?, ?> map = (Map<?, ?>) obj;
                String type = map.get("type").toString().toUpperCase();

                DropActionFactory factory = ActionRegistry.get(type);
                if (factory == null) {
                    plugin.getLogger().warning("未知的掉落类型: " + type);
                    continue;
                }

                try {
                    actions.add(factory.create(map));
                } catch (Exception e) {
                    plugin.getLogger().warning("加载掉落类型失败: " + type);
                    e.printStackTrace();
                }
            }

            pool.register(new DropEntry(weight, actions));
        }

        return pool;
    }
}