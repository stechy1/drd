package cz.stechy.drd;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;

/**
 * Třída obsahující threadpool pro exekuci krátkých tasků
 */
public final class ThreadPool {

    // region Constants

    public static final ForkJoinPool DB_EXECUTOR = new ForkJoinPool(1);

    public static final ForkJoinPool COMMON_EXECUTOR = ForkJoinPool.commonPool();

    public static final Executor JAVAFX_EXECUTOR = Platform::runLater;

    // endregion

    // region Public static methods

    /**
     * Zkončí práci threadpoolu
     */
    public static void shutDown() {
        DB_EXECUTOR.shutdown();
        COMMON_EXECUTOR.shutdown();

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
    }

    // endregion
}
