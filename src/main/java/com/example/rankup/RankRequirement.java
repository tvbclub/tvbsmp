package com.example.rankup;

import org.bukkit.entity.Player;
import java.util.Map;

public interface RankRequirement {
    boolean matches(Player player, PlayerData data, Map<String, Object> requirementConfig, RankManager manager);
    String getFailureMessage(Map<String, Object> requirementConfig);
}
