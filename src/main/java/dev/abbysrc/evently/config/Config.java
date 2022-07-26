package dev.abbysrc.evently.config;

import com.moandjiezana.toml.Toml;
import dev.abbysrc.evently.EventlyCore;
import lombok.Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config extends Toml {

    private static Lang lang;

    public Config() {
        super();
        read(
            new File(
                EventlyCore.getInstance().getDataFolder(),
                "config.toml"
            )
        );

        lang = Lang.builder()
                .commands(
                    buildCommands()
                ).events(
                    buildEvents()
                ).generics(
                    buildGenerics()
                ).build();

    }

    private Map<String, Lang.Command> buildCommands() {
        HashMap<String, Lang.Command> commandMap = new HashMap<>();
        List<Toml> cmds = getList("lang.command");
        cmds.forEach(cmd -> {
            HashMap<String, String> strings = new HashMap<>();
            cmd.toMap().entrySet().forEach(e -> {
                strings.put(e.getKey(), (String) e.getValue());
            });
            commandMap.put(
                    cmd.getString("name"),
                    Lang.Command.builder().strings(strings).build()
            );
        });
        return commandMap;
    }

    private Map<String, Lang.Event> buildEvents() {
        HashMap<String, Lang.Event> eventMap = new HashMap<>();
        List<Toml> events = getList("lang.event");
        events.forEach(event -> {
            HashMap<String, String> strings = new HashMap<>();
            event.toMap().entrySet().forEach(e -> {
                strings.put(e.getKey(), (String) e.getValue());
            });
            eventMap.put(
                    event.getString("name"),
                    Lang.Event.builder().strings(strings).build()
            );
        });
        return eventMap;
    }

    private Map<String, String> buildGenerics() {
        HashMap<String, String> m = new HashMap<>();
        getTable("lang.generic").toMap().entrySet().forEach(e -> {
            m.put(e.getKey(), (String) e.getValue());
        });
        return m;
    }

    public static Lang lang() {
        return lang;
    }

    @Builder
    public static class Lang {

        private Map<String, Command> commands;
        private Map<String, Event> events;
        private Map<String, String> generics;

        public @Nullable Command command(String k) {
            return commands.get(k);
        }

        public @Nullable Event event(String k) {
            return events.get(k);
        }

        public Map<String, String> generics() {
            return generics;
        }


        @Builder
        public static class Command {
            private Map<String, String> strings;

            public String get(String k) {
                return strings.get(k);
            }

            public Component getWithLegacyCodes(String k) {
                return LegacyComponentSerializer.legacyAmpersand().deserialize(strings.get(k));
            }

            public Component getwithMiniMessage(String k) {
                return MiniMessage.miniMessage().deserialize(strings.get(k));
            }
        }

        @Builder
        public static class Event {
            private Map<String, String> strings;

            public String get(String k) {
                return strings.get(k);
            }

            public Component getWithLegacyCodes(String k) {
                return LegacyComponentSerializer.legacyAmpersand().deserialize(strings.get(k));
            }

            public Component getwithMiniMessage(String k) {
                return MiniMessage.miniMessage().deserialize(strings.get(k));
            }
        }
    }

}
