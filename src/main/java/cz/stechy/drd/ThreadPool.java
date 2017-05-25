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

    // Velikost threadpoolu
    private static final int EXECUTORS_COUNT = 4;

    // Jediná instance této třídy
    private static ThreadPool INSTANCE;

    // Samotný threadpool
    private final ExecutorService executor = Executors.newFixedThreadPool(EXECUTORS_COUNT);

    /**
     * Privátní konstruktor
     */
    private ThreadPool() {
    }

    public static ThreadPool getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ThreadPool();
        }

        return INSTANCE;
    }

    /**
     * Zařadí task do fronty tasků
     *
     * @param task {@link Callable}
     */
    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
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
}
