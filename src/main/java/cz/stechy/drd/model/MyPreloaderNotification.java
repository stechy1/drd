package cz.stechy.drd.model;

import javafx.application.Preloader.PreloaderNotification;

/**
 * Třída představující notifikaci pro preloader o aktuálním stavu načítání aplikace
 */
public final class MyPreloaderNotification implements PreloaderNotification {

    // Progres načítání
    private final double progress;
    // Detail aktuálního progresu
    private final String description;

    /**
     * Vytvoří novou notifikaci
     *
     * @param progress Progres načítání
     * @param description Detail aktuálního progresu
     */
    public MyPreloaderNotification(double progress, String description) {
        this.progress = progress;
        this.description = description;
    }

    public double getProgress() {
        return progress;
    }

    public String getDescription() {
        return description;
    }
}
