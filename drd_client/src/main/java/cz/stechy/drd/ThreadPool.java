package cz.stechy.drd;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;

/**
 * Třída obsahující threadpool pro exekuci krátkých tasků
 */
public final class ThreadPool {

    // region Constants

    public static final ForkJoinPool DB_EXECUTOR = new ForkJoinPool(1, forkJoinPool -> {
            final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(forkJoinPool);
            worker.setName("DB-executor-" + worker.getPoolIndex());
            return worker;
        }, null, false);

    public static final ForkJoinPool COMMON_EXECUTOR = ForkJoinPool.commonPool();

    public static final Executor JAVAFX_EXECUTOR = Platform::runLater;

    public static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    // endregion

    // region Public static methods

    /**
     * Zkončí práci threadpoolu
     */
    static void shutDown() {
        DB_EXECUTOR.shutdown();
        COMMON_EXECUTOR.shutdown();
        SCHEDULER.shutdown();

        try {
            DB_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            COMMON_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            SCHEDULER.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // endregion
}
