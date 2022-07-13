package dev.abbysrc.evently.hook;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Bukkit;

public class MultiverseHook implements Hook {

    public MultiverseCore mvInstance;

    @Override
    public String getPluginId() {
        return "MultiverseCore";
    }

    public MultiverseCore hook() {
        mvInstance = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        return mvInstance;
    }

}
