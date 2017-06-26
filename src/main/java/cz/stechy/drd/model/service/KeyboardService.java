package cz.stechy.drd.model.service;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 * Pomocná třída sloužící pro přímý přístup ke klávesnici
 */
public final class KeyboardService implements NativeKeyListener {

    private static KeyboardService INSTANCE;

    private int modifiers = 0;

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
    
    public boolean isShiftPressed() {
        return (modifiers & NativeKeyEvent.SHIFT_L_MASK) != 0;
    }

    public boolean isCtrlPressed() {
        return (modifiers & NativeKeyEvent.CTRL_L_MASK) != 0;
    }

    public boolean isAltPressed() {
        return (modifiers & NativeKeyEvent.ALT_L_MASK) != 0;
    }

    // endregion

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
        
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        modifiers = nativeKeyEvent.getModifiers();
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }
}
