package com.example.rankup;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;
import java.util.UUID;

public class PlaytimeListener implements Listener {
    private final TvbSmpPlugin plugin;
    public PlaytimeListener(TvbSmpPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        UUID u = e.getPlayer().getUniqueId();
        PlayerData d = plugin.getOrCreatePlayerData(u);
        d.setLastJoinTimestamp(Instant.now().getEpochSecond());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        UUID u = e.getPlayer().getUniqueId();
        PlayerData d = plugin.getOrCreatePlayerData(u);
        long now = Instant.now().getEpochSecond();
        long join = d.getLastJoinTimestamp();
        if (join > 0) {
            d.addSessionSeconds(now - join);
            d.setLastJoinTimestamp(0);
            plugin.savePlayerData(u, d);
        }
    }
}
