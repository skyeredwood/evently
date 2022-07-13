package dev.abbysrc.evently.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import dev.abbysrc.evently.EventlyCore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

@CommandAlias("joinevent|eventjoin|eventlyjoin")
public class JoinEventCommand extends BaseCommand {

    @Default
    @Subcommand("join")
    @Description("Joins an event")
    public static void handleJoin(Player p, String[] args) {
        if (EventlyCore.getAdminEventManager().getCurrentEvent() == null) {
            p.sendMessage(
                    MiniMessage.miniMessage().deserialize(EventlyCore.prefix() + " <red>There are no events going on currently!</red>")
            );
        } else {
            EventlyCore.getAdminEventManager().getCurrentEvent().addPlayer(p);
            p.sendMessage(
                    MiniMessage.miniMessage().deserialize(EventlyCore.prefix() + " Joined the event!")
            );
        }
    }

}
