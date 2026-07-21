package tvbclub.tvbsmp.rank.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tvbclub.tvbsmp.TvbSmpPlugin;
import tvbclub.tvbsmp.storage.PlayerData;

import java.util.ArrayList;
import java.util.List;

public class RankCommand implements CommandExecutor {
    private final TvbSmpPlugin plugin;

    public RankCommand(TvbSmpPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("Only players."); return true; }
        Player p = (Player) sender;
        PlayerData d = plugin.getOrCreatePlayerData(p.getUniqueId());
        List<String> fails = new ArrayList<>();
        if (!plugin.getRankManager().canRankUp(p, d, fails)) {
            p.sendMessage("Cannot rank up: " + String.join(", ", fails));
            return true;
        }
        boolean ok = plugin.getRankManager().performRankup(p, d);
        if (ok) p.sendMessage("Rank up successful! New rank: " + d.getRank());
        else p.sendMessage("Rank up failed during execution.");
        return true;
    }
}
