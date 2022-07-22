package dev.abbysrc.evently.config;

import dev.abbysrc.evently.config.loader.MainConfigLoader;
import dev.abbysrc.evently.config.output.MainConfig;

/**
 * Overarching class for accessing config loaders.
 * @author Abby Redwood
 */
public class Configs {

    /**
     * Fetches the accessor for config loaders.
     * @return a ConfigLoaders instance.
     */
    public static ConfigLoaders loaders() {
        return new ConfigLoaders();
    }

    public static ConfigOutputs outputs() {
        return new ConfigOutputs();
    }

    public static class ConfigLoaders {

        public MainConfigLoader main() {
            return MainConfigLoader.instance;
        }

    }

    public static class ConfigOutputs {

        public MainConfig main() {
            return MainConfig.getLoader().build();
        }

    }
}
