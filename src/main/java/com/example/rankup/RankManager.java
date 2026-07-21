package com.example.rankup;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class RankManager {
    private final TvbSmpPlugin plugin;
    private final Map<String, Map<String, Object>> ranks = new LinkedHashMap<>();
    private final Economy econ;
    private final List<RankRequirement> requirements = new ArrayList<>();

    public RankManager(TvbSmpPlugin plugin, Economy econ) {
        this.plugin = plugin;
        this.econ = econ;
        requirements.add(new TimeRequirement());
        requirements.add(new MineBlockRequirement());
        requirements.add(new MoneyRequirement(econ));
    }

    public void loadConfig(FileConfiguration cfg) {
        ranks.clear();
        if (cfg == null) return;
        if (!cfg.isConfigurationSection("ranks")) return;
        for (String key : cfg.getConfigurationSection("ranks").getKeys(false)) {
            FileConfiguration rs = cfg.getConfigurationSection("ranks");
            // careful: getConfigurationSection returns nested; use existing API
            Object node = cfg.getConfigurationSection("ranks").get(key);
            // We'll read specific subkeys
            String next = cfg.getString("ranks." + key + ".next", "");
            Map<String,Object> map = new HashMap<>();
            map.put("next", next);
            if (cfg.isConfigurationSection("ranks." + key + ".requirements")) {
                map.put("requirements", cfg.getConfigurationSection("ranks." + key + ".requirements").getValues(false));
            } else {
                map.put("requirements", Collections.emptyMap());
            }
            map.put("promote-command", cfg.getString("ranks." + key + ".promote-command", ""));
            ranks.put(key, map);
        }
    }

    public Optional<String> getNextRank(String current) {
        Map<String,Object> data = ranks.get(current);
        if (data == null) return Optional.empty();
        String next = (String) data.get("next");
        if (next == null || next.isEmpty()) return Optional.empty();
        return Optional.of(next);
    }

    @SuppressWarnings("unchecked")
    public boolean canRankUp(Player p, PlayerData data, List<String> failReasons) {
        Optional<String> nextOpt = getNextRank(data.getRank());
        if (!nextOpt.isPresent()) {
            failReasons.add("You are at the highest rank.");
            return false;
        }
        String next = nextOpt.get();
        Map<String,Object> cfg = ranks.get(next);
        if (cfg == null) { failReasons.add("Rank config missing."); return false; }
        Map<String,Object> reqs = (Map<String,Object>) cfg.getOrDefault("requirements", Collections.emptyMap());
        for (RankRequirement r : requirements) {
            if (!r.matches(p, data, reqs, this)) {
                failReasons.add(r.getFailureMessage(reqs));
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean performRankup(Player p, PlayerData data) {
        Optional<String> nextOpt = getNextRank(data.getRank());
        if (!nextOpt.isPresent()) return false;
        String next = nextOpt.get();
        Map<String,Object> cfg = ranks.get(next);
        if (cfg == null) return false;
        Map<String,Object> reqs = (Map<String,Object>) cfg.getOrDefault("requirements", Collections.emptyMap());

        if (econ != null && reqs.containsKey("money")) {
            double amount = ((Number)reqs.get("money")).doubleValue();
            if (amount > 0) {
                if (!econ.withdrawPlayer(p, amount).transactionSuccess()) return false;
            }
        }

        data.setRank(next);
        plugin.savePlayerData(p.getUniqueId(), data);

        String cmd = (String) cfg.getOrDefault("promote-command", "");
        if (cmd != null && !cmd.isEmpty()) {
            cmd = cmd.replace("%player%", p.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }

        return true;
    }
}
