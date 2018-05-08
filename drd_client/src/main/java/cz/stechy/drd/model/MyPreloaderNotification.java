package cz.stechy.drd.model;

import javafx.application.Preloader.PreloaderNotification;

/**
 * Třída představující notifikaci pro preloader o aktuálním stavu načítání aplikace
 */
public final class MyPreloaderNotification implements PreloaderNotification {

    // region Variables

    private static double oldProgress = 0;
    private static String oldDescription = "";

    // Progres načítání
    private final double progress;
    // Detail aktuálního průběhu
    private final String description;

    // endregion

    // region Constructors

    /**
     * Aktualizuje popis aktuálního průběhu bez inkrementace průběhu
     *
     * @param description Popis aktuálního průběhu
     */
    public MyPreloaderNotification(String description) {
        this(MyPreloaderNotification.oldProgress, description);
    }

    /**
     * Aktualizuje progres bez akktualizace průběhu
     *
     * @param progress Nový progres
     */
    public MyPreloaderNotification(double progress) {
        this(progress, MyPreloaderNotification.oldDescription);
    }

    /**
     * Vytvoří novou notifikaci
     *
     * @param progress Progres načítání
     * @param description Detail aktuálního průběhu
     */
    public MyPreloaderNotification(double progress, String description) {
        this.progress = progress;
        this.description = description;
        MyPreloaderNotification.oldProgress = progress;
        MyPreloaderNotification.oldDescription = description;
    }

    // endregion

    // region Getters & Setters

    /**
     * Vrátí aktuální stav průběhu načítání
     *
     * @return stav průběhu načítání
     */
    public double getProgress() {
        return progress;
    }

    /**
     * Vrátí popis průběhu načítání
     *
     * @return Popis průběhu
     */
    public String getDescription() {
        return description;
    }

    // endregion
}
