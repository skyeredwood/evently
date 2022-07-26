package dev.abbysrc.evently.util;

import dev.abbysrc.evently.EventlyCore;
import dev.abbysrc.evently.events.AdminEvent;
import dev.abbysrc.evently.hook.LuckPermsHook;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Date;

public class BossBarUtil {

    private static BossBar last;

    public static void clear() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (last != null) p.hideBossBar(last);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void update(AdminEvent e) {

        String pfx = EventlyCore.getHook("LuckPerms", LuckPermsHook.class)
                .getUser(e.getHost())
                .getCachedData()
                .getMetaData()
                .getPrefix();

        if (pfx == null) pfx = "";

        Component host = LegacyComponentSerializer.legacyAmpersand().deserialize(pfx + e.getHost().getName());

        long msNow = e.getStart().getTime() - new Date().getTime();
        long minutes = msNow / (60 * 1000);
        long seconds = (msNow / 1000) % 60;

        String timeString = String.format("%d:%02d", minutes, seconds);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (last != null) p.hideBossBar(last);
        }
        last = BossBar.bossBar(
                MiniMessage.miniMessage()
                        .deserialize(
                                "<host> is hosting a <event> admin event in <time>",
                                Placeholder.component("host", host),
                                Placeholder.component("event", Component.text(e.getEventName()).color(TextColor.fromHexString("#ecabff"))),
                                Placeholder.component("time", Component.text(timeString).color(TextColor.fromHexString("#ecabff")))
                        ),
                1f,
                BossBar.Color.PURPLE,
                BossBar.Overlay.PROGRESS
        );
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showBossBar(last);
        }
    }

}
