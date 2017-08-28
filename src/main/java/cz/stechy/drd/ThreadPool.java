package cz.stechy.drd;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Třída obsahující threadpool pro exekuci krátkých tasků
 */
public final class ThreadPool {

    // region Constants

    // Velikost threadpoolu
    private static final int EXECUTORS_COUNT = 4;

    // endregion

    // region Variables

    // Jediná instance této třídy
    private static ThreadPool INSTANCE;

    // Samotný threadpool
    private final ExecutorService executor = Executors.newFixedThreadPool(EXECUTORS_COUNT);

    // endregion

    // region Constructors

    /**
     * Privátní konstruktor
     */
    private ThreadPool() {
    }

    // endregion

    /**
     * Vrátí jedinou instanci třídy
     *
     * @return {@link ThreadPool}
     */
    public static ThreadPool getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ThreadPool();
        }

        return INSTANCE;
    }

    // region Public methods

    /**
     * Zařadí úlohu do fronty
     *
     * @param task {@link Callable}
     */
    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }

    /**
     * Zařadí úlohu do fronty
     *
     * @param runnable {@link Runnable}
     * @return {@link Future}
     */
    public Future<?> submit(Runnable runnable) {
        return executor.submit(runnable);
    }

    /**
     * Zkončí práci threadpoolu
     */
    public void shutDown() {
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // endregion
}
