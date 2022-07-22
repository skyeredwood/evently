package dev.abbysrc.evently.config;

import java.util.List;

/**
 * Standard library I use for all config loading in my plugins.
 * @param <T> the class of the config file you are reading from (generic so it can be used e.g. on BungeeCord plugins)
 * @author Abby Redwood
 */
public interface ConfigLoader<T, O> {

    String getString(String path);
    int getInt(String path);
    List<?> getList(String path);
    boolean getBoolean(String path);

    T getFile();
    O build();

}
