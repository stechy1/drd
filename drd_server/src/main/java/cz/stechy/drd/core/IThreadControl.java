package cz.stechy.drd.core;

/**
 * Rozhraní pro ovládání vlákna
 */
public interface IThreadControl {

    /**
     * Spustí vlákno
     */
    void start();

    /**
     * Spustí ukončovací sekvenci vlákna
     */
    void shutdown();
}
