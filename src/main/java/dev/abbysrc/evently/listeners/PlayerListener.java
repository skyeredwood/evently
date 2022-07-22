package dev.abbysrc.evently.listeners;

import dev.abbysrc.evently.EventlyCore;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        EventlyCore.getPlayerManager().create(e.getPlayer());
        if (EventlyCore.getLogoutLocations().containsValue(e.getPlayer().getName())) {
            e.getPlayer().teleport(
                    EventlyCore.getLogoutLocations().get(e.getPlayer().getName())
            );
        }
    }


    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        if (EventlyCore.getPlayerManager().get(e.getPlayer()).isAfk()) {
            EventlyCore.getLogoutLocations().put(
                    e.getPlayer().getName(),
                    EventlyCore.getPlayerManager().get(e.getPlayer()).lastLocation
            );
        }
        EventlyCore.getPlayerManager().delete(e.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        EventlyCore.getPlayerManager().get(e.getPlayer()).activity();
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncChatEvent e) {
        EventlyCore.getPlayerManager().get(e.getPlayer()).activity();
    }

}
