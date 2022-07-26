package dev.abbysrc.evently.events;

import dev.abbysrc.evently.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.List;

public interface AdminEvent {

    void start();
    void onEnd(Player w);


    default void addPlayer(Player p) {
        getPlayers().add(p);
        getHost().sendMessage(
                MiniMessage.miniMessage().deserialize(
                        Config.lang().generics().get("joined_event"), Placeholder.component("player", Component.text(p.getName()))
                )
        );
    }

    default void removePlayer(Player p) {
        getPlayers().remove(p);
        getHost().sendMessage(
                MiniMessage.miniMessage().deserialize(
                        Config.lang().generics().get("left_event"), Placeholder.component("player", Component.text(p.getName()))
                )
        );
    }

    Date getStart();
    String getEventName();
    Player getHost();
    List<Player> getPlayers();

    State getState();
    void setState(State s);

    default Config.Lang.Event lang() {
        return Config.lang().event(getEventName());
    }

    enum State {
        WAITING, QUEUEING, ACTIVE, ENDED
    }

}
