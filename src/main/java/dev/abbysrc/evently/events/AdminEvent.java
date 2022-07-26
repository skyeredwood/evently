package dev.abbysrc.evently.events;

import org.bukkit.entity.Player;

import java.util.Date;
import java.util.List;

public interface AdminEvent {

    void start();
    void onEnd(Player w);

    void addPlayer(Player p);
    void removePlayer(Player p);

    Date getStart();
    String getEventName();
    Player getHost();
    List<Player> getPlayers();

    boolean isDisabled();
    void disable();

}
