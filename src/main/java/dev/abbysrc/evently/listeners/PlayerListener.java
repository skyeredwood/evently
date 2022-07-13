package dev.abbysrc.evently.listeners;

import dev.abbysrc.evently.EventlyCore;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        EventlyCore.getPlayerManager().create(e.getPlayer());
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
