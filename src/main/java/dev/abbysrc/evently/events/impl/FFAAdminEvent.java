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
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
public class FFAAdminEvent implements AdminEvent, Listener {

    // Eventually these will be loaded from a config file
    @Getter(AccessLevel.NONE)
    private final String WORLD_NAME = "admin";
    @Getter(AccessLevel.NONE)
    private final float SPAWN_POINT_X = 0;
    @Getter(AccessLevel.NONE)
    private final float SPAWN_POINT_Y = -60;
    @Getter(AccessLevel.NONE)
    private final float SPAWN_POINT_Z = 0;

    private final Player host;
    private final Date start;
    private final List<Player> players = new ArrayList<>();
    private final List<Player> eliminated = new ArrayList<>();

    private State state = State.WAITING;

    public FFAAdminEvent(Player h, Date s) {
        Bukkit.getPluginManager().registerEvents(this, EventlyCore.getInstance());
        host = h;
        start = s;
        players.add(h);
    }

    @Override
    public void start() {
        try {
            state = State.ACTIVE;
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

                EntityEquipment equ = p.getEquipment();
                equ.setHelmet(new ItemStack(Material.LEATHER_HELMET));
                equ.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
                equ.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
                equ.setBoots(new ItemStack(Material.LEATHER_BOOTS));

                p.getInventory().addItem(
                    new ItemStack(Material.STONE_SWORD),
                    new ItemStack(Material.BOW),
                    new ItemStack(Material.ARROW, 5)
                );

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

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        Player p = e.getPlayer();

        if (getState() == State.ACTIVE && players.contains(p)) {
            e.deathMessage(Component.empty());
            players.forEach(pl -> pl.sendMessage(
                    lang().getWithLegacyCodes("elimination")
            ));

            eliminated.add(e.getPlayer());

            if (eliminated.size() == (players.size() - 1)) {
                for (Player pl : players) {
                    if (!eliminated.contains(pl)) {
                        onEnd(pl);
                    }
                }
            }

            p.setGameMode(GameMode.SURVIVAL);
            p.getInventory().clear();

            EventlyPlayer ep = EventlyCore.getPlayerManager().get(p);
            p.teleport(ep.getLastLocation());
            ep.setLastLocation(null);


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

    @Override
    public void setState(State s) {
        state = s;
    }

}
