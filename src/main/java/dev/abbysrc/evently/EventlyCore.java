package dev.abbysrc.evently;

import co.aikar.commands.PaperCommandManager;
import dev.abbysrc.evently.commands.JoinEventCommand;
import dev.abbysrc.evently.commands.RunEventCommand;
import dev.abbysrc.evently.config.Config;
import dev.abbysrc.evently.events.EventlyAdminEventManager;
import dev.abbysrc.evently.commands.EventlyCommand;
import dev.abbysrc.evently.hook.*;
import dev.abbysrc.evently.listeners.PlayerListener;
import dev.abbysrc.evently.player.EventlyPlayerManager;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class EventlyCore extends JavaPlugin {

    @Getter
    private static EventlyCore instance;
    @Getter
    private static EventlyAdminEventManager adminEventManager;
    @Getter
    private static EventlyPlayerManager playerManager;
    @Getter
    private static Map<String, Location> logoutLocations = new HashMap<>();

    private static final List<Hook> hooks = List.of(
            new LuckPermsHook(),
            new ExcellentCratesHook(),
            new EssentialsHook()
    );

    @Getter
    private static Config configuration;

    @Override
    public void onEnable() {
        instance = this;

        adminEventManager = new EventlyAdminEventManager();
        playerManager = new EventlyPlayerManager();

        configuration = new Config();

        registerHooks();
        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        setEnabled(false);
        // nothing needs to be disabled as of 13/07/2022
        // - abbysrc
    }

    /**
     * Registers all hooks.
     */
    private void registerHooks() {
        for (Hook h : hooks) {
            if (getServer().getPluginManager().getPlugin(h.getPluginId()) == null) {
                // The dependency needed couldn't be found! All dependencies that are hooked into are required so we need to shutdown.
                getLogger().severe("Required plugin " + h.getPluginId() + " could not be found! This is a required dependency.");
                this.setEnabled(false);
            } else {
                try {
                    // If this hook needs manual enabling, invoke its hook method using reflection
                    h.getClass().getMethod("hook").invoke(h);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
                    // If not, it's fine to ignore any exceptions generated
                }
            }
        }
    }

    /**
     * Registers all commands.
     */
    private void registerCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new EventlyCommand("evently"));
        commandManager.registerCommand(new JoinEventCommand("joinevent"));
        commandManager.registerCommand(new RunEventCommand("runevent"));
    }

    /**
     * Registers all listeners.
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    /**
     * Builds prefix used for all commands & messages.
     * @return the prefix
     */
    public static String prefix() {
        return "<gradient:#bae6ff:#ecabff><bold>Events</bold></gradient> •";
    }

    /**
     * Fetches a hook into a plugin based on the plugin's name.
     * @param key The plugin's name
     * @param clazz The class of the plugin's hook
     * @param <T> The hook class
     * @return An instance of the hook class (or null if it is not found)
     */
    @SuppressWarnings("unchecked")
    public static <T extends Hook> T getHook(String key, Class<T> clazz) {
        try {
            List<Hook> dhk = hooks.stream().filter(
                    h -> Objects.equals(h.getPluginId(), key)
            ).collect(Collectors.toList());
            if (dhk.size() == 0) return null;
            else return (T) dhk.get(0);
        } catch (ClassCastException e) {
            return null;
        }
    }

}
