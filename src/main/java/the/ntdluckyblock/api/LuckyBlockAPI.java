package the.ntdluckyblock.api;

public final class LuckyBlockAPI {

    private LuckyBlockAPI() {}

    public static void registerDropAction(String type, DropActionFactory factory) {
        ActionRegistry.register(type, factory);
    }
}