package dev.abbysrc.evently.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import dev.abbysrc.evently.EventlyCore;
import dev.abbysrc.evently.events.AdminEvent;
import org.bukkit.entity.Player;

@CommandAlias("joinevent|eventjoin|eventlyjoin")
public class JoinEventCommand extends BaseCommandWithLang {

    public JoinEventCommand(String cn) {
        super(cn);
    }

    @Default
    @Subcommand("join")
    @Description("Joins an event")
    public static void handleJoin(Player p, String[] args) {
        if (EventlyCore.getAdminEventManager().getCurrentEvent() == null || EventlyCore.getAdminEventManager().getCurrentEvent().getState() != AdminEvent.State.QUEUEING) {
            p.sendMessage(
                    lang().getWithLegacyCodes("no_joinable_events")
            );
        } else if (EventlyCore.getAdminEventManager().getCurrentEvent().getPlayers().contains(p)) {
            p.sendMessage(
                    lang().getWithLegacyCodes("already_in_event")
            );
        } else {
                EventlyCore.getAdminEventManager().getCurrentEvent().addPlayer(p);
                p.sendMessage(
                        lang().getWithLegacyCodes("joined_event")
                );
        }
    }

}
