package dev.abbysrc.evently.hook;

import dev.abbysrc.evently.EventlyCore;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
                "excellentcrates give " + p.getName() + " admin 1"
        );
        p.sendMessage(
                MiniMessage.miniMessage().deserialize(
                        EventlyCore.prefix() + " You won! As a prize, you get 1x Admin Event crate."
                )
        );
    }

}
