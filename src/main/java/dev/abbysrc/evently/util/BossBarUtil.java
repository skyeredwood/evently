package dev.abbysrc.evently.util;

import dev.abbysrc.evently.EventlyCore;
import dev.abbysrc.evently.events.AdminEvent;
import dev.abbysrc.evently.hook.LuckPermsHook;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BossBarUtil {

    @SuppressWarnings("ConstantConditions")
    public static void update(AdminEvent e) {

        Component prefix = MiniMessage.miniMessage().deserialize(
                EventlyCore.getHook("LuckPerms", LuckPermsHook.class)
                        .getUser(e.getHost())
                        .getCachedData()
                        .getMetaData()
                        .getPrefix()
        );

        long minutes = (e.getStart().getTime() % 3600) / 60;
        long seconds = e.getStart().getTime() % 60;

        String timeString = String.format("%02d:%02d", minutes, seconds);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showBossBar(
                    BossBar.bossBar(
                            MiniMessage.miniMessage()
                                    .deserialize(
                                            "<prefix> is hosting a <event> event in <time>",
                                            Placeholder.component("prefix", prefix),
                                            Placeholder.component("event", Component.text(e.getEventName()).color(TextColor.fromHexString("#ecabff"))),
                                            Placeholder.component("time", Component.text(timeString).color(TextColor.fromHexString("#ecabff")))
                                    ),
                            1f,
                            BossBar.Color.PURPLE,
                            BossBar.Overlay.PROGRESS
                    )
            );
        }

    }

}
