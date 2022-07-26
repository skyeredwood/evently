package dev.abbysrc.evently.commands;

import co.aikar.commands.annotation.*;
import dev.abbysrc.evently.EventlyCore;
import dev.abbysrc.evently.config.Config;
import dev.abbysrc.evently.events.impl.FFAAdminEvent;
import dev.abbysrc.evently.events.impl.KOTHAdminEvent;
import dev.abbysrc.evently.events.impl.RaceAdminEvent;
import org.bukkit.entity.Player;

@CommandAlias("runevent|eventrun|startevent|eventstart")
@CommandPermission("events.start")
public class RunEventCommand extends BaseCommandWithLang {

    public RunEventCommand(String cn) {
        super(cn);
    }

    @Default
    @Subcommand("run")
    @Description("Starts an event")
    public static void startEvent(Player p, String[] args) {
        if (args.length == 0) {
            p.sendMessage(Config.lang().generics().get("command_missing_arguments"));
        } else {
            switch (args[0].toLowerCase()) {
                case "ffa" -> {
                    long timerLength = args.length == 2 && p.hasPermission("events.override-timer") ? Long.parseLong(args[1]) : 5;
                    FFAAdminEvent e = EventlyCore.getAdminEventManager().startEvent(p, FFAAdminEvent.class, timerLength);
                    if (e == null) {
                        p.sendMessage(
                                lang().getWithLegacyCodes("event_timer_start_error")
                        );
                    } else {
                        p.sendMessage(
                                lang().getWithLegacyCodes("started_ffa")
                        );
                    }
                }
                case "koth" -> {
                    long timerLength = args.length == 2 && p.hasPermission("events.override-timer") ? Long.parseLong(args[1]) : 5;
                    KOTHAdminEvent e = EventlyCore.getAdminEventManager().startEvent(p, KOTHAdminEvent.class, timerLength);
                    if (e == null) {
                        p.sendMessage(
                                lang().getWithLegacyCodes("event_timer_start_error")
                        );
                    } else {
                        p.sendMessage(
                                lang().getWithLegacyCodes("started_koth")
                        );
                    }
                }
                case "race" -> {
                    long timerLength = args.length == 2 && p.hasPermission("events.override-timer") ? Long.parseLong(args[1]) : 5;
                    RaceAdminEvent e = EventlyCore.getAdminEventManager().startEvent(p, RaceAdminEvent.class, timerLength);
                    if (e == null) {
                        p.sendMessage(
                                lang().getWithLegacyCodes("event_timer_start_error")
                        );
                    } else {
                        p.sendMessage(
                                lang().getWithLegacyCodes("started_race")
                        );
                    }
                }
                default -> p.sendMessage(
                    lang().getWithLegacyCodes("invalid_type")
                );
            }
        }
    }

}
