package dev.abbysrc.evently.events.impl;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import dev.abbysrc.evently.EventlyCore;
import dev.abbysrc.evently.events.AdminEvent;
import dev.abbysrc.evently.hook.ExcellentCratesHook;
import dev.abbysrc.evently.hook.MultiverseHook;
import dev.abbysrc.evently.player.EventlyPlayer;
import lombok.AccessLevel;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
public class FFAAdminEvent implements AdminEvent, Listener {

    @Getter(AccessLevel.NONE)
    private final String WORLD_NAME = "admin";
    @Getter(AccessLevel.NONE)
    private final float SPAWN_POINT_X = 0;
    @Getter(AccessLevel.NONE)
    private final float SPAWN_POINT_Y = 80;
    @Getter(AccessLevel.NONE)
    private final float SPAWN_POINT_Z = 0;

    private final Player host;
    private final Date start;
    private final List<Player> players = new ArrayList<>();
    private final List<Player> eliminated = new ArrayList<>();

    public FFAAdminEvent(Player h, Date s) {
        Bukkit.getPluginManager().registerEvents(this, EventlyCore.getInstance());

        host = h;
        start = s;
        players.add(h);
    }

    @Override
    public void start() {
        MVWorldManager worldManager = EventlyCore.getHook("MultiverseCore", MultiverseHook.class).mvInstance.getMVWorldManager();
        MultiverseWorld world = worldManager.getMVWorld(WORLD_NAME);
        for (Player p : players) {

            EventlyCore.getPlayerManager().get(p).setLastLocation(p.getLocation());

            p.teleport(new Location(
                world.getCBWorld(),
                SPAWN_POINT_X,
                SPAWN_POINT_Y,
                SPAWN_POINT_Z
            ));

            p.sendMessage(
                    MiniMessage.miniMessage().deserialize(
                            EventlyCore.prefix() + " Welcome to FFA: the aim is to survive, and the last player alive wins. Good luck!"
                    )
            );
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.deathMessage(Component.text().build());

        e.getPlayer().setGameMode(GameMode.SPECTATOR);
        players.forEach(p -> p.sendMessage(
                MiniMessage.miniMessage().deserialize(
                        EventlyCore.prefix() + e.getPlayer().getName() + " has been eliminated."
                )
        ));

        eliminated.add(e.getPlayer());

        if (eliminated.size() == (players.size() - 1)) {
            for (Player p : players) {
                if (!eliminated.contains(p)) {
                    onEnd(p);
                }
            }
        }
    }

    @Override
    public void onEnd(Player w) {
        EventlyCore.getHook("ExcellentCrates", ExcellentCratesHook.class)
                .giveAdminEventCrate(w);

        for (Player p : players) {
            EventlyPlayer ep = EventlyCore.getPlayerManager().get(p);
            p.teleport(ep.getLastLocation());
            ep.setLastLocation(null);
            p.sendMessage(
                    MiniMessage.miniMessage().deserialize(
                            EventlyCore.prefix() + " The winner is " + w.getName() + " - congratulations!"
                    )
            );
        }
    }

    @Override
    public void addPlayer(Player p) {
        players.add(p);
        host.sendMessage(
            MiniMessage.miniMessage().deserialize(
                EventlyCore.prefix() + " <player> joined the event.", Placeholder.component("player", Component.text(p.getName()))
            )
        );
    }

    @Override
    public String getEventName() {
        return "FFA";
    }

    @Override
    public Date getStart() {
        return start;
    }

    @Override
    public Player getHost() {
        return host;
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getEliminatedPlayers() {
        return eliminated;
    }
}
