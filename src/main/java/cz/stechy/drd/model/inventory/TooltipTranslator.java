package cz.stechy.drd.model.inventory;

import java.util.Map;

/**
 * Rozhraní s metodou, která se zavolá při inicializaci tooltipu
 */
public interface TooltipTranslator {

    /**
     * Zavolá se při inicializaci tooltipu
     *
     * @param map {@link Map} Klíč obsahuje název překládaného atributu
     * hodnota obsahuje hodnotu atributu
     */
    void onTooltipTranslateRequest(Map<String, String> map);

}
