package tvbclub.tvbsmp.rank;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import tvbclub.tvbsmp.storage.PlayerData;

import java.util.Map;

public class MineBlockRequirement implements RankRequirement {
    @Override
    public boolean matches(Player player, PlayerData data, Map<String, Object> conf, RankManager manager) {
        Object raw = conf.get("mined");
        if (!(raw instanceof Map)) return true;
        Map<?,?> req = (Map<?,?>) raw;
        for (Map.Entry<?,?> e : req.entrySet()) {
            String matName = e.getKey().toString();
            int need = ((Number)e.getValue()).intValue();
            Material mat = Material.matchMaterial(matName);
            if (mat == null) return false;
            if (data.getMinedCount(mat) < need) return false;
        }
        return true;
    }

    @Override
    public String getFailureMessage(Map<String, Object> conf) {
        return "You haven't mined enough required blocks to rank up.";
    }
}
