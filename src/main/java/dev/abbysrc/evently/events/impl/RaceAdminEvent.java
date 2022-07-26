package dev.abbysrc.evently.events.impl;

import dev.abbysrc.evently.EventlyCore;
import dev.abbysrc.evently.config.Config;
import dev.abbysrc.evently.events.AdminEvent;
import dev.abbysrc.evently.hook.ExcellentCratesHook;
import dev.abbysrc.evently.player.EventlyPlayer;
import dev.abbysrc.evently.player.inventory.SavedInventory;
import lombok.AccessLevel;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Getter
public class RaceAdminEvent implements AdminEvent, Listener {

    // Eventually these will be loaded from a config file
    @Getter(AccessLevel.NONE)
    private final String WORLD_NAME = "admin";
    @Getter(AccessLevel.NONE)
    private final float SPAWN_POINT_X = 0;
    @Getter(AccessLevel.NONE)
    private final float SPAWN_POINT_Y = -60;
    @Getter(AccessLevel.NONE)
    private final float SPAWN_POINT_Z = 0;

    private final Location CP_V1 = new Location(
            Bukkit.getWorld(WORLD_NAME),
            5, -60, 5
    );

    private final Location CP_V2_1 = new Location(
            Bukkit.getWorld(WORLD_NAME),
            10, -60, 10
    );

    private final Location CP_V2_2 = new Location(
            Bukkit.getWorld(WORLD_NAME),
            15, -60, 15
    );

    private BukkitTask lifecycleTask;

    private final Player host;
    private final Date start;
    private final List<Player> players = new ArrayList<>();

    private final Map<Player, Integer> stages = new HashMap<>();

    private State state = State.WAITING;

    public RaceAdminEvent(Player h, Date s) {
        Bukkit.getPluginManager().registerEvents(this, EventlyCore.getInstance());
        host = h;
        start = s;
        players.add(h);
    }

    @Override
    public void start() {
        try {
            setState(State.ACTIVE);
            lifecycleTask = new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player p : players) {
                        int stage = stages.get(p);
                        if (stage == 0) {
                            double distance = p.getLocation().distance(CP_V1);
                            int sections = (int) Math.floor(distance % 3);

                            Location clone = CP_V1;
                            clone.subtract(p.getLocation().toVector());
                            double sectionX = clone.getX() / sections;
                            double sectionY = clone.getY() / sections;
                            double sectionZ = clone.getZ() / sections;

                            for (int i = 0; i == sections; i++) {
                                p.getWorld().spawnParticle(
                                        Particle.REDSTONE,
                                        new Location(
                                                Bukkit.getWorld(WORLD_NAME),
                                                sectionX * i + 1,
                                                sectionY * i + 1,
                                                sectionZ * i + 1
                                        ),
                                        1,
                                        new Particle.DustOptions(Color.fromBGR(133, 232, 29), 1)
                                );
                            }
                        } else if (stage == 1) {
                            double distance = p.getLocation().distance(CP_V2_1);
                            int sections = (int) Math.floor(distance % 3);

                            Location clone = CP_V2_1;
                            clone.subtract(p.getLocation().toVector());
                            double sectionX = clone.getX() / sections;
                            double sectionY = clone.getY() / sections;
                            double sectionZ = clone.getZ() / sections;

                            for (int i = 0; i == sections; i++) {
                                p.getWorld().spawnParticle(
                                        Particle.REDSTONE,
                                        new Location(
                                                Bukkit.getWorld(WORLD_NAME),
                                                sectionX * i + 1,
                                                sectionY * i + 1,
                                                sectionZ * i + 1
                                        ),
                                        1,
                                        new Particle.DustOptions(Color.fromBGR(133, 232, 29), 1)
                                );
                            }
                        } else if (stage == 2) {

                            double distance = p.getLocation().distance(CP_V2_2);
                            int sections = (int) Math.floor(distance % 3);

                            Location clone = CP_V2_2;
                            clone.subtract(p.getLocation().toVector());
                            double sectionX = clone.getX() / sections;
                            double sectionY = clone.getY() / sections;
                            double sectionZ = clone.getZ() / sections;

                            for (int i = 0; i == sections; i++) {
                                p.getWorld().spawnParticle(
                                        Particle.REDSTONE,
                                        new Location(
                                                Bukkit.getWorld(WORLD_NAME),
                                                sectionX * i + 1,
                                                sectionY * i + 1,
                                                sectionZ * i + 1
                                        ),
                                        1,
                                        new Particle.DustOptions(Color.fromBGR(133, 232, 29), 1)
                                );
                            }
                        }
                    }
                }
            }.runTaskTimer(EventlyCore.getInstance(), 0, 20);

            for (Player p : players) {
                EventlyCore.getPlayerManager().get(p).setLastLocation(p.getLocation());
                SavedInventory.save(p);

                p.teleport(new Location(
                        Bukkit.getWorld(WORLD_NAME),
                        SPAWN_POINT_X,
                        SPAWN_POINT_Y,
                        SPAWN_POINT_Z
                ));

                p.getInventory().clear();

                p.sendMessage(
                    lang().getWithLegacyCodes("welcome")
                );

                p.sendMessage(MiniMessage.miniMessage().deserialize(
                "<yellow>Follow the particles to reach the next checkpoint."
                ));

                stages.put(p, 0);
            }

        } catch (NullPointerException e) {
            getPlayers().forEach(p -> p.sendMessage(
                    Config.lang().generics().get("event_teleport_error")
            ));
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        Player p = e.getPlayer();

        if (getState() == State.ACTIVE && players.contains(p)) {
            Location l = e.getTo();
            if (l.getBlockX() == CP_V1.getBlockX() &&
                    l.getBlockY() == CP_V1.getBlockY() &&
                    l.getBlockZ() == CP_V1.getBlockZ()) {
                players.forEach(pl -> pl.sendMessage(
                        MiniMessage.miniMessage().deserialize(
                                "<dark_green>" + p.getName() + " has reached Checkpoint #1!</dark_green>"
                        )
                ));
                stages.replace(p, 1);
            } else if (l.getBlockX() == CP_V2_1.getBlockX() &&
                    l.getBlockY() == CP_V2_1.getBlockY() &&
                    l.getBlockZ() == CP_V2_1.getBlockZ()) {
                players.forEach(pl -> pl.sendMessage(
                        MiniMessage.miniMessage().deserialize(
                                "<green>" + p.getName() + " has reached Checkpoint #2!</gold>"
                        )
                ));
                stages.replace(p, 2);
            } else if (l.getBlockX() == CP_V2_2.getBlockX() &&
                    l.getBlockZ() == CP_V2_2.getBlockZ()) {
                onEnd(p);
            }
        }
    }

    @Override
    public void onEnd(Player w) {
        EventlyCore.getHook("ExcellentCrates", ExcellentCratesHook.class)
                .giveAdminEventCrate(w);

        for (Player p : players) {
            p.setGameMode(GameMode.SURVIVAL);
            p.getInventory().clear();

            EventlyPlayer ep = EventlyCore.getPlayerManager().get(p);
            if (p == w) {
                Bukkit.dispatchCommand(w, "spawn");
            } else p.teleport(
                    ep.getLastLocation() == null
                            ? new Location(Bukkit.getWorld("admin"), 0, -60,0)
                            : ep.getLastLocation()
            );
            ep.setLastLocation(null);

            SavedInventory.get(p).apply(p);

            p.sendMessage(
                    MiniMessage.miniMessage().deserialize(
                            lang().get("winner"), Placeholder.component("player", Component.text(w.getName()))
                    )
            );
        }

        EventlyCore.getAdminEventManager().endCurrentEvent();
        setState(State.ENDED);
    }

    @Override
    public String getEventName() {
        return "Race";
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

    @Override
    public void setState(State s) {
        state = s;
    }

}

