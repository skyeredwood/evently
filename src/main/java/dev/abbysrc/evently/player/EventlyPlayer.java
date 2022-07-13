package dev.abbysrc.evently.player;

import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import net.ess3.api.IWarps;
import dev.abbysrc.evently.EventlyCore;
import dev.abbysrc.evently.hook.EssentialsHook;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class EventlyPlayer {

    public Player player;
    @Nullable
    public Location lastLocation;
    public boolean afk;

    public EventlyPlayer(Player p) {
        player = p;
        lastLocation = null;
        afk = false;
    }

    private BukkitTask afkCheckTask;

    public void activity() {
        afkCheckTask = new BukkitRunnable() {
            @Override
            public void run() {
                setAfk(true);

                // Essentials API is not at all intuitive. I hate my life
                EssentialsHook h = EventlyCore.getHook("Essentials", EssentialsHook.class);
                User u = h.esxInstance.getUser(player);
                u.getAsyncTeleport().warp(
                        u,
                        "afk",
                        new Trade(BigDecimal.valueOf(0), h.esxInstance),
                        PlayerTeleportEvent.TeleportCause.PLUGIN,
                        new CompletableFuture<>());
            }
        }.runTaskLater(EventlyCore.getInstance(), 300 * 20);
    }

}
