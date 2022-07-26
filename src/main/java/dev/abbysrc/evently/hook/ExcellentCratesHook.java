package dev.abbysrc.evently.hook;

import dev.abbysrc.evently.EventlyCore;
import dev.abbysrc.evently.config.Config;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ExcellentCratesHook implements Hook {

    @Override
    public String getPluginId() {
        return "ExcellentCrates";
    }

    public void giveAdminEventCrate(Player p) {
        Bukkit.getServer().dispatchCommand(
            Bukkit.getConsoleSender(),
            "excellentcrates give " + p.getName() + " " + EventlyCore.getConfiguration().getTable("config").getString("crate_name") + " 1"
        );
        p.sendMessage(
            LegacyComponentSerializer.legacyAmpersand().deserialize(
                Config.lang().generics().get("event_crate")
            )
        );
    }

}
