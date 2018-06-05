package cz.stechy.drd.net;

import cz.stechy.drd.app.server.ServerStatusModel;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LanServerWatchdog implements Runnable {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(LanServerWatchdog.class);

    private static final long SLEEP_TIME = 3000l;

    private final Map<UUID, ServerStatusModel> serverMap;
    private final Semaphore semaphore = new Semaphore(0);
    private boolean interupt = false;
    private AtomicBoolean mapEmpty = new AtomicBoolean(true);

    public LanServerWatchdog(Map<UUID, ServerStatusModel> serverMap) {
        this.serverMap = serverMap;
    }

    public void startWatchdog() {
        semaphore.release();
        mapEmpty.set(false);
    }

    public void shutdown() {
        interupt = true;
        mapEmpty.set(true);
        startWatchdog();
    }

    @Override
    public void run() {
        LOGGER.info("Spouštím LanServerWatchdog.");
        while(!interupt) {

            while(mapEmpty.get() && !interupt) {
                LOGGER.info("Jdu spát na semaforu.");
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            LOGGER.info("Jdu pracovat.");
            while(!mapEmpty.get() && !interupt) {

                for (Iterator<ServerStatusModel> iterator = serverMap.values().iterator(); iterator.hasNext();) {
                    final ServerStatusModel model = iterator.next();
                    if (model.hasOldData()) {
                        Platform.runLater(() -> serverMap.remove(model.getServerID()));
                    }
                }

                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException ignored) {}

                mapEmpty.set(serverMap.size() == 0);
            }
        }

        LOGGER.info("LanServerWatchdog končí.");
    }
}
