package cz.stechy.drd.model.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Pomocná třída sloužící pro přímý přístup ke klávesnici
 */
public final class KeyboardService {

    private static KeyboardService INSTANCE;

    private final ObservableSet<KeyCode> pressedKeys = FXCollections.observableSet();

    public final EventHandler<? super KeyEvent> keyPressHandler = event -> {
        pressedKeys.add(event.getCode());
    };
    public EventHandler<? super KeyEvent> keyReleasedHandler = event -> {
        pressedKeys.remove(event.getCode());
    };

    // region Constructors

    private KeyboardService() {}

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

    // endregion

}
