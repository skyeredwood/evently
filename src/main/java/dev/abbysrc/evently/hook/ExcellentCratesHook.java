package dev.abbysrc.evently.hook;

import dev.abbysrc.evently.EventlyCore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class ExcellentCratesHook implements Hook {

    @Override
    public String getPluginId() {
        return "ExcellentCrates";
    }

    private static Class<?> ExcellentCratesAPI;
    private static Method getUserData;

    private static Class<?> CrateUser;
    private static Method addKeys;

    static {
        try {
            ExcellentCratesAPI = Class.forName("su.nightexpress.excellentcrates.ExcellentCratesAPI");
            getUserData = ExcellentCratesAPI.getMethod("getUserData", Player.class);
            CrateUser = Class.forName("su.nightexpress.excellentcrates.data.CrateUser");
            addKeys = CrateUser.getMethod("addKeys", String.class, Integer.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            Logger.getLogger("Evently").severe("An issue occured while using reflection to hook into the ExcellentCrates API.");
            e.printStackTrace();
            if (EventlyCore.getInstance() != null) {
                Bukkit.getPluginManager().disablePlugin(EventlyCore.getInstance());
            }
        }
    }

    public void giveAdminEventCrate(Player p) {
        try {
            CrateUser = getUserData.invoke(p).getClass();
            addKeys.invoke("admin", 1);
            p.sendMessage(
                    MiniMessage.miniMessage().deserialize(
                            EventlyCore.prefix() + " You won! As a prize, you get 1x Admin Event crate."
                    )
            );
        } catch (InvocationTargetException | IllegalAccessException e) {
            p.sendMessage(
                    MiniMessage.miniMessage().deserialize(
                            EventlyCore.prefix() + " <red>Whoops! Something went wrong while trying to give you an Admin Event crate. Contact a staff member to get your prize!</red>"
                    )
            );
        }
    }

}
