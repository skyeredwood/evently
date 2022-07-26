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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class KOTHAdminEvent implements AdminEvent, Listener {

    // Eventually these will be loaded from a config file
    @Getter(AccessLevel.NONE)
    private final String WORLD_NAME = "admin";
    @Getter(AccessLevel.NONE)
    private final float SPAWN_POINT_X = 2;
    @Getter(AccessLevel.NONE)
    private final float SPAWN_POINT_Y = -53;
    @Getter(AccessLevel.NONE)
    private final float SPAWN_POINT_Z = 53;
    @Getter(AccessLevel.NONE)
    private final Location KOTH_POINT = new Location(
            Bukkit.getWorld("admin"),
            2,
            -53,
            53
    );

    private final Player host;
    private final Date start;
    private final List<Player> players = new ArrayList<>();

    private BukkitTask lifecycleTask;
    private BukkitTask forceEndTask;

    private final Map<Player, Integer> points = new HashMap<>();

    private boolean disabled = false;

    public KOTHAdminEvent(Player h, Date s) {
        Bukkit.getPluginManager().registerEvents(this, EventlyCore.getInstance());

        host = h;
        start = s;
        players.add(h);
    }

    @Override
    public void start() {

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
                kbStick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 5);

                p.getInventory().addItem(kbStick);

                points.put(p, 0);

                p.sendMessage(
                        MiniMessage.miniMessage().deserialize(
                                EventlyCore.prefix() + " Welcome to KOTH: stay on the top of the hill the longest to win the game. Good luck!"
                        )
                );
            }

        } catch (NullPointerException e) {
            getPlayers().forEach(p -> p.sendMessage(
                    MiniMessage.miniMessage().deserialize(EventlyCore.prefix() + " <red>An issue occured teleporting players into the game!</red>")
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
                            EventlyCore.prefix() + " " + w.getName() + " is the King of the Hill with " + points.get(w) + " points - congratulations!"
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
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void disable() {
        disabled = true;
    }

}
