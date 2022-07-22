package dev.abbysrc.evently.config.output;

import dev.abbysrc.evently.config.Config;
import dev.abbysrc.evently.config.Configs;
import dev.abbysrc.evently.config.loader.MainConfigLoader;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Builder(builderClassName = "Builder")
@NoArgsConstructor
@Getter()
public class MainConfig implements Config {

    private List<EventSubConfig> events;

    public static MainConfigLoader getLoader() {
        return Configs.loaders().main();
    }

    @lombok.Builder(builderClassName = "Builder")
    @Getter()
    public static class EventSubConfig {
        private String name;
        private String worldName;
        private int x;
        private int y;
        private int z;
    }

    @Nullable
    public EventSubConfig getEvent(String k) {
        return events.stream().filter(i -> Objects.equals(i.name, k)).collect(Collectors.toList()).get(0);
    }

}
