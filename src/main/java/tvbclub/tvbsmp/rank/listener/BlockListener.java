package tvbclub.tvbsmp.rank.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import tvbclub.tvbsmp.TvbSmpPlugin;
import tvbclub.tvbsmp.storage.PlayerData;

import java.util.UUID;

public class BlockListener implements Listener {
    private final TvbSmpPlugin plugin;
    public BlockListener(TvbSmpPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        UUID u = e.getPlayer().getUniqueId();
        PlayerData d = plugin.getOrCreatePlayerData(u);
        Material m = e.getBlock().getType();
        d.incrementMined(m);
    }
}
