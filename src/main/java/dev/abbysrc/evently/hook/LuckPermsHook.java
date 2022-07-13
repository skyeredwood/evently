package dev.abbysrc.evently.hook;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LuckPermsHook implements Hook {

    public LuckPerms lpInstance;

    @Override
    public String getPluginId() {
        return "LuckPerms";
    }

    public LuckPerms hook() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        lpInstance = provider.getProvider();
        return lpInstance;
    }

    public LuckPerms get() {
        return lpInstance;
    }

    public boolean inGroup(Player p, String g) {
        return p.hasPermission("group." + g);
    }

    public Group getPlayerGroup(Player player) {
        return lpInstance.getGroupManager().getGroup(getUser(player).getPrimaryGroup());
    }

    public User getUser(Player player) {
        return lpInstance.getPlayerAdapter(Player.class).getUser(player);
    }
}
