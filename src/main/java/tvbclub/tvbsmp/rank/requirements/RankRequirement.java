package tvbclub.tvbsmp.rank;

import org.bukkit.entity.Player;
import tvbclub.tvbsmp.storage.PlayerData;

import java.util.Map;

public interface RankRequirement {
    boolean matches(Player player, PlayerData data, Map<String, Object> requirementConfig, RankManager manager);
    String getFailureMessage(Map<String, Object> requirementConfig);
}
