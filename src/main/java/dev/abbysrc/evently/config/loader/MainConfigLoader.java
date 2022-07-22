package dev.abbysrc.evently.config.loader;

import dev.abbysrc.evently.EventlyCore;
import dev.abbysrc.evently.config.ConfigLoader;
import dev.abbysrc.evently.config.output.MainConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainConfigLoader implements ConfigLoader<File, MainConfig> {

    File file;
    YamlConfiguration config;

    public static MainConfigLoader instance = new MainConfigLoader();
    public MainConfigLoader() {
        file = new File(EventlyCore.getInstance().getDataFolder(), "config.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            EventlyCore.getInstance().saveResource("config.yml", false);
        }

        config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getString(String path) {
        return config.getString(path);
    }

    @Override
    public int getInt(String path) {
        return config.getInt(path);
    }

    @Override
    public List<?> getList(String path) {
        return config.getList(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    @Override
    public File getFile() {
        return new File(EventlyCore.getInstance().getDataFolder(), "config.yml");
    }

    @Override
    public MainConfig build() {
        MainConfig.Builder b =  MainConfig.builder();
        List<MainConfig.EventSubConfig> evq = new ArrayList<>();
        config.getList("events").forEach(evc -> {
            if (evc instanceof ConfigurationSection evcv) {

                Objects.requireNonNull(evcv.getString("name"), "event name");
                Objects.requireNonNull(evcv.getString("world"), "world name");

                // Just got to hope no one chooses to build the map at these coords...
                if (evcv.getInt("x", -32768) == -32768) throw new NullPointerException("spawnpoint X coordinate");
                if (evcv.getInt("y", -32768) == -32768) throw new NullPointerException("spawnpoint Y coordinate");
                if (evcv.getInt("z", -32768) == -32768) throw new NullPointerException("spawnpoint Z coordinate");

                evq.add(MainConfig.EventSubConfig.builder()
                                .name(evcv.getString("name"))
                                .worldName(evcv.getString("world"))
                                .x(evcv.getInt("x"))
                                .y(evcv.getInt("y"))
                                .z(evcv.getInt("z"))
                                .build());
            }
        });
        b.events(evq);
        return b.build();
    }

}
