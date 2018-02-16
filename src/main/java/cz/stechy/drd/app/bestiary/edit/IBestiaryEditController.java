package cz.stechy.drd.app.bestiary.edit;

import cz.stechy.screens.Bundle;
import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * Značkovací rozhraní pro identifikaci kontroleru, který slouží pro úpravu vlastností nestvůry.
 */
interface IBestiaryEditController {

    /**
     * Načte z bundlu potřebné vlastnosti nestvůry
     *
     * @param bundle {@link Bundle}
     */
    void loadMobPropertiesFromBundle(Bundle bundle);

    /**
     * Uloží do bundlu vlastnosti nestvůry
     *
     * @param bundle {@link Bundle}
     */
    void saveMobPropertiesToBundle(Bundle bundle);

    /**
     * Vrátí validační property, která říká, zda-li jsou hodnoty v kontroleru validní
     *
     * @return {@link ReadOnlyBooleanProperty}
     */
    ReadOnlyBooleanProperty validProperty();

}
