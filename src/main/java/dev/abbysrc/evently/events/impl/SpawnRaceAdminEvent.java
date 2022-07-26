package dev.abbysrc.evently.events.impl;

import dev.abbysrc.evently.EventlyCore;
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
public class SpawnRaceAdminEvent implements AdminEvent, Listener {

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

    private boolean disabled = false;

    public SpawnRaceAdminEvent(Player h, Date s) {
        Bukkit.getPluginManager().registerEvents(this, EventlyCore.getInstance());

        host = h;
        start = s;
        players.add(h);
    }

    @Override
    public void start() {
        try {

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
                                        Particle.DUST_COLOR_TRANSITION
                                                .builder()
                                                .color(Color.GREEN)
                                                .particle(),
                                        new Location(
                                                Bukkit.getWorld(WORLD_NAME),
                                                sectionX * i + 1,
                                                sectionY * i + 1,
                                                sectionZ * i + 1
                                        ),
                                        1
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
                                        Particle.DUST_COLOR_TRANSITION
                                                .builder()
                                                .color(Color.GREEN)
                                                .particle(),
                                        new Location(
                                                Bukkit.getWorld(WORLD_NAME),
                                                sectionX * i + 1,
                                                sectionY * i + 1,
                                                sectionZ * i + 1
                                        ),
                                        1
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
                                        Particle.DUST_COLOR_TRANSITION
                                                .builder()
                                                .color(Color.GREEN)
                                                .particle(),
                                        new Location(
                                                Bukkit.getWorld(WORLD_NAME),
                                                sectionX * i + 1,
                                                sectionY * i + 1,
                                                sectionZ * i + 1
                                        ),
                                        1
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
                        MiniMessage.miniMessage().deserialize(
                                EventlyCore.prefix() + " Welcome to Spawn Race: hurry through Nebula's spawnpoints, past and present, to win the race. Get running!"
                        )
                );

                p.sendMessage(
                        MiniMessage.miniMessage().deserialize(
                                EventlyCore.prefix() + " Follow the particles to reach the next checkpoint."
                        )
                );

                stages.put(p, 0);
            }

        } catch (NullPointerException e) {
            getPlayers().forEach(p -> p.sendMessage(
                    MiniMessage.miniMessage().deserialize(EventlyCore.prefix() + " <red>An issue occured teleporting players into the game!</red>")
            ));
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        Player p = e.getPlayer();

        if (!disabled && players.contains(p)) {
            Location l = e.getTo();
            if (l.getBlockX() == CP_V1.getBlockX() &&
                    l.getBlockY() == CP_V1.getBlockY() &&
                    l.getBlockZ() == CP_V1.getBlockZ()) {
                players.forEach(pl -> pl.sendMessage(
                        MiniMessage.miniMessage().deserialize(
                                EventlyCore.prefix() + " " + p.getName() + " has reached Checkpoint #1!"
                        )
                ));
                stages.replace(p, 1);
            } else if (l.getBlockX() == CP_V2_1.getBlockX() &&
                    l.getBlockY() == CP_V2_1.getBlockY() &&
                    l.getBlockZ() == CP_V2_1.getBlockZ()) {
                players.forEach(pl -> pl.sendMessage(
                        MiniMessage.miniMessage().deserialize(
                                EventlyCore.prefix() + " " + p.getName() + " has reached Checkpoint #2!"
                        )
                ));
                stages.replace(p, 2);
            } else if (l.getBlockX() == CP_V2_2.getBlockX() &&
                    l.getBlockY() == CP_V2_2.getBlockY() &&
                    l.getBlockZ() == CP_V2_2.getBlockZ()) {
                players.forEach(pl -> pl.sendMessage(
                        MiniMessage.miniMessage().deserialize(
                                EventlyCore.prefix() + " <green>" + p.getName() + " has won the race!</green>"
                        )
                ));
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
                            EventlyCore.prefix() + " " + w.getName() + " went turbo mode and came out victorious - congratulations!"
                    )
            );
        }

        EventlyCore.getAdminEventManager().endCurrentEvent();
        disable();
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
    public void removePlayer(Player p) {
        players.remove(p);
        host.sendMessage(
                MiniMessage.miniMessage().deserialize(
                        EventlyCore.prefix() + " <player> left the event.", Placeholder.component("player", Component.text(p.getName()))
                )
        );
    }

    @Override
    public String getEventName() {
        return "Spawn Race";
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
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void disable() {
        disabled = true;
    }

}

