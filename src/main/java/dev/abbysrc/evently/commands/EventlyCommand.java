package dev.abbysrc.evently.commands;

import co.aikar.commands.annotation.*;
import org.bukkit.entity.Player;

@CommandAlias("evently|events|adminevent|adminevents")
public class EventlyCommand extends BaseCommandWithLang {

    public EventlyCommand(String cn) {
        super(cn);
    }

    @Default
    @Subcommand("about")
    @Description("Information about the Evently plugin")
    public static void onAbout(Player p, String[] args) {
        p.sendMessage(
            lang().getWithLegacyCodes("message")
        );
    }

}
