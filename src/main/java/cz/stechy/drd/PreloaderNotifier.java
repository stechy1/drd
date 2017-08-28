package cz.stechy.drd;

/**
 * Rozhraní pro
 */
public interface PreloaderNotifier {

    /**
     * Aktualizuje samotný popis progresu
     *
     * @param description Popis právě vykonávané akce
     */
    void updateProgressDescription(String description);

    /**
     * Inkrementuje maximální progress
     *
     * @param max Maximální progress
     */
    void increaseMaxProgress(int max);

    /**
     * Inkrementuje progress o hodnotu 1 a přidá popis aktuálního progresu
     *
     * @param description Popis progresu
     */
    default void increaseProgress(String description) {
        increaseProgress(1, description);
    }

    /**
     * Inkrementuje progress a přidá popis aktuálního progresu
     *
     * @param progress Progres
     * @param description Popis progresu
     */
    void increaseProgress(int progress, String description);

}
