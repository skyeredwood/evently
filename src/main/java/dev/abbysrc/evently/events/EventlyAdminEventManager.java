package dev.abbysrc.evently.events;

import dev.abbysrc.evently.EventlyCore;
import dev.abbysrc.evently.util.BossBarUtil;
import dev.abbysrc.evently.util.InviteMessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class EventlyAdminEventManager {

    private AdminEvent currentAdminEvent;

    public AdminEvent getCurrentEvent() {
        return currentAdminEvent;
    }

    @SuppressWarnings("unchecked")
    public <T extends AdminEvent> T startEvent(Player host, Class<T> event) {
        try {
            currentAdminEvent = event.getDeclaredConstructor(
                    Player.class,
                    Date.class
            ).newInstance(host, new Date(
                    System.currentTimeMillis() + (5 * 60 * 1000)
            ));

            // I would just use a normal integer but Runnables are annoying and Java keeps whining
            final int[] runTimes = {0};

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (runTimes[0] == 300) {
                        currentAdminEvent.start();
                        cancel();
                    } else if (runTimes[0] == 240) {
                        runTimes[0]++;
                        InviteMessageUtil.send(currentAdminEvent);
                    } else {
                        runTimes[0]++;
                        BossBarUtil.update(currentAdminEvent);
                    }
                }
            }.runTaskTimer(EventlyCore.getInstance(), 0, 20);

            return (T) getCurrentEvent();
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

}
