package dev.abbysrc.evently.events;

import dev.abbysrc.evently.EventlyCore;
import dev.abbysrc.evently.config.Config;
import dev.abbysrc.evently.util.BossBarUtil;
import dev.abbysrc.evently.util.InviteMessageUtil;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EventlyAdminEventManager {

    private AdminEvent currentAdminEvent;

    public AdminEvent getCurrentEvent() {
        return currentAdminEvent;
    }
    public void endCurrentEvent() {
        currentAdminEvent = null;
    }

    @SuppressWarnings("unchecked")
    public <T extends AdminEvent> T startEvent(Player host, Class<T> event, long timerLength) {
        try {
            currentAdminEvent = event.getDeclaredConstructor(
                    Player.class,
                    Date.class
            ).newInstance(host, new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(timerLength)));

            int length = (int) (timerLength * 60);
            final int[] runTimes = { 0 };

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (runTimes[0] == length) {
                        if (currentAdminEvent.getPlayers().size() < 2) {
                            currentAdminEvent.getHost().sendMessage(
                                LegacyComponentSerializer.legacyAmpersand().deserialize(
                                    Config.lang().generics().get("event_not_enough_players")
                                )
                            );
                        } else {
                            BossBarUtil.clear();
                            currentAdminEvent.start();
                        }
                        cancel();
                    } else if (runTimes[0] == (length - 30)) {
                        runTimes[0]++;
                        InviteMessageUtil.send(currentAdminEvent);
                        currentAdminEvent.setState(AdminEvent.State.QUEUEING);
                    } else {
                        runTimes[0]++;
                        BossBarUtil.update(currentAdminEvent);
                    }
                }
            }.runTaskTimer(EventlyCore.getInstance(), 0, 20);

            return (T) getCurrentEvent();
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            currentAdminEvent.getHost().sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize(
                    Config.lang().generics().get("event_lifecycle_error")
                )
            );
            return null;
        }
    }

}
