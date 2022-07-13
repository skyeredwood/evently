package dev.abbysrc.evently.player;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;

public class EventlyPlayerManager {

    public HashMap<Player, EventlyPlayer> players = new HashMap<>();

    public EventlyPlayer create(Player p) {
        return players.put(
                p,
                new EventlyPlayer(p)
        );
    }

    public void delete(Player p) {
        players.remove(p);
    }

    @Nullable
    public EventlyPlayer get(Player p) {
        return players.get(p);
    }

}
