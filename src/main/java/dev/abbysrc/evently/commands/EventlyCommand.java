package dev.abbysrc.evently.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.abbysrc.evently.EventlyCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

@CommandAlias("evently|events|adminevent|adminevents")
@CommandPermission("events.start")
public class EventlyCommand extends BaseCommand {

    @Default
    @Subcommand("about")
    @Description("Information about the Evently plugin")
    public static void onAbout(Player p, String[] args) {
        p.sendMessage(
            MiniMessage.miniMessage().deserialize(
                EventlyCore.prefix() + " <version> • Developed by abbysrc • Use /runevent to start an event",
                Placeholder.component(
                    "version",
                    Component.text("Running version " + EventlyCore.getInstance().getDescription().getVersion())
                )
            )
        );
    }

}
