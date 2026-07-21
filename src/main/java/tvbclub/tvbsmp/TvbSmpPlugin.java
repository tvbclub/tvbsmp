package tvbclub.tvbsmp;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;
import tvbclub.tvbsmp.rank.RankManager;
import tvbclub.tvbsmp.rank.listener.BlockListener;
import tvbclub.tvbsmp.rank.listener.PlaytimeListener;
import tvbclub.tvbsmp.rank.command.RankCommand;
import tvbclub.tvbsmp.storage.PlayerData;
import tvbclub.tvbsmp.storage.YamlStorageProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TvbSmpPlugin extends JavaPlugin {
    private final Map<UUID, PlayerData> players = new HashMap<>();
    private YamlStorageProvider storage;
    private RankManager rankManager;
    private Economy econ;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupVault();
        storage = new YamlStorageProvider(this);
        rankManager = new RankManager(this, econ);
        rankManager.loadConfig(getConfig());

        // load players
        storage.loadAll(players);

        getServer().getPluginManager().registerEvents(new PlaytimeListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getCommand("rankup").setExecutor(new RankCommand(this));

        getLogger().info("TvbSmpRankup enabled");
    }

    @Override
    public void onDisable() {
        storage.saveAll(players);
        getLogger().info("TvbSmpRankup disabled");
    }

    private void setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault not found - money requirements will not work");
            econ = null;
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) { getLogger().warning("No Economy provider found through Vault"); econ = null; return; }
        econ = rsp.getProvider();
    }

    public PlayerData getOrCreatePlayerData(UUID uuid) { return players.computeIfAbsent(uuid, PlayerData::new); }
    public void savePlayerData(UUID uuid, PlayerData data) { storage.save(uuid, data); }
    public void saveAllPlayerData() { storage.saveAll(players); }

    public RankManager getRankManager() { return rankManager; }
}
