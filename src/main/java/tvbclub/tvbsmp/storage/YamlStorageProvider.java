package tvbclub.tvbsmp.storage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class YamlStorageProvider {
    private final JavaPlugin plugin;
    private File playersFile;
    private YamlConfiguration playersConfig;

    public YamlStorageProvider(JavaPlugin plugin) {
        this.plugin = plugin;
        playersFile = new File(plugin.getDataFolder(), "players.yml");
        if (!playersFile.exists()) {
            playersFile.getParentFile().mkdirs();
            try { playersFile.createNewFile(); } catch (IOException ignored) {}
        }
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
    }

    @SuppressWarnings("unchecked")
    public void loadAll(Map<UUID, PlayerData> out) {
        for (String key : playersConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                Object raw = playersConfig.get(key);
                if (raw instanceof Map) {
                    PlayerData d = new PlayerData(uuid);
                    d.deserialize((Map<String,Object>) raw);
                    out.put(uuid, d);
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(UUID uuid, PlayerData data) {
        playersConfig.set(uuid.toString(), data.serialize());
        try { playersConfig.save(playersFile); } catch (IOException e) { plugin.getLogger().severe("Failed to save player data: " + e.getMessage()); }
    }

    public void saveAll(Map<UUID, PlayerData> all) {
        for (Map.Entry<UUID, PlayerData> e : all.entrySet()) playersConfig.set(e.getKey().toString(), e.getValue().serialize());
        try { playersConfig.save(playersFile); } catch (IOException ex) { plugin.getLogger().severe("Failed to save players file: " + ex.getMessage()); }
    }
}
