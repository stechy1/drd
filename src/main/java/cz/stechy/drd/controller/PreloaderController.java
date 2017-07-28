package cz.stechy.drd.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class PreloaderController {

    // region Variables

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label lblDescription;

    // endregion

    /**
     * Aktualizuje stav načítacího procesu
     *
     * @param progress Stav načítání
     * @param description Popis, co se děje
     */
    public void updateProgress(double progress, String description) {
        progressBar.setProgress(progress);
        lblDescription.setText(description);
    }
}
