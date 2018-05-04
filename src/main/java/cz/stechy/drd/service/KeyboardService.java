package cz.stechy.drd.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Pomocná třída sloužící pro přímý přístup ke klávesnici
 */
public final class KeyboardService {

    // region Variables

    private static KeyboardService INSTANCE;

    private final ObservableSet<KeyCode> pressedKeys = FXCollections.observableSet();

    // endregion

    // region Constructors

    /**
     * Privátní konstruktor k zabránění vytvoření dalších instancí
     */
    private KeyboardService() {
    }

    // endregion

    // region Public static methods

    public static KeyboardService getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new KeyboardService();
        }

        return INSTANCE;
    }

    // endregion

    // region Public methods

    public boolean isShiftDown() {
        return pressedKeys.contains(KeyCode.SHIFT);
    }

    public boolean isCtrlDown() {
        return pressedKeys.contains(KeyCode.CONTROL);
    }

    public boolean isAltDown() {
        return pressedKeys.contains(KeyCode.ALT);
    }

    public boolean isAltGrDown() {
        return pressedKeys.contains(KeyCode.ALT_GRAPH);
    }

    // region Method handlers

    public void keyPressHandler(KeyEvent event) {
        pressedKeys.add(event.getCode());
    }

    public void keyReleasedHandler(KeyEvent event) {
        pressedKeys.remove(event.getCode());
    }

    // endregion

    // endregion

}
