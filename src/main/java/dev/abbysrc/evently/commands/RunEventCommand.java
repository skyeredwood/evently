package dev.abbysrc.evently.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.abbysrc.evently.EventlyCore;
import dev.abbysrc.evently.events.impl.FFAAdminEvent;
import dev.abbysrc.evently.events.impl.KOTHAdminEvent;
import dev.abbysrc.evently.events.impl.SpawnRaceAdminEvent;
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
                    long timerLength = args.length == 2 && p.hasPermission("events.override-timer") ? Long.parseLong(args[1]) : 5;
                    FFAAdminEvent e = EventlyCore.getAdminEventManager().startEvent(p, FFAAdminEvent.class, timerLength);
                    if (e == null) {
                        p.sendMessage(
                                MiniMessage.miniMessage().deserialize(EventlyCore.prefix() + " <red>An issue occured while starting that event!</red>")
                        );
                    } else {
                        p.sendMessage(
                                MiniMessage.miniMessage().deserialize(EventlyCore.prefix() + " An FFA event has been started!")
                        );
                    }
                }
                case "koth" -> {
                    long timerLength = args.length == 2 && p.hasPermission("events.override-timer") ? Long.parseLong(args[1]) : 5;
                    KOTHAdminEvent e = EventlyCore.getAdminEventManager().startEvent(p, KOTHAdminEvent.class, timerLength);
                    if (e == null) {
                        p.sendMessage(
                                MiniMessage.miniMessage().deserialize(EventlyCore.prefix() + " <red>An issue occured while starting that event!</red>")
                        );
                    } else {
                        p.sendMessage(
                                MiniMessage.miniMessage().deserialize(EventlyCore.prefix() + " A KOTH event has been started!")
                        );
                    }
                }
                case "spawnrace" -> {
                    long timerLength = args.length == 2 && p.hasPermission("events.override-timer") ? Long.parseLong(args[1]) : 5;
                    SpawnRaceAdminEvent e = EventlyCore.getAdminEventManager().startEvent(p, SpawnRaceAdminEvent.class, timerLength);
                    if (e == null) {
                        p.sendMessage(
                                MiniMessage.miniMessage().deserialize(EventlyCore.prefix() + " <red>An issue occured while starting that event!</red>")
                        );
                    } else {
                        p.sendMessage(
                                MiniMessage.miniMessage().deserialize(EventlyCore.prefix() + " A Spawn Race event has been started! Spawn Race is still in BETA so bugs may still occur.")
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
