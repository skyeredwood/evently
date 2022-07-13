package dev.abbysrc.evently.hook;

import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;

public class EssentialsHook implements Hook {

    public IEssentials esxInstance;

    @Override
    public String getPluginId() {
        return "Essentials";
    }

    public IEssentials hook() {
        esxInstance = (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");
        return esxInstance;
    }

}
