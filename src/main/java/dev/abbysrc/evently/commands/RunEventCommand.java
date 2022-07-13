package dev.abbysrc.evently.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.abbysrc.evently.EventlyCore;
import dev.abbysrc.evently.events.impl.FFAAdminEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

@CommandAlias("runevent|eventrun|startevent|eventstart")
@CommandPermission("events.start")
public class RunEventCommand extends BaseCommand {

    @Default
    @Subcommand("run")
    @Description("Starts an event")
    public static void startEvent(Player p, String[] args) {
        if (args.length == 0) {
            p.sendMessage(
                    MiniMessage.miniMessage().deserialize(EventlyCore.prefix() + " <red>Missing arguments!</red>")
            );
        } else {
            // Using a switch statement despite IntelliJ yelling so we can add future events.
            switch (args[0].toLowerCase()) {
                case "ffa" -> {
                    FFAAdminEvent e = EventlyCore.getAdminEventManager().startEvent(p, FFAAdminEvent.class);
                    if (e == null) {
                        p.sendMessage(
                                MiniMessage.miniMessage().deserialize(EventlyCore.prefix() + " <red>An issue occured while starting that event!</red>")
                        );
                    } else {
                        p.sendMessage(
                                MiniMessage.miniMessage().deserialize(EventlyCore.prefix() + " An event has been started! Online players can join it in around 5 minutes.")
                        );
                    }
                }
                default -> p.sendMessage(
                                MiniMessage.miniMessage().deserialize(EventlyCore.prefix() + " <red>Invalid event type!</red>")
                        );
            }
        }
    }

}
