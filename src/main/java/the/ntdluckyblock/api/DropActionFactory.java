package the.ntdluckyblock.api;

import the.ntdluckyblock.drop.DropAction;

import java.util.Map;

public interface DropActionFactory {

    /**
     * @param config yml 中 actions 里的单个 map
     * @return DropAction 实例
     */
    DropAction create(Map<?, ?> config);
}