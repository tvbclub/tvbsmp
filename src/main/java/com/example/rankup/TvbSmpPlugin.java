package com.example.rankup;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TvbSmpPlugin extends JavaPlugin {
    private final Map<UUID, PlayerData> players = new HashMap<>();
    private File playersFile;
    private YamlConfiguration playersConfig;
    private RankManager rankManager;
    private Economy econ;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupVault();
        rankManager = new RankManager(this, econ);
        rankManager.loadConfig(getConfig());

        loadPlayers();

        getServer().getPluginManager().registerEvents(new PlaytimeListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getCommand("rankup").setExecutor(new RankCommand(this));

        // periodic autosave
        new BukkitRunnable() {
            @Override
            public void run() {
                saveAllPlayerData();
            }
        }.runTaskTimerAsynchronously(this, 20L * 60 * 5, 20L * 60 * 5); // every 5 minutes

        getLogger().info("TvbSmpRankup enabled");
    }

    @Override
    public void onDisable() {
        saveAllPlayerData();
        getLogger().info("TvbSmpRankup disabled");
    }

    private void setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault not found - money requirements will not work");
            econ = null;
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().warning("No Economy provider found through Vault");
            econ = null;
            return;
        }
        econ = rsp.getProvider();
    }

    public PlayerData getOrCreatePlayerData(UUID uuid) {
        return players.computeIfAbsent(uuid, PlayerData::new);
    }

    public void savePlayerData(UUID uuid, PlayerData data) {
        if (playersConfig == null || playersFile == null) return;
        playersConfig.set(uuid.toString(), data.serialize());
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            getLogger().severe("Failed to save player data: " + e.getMessage());
        }
    }

    public void saveAllPlayerData() {
        if (playersConfig == null || playersFile == null) return;
        for (Map.Entry<UUID, PlayerData> e : players.entrySet()) playersConfig.set(e.getKey().toString(), e.getValue().serialize());
        try {
            playersConfig.save(playersFile);
        } catch (IOException ex) {
            getLogger().severe("Failed to save players file: " + ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadPlayers() {
        playersFile = new File(getDataFolder(), "players.yml");
        if (!playersFile.exists()) {
            playersFile.getParentFile().mkdirs();
            try {
                playersFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("Could not create players.yml: " + e.getMessage());
            }
        }
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
        for (String key : playersConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                Object raw = playersConfig.get(key);
                if (raw instanceof java.util.Map) {
                    PlayerData d = new PlayerData(uuid);
                    d.deserialize((Map<String,Object>) raw);
                    players.put(uuid, d);
                }
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public RankManager getRankManager() { return rankManager; }

    public void reloadRankConfig() {
        reloadConfig();
        rankManager.loadConfig(getConfig());
    }
}
