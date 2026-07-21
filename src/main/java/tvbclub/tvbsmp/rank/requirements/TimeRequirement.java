package tvbclub.tvbsmp.rank;

import org.bukkit.entity.Player;
import tvbclub.tvbsmp.storage.PlayerData;

import java.util.Map;

public class TimeRequirement implements RankRequirement {
    @Override
    public boolean matches(Player player, PlayerData data, Map<String, Object> conf, RankManager manager) {
        long required = ((Number) conf.getOrDefault("time", 0)).longValue();
        return data.getTotalPlayTimeSeconds() >= required;
    }

    @Override
    public String getFailureMessage(Map<String, Object> conf) {
        return "You need more playtime to rank up.";
    }
}
