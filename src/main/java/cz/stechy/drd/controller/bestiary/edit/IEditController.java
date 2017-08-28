package cz.stechy.drd.controller.bestiary.edit;

import cz.stechy.screens.Bundle;

/**
 * Značkovací rozhraní pro identifikaci kontroleru, který slouží pro úpravu vlastností nestvůry.
 */
interface IEditController {

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

}
