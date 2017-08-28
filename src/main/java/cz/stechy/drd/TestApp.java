package cz.stechy.drd;

import cz.stechy.screens.ScreenManager;

/**
 * Pomocný vstupní bod aplikace pro testovací účely
 */
public final class TestApp extends App {

    // region Constructors

    public TestApp() throws Exception {
    }

    // endregion

    // region Getters & Setters

    public ScreenManager getScreenManager() {
        return manager;
    }

    public Context getContext() {
        return context;
    }

    // endregion
}
