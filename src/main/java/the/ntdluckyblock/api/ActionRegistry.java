package the.ntdluckyblock.api;

import java.util.HashMap;
import java.util.Map;

public final class ActionRegistry {

    private static final Map<String, DropActionFactory> FACTORIES = new HashMap<>();

    private ActionRegistry() {}

    public static void register(String type, DropActionFactory factory) {
        FACTORIES.put(type.toUpperCase(), factory);
    }

    public static DropActionFactory get(String type) {
        return FACTORIES.get(type.toUpperCase());
    }

    public static boolean has(String type) {
        return FACTORIES.containsKey(type.toUpperCase());
    }
    public static void clear() {
        FACTORIES.clear();
    }
}