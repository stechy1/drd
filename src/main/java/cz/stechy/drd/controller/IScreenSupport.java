package cz.stechy.drd.controller;

import cz.stechy.screens.Bundle;
import javafx.scene.Node;

/**
 * Pomocný interface obsahující metody pro komunikaci s BaseControllerem
 */
public interface IScreenSupport {

    /**
     * Zobrazí nový screen a vloží ho na stack
     *
     * @param name Název FXML souboru definující screen
     * @param bundle {@link Bundle} Parametry, které se předají dalšímu screenu
     */
    void startScreen(final String name, final Bundle bundle);

    /**
     * Zobrazí nový screen s tím, že se očekává výsledek
     *
     * @param name Název FXML souboru definující screen
     * @param actionId Id akce, na kterou se pak bude reagovat
     * @param bundle {@link Bundle} Parametry, které se předají dalšímu screenu
     */
    void startScreenForResult(final String name, final int actionId, final Bundle bundle);

    /**
     * Zobrazí nový dialog
     *
     * @param name Název FXML souboru definující screen
     * @param bundle {@link Bundle} Parametry, které se předají dalšímu screenu
     */
    void startNewDialog(final String name, final Bundle bundle);

    /**
     * Zobrazí nový dialog s tím, že se očekává výsledek
     *
     * @param name Název FXML souboru definující screen
     * @param actionId Id akce, na kterou se pak bude reagovat
     * @param bundle {@link Bundle} Parametry, které se předají dalšímu screenu
     */
    void startNewDialogForResult(final String name, final int actionId, final Bundle bundle);

    /**
     * Zobrazí popup dialog na pozici získané z rodičovského prvku
     *
     * @param name Název screenu
     * @param bundle Parametry, které se předají popup dialogu
     * @param parentNode Rodičovský node, ke kterému se dialog "připne"
     */
    void startNewPopupWindow(final String name, final Bundle bundle, Node parentNode);

    /**
     * Zobrazí popup dialog na pozici získané z rodičovského prvku
     *
     * @param name Název screenu
     * @param actionId  ID akce, na kterou se bude později reagovat
     * @param bundle Parametry, které se předají popup dialogu
     * @param parentNode Rodičovský node, ke kterému se dialog "připne"
     */
    void startNewPopupWindowForResult(final String name, final int actionId, final Bundle bundle, Node parentNode);
}
