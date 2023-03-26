package group.aelysium.rustyconnector.core.lib.config;

import java.util.HashMap;
import java.util.Map;

public class MigrationDirections {
    private static final Map<Integer, String> directions = new HashMap<>();

    public static void init() {
        directions.put(1 + 2, "https://github.com/Aelysium-Group/rusty-connector/wiki/Update-from-Config-v1-to-v2");
    }

    public static String findUpgradeDirections(int from, int to)  {
        String url = directions.get(from + to);
        if(url == null) return "https://github.com/Aelysium-Group/rusty-connector/wiki/Config-Migration";
        return url;
    }
}