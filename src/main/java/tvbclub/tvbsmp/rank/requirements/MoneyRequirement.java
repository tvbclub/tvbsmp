package tvbclub.tvbsmp.rank;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import tvbclub.tvbsmp.storage.PlayerData;

import java.util.Map;

public class MoneyRequirement implements RankRequirement {
    private final Economy econ;

    public MoneyRequirement(Economy econ) { this.econ = econ; }

    @Override
    public boolean matches(Player player, PlayerData data, Map<String, Object> conf, RankManager manager) {
        double required = ((Number) conf.getOrDefault("money", 0.0)).doubleValue();
        if (required <= 0) return true;
        if (econ == null) return false;
        double bal = econ.getBalance(player);
        return bal >= required;
    }

    @Override
    public String getFailureMessage(Map<String, Object> conf) {
        return "You don't have enough money to rank up.";
    }
}
