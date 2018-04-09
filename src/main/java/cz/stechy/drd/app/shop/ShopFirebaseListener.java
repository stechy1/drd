package cz.stechy.drd.app.shop;

/**
 * Listener obsahující metody pro ošetření firebase odpovědi
 */
interface ShopFirebaseListener {

    /**
     * Reakce na odstranění položky z online databáze
     *
     * @param name Název položky
     * @param remote
     * @param success True, pokud bylo odstranění úspěšné, jinak false
     */
    void handleItemRemove(String name, boolean remote, boolean success);

    /**
     * Reakce na nahrání položky do online databáze
     *
     * @param name Název položky
     * @param success True, pokud bylo nahrání úspěšné, jinak false
     */
    void handleItemUpload(String name, boolean success);

}
