package dev.abbysrc.evently.commands;

import co.aikar.commands.BaseCommand;
import dev.abbysrc.evently.config.Config;

public class BaseCommandWithLang extends BaseCommand {

    private static String cmdName;

    public BaseCommandWithLang(String cn) {
        this.cmdName = cn;
    }

    public static Config.Lang.Command lang() {
        return Config.lang().command(cmdName);
    }

}
