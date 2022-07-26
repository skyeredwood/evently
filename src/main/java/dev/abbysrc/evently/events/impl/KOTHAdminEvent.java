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
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Getter
public class KOTHAdminEvent implements AdminEvent, Listener {

    // Eventually these will be loaded from a config file
    @Getter(AccessLevel.NONE)
    private final String WORLD_NAME = EventlyCore.getConfiguration().getTable("config.event_koth").getString("world");
    @Getter(AccessLevel.NONE)
    private final double SPAWN_POINT_X = EventlyCore.getConfiguration().getTable("config.event_koth").getDouble("x");
    @Getter(AccessLevel.NONE)
    private final double SPAWN_POINT_Y = EventlyCore.getConfiguration().getTable("config.event_koth").getDouble("y");
    @Getter(AccessLevel.NONE)
    private final double SPAWN_POINT_Z = EventlyCore.getConfiguration().getTable("config.event_koth").getDouble("z");
    @Getter(AccessLevel.NONE)
    private final Location KOTH_POINT = new Location(
            Bukkit.getWorld(WORLD_NAME),
            SPAWN_POINT_X,
            SPAWN_POINT_Y,
            SPAWN_POINT_Z
    );

    private final Player host;
    private final Date start;
    private final List<Player> players = new ArrayList<>();

    private BukkitTask lifecycleTask;
    private BukkitTask forceEndTask;

    private final Map<Player, Integer> points = new HashMap<>();

    private State state = State.WAITING;

    public KOTHAdminEvent(Player h, Date s) {
        Bukkit.getPluginManager().registerEvents(this, EventlyCore.getInstance());

        host = h;
        start = s;
        players.add(h);
    }

    @Override
    public void start() {

        state = State.ACTIVE;

        forceEndTask = new BukkitRunnable() {
            @Override
            public void run() {
                var stats = new Object() {
                    Player heighest = null;
                    int heighestPoints = -1;
                };

                points.entrySet().forEach(s -> {
                    if (stats.heighest == null) {
                        stats.heighest = s.getKey();
                    }
                    if (stats.heighestPoints == -1) {
                        stats.heighestPoints = s.getValue();
                    }

                    if (s.getValue() > stats.heighestPoints) {
                        stats.heighest = s.getKey();
                        stats.heighestPoints = s.getValue();
                    }
                });

                onEnd(stats.heighest);
            }
        }.runTaskLater(EventlyCore.getInstance(), 20 * 60 * 2);

        lifecycleTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : getPlayers()) {
                    Location l = p.getLocation();
                    if (l.getBlockX() == KOTH_POINT.getBlockX()
                            && l.getBlockY() == KOTH_POINT.getBlockY()
                            && l.getBlockZ() == KOTH_POINT.getBlockZ()) {
                        p.playSound(
                            KOTH_POINT,
                            Sound.BLOCK_NOTE_BLOCK_BIT,
                            1.2f,
                            1.1f
                        );
                        points.replace(p, points.get(p) + 1);
                    }
                }
            }
        }.runTaskTimer(EventlyCore.getInstance(), 0, 20);

        try {
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

                ItemStack kbStick = new ItemStack(Material.STICK);
                kbStick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);

                p.getInventory().addItem(kbStick);

                points.put(p, 0);

                p.sendMessage(
                        lang().getWithLegacyCodes("welcome")
                );
            }

        } catch (NullPointerException e) {
            getPlayers().forEach(p -> p.sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize(
                    Config.lang().generics().get("event_lifecycle_error")
                )
            ));
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
            } else p.teleport(ep.getLastLocation());
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
        return "KOTH";
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
