package com.example.rankup;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private long totalPlayTimeSeconds = 0;
    private long lastJoinTimestamp = 0;
    private final Map<Material, Integer> mined = new HashMap<>();
    private String rank = "default";

    public PlayerData(UUID uuid) { this.uuid = uuid; }

    public void setLastJoin(long epochSec) { this.lastJoinTimestamp = epochSec; }
    public void setLastJoinTimestamp(long epochSec) { this.lastJoinTimestamp = epochSec; }
    public long getLastJoinTimestamp() { return lastJoinTimestamp; }
    public void addSessionSeconds(long seconds) { this.totalPlayTimeSeconds += seconds; }
    public long getTotalPlayTimeSeconds() { return totalPlayTimeSeconds; }

    public void incrementMined(Material m) { mined.put(m, mined.getOrDefault(m, 0) + 1); }
    public int getMinedCount(Material m) { return mined.getOrDefault(m, 0); }

    public String getRank() { return rank; }
    public void setRank(String r) { this.rank = r; }

    public Map<String,Object> serialize() {
        Map<String,Object> out = new HashMap<>();
        out.put("playtime", totalPlayTimeSeconds);
        Map<String,Integer> m = new HashMap<>();
        for (Map.Entry<Material,Integer> e : mined.entrySet()) m.put(e.getKey().name(), e.getValue());
        out.put("mined", m);
        out.put("rank", rank);
        return out;
    }
    @SuppressWarnings("unchecked")
    public void deserialize(Map<String,Object> in) {
        if (in == null) return;
        if (in.containsKey("playtime")) totalPlayTimeSeconds = ((Number)in.get("playtime")).longValue();
        if (in.containsKey("rank")) rank = in.get("rank").toString();
        Object mm = in.get("mined");
        if (mm instanceof Map) {
            Map<String,Object> map = (Map<String,Object>) mm;
            mined.clear();
            for (Map.Entry<String,Object> e : map.entrySet()) {
                Material mat = Material.matchMaterial(e.getKey());
                if (mat != null) mined.put(mat, ((Number)e.getValue()).intValue());
            }
        }
    }
}
